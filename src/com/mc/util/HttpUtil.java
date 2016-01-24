package com.mc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpConnection;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;

/**
 * 
 * @author mc
 * @description �ǵ��޸Ĵ��룬���ͷ�������Ӧ��ʱ����Ҫ fall back ����
 */
public class HttpUtil {
	// ����URL

	public static final String BASE_URL = "http://222.24.19.201";
	// public static final String BASE_URL_BACKUP =
	// "http://222.24.19.202/";//����IP
	public static final String pic_url = BASE_URL + "/CheckCode.aspx";
	public static final String LOGIN_URL = "/default2.aspx";// ������֤��
	// public static final String LOGIN_URL = BASE_URL + "default2.aspx";//ʹ����֤��
	public static final String IP_BACKUP = "222.24.62.120";// ����IP
	private static String LOGINERROR = "loginerror";
	public static String CONNECT_EXCEPTION = "�����쳣��";
	public static final String IP_CET = "http://cet.99sushe.com/find";

	/**
	 * ���Ո��/�޸��ܴa post����
	 * 
	 * @param url
	 * @param params
	 * @param cookie
	 * @return
	 */
	public static String http(String url, Map<String, String> params,
			String cookie) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		URL u = null;
		HttpURLConnection con = null;
		// �����������
		StringBuffer sb = new StringBuffer();
		if (params != null) {
			for (Entry<String, String> e : params.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("&");
			}
		}
		// ���Է�������
		try {
			// ���Ը�IP�Ƿ���ͨ�����������ͨ���ñ���IP
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection(/* proxy */);
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Cookie", cookie);
			// con.setRequestProperty("Host", "222.24.62.120");
			// con.setRequestProperty("Cookie", cookie);
			con.setInstanceFollowRedirects(false);
			// ��ȡURLConnection�����Ӧ�������
			out = new PrintWriter(con.getOutputStream());
			// �����������
			out.print(sb.toString().substring(0, sb.length() - 1).toString());
			// flush������Ļ���
			out.flush(); // ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			if (new Integer(con.getResponseCode()).toString().equals("302")) {// ��ȡ״̬��
				String newurl = con.getHeaderField("location");
				System.out.println("newurl:" + newurl + " url:" + url);
				con.disconnect();
				newurl = BASE_URL + newurl;
				if (!newurl.startsWith("/")) {
					result = HttpUtil.gethttp(newurl, cookie);// �����ض����ҳ��
				}
			}

