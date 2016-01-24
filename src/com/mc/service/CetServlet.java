package com.mc.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mc.util.HttpUtil;
import com.mc.util.Passport;

public class CetServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CetServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String adminTicket = request.getParameter("ticket");
		String time = request.getParameter("time");
		String name = URLDecoder.decode(request.getParameter("name"), "utf-8");

		System.out.println(name);
		if (name.length() >= 3) {
			name = name.substring(0, 2);
		}
		name = URLEncoder.encode(name, "gb2312");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		adminTicket = calculateXh(adminTicket, time);
		String result = postCet(adminTicket, name);
		System.out.println(result);
		out.print(result);

	}

	private String postCet(String adminTicket, String name) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", adminTicket);
		map.put("name", name);
		String result = HttpUtil.http(HttpUtil.IP_CET, map).replace("/n", "")
				.trim();
		return result;
	}

	private String calculateXh(String falseTic, String time) {
		// TODO Auto-generated method stub
		String realTic = "";
		String realTime = new Passport().jiemi(time,
				String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
		HashMap<String, String> xhAndXnMap = new HashMap<String, String>();// 学号
																			// 学年
																			// 学期
		try {
			if (time.equals("iOS")) {
				realTic = new Passport().base64_decode(falseTic);// 提供给 iOS的接口
			} else {
				realTic = new Passport().jiemi(falseTic, realTime);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return realTic;
	}

}
