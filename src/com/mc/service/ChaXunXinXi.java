package com.mc.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.ScoreModel;
import model.XueKeScore;

import com.mc.db.DBUtil;
import com.mc.jsonutil.JSONUtil;
import com.mc.thread.MyThread;
import com.mc.thread.ThreadMain;
import com.mc.util.BASE64;
import com.mc.util.CalculateFileTime;
import com.mc.util.ChaXunChengJiUtil;
import com.mc.util.FileEncryptAndDecrypt;
import com.mc.util.HtmlUtil;
import com.mc.util.HttpUtil;
import com.mc.util.Passport;
import com.mc.util.StaticVARUtil;
import com.mc.xml.parse.SaxScoreParser;

import dao.ScoreModelDAO;

public class ChaXunXinXi extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ChaXunXinXi() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String session = request.getParameter("session");
		String filename = request.getParameter("url").split("=")[1];// 将学号作为 文件名
		String name = URLDecoder.decode(request.getParameter("xm"), "utf-8");// 用户名
		String xh = filename;
		// 将用户的名字保存到数据库
		System.out.println("将用户名插入数据库");
		try {
			DBUtil.insertUserName(filename, name);// 在这个方法中增加了 判断是否已经更新了学生姓名。
													// 保证了
			// url 不能通过自己学号去更改别人的姓名。
		} catch (Exception e) {
			// TODO: handle exception
		}

		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		// 修复漏洞 查询成绩 修改xh 可以查询到其他人的成绩。
		/**
		 * 查询数据库该学号和姓名是否对应
		 */
		System.out.println("name:" + name);
		String checkName = name.substring(0, 1);
		if ("?".equals(checkName)) {
			checkName = name.substring(1, 2);
			System.out
					.println("?".equals(checkName) + " " + checkName.length());
			try {
				checkName = name.substring(2, 3);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		// if (!DBUtil.checkXhAndName("%" + checkName + "%", filename)) {//
		// 学号和姓名不能匹配
		// System.out.println("ip warning!!!");
		// out.print("ip warning!!!");
		// } else {
		String root_path = request.getSession().getServletContext()
				.getRealPath(request.getRequestURI());
		root_path = root_path.substring(0, root_path.lastIndexOf("xupt"))
				+ "student_score\\";
		File file = new File(root_path);
		if (!file.exists()) {
			file.mkdirs();// 创建保存 学生 xml的文件夹
		}
		filename = root_path + filename + ".xml";// 文件名, 对学号进行加密
		int is_evaluation = 1;// 判断用户是否评价 如果评价则为1 未评价则为0
		if (new File(filename).exists()
				&& CalculateFileTime.isRequest(new File(filename))) {
			System.out.println("删除文件");
			new File(filename).delete();
		}

		if (!new File(filename).exists()
				|| CalculateFileTime.isRequest(new File(filename))) {// 如果文件不存在
																		// 或者创建文件的日志，超过了最长期限则重新请求。
			@SuppressWarnings("deprecation")
			String url = request.getParameter("url")
					+ "&xm="
					+ URLEncoder.encode(URLDecoder.decode(
							request.getParameter("xm"), "utf-8"), "GBK")
					+ "&gnmkdm=" + request.getParameter("gnmkdm");
			try {
				// 这里是处理异常 如果发生异常则表明 用户未评价
				new ChaXunChengJiUtil().requestHttpGetXML(session, filename,
						url, xh);

			} catch (StringIndexOutOfBoundsException e) {
				// TODO: handle exception
				e.printStackTrace();
				is_evaluation = 0;// 标为0 表示未评价
			}
			/*
			 * if (is_evaluation != 0) {//如果用户已经评价过 则并发请求 ThreadMain threadMain
			 * = new ThreadMain();//定义一个线程池,并发请求
			 * threadMain.thread_request(session, filename, url, threadMain); }
			 */catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String json_result = "";
		if (is_evaluation == 1) {// 评价过
			json_result = getJson(filename);
		} else {
			json_result = "no_evaluation";// 未评价教师
		}
		// out.print(_end_data);//返回xml
		out.print(json_result);// 返回json
		// }
	}

	/**
	 * 将xml字符串整合成json
	 * 
	 * @param result
	 * @return
	 */
	private String getJson(String filename) {
		String json_score = "";
		// TODO Auto-generated method stub
		List<XueKeScore> listScores;// 保存了所有成绩的信息
		// 第一个string 是学年， 第二个是 学期 ，第三个是 成绩
		List<ScoreModel> listTableScore = null;
		SaxScoreParser parser;
		// 第一个string 是学年， 第二个是 学期 ，第三个是 成绩
		listTableScore = new ArrayList<ScoreModel>();
		try {
			// InputStream is =
			// getAssets().open(sdCardDir.getAbsolutePath()+"/xuptscore/score.xml");
			InputStream is = getStringStream(filename);
			parser = new SaxScoreParser(); // 创建SaxBookParser实例
			listScores = parser.parse(is); // 解析输入流
			for (String xn : StaticVARUtil.xn) {
				ScoreModel scoreModel = new ScoreModel();
				scoreModel.setXn(xn);// 添加中间层的 学期参数
				List<XueKeScore> listKeScores = new ArrayList<XueKeScore>();
				for (XueKeScore score : listScores) {// 查找同一学年的成绩，并将这些成绩保存在list中
					if (score.getXn().equals(scoreModel.getXn())) {// 如果是同一学年
						XueKeScore tableScore = new XueKeScore();
						tableScore.setKcdm(score.getKcdm());// 增加课程代码，为了实现好友推荐
						tableScore.setXq(score.getXq());
						tableScore.setCj(score.getCj());
						tableScore.setXf(score.getXf());
						tableScore.setPscj(score.getPscj() == null ? "\\"
								: score.getPscj());
						tableScore.setQmcj(score.getQmcj() == null ? "\\"
								: score.getQmcj());
						tableScore.setXymc(score.getXymc());
						tableScore.setBkcj(score.getBkcj() == null ? " "
								: score.getBkcj());
						if (score.getKcmc() != null) {
							tableScore
									.setKcmc(score.getKcmc().length() > 7 ? score
											.getKcmc().substring(0, 7) + "..."
											: score.getKcmc());
						}
						tableScore.setKcxz(score.getKcxz());
						listKeScores.add(tableScore);
					}
				}
				scoreModel.setList_xueKeScore(listKeScores);
				listTableScore.add(scoreModel);// 对整个数据进行封装
			}
			ScoreModelDAO scoreModelDAO = new ScoreModelDAO();
			scoreModelDAO.setLiScoreModels(listTableScore);
			json_score = JSONUtil.toJSON(scoreModelDAO);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json_score;
	}

	/**
	 * 实现对xml的加密
	 * 
	 * @param filename
	 * @return
	 */
	private InputStream getStringStream(String filename) {
		InputStream in = null;
		try {
			int index = filename.lastIndexOf("\\");

			String xuehao = filename.substring(index + 1, filename.length())
					.split("\\.")[0];
			if (!FileEncryptAndDecrypt.readFileLastByte(filename,
					xuehao.length()).equals(xuehao)) {// 未加密
				// 加密
				FileEncryptAndDecrypt.encrypt(filename, xuehao);
			}
			// 文件加密过
			String destFile = filename.substring(0, index) + "\\temp\\"
					+ Passport.jiami(xuehao);// 临时文件名 用base64进行加密
			if (new File(destFile).exists()) {
				;
			} else {
				FileEncryptAndDecrypt.decrypt(filename, destFile,
						xuehao.length());// 解密
			}
			in = new FileInputStream(new File(destFile));// 读取临时文件的内容
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return in;
	}

}
