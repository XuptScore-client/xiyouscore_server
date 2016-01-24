package com.mc.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mc.db.DBUtil;
import com.mc.util.CalculateFileTime;
import com.mc.util.HtmlUtil;
import com.mc.util.HttpUtil;

public class LoginServlet extends HttpServlet {

	public LoginServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		int xunhuan_times = 0;
		String sessionID = request.getParameter("session");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		System.out.println("username:" + username + " +" + password);
		String txtSecretCode = request.getParameter("txtSecretCode");

		try {
			Map<String, String> map = new HashMap<String, String>();
			// dDw1MjQ2ODMxNzY7Oz799QJ05KLrvCwm73IGbcfJPI91Aw==
			// dDwxMDQ4ODYxMzk7Oz40hLwUZigX8uXjw9/VSd2LJBb6EA==
			try {
				map.put("__VIEWSTATE", getViewState());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			map.put("txtUserName", username);
			map.put("TextBox2", password);
			map.put("RadioButtonList1", "%D1%A7%C9%FA");// ѧ��
			map.put("Button1", "");
			map.put("lbLanguage", "");
			map.put("hidPdrs", "");
			map.put("hidsc", "");
			if (!txtSecretCode.isEmpty()) {
				map.put("txtSecretCode", txtSecretCode);
				System.out.println("user code:" + txtSecretCode);
			}

			requestData(request, response, sessionID, username, password, map,
					xunhuan_times/* ,filename */);
			// }
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("mcmc"+e);
			e.printStackTrace();
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			out.write("error");
		}

	}

	private synchronized String getViewState() {
		// String url = "http://222.24.19.201/default_ysdx.aspx";
		// String str = HttpUtil.http(url, null, null, "GET", "222.24.19.201");
		// String data = str.substring(str.lastIndexOf("VIEWSTATE"),
		// str.length());

		// return data.split("\"")[2];

		return "dDwyODE2NTM0OTg7Oz4EPWKUJ7QVy9jt5geaO9kcCdS0zQ%3D%3D";
	}

	private synchronized void requestData(HttpServletRequest request,
			HttpServletResponse response, String sessionID, String username,
			String password, Map<String, String> map, int xunhuan_times)
			throws IOException {
		boolean is_succ = false;
		// ��¼���󣬷��� ���û����������������е�ַ��
		String result = null;
		result = HttpUtil.http(HttpUtil.BASE_URL + HttpUtil.LOGIN_URL, map,
				sessionID);// post����
		String msg = result;
		if (!"error".equals(result)) {
			// ������ʽ ��ȡscript�е�����
			msg = HtmlUtil.htmlRemoveTag(result);
		}
		System.out.println("result1:" + result);
		// else {
		// response.setCharacterEncoding("utf-8");
		// PrintWriter out = response.getWriter();
		// result = "{\"listHerf\":{\"herf\":\"error\",\"tittle\":\"�ɼ���ѯ\"}}";
		// out.write(result);
		// return;
		// }

		String newurl = null;
		try {
			newurl = msg.split("\\;")[0].split("\\'")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			do {
				xunhuan_times++;
				requestData(request, response, sessionID, username, password,
						map, xunhuan_times/* ,filename */);// �������쳣��û����ʾ
															// alert��ֱ����ʾ��¼ҳ��
				System.out.println("�������쳣��û����ʾ alert��ֱ����ʾ��¼ҳ��");
			} while (xunhuan_times < 10);
			e.printStackTrace();
		}
		if (newurl.equals("������󣡣�")) {// �������
			result = "error";
		} else if (result.contains("txtSecretCode")) {
			result = "errorCode";
		} else {
			if (username.length() != 8) {
				result = "no_user";
			} else {
				if (newurl.equals("�û��������ڣ���")) {
					xunhuan_times++;
					if (xunhuan_times < 10) {
						try {
							wait(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						map.put("__VIEWSTATE",
								"dDwyODE2NTM0OTg7Oz4EPWKUJ7QVy9jt5geaO9kcCdS0zQ==");
						requestData(request, response, sessionID, username,
								password, map, xunhuan_times/* ,filename */);
					} else {
						System.out.println("ִ��");
						is_succ = true;
						result = "no_user";
					}
				} else {
					// ��¼�ɹ� �������ݿ�
					try {
						if (!DBUtil.isHaveUser(username)) {// ��������ڣ���������û�
							DBUtil.insertUser(username, password);
						} else {// �����û� ����
							DBUtil.updateUserPassword(username, password);
						}
						DBUtil.updateLoginTimes(username);// �����û���¼����
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					newurl = HttpUtil.BASE_URL + newurl;

					// result = HttpUtil.gethttp(newurl, sessionID);// �����ض����ҳ��
					System.out.println("result2:" + result);
					result = HtmlUtil.getHERF(result, 1);// ��ȡ���е�herf �� ����
															// ��ѯ�ɼ���herf
					System.out.println("result3:" + result);
					// ����resultд��txt
					// writeTxt(result, filename);
					/**
					 * �޸�ѧУ������ �����û������ڵ����⣬ ���Խ����result ���浽���أ� Ȼ��ͨ����ѯ���ݿ��ȡ
					 * �û��Ƿ���ȷ�� ��ȻҲ��Ҫÿ��һ��ʱ�����
					 */
				}
			}
		}
		System.out.println("Longi:" + result);
		// ���û��˺� ���뵽 ip��
		if (xunhuan_times == 0 || is_succ) {
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			System.out.println("Login:" + result);
			out.write(result);
		}

		String version = request.getParameter("version");
		if (version != null && !version.isEmpty()) {
			DBUtil.updateUserVersion(username, version);
		}
	}

	public String readTxt(String filePath) {
		String result = "";
		try {
			String lineTxt = "";
			String encoding = "GBK";
			File file = new File(filePath);
			System.out.println(filePath);
			if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// ���ǵ������ʽ
				BufferedReader bufferedReader = new BufferedReader(read);

				while ((lineTxt = bufferedReader.readLine()) != null) {
					result += lineTxt;
				}
				read.close();
			} else {
				System.out.println("�Ҳ���ָ�����ļ�");
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}
		return result;
	}

	public void writeTxt(String data, String filename) {
		File saveFile;
		try {
			saveFile = new File(filename);
			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}
			Writer outStream = new OutputStreamWriter(new FileOutputStream(
					saveFile), "UTF-8");
			outStream.write(data);
			outStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ��ȡip
	public String getRemortIP(HttpServletRequest request) {

		if (request.getHeader("x-forwarded-for") == null) {

			return request.getRemoteAddr();

		}

		return request.getHeader("x-forwarded-for");

	}

	/**
	 * ����������req©���İ���ַ�ֱ���滻��ȫ���ַ�
	 * 
	 * @param s
	 * @return
	 */
	private static String reqEncode(String s) {
		if (s == null || "".equals(s)) {
			return s;
		}
		StringBuilder sb = new StringBuilder(s.length() + 16);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\'':
				sb.append("&prime;");// &acute;");
				break;
			case '��':
				sb.append("&prime;");// &acute;");
				break;
			case '\"':
				sb.append("&quot;");
				break;
			case '��':
				sb.append("&quot;");
				break;
			case '&':
				sb.append("��");
				break;
			case '#':
				sb.append("��");
				break;
			case '\\':
				sb.append('��');
				break;

			default:
				sb.append(c);
				break;
			}
		}
		return sb.toString();
	}
}
