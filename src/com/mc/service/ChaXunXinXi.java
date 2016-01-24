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
		String filename = request.getParameter("url").split("=")[1];// ��ѧ����Ϊ �ļ���
		String name = URLDecoder.decode(request.getParameter("xm"), "utf-8");// �û���
		String xh = filename;
		// ���û������ֱ��浽���ݿ�
		System.out.println("���û����������ݿ�");
		try {
			DBUtil.insertUserName(filename, name);// ����������������� �ж��Ƿ��Ѿ�������ѧ��������
													// ��֤��
			// url ����ͨ���Լ�ѧ��ȥ���ı��˵�������
		} catch (Exception e) {
			// TODO: handle exception
		}

		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		// �޸�©�� ��ѯ�ɼ� �޸�xh ���Բ�ѯ�������˵ĳɼ���
		/**
		 * ��ѯ���ݿ��ѧ�ź������Ƿ��Ӧ
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
		// ѧ�ź���������ƥ��
		// System.out.println("ip warning!!!");
		// out.print("ip warning!!!");
		// } else {
		String root_path = request.getSession().getServletContext()
				.getRealPath(request.getRequestURI());
		root_path = root_path.substring(0, root_path.lastIndexOf("xupt"))
				+ "student_score\\";
		File file = new File(root_path);
		if (!file.exists()) {
			file.mkdirs();// �������� ѧ�� xml���ļ���
		}
		filename = root_path + filename + ".xml";// �ļ���, ��ѧ�Ž��м���
		int is_evaluation = 1;// �ж��û��Ƿ����� ���������Ϊ1 δ������Ϊ0
		if (new File(filename).exists()
				&& CalculateFileTime.isRequest(new File(filename))) {
			System.out.println("ɾ���ļ�");
			new File(filename).delete();
		}

		if (!new File(filename).exists()
				|| CalculateFileTime.isRequest(new File(filename))) {// ����ļ�������
																		// ���ߴ����ļ�����־�����������������������
			@SuppressWarnings("deprecation")
			String url = request.getParameter("url")
					+ "&xm="
					+ URLEncoder.encode(URLDecoder.decode(
							request.getParameter("xm"), "utf-8"), "GBK")
					+ "&gnmkdm=" + request.getParameter("gnmkdm");
			try {
				// �����Ǵ����쳣 ��������쳣����� �û�δ����
				new ChaXunChengJiUtil().requestHttpGetXML(session, filename,
						url, xh);

			} catch (StringIndexOutOfBoundsException e) {
				// TODO: handle exception
				e.printStackTrace();
				is_evaluation = 0;// ��Ϊ0 ��ʾδ����
			}
			/*
			 * if (is_evaluation != 0) {//����û��Ѿ����۹� �򲢷����� ThreadMain threadMain
			 * = new ThreadMain();//����һ���̳߳�,��������
			 * threadMain.thread_request(session, filename, url, threadMain); }
			 */catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String json_result = "";
		if (is_evaluation == 1) {// ���۹�
			json_result = getJson(filename);
		} else {
			json_result = "no_evaluation";// δ���۽�ʦ
		}
		// out.print(_end_data);//����xml
		out.print(json_result);// ����json
		// }
	}

	/**
	 * ��xml�ַ������ϳ�json
	 * 
	 * @param result
	 * @return
	 */
	private String getJson(String filename) {
		String json_score = "";
		// TODO Auto-generated method stub
		List<XueKeScore> listScores;// ���������гɼ�����Ϣ
		// ��һ��string ��ѧ�꣬ �ڶ����� ѧ�� ���������� �ɼ�
		List<ScoreModel> listTableScore = null;
		SaxScoreParser parser;
		// ��һ��string ��ѧ�꣬ �ڶ����� ѧ�� ���������� �ɼ�
		listTableScore = new ArrayList<ScoreModel>();
		try {
			// InputStream is =
			// getAssets().open(sdCardDir.getAbsolutePath()+"/xuptscore/score.xml");
			InputStream is = getStringStream(filename);
			parser = new SaxScoreParser(); // ����SaxBookParserʵ��
			listScores = parser.parse(is); // ����������
			for (String xn : StaticVARUtil.xn) {
				ScoreModel scoreModel = new ScoreModel();
				scoreModel.setXn(xn);// ����м��� ѧ�ڲ���
				List<XueKeScore> listKeScores = new ArrayList<XueKeScore>();
				for (XueKeScore score : listScores) {// ����ͬһѧ��ĳɼ���������Щ�ɼ�������list��
					if (score.getXn().equals(scoreModel.getXn())) {// �����ͬһѧ��
						XueKeScore tableScore = new XueKeScore();
						tableScore.setKcdm(score.getKcdm());// ���ӿγ̴��룬Ϊ��ʵ�ֺ����Ƽ�
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
				listTableScore.add(scoreModel);// ���������ݽ��з�װ
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
	 * ʵ�ֶ�xml�ļ���
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
					xuehao.length()).equals(xuehao)) {// δ����
				// ����
				FileEncryptAndDecrypt.encrypt(filename, xuehao);
			}
			// �ļ����ܹ�
			String destFile = filename.substring(0, index) + "\\temp\\"
					+ Passport.jiami(xuehao);// ��ʱ�ļ��� ��base64���м���
			if (new File(destFile).exists()) {
				;
			} else {
				FileEncryptAndDecrypt.decrypt(filename, destFile,
						xuehao.length());// ����
			}
			in = new FileInputStream(new File(destFile));// ��ȡ��ʱ�ļ�������
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
