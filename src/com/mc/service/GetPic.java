package com.mc.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Session;

import com.mc.db.DBUtil;
import com.mc.jsonutil.JSONUtil;
import com.mc.util.FilePathUtil;
import com.mc.util.HttpUtil;
import com.mc.util.ImagePreProcess;

public class GetPic extends HttpServlet {

	static String cookie = "";

	static boolean times = true;
	// static String jiamiSessionID = "";

	/**
	 * Constructor of the object.
	 */
	public GetPic() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Connection conn = DBUtil.openConnection();
		Enumeration en = request.getParameterNames();
		boolean isCheck = en.hasMoreElements();
		int i = 0;
		do {
			i++;
			cookie = getSessionID(isCheck);

			if (i > 4) {
				cookie = "null";
				break;
			}
		} while (cookie == null);
		//
		long time = System.currentTimeMillis();
		try {
			if (!isCheck) {
				saveImage(time);
				if (!cookie.equals("null")) {
					cookie = cookie;// + "&txtSecretCode=" + time
					System.out.println("textSession:" + cookie);
				}
			}

			// TODO error pic
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println("getPic:" + e);
		}

		Session session = new Session();
		session.setCookieSessionID(cookie);
		session.setPicurl(isCheck ? ""
				: (FilePathUtil.picPath + "/" + time + ".jpg"));
		PrintWriter out = response.getWriter();
		String json = JSONUtil.toJSON(session);
		// String jsonp = Util.getJsonp(request, json);
		out.write(json);
		DBUtil.closeConn(conn);
	}

	// 获取ip
	public String getRemortIP(HttpServletRequest request) {

		if (request.getHeader("x-forwarded-for") == null) {

			return request.getRemoteAddr();

		}

		return request.getHeader("x-forwarded-for");

	}

	// 得到图片
	public static synchronized InputStream getInputStream() {
		InputStream inputStream = null;
		HttpURLConnection httpURLConnection = null;
		try {
			URL url = new URL(HttpUtil.pic_url);
			if (url != null) {
				httpURLConnection = (HttpURLConnection) url.openConnection();
				String sessionid = null;
				httpURLConnection.setConnectTimeout(6000);// 最大延迟6000毫秒
				httpURLConnection.setRequestProperty("Cookie", cookie);
				int responseCode = httpURLConnection.getResponseCode();
				if (responseCode == 200) {
					inputStream = httpURLConnection.getInputStream();
				}
			}
			System.out.println("getInputstream");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return inputStream;

	}

	public static void main(String[] args) {
		getSessionID(true);
	}

	
	/**
	 * 获取 session
	 */
	private static synchronized String getSessionID(boolean isCheck) {
		String script = "error";
		HttpURLConnection httpURLConnection = null;
		try {
			// Proxy proxy = new Proxy(Type.HTTP, new
			// InetSocketAddress("localhost", 8888));
			/*
			 * if (!HttpUtil.IsReachIP(url_ip.split("/")[2])) { url_ip =
			 * url_ip.replace(url_ip.split("/")[2], HttpUtil.IP_BACKUP);//启用
			 * 备用服务器 }
			 */
			String urlStr = "";
			if (isCheck) {
				urlStr = HttpUtil.BASE_URL + HttpUtil.MOGIC_URl;
			} else {
				urlStr = HttpUtil.BASE_URL + HttpUtil.LOGIN_URL;
			}
			System.out.println(urlStr);
			URL url = new URL(urlStr);
			if (url != null) {
				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				httpURLConnection.setUseCaches(false);
				httpURLConnection.setConnectTimeout(60000);// 最大延迟6000毫秒
				String sessionid = null;
				String cookieval = httpURLConnection
						.getHeaderField("set-cookie");
				if (cookieval != null) {
					sessionid = cookieval.substring(0, cookieval.indexOf(";"));
				}
				System.out.println("session:" + sessionid);
				if ((sessionid == null || sessionid.isEmpty())
						&& !urlStr.equals(HttpUtil.BASE_URL
								+ HttpUtil.LOGIN_URL) && times) {
					times = false;
					return getSessionID(true);
				}

				return sessionid;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return script;
	}

	/**
	 * 保存图片
	 */

	public static synchronized void saveImage(long time) {
		InputStream inputStream = getInputStream();
		FileOutputStream fileOutputStream = null;
		byte[] data = new byte[1024];
		int len = 0;
		try {
			File file = new File(FilePathUtil.tomcatPath + "/" + time + ".jpg");
			if (!file.exists()) {
				System.out.println("createFile");
				file.createNewFile();
			}
			fileOutputStream = new FileOutputStream(FilePathUtil.tomcatPath
					+ "/" + time + ".jpg");
			while ((len = inputStream.read(data)) != -1) {
				fileOutputStream.write(data, 0, len);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}

			} catch (IOException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
