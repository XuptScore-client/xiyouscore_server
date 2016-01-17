package com.mc.util;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

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
		String xh = xmlFileName.substring(xmlFileName.lastIndexOf("\\")+1, xmlFileName.lastIndexOf("."));
		System.out.println(xh);
		/*System.out.println(System.currentTimeMillis());
		System.out.println(getDistance(34.8082342, 113.6125439, 34.8002478,
				113.659779));
		System.out.println(computeDistance(34.8082342, 113.6125439, 34.8002478,
				113.659779));*/
	}
	public static String getJsonp(HttpServletRequest request,String json){
		
		String pc_json = "";

		if (request.getParameter("callback")==null) {//如果没有这个参数
			return json;
		}else {
			String callback = request.getParameter("callback");
			pc_json = callback+"("+json+")";
		}
		return pc_json;
	}
}
