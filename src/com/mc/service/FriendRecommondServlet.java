package com.mc.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mc.util.Passport;

public class FriendRecommondServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FriendRecommondServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);

	}

	@SuppressWarnings("deprecation")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String falseData = URLDecoder.decode(request.getParameter("data"));// 通过时间进行加密之后的学号
		String time = URLDecoder.decode(request.getParameter("viewstate"));// 客户端产生的时间
																			// 通过
																			// 密钥进行机密
		HashMap<String, String> xhAndXnMap = calculateXh(falseData, time);
		String realXh = xhAndXnMap.get("xh");// 学号
		String realKcdm = xhAndXnMap.get("kcdm");
		String realScore = xhAndXnMap.get("score");
		String xn = xhAndXnMap.get("xn");// 学年
		String xq = xhAndXnMap.get("xq");// 学期

	}

	/**
	 * 首先解密得到正确的时间，然后通过正确的时间解密得到正确的学号 加密算法: 对时间进行base64加密，然后作为密钥对时间进行加密base64
	 * 
	 * @param falseXh
	 *            假的学号
	 * @param time
	 *            时间点
	 * @return 返回真实学号
	 */
	private HashMap<String, String> calculateXh(String falseXh, String time) {
		// TODO Auto-generated method stub
		String realXh = "";
		String realTime = new Passport().jiemi(time,
				String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
		HashMap<String, String> xhAndXnMap = new HashMap<String, String>();// 学号
																			// 学年
																			// 学期
		try {
			realXh = new Passport().jiemi(falseXh, realTime);
			System.out.println("真实数据:" + realXh);
			String[] xhAndXn = new String(realXh).split("\\|");
			xhAndXnMap.put("xh", xhAndXn[0]);
			xhAndXnMap.put("kcdm", xhAndXn[1].split("\\|")[0]);
			xhAndXnMap.put("xn", xhAndXn[1].split("\\|")[1]);
			xhAndXnMap.put("xq", xhAndXn[1].split("\\|")[2]);
			xhAndXnMap.put("score", xhAndXn[2]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xhAndXnMap;
	}

}
