package com.mc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ChaXunChengJiUtil {
	private static final boolean STATISTICAL = true;

	/**
	 * 查询成绩 请求获取xml
	 * 
	 * @param session
	 * @param filename
	 *            文件名
	 * @param url
	 *            请求 url
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 * @throws StringIndexOutOfBoundsException
	 */
	public void requestHttpGetXML(String session, String filename, String url,
			String xh) throws Exception, UnsupportedEncodingException,
			StringIndexOutOfBoundsException {
		/**
		 * 先进行get请求，获取html中 __VIEWSTATE参数的值
		 */
		String post_result = scoreHttpResult(session, url, !STATISTICAL, xh);
		System.out.println("post_result" + post_result);
		String _VIEWSTATE = HtmlUtil.getInput(post_result, "__VIEWSTATE");// 所有的成绩

		String first_data = new String(BASE64.decryptBASE64(_VIEWSTATE));
		Pattern p = Pattern.compile("b<(.*?)>;");
		java.util.regex.Matcher m = p.matcher(first_data);
		String end_data = "";// 保存平时成绩的

		while (m.find()) {
			end_data = m.group(1);
		}
		String _end_data = new String(BASE64.decryptBASE64(end_data), "utf-8");
		_end_data = _end_data.substring(_end_data.indexOf("<?xml"),
				_end_data.indexOf("ram>"))
				+ "ram>";
		// 替换xml中不合法的字符
		_end_data = _end_data.replace(
				_end_data.substring(_end_data.indexOf("<xs:schema"),
						_end_data.indexOf("<diffgr")), " ");
		_end_data = _end_data.replace("utf-16", "UTF-8");
		System.out.println("_end_data" + _end_data);
		// 将xml写入文件
		writeXML(_end_data, filename);
	}

	/**
	 * 请求成绩获取返回的数据
	 * 
	 * @param session
	 * @param url
	 * @param isStatistical
	 *            是否统计成绩
	 * @return
	 */
	public String scoreHttpResult(String session, String url,
			boolean isStatistical, String xh) {
		String get_result = HttpUtil.gethttp(HttpUtil.BASE_URL + url, session,
				xh);// 查询为通过的学科
		System.err.println("get_result:" + get_result);
		// get请求
		String viewstate = HtmlUtil.getInput(get_result, "__VIEWSTATE");
		viewstate = URLEncoder.encode(viewstate);// 必须使用编码之后的字符串
		// System.out.println("viewstate:" + viewstate);
		Map<String, String> map = new HashMap<String, String>();
		map.put("__EVENTTARGET", "");
		map.put("__EVENTARGUMENT", "");
		map.put("__VIEWSTATE", viewstate);
		map.put("hidLanguage", "");
		map.put("ddlXN", "");
		map.put("ddlXQ", "");
		map.put("ddl_kcxz", "");
		map.put(isStatistical ? "Button1" : "btn_zcj",
				isStatistical ? "%B3%C9%BC%A8%CD%B3%BC%C6"
						: "%C0%FA%C4%EA%B3%C9%BC%A8");
		String post_result = HttpUtil.http(HttpUtil.BASE_URL + url, map,
				session, HttpUtil.BASE_URL + url);
		return post_result;
	}

	public String scoreHttpResult(String session, String url,
			boolean isStatistical) {
		String get_result = HttpUtil.gethttp(HttpUtil.BASE_URL + url, session);// 查询为通过的学科
		System.err.println("get_result:" + get_result);
		// get请求
		String viewstate = HtmlUtil.getInput(get_result, "__VIEWSTATE");
		viewstate = URLEncoder.encode(viewstate);// 必须使用编码之后的字符串
		System.out.println("viewstate:" + viewstate);
		Map<String, String> map = new HashMap<String, String>();
		map.put("__EVENTTARGET", "");
		map.put("__EVENTARGUMENT", "");
		map.put("__VIEWSTATE", viewstate);
		map.put("hidLanguage", "");
		map.put("ddlXN", "");
		map.put("ddlXQ", "");
		map.put("ddl_kcxz", "");
		map.put(isStatistical ? "Button1" : "btn_zcj",
				isStatistical ? "%B3%C9%BC%A8%CD%B3%BC%C6"
						: "%C0%FA%C4%EA%B3%C9%BC%A8");
		String post_result = HttpUtil.http(HttpUtil.BASE_URL + url, map,
				session);
		return post_result;
	}

	/**
	 * 写文件 将xml写入 自己的.xml文件中
	 * 
	 * 版本更新： 实现将xml文件进行加密
	 */
	private void writeXML(String xml, String filename) {
		File saveFile;
		try {
			saveFile = new File(filename);
			if (saveFile.exists()) {
				saveFile.delete();
			}
			// 如果存在则删除 重新创建
			saveFile.createNewFile();
			Writer outStream = new OutputStreamWriter(new FileOutputStream(
					saveFile), "UTF-8");
			int index = filename.lastIndexOf("\\");

			String xuehao = filename.substring(index + 1, filename.length())
					.split("\\.")[0];
			System.out.println("学号:" + xuehao);
			outStream.write(xml);
			outStream.close();
			// 对文件进行加密
			try {
				FileEncryptAndDecrypt.encrypt(filename, xuehao);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
