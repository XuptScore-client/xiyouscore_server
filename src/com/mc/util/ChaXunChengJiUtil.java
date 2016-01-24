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
	 * ��ѯ�ɼ� �����ȡxml
	 * 
	 * @param session
	 * @param filename
	 *            �ļ���
	 * @param url
	 *            ���� url
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 * @throws StringIndexOutOfBoundsException
	 */
	public void requestHttpGetXML(String session, String filename, String url,
			String xh) throws Exception, UnsupportedEncodingException,
			StringIndexOutOfBoundsException {
		/**
		 * �Ƚ���get���󣬻�ȡhtml�� __VIEWSTATE������ֵ
		 */
		String post_result = scoreHttpResult(session, url, !STATISTICAL, xh);
		System.out.println("post_result" + post_result);
		String _VIEWSTATE = HtmlUtil.getInput(post_result, "__VIEWSTATE");// ���еĳɼ�

		String first_data = new String(BASE64.decryptBASE64(_VIEWSTATE));
		Pattern p = Pattern.compile("b<(.*?)>;");
		java.util.regex.Matcher m = p.matcher(first_data);
		String end_data = "";// ����ƽʱ�ɼ���

		while (m.find()) {
			end_data = m.group(1);
		}
		String _end_data = new String(BASE64.decryptBASE64(end_data), "utf-8");
		_end_data = _end_data.substring(_end_data.indexOf("<?xml"),
				_end_data.indexOf("ram>"))
				+ "ram>";
		// �滻xml�в��Ϸ����ַ�
		_end_data = _end_data.replace(
				_end_data.substring(_end_data.indexOf("<xs:schema"),
						_end_data.indexOf("<diffgr")), " ");
		_end_data = _end_data.replace("utf-16", "UTF-8");
		System.out.println("_end_data" + _end_data);
		// ��xmlд���ļ�
		writeXML(_end_data, filename);
	}

	/**
	 * ����ɼ���ȡ���ص�����
	 * 
	 * @param session
	 * @param url
	 * @param isStatistical
	 *            �Ƿ�ͳ�Ƴɼ�
	 * @return
	 */
	public String scoreHttpResult(String session, String url,
			boolean isStatistical, String xh) {
		String get_result = HttpUtil.gethttp(HttpUtil.BASE_URL + url, session,
				xh);// ��ѯΪͨ����ѧ��
		System.err.println("get_result:" + get_result);
		// get����
		String viewstate = HtmlUtil.getInput(get_result, "__VIEWSTATE");
		viewstate = URLEncoder.encode(viewstate);// ����ʹ�ñ���֮����ַ���
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
		String get_result = HttpUtil.gethttp(HttpUtil.BASE_URL + url, session);// ��ѯΪͨ����ѧ��
		System.err.println("get_result:" + get_result);
		// get����
		String viewstate = HtmlUtil.getInput(get_result, "__VIEWSTATE");
		viewstate = URLEncoder.encode(viewstate);// ����ʹ�ñ���֮����ַ���
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
	 * д�ļ� ��xmlд�� �Լ���.xml�ļ���
	 * 
	 * �汾���£� ʵ�ֽ�xml�ļ����м���
	 */
	private void writeXML(String xml, String filename) {
		File saveFile;
		try {
			saveFile = new File(filename);
			if (saveFile.exists()) {
				saveFile.delete();
			}
			// ���������ɾ�� ���´���
			saveFile.createNewFile();
			Writer outStream = new OutputStreamWriter(new FileOutputStream(
					saveFile), "UTF-8");
			int index = filename.lastIndexOf("\\");

			String xuehao = filename.substring(index + 1, filename.length())
					.split("\\.")[0];
			System.out.println("ѧ��:" + xuehao);
			outStream.write(xml);
			outStream.close();
			// ���ļ����м���
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