			System.out.println("result result :" + result);
			// System.out.println(result);
		} catch (Exception e) {
			System.out.println("���� POST ��������쳣��" + e);
			e.printStackTrace();
			try {
				if (!new Integer(con.getResponseCode()).toString()
						.equals("302")) {
					return "error";
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return "error";
			}

		}
		// ʹ��finally�����ر��������������
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String http(String url, Map<String, String> params,
			String cookie, String referUrl) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		URL u = null;
		HttpURLConnection con = null;
		// �����������
		StringBuffer sb = new StringBuffer();
		if (params != null) {
			for (Entry<String, String> e : params.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("&");
			}
		}
		// ���Է�������
		try {
			// ���Ը�IP�Ƿ���ͨ�����������ͨ���ñ���IP
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection(/* proxy */);
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Cookie", cookie);
			con.setRequestProperty("Referer", referUrl);
			// con.setRequestProperty("Host", "222.24.62.120");
			// con.setRequestProperty("Cookie", cookie);
			con.setInstanceFollowRedirects(false);
			// ��ȡURLConnection�����Ӧ�������
			out = new PrintWriter(con.getOutputStream());
			// �����������
			out.print(sb.toString().substring(0, sb.length() - 1).toString());
			// flush������Ļ���
			out.flush();
			// ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			if (new Integer(con.getResponseCode()).toString().equals("302")) {// ��ȡ״̬��
				String newurl = con.getHeaderField("location");
				System.out.println("newurl:" + newurl + " url:" + url);
				con.disconnect();
				newurl = BASE_URL + newurl;
				if (!newurl.startsWith("/")) {
					result = HttpUtil.gethttp(newurl, cookie);// �����ض����ҳ��
				}
			}

			System.out.println("result result :" + result);
			// System.out.println(result);
		} catch (Exception e) {
			System.out.println("���� POST ��������쳣��" + e);
			e.printStackTrace();
			try {
				if (!new Integer(con.getResponseCode()).toString()
						.equals("302")) {
					return "error";
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return "error";
			}

		}
		// ʹ��finally�����ر��������������
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * ֱ������ ������cookie������
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String http(String url, Map<String, String> params) {
		URL u = null;
		HttpURLConnection con = null;
		// �����������
		StringBuffer sb = new StringBuffer();
		if (params != null) {
			for (Entry<String, String> e : params.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("&");
			}
		}
		sb = sb.deleteCharAt(sb.length() - 1);
		// ���Է�������
		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			con.setRequestProperty("Referer", "http://cet.99sushe.com/");
			OutputStreamWriter osw = new OutputStreamWriter(
					con.getOutputStream(), "gb2312");
			osw.write(sb.toString());
			osw.flush();
			if (con.getResponseCode() == 200) {
				osw.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} /*
		 * finally { if (con != null) { con.disconnect(); } }
		 */

		// ��ȡ��������
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					con.getInputStream(), "gb2312"));
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
				buffer.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		con.disconnect();
		return buffer.toString();
	}

	public static String http(String url, Map<String, String> params,
			String cookie, String requestType, String host) {
		URL u = null;
		HttpURLConnection con = null;
		// �����������
		StringBuffer sb = new StringBuffer();
		if (params != null) {
			for (Entry<String, String> e : params.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("&");
			}
			sb = sb.deleteCharAt(sb.length() - 1);
		}
		// ���Է�������
		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod(requestType);
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			if (cookie != null) {
				con.setRequestProperty("Cookie", cookie);
			}
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			con.setRequestProperty("Host", host);
			con.setRequestProperty("Connection", "Keep-Alive");

			if (requestType.equals("POST")) {
				OutputStreamWriter osw = new OutputStreamWriter(
						con.getOutputStream(), "utf-8");
				osw.write(sb.toString());
				osw.flush();
				if (con.getResponseCode() == 200) {
					System.out.println("����");
					osw.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} /*
		 * finally { if (con != null) { con.disconnect(); } }
		 */

		// ��ȡ��������
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					con.getInputStream(), "UTF-8"));
			String temp;
			while ((temp = br.readLine()) != null) {
				buffer.append(temp);
				buffer.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		con.disconnect();
		return buffer.toString();
	}

	/**
	 * ��¼�ɹ�֮�󣬻�ȡ ���е�herf
	 * 
	 * @param url
	 * @param cookie
	 * @return
	 */
	public static String gethttp(String url, String cookie/* , String displaymode */) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		URL u = null;
		HttpURLConnection con = null;
		// ���Է�������

		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection(/* proxy */);
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(6000);// ����ӳ�3000����
			con.setRequestProperty("Cookie", cookie);
			// ����BufferedReader����������ȡURL����Ӧ
			try {
				in = new BufferedReader(new InputStreamReader(
						con.getInputStream(), "gb2312"));
				if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
					String line;
					while ((line = in.readLine()) != null) {
						result += line;
					}
					// break;
				}
				System.out.println("Http:" + result);
			} catch (ProtocolException e) {
				// TODO: handle exception
				System.out.println("����ض���");
			}
		} catch (Exception e) {
			System.out.println("get error��" + e);
			e.printStackTrace();
		}
		// ʹ��finally�����ر��������������
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String gethttp(String url,
			String cookie/* , String displaymode */, String xh) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		URL u = null;
		HttpURLConnection con = null;
		// ���Է�������

		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection(/* proxy */);
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(6000);// ����ӳ�3000����
			con.setRequestProperty("Cookie", cookie);
			con.setRequestProperty("Referer",
					"http://222.24.19.201/xs_main.aspx?xh=" + xh);
			// ����BufferedReader����������ȡURL����Ӧ
			try {
				in = new BufferedReader(new InputStreamReader(
						con.getInputStream(), "gb2312"));
				if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
					String line;
					while ((line = in.readLine()) != null) {
						result += line;
					}
					// break;
				}
				System.out.println("Http:" + result);
			} catch (ProtocolException e) {
				// TODO: handle exception
				System.out.println("����ض���");
			}
		} catch (Exception e) {
			System.out.println("get error��" + e);
			e.printStackTrace();
		}
		// ʹ��finally�����ر��������������
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	/*
	 * // �ж� ��ͨ�� public static boolean IsReachIP(String ip) {
	 * 
	 * int timeout = 3000;// ������֤IP��ͨ�ӳ�ʱ�� System.out.println("���ñ���IP"); // url =
	 * BASE_URL_BACKUP; try { return
	 * InetAddress.getByName(ip).isReachable(timeout); } catch
	 * (UnknownHostException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); return false; } catch (IOException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); return false; } }
	 */
	/*
	 * //���� �����ض��� public static String getHttp1(String url, String
	 * cookie,Map<String, String> params){ String result = ""; DefaultHttpClient
	 * httpClient = new DefaultHttpClient(); HttpHost proxy = new
	 * HttpHost("222.24.19.202", 80);
	 * httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,proxy);
	 * httpClient.getParams().setParameter("Cookie", cookie);
	 * HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),20 *
	 * 1000); HttpConnectionParams.setSoTimeout(httpClient.getParams(), 20 *
	 * 1000); HttpGet httpget = new HttpGet(url); // httpget.setHeader("Cookie",
	 * cookie); try { HttpResponse response = httpClient.execute(httpget);
	 * result = EntityUtils.toString(response.getEntity());
	 * System.out.println("result:" + result); } catch (ClientProtocolException
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace();
	 * } return result; }
	 */

}
