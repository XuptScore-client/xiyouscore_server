package com.mc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;
import java.util.Map;
import java.util.Map.Entry;

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
 * @author Administrator
 * @description �ǵ��޸Ĵ��룬���ͷ�������Ӧ��ʱ����Ҫ fall back ����
 */
public class HttpUtil {
	// ����URL
	public static final String BASE_URL = "http://222.24.19.202/";
	public static final String pic_url = BASE_URL + "CheckCode.aspx";
	public static final String LOGIN_URL = BASE_URL + "default_ysdx.aspx";// ������֤��
	// public static final String LOGIN_URL = BASE_URL + "default2.aspx";//ʹ����֤��
	public static final String IP = "http://222.24.19.202/";
	private static String LOGINERROR = "loginerror";
	public static String CONNECT_EXCEPTION = "�����쳣��";

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
		/*
		 * System.out.println("send_url:" + url);
		 * System.out.println("send_data:" + sb.toString().substring(0,
		 * sb.length() - 1));
		 */
		// ���Է�������
		try {

			/*
			 * Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(
			 * "localhost", 8888));
			 */

			u = new URL(url);
			con = (HttpURLConnection) u.openConnection(/* proxy */);
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Cookie", cookie);
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
				// String newurl = HtmlUtil.getHERF(result,0) ;//��ȡ��ת��herf
				con.disconnect();

//				System.out.println("�ض���" + newurl + "\n " + result);
				newurl = HttpUtil.IP + newurl;
				result = HttpUtil.gethttp(newurl, cookie);// �����ض����ҳ��
//				System.out.println("�ض���" + newurl + "\n " + result);
			}
			//System.out.println(result);
		} catch (Exception e) {
			System.out.println("���� POST ��������쳣��" + e);
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

	/**
	 * ��¼�ɹ�֮�󣬻�ȡ ���е�herf
	 * 
	 * @param url
	 * @param cookie
	 * @return
	 */
	public static String gethttp(String url, String cookie/* , String displaymode */) {
		/*
		 * System.setProperty("http.maxRedirects", "100");
		 * CookieHandler.setDefault(new CookieManager(null,
		 * CookiePolicy.ACCEPT_ALL));
		 */
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		URL u = null;
		HttpURLConnection con = null;
//		System.out.println("send_url:" + url);
		// ���Է�������

		try {
			/*
			 * Proxy proxy = new Proxy(Type.HTTP, new
			 * InetSocketAddress("localhost", 8888));
			 */
			/*
			 * BasicHttpParams params = new BasicHttpParams();
			 * 
			 * HttpClientParams.setRedirecting(params, true);
			 */
			// do{
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection(/* proxy */);
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(6000);// ����ӳ�3000����
			con.setRequestProperty("Cookie", cookie);
			// con.setReadTimeout(3000);
			// con.setFollowRedirects(true);
			// con.setInstanceFollowRedirects(false);
			// ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String line;
				while ((line = in.readLine()) != null) {
					result += line;
				}
			//	System.out.println("���ص�html����:" + result);

				// break;
			}
			// }while(true);
			/*
			 * if (s.equals("��¼ҳ��")) {// �ص���ҳ ��¼ʧ�� return "��֤����"; } else { if
			 * (s.equals("selfserviceerror")) {// ������� �����˺Ŵ��� ����Ǹ�������Ļ� ����
			 * String str = HtmlUtil.getHtmlBody(result).split("\\,")[0];// ��ȡ
			 * // ����ԭ�� return str; } else if (s.equals("�Է�����ҳ")) {// ��¼�ɹ� //
			 * ���ﷵ�� �Է���ҳ��� ������Ϣ // HtmlUtil.parseHtmlTR(result); return
			 * HtmlUtil.parseHtmlTR(result, displaymode); } }
			 */

		} catch (Exception e) {
			System.out.println("���� get ��������쳣��" + e);
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
