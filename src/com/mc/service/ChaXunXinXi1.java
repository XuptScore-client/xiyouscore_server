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
import com.mc.util.BASE64;
import com.mc.util.CalculateFileTime;
import com.mc.util.HtmlUtil;
import com.mc.util.HttpUtil;
import com.mc.util.StaticVARUtil;
import com.mc.util.UnicodeEncoder;
import com.mc.xml.parse.SaxScoreParser;

import dao.ScoreModelDAO;

public class ChaXunXinXi1 extends HttpServlet {

	public ChaXunXinXi1() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String session = request.getParameter("session");
		String username = request.getParameter("username");
		String name = URLEncoder.encode(URLDecoder.decode(
				request.getParameter("xm"), "utf-8"));
		System.out.println(name);
		String filename = username;// 将学号作为 文件名
		String root_path = request.getSession().getServletContext()
				.getRealPath(request.getRequestURI());
		root_path = root_path.substring(0, root_path.lastIndexOf("xupt"))
				+ "student_score/";
		File file = new File(root_path);
		if (!file.exists()) {
			file.mkdirs();// 创建保存 学生 xml的文件夹
		}
		// 将用户的名字保存到数据库
		DBUtil.insertUserName(filename, name);

		filename = root_path + filename + ".xml";// 文件名

		String url = "xscjcx.aspx?xh=" + username + "&xm=" + name + "&gnmkdm="
				+ "gnmkdm=N121605";

		System.out.println(url);

		try {
			if (!new File(filename).exists()
					|| CalculateFileTime.isRequest(new File(filename))) {
				requestHttpGetXML(session, filename, url);
			}
			String json_result = getJson(filename);

			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			// out.print(_end_data);//返回xml
			out.print(json_result);// 返回json
			// System.out.println(_end_data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void requestHttpGetXML(String session, String filename, String url)
			throws Exception, UnsupportedEncodingException {
		/**
		 * 先进行get请求，获取html中 __VIEWSTATE参数的值
		 */
		String get_result = HttpUtil.gethttp(HttpUtil.BASE_URL + url, session);// 查询为通过的学科
		System.out.println(HttpUtil.BASE_URL + url);
		// get请求
		String viewstate = HtmlUtil.getInput(get_result, "__VIEWSTATE");
		viewstate = URLEncoder.encode(viewstate);// 必须使用编码之后的字符串
		/*
		 * System.out.println(); System.out.println(viewstate);
		 */

		Map<String, String> map = new HashMap<String, String>();
		map.put("__EVENTTARGET", "");
		map.put("__EVENTARGUMENT", "");
		map.put("__VIEWSTATE", viewstate);
		map.put("hidLanguage", "");
		map.put("ddlXN", "");
		map.put("ddlXQ", "");
		map.put("ddl_kcxz", "");
		map.put("btn_zcj", "%C0%FA%C4%EA%B3%C9%BC%A8");
		String post_result = HttpUtil.http(HttpUtil.BASE_URL + url, map,
				session);
		String _VIEWSTATE = HtmlUtil.getInput(post_result, "__VIEWSTATE");

		String first_data = new String(BASE64.decryptBASE64(_VIEWSTATE));
		// String end_data =
		// first_data.replaceAll("<t[^>]*>|</t>|<td[^>]*>|</td>|<p[^>]*>|</p>|<span[^>]*>|</span>|<o:p[^>]*>|</o:p>",
		// "");
		// System.out.println(first_data);
		Pattern p = Pattern.compile("b<(.*?)>;");
		java.util.regex.Matcher m = p.matcher(first_data);
		String end_data = "";// 保存平时成绩的

		while (m.find()) {
			end_data = m.group(1);
		}
		// System.out.println(end_data);
		String _end_data = new String(BASE64.decryptBASE64(end_data), "utf-8");

		_end_data = _end_data.substring(_end_data.indexOf("<?xml"),
				_end_data.indexOf("ram>"))
				+ "ram>";
		// 替换xml中不合法的字符
		_end_data = _end_data.replace(
				_end_data.substring(_end_data.indexOf("<xs:schema"),
						_end_data.indexOf("<diffgr")), " ");
		_end_data = _end_data.replace("utf-16", "UTF-8");
		// System.out.println(_end_data);
		// String json = JSONutil.get_JSON(_end_data);
		// System.out.println(_end_data);
		// 将xml写入文件
		writeXML(_end_data, filename);
	}

	/**
	 * 写文件 将xml写入 自己的.xml文件中
	 */
	private void writeXML(String xml, String filename) {
		File saveFile;
		try {
			saveFile = new File(filename);
			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}
			Writer outStream = new OutputStreamWriter(new FileOutputStream(
					saveFile), "UTF-8");
			outStream.write(xml);
			outStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
						tableScore.setXq(score.getXq());
						tableScore.setCj(score.getCj());
						tableScore.setXf(score.getXf());
						tableScore.setPscj(score.getPscj() == null ? "\\"
								: score.getPscj());
						tableScore.setQmcj(score.getQmcj() == null ? "\\"
								: score.getQmcj());
						tableScore.setXymc(score.getXymc());
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
			// System.out.println("json:" + json_score);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json_score;
	}

	/**
	 * 读文件
	 */
	private InputStream getStringStream(String filename) {
		InputStream in = null;
		try {
			// System.out.println("读文件" + filename);
			in = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return in;
	}

}
