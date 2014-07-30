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
 * @description 记得修改代码，当和服务器响应的时候需要 fall back 代码
 */
public class HttpUtil {
	// 基础URL
	public static final String BASE_URL = "http://222.24.19.202/";
	public static final String pic_url = BASE_URL + "CheckCode.aspx";
	public static final String LOGIN_URL = BASE_URL + "default_ysdx.aspx";// 不用验证码
	// public static final String LOGIN_URL = BASE_URL + "default2.aspx";//使用验证码
	public static final String IP = "http://222.24.19.202/";
	private static String LOGINERROR = "loginerror";
	public static String CONNECT_EXCEPTION = "网络异常！";

	/**
	 * 登錄請求/修改密碼 post请求
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
		// 构建请求参数
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
		// 尝试发送请求
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
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(con.getOutputStream());
			// 发送请求参数
			out.print(sb.toString().substring(0, sb.length() - 1).toString());
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}

			if (new Integer(con.getResponseCode()).toString().equals("302")) {// 获取状态吗
				String newurl = con.getHeaderField("location");
				// String newurl = HtmlUtil.getHERF(result,0) ;//获取跳转的herf
				con.disconnect();

//				System.out.println("重定向" + newurl + "\n " + result);
				newurl = HttpUtil.IP + newurl;
				result = HttpUtil.gethttp(newurl, cookie);// 返回重定向的页面
//				System.out.println("重定向" + newurl + "\n " + result);
			}
			//System.out.println(result);
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
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
	 * 登录成功之后，获取 所有的herf
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
		// 尝试发送请求

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
			con.setConnectTimeout(6000);// 最大延迟3000毫秒
			con.setRequestProperty("Cookie", cookie);
			// con.setReadTimeout(3000);
			// con.setFollowRedirects(true);
			// con.setInstanceFollowRedirects(false);
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String line;
				while ((line = in.readLine()) != null) {
					result += line;
				}
			//	System.out.println("返回的html界面:" + result);

				// break;
			}
			// }while(true);
			/*
			 * if (s.equals("登录页面")) {// 回到主页 登录失败 return "验证错误"; } else { if
			 * (s.equals("selfserviceerror")) {// 密码错误 或者账号错误 如果是更改密码的话 返回
			 * String str = HtmlUtil.getHtmlBody(result).split("\\,")[0];// 获取
			 * // 错误原因 return str; } else if (s.equals("自服务网页")) {// 登录成功 //
			 * 这里返回 自服务页面的 个人信息 // HtmlUtil.parseHtmlTR(result); return
			 * HtmlUtil.parseHtmlTR(result, displaymode); } }
			 */

		} catch (Exception e) {
			System.out.println("发送 get 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
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
	 * //避免 连续重定向 public static String getHttp1(String url, String
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
