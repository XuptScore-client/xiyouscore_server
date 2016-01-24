package com.mc.util;

import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.mc.db.DBUtil;

public class Util {

	// 计算两个坐标的距离
	public static double getDistance(double lat1, double longt1, double lat2,
			double longt2) {
		double PI = 3.14159265358979323; // 圆周率
		double R = 6371229; // 地球的半径

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

		if (request.getParameter("callback") == null) {// 如果没有这个参数
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
			// 正则表达式 获取script中的数据
			msg = HtmlUtil.htmlRemoveTag(result);
		}
		System.out.println("result1:" + result);
		// else {
		// response.setCharacterEncoding("utf-8");
		// PrintWriter out = response.getWriter();
		// result = "{\"listHerf\":{\"herf\":\"error\",\"tittle\":\"成绩查询\"}}";
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
		if (newurl.equals("密码错误！！")) {// 密码错误
			result = "error";
		} else if (result.contains("txtSecretCode")) {
			result = "errorReq";
		} else {
			if (username.length() != 8) {
				result = "no_user";
			} else {
				if (!newurl.equals("用户名不存在！！")) {
					newurl = HttpUtil.BASE_URL + newurl;

					// result = HttpUtil.gethttp(newurl, sessionID);// 返回重定向的页面
					System.out.println("result2:" + result);
					result = HtmlUtil.getHERF(result, 1);// 获取所有的herf ， 比如
															// 查询成绩的herf
					System.out.println("result3:" + result);
					// 将该result写进txt
					// writeTxt(result, filename);
					/**
					 * 修改学校服务器 出现用户不存在的问题， 可以将这个result 保存到本地， 然后通过查询数据库获取
					 * 用户是否正确。 当然也是要每个一段时间更新
					 */
				}
			}
		}
		return result;
	}
}
