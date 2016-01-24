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

public class ForgetCetServlet extends HttpServlet {

	private boolean isSuccess = false;

	/**
	 * Constructor of the object.
	 */
	public ForgetCetServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pCode = request.getParameter("code");
		String kChang = request.getParameter("kc");
		String zWei = request.getParameter("zw");
		String name = URLDecoder.decode(request.getParameter("name"), "utf-8");

		System.out.println(name);
		if (name.length() >= 3) {
			name = name.substring(0, 2);
		}
		name = URLEncoder.encode(name, "gb2312");

		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String result = null;
		if (kChang.equals("no")) {
			kChang = "0";
		}
		if (zWei.equals("no")) {
			zWei = "0";
		}
		try {

			int pCodeInt = Integer.parseInt(pCode);
			int kChangInt = Integer.parseInt(kChang);
			int zWeiInt = Integer.parseInt(zWei);

			long code = pCodeInt + kChangInt + zWeiInt;
			if (String.valueOf(code).length() != 15
					&& (kChangInt + zWeiInt) > kChangInt
					&& (kChangInt + zWeiInt) > zWeiInt) {
				out.print("error");
			}

			new RequestThread(name, pCodeInt, kChangInt, zWeiInt, false, out)
					.start();
			new RequestThread(name, pCodeInt, kChangInt, zWeiInt, true, out)
					.start();

		} catch (Exception e) {
			// TODO: handle exception
			result = "error";
			out.print(result);
		}

	}

	private String postCet(String adminTicket, String name) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", adminTicket);
		map.put("name", name);
		String result = HttpUtil.http(HttpUtil.IP_CET, map).replace("/n", "")
				.trim();
		return result;
	}

	private String getCet(int pCodeInt, int kChangInt, int zWeiInt) {

		long adminTicket = pCodeInt + kChangInt + zWeiInt;
		return String.valueOf(adminTicket);

	}

	class RequestThread extends Thread {

		String name;
		int pCodeInt;
		int kChangInt;
		int zWeiInt;
		boolean isBingfa;
		PrintWriter out;

		public RequestThread(String name, int pCodeInt, int kChangInt,
				int zWeiInt, boolean isBingfa, PrintWriter out) {
			this.name = name;
			this.pCodeInt = pCodeInt;
			this.kChangInt = kChangInt;
			this.zWeiInt = zWeiInt;
			this.isBingfa = isBingfa;
			this.out = out;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int swap = kChangInt + zWeiInt;
			if (swap == kChangInt) {// 座位未知
				for (int i = isBingfa ? zWeiInt : 50; i < (isBingfa ? 50 : 100)
						&& !isSuccess; i++) {
					String adminTicket = getCet(pCodeInt, kChangInt, zWeiInt);
					String result = postCet(adminTicket, name);
					if (!("2").equals(result) && !("4").equals(result)) {
						isSuccess = true;
						out.print(result);
					}
				}

			} else if (swap == zWeiInt) {// 考场未知
				for (int i = isBingfa ? kChangInt : 50; i < (isBingfa ? 50
						: 100) && !isSuccess; i++) {
					String adminTicket = getCet(pCodeInt, kChangInt, zWeiInt);
					String result = postCet(adminTicket, name);
					if (!("2").equals(result) && !("4").equals(result)) {
						isSuccess = true;
						out.print(result);
					}
				}
			} else if (swap == 0) {
				for (int i = isBingfa ? kChangInt : 50; i < (isBingfa ? 50
						: 100) && !isSuccess; i++) {
					for (int j = isBingfa ? zWeiInt : 50; j < (isBingfa ? 50
							: 100) && !isSuccess; i++) {
						String adminTicket = getCet(pCodeInt, kChangInt,
								zWeiInt);
						String result = postCet(adminTicket, name);
						if (!("2").equals(result) && !("4").equals(result)) {
							isSuccess = true;
							out.print(result);
						}
					}
				}
			} else {
				String adminTicket = getCet(pCodeInt, kChangInt, zWeiInt);
				String result = postCet(adminTicket, name);
				if (!("2").equals(result) && !("4").equals(result)) {
					isSuccess = true;
					out.print(result);
				}
			}
		}

	}

}
