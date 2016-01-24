package com.mc.util;

import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.mc.db.DBUtil;

public class Util {

	// ������������ľ���
	public static double getDistance(double lat1, double longt1, double lat2,
			double longt2) {
		double PI = 3.14159265358979323; // Բ����
		double R = 6371229; // ����İ뾶

		double x, y, distance;
		x = (longt2 - longt1) * PI * R
				* Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;
		y = (lat2 - lat1) * PI * R / 180;
		distance = Math.hypot(x, y);

		return distance;
	}

	public static void main(String[] args) {
		String xmlFileName = "E:\\apache-tomcat-6.0.30\\apache-tomcat-6.0.30\\webapps\\xuptqueryscore\\student_score\\01121012.xml";
		String xh = xmlFileName.substring(xmlFileName.lastIndexOf("\\") + 1,
				xmlFileName.lastIndexOf("."));
		System.out.println(xh);
		/*
		 * System.out.println(System.currentTimeMillis());
		 * System.out.println(getDistance(34.8082342, 113.6125439, 34.8002478,
		 * 113.659779)); System.out.println(computeDistance(34.8082342,
		 * 113.6125439, 34.8002478, 113.659779));
		 */
	}

	public static String getJsonp(HttpServletRequest request, String json) {

		String pc_json = "";

		if (request.getParameter("callback") == null) {// ���û���������
			return json;
		} else {
			String callback = request.getParameter("callback");
			pc_json = callback + "(" + json + ")";
		}
		return pc_json;
	}

	public static String requestMogic(String result, String username) {
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
			e.printStackTrace();
		}
		if (newurl.equals("������󣡣�")) {// �������
			result = "error";
		} else if (result.contains("txtSecretCode")) {
			result = "errorReq";
		} else {
			if (username.length() != 8) {
				result = "no_user";
			} else {
				if (!newurl.equals("�û��������ڣ���")) {
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
		return result;
	}
}
