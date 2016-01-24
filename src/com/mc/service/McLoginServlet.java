package com.mc.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mc.util.HttpUtil;
import com.mc.util.Passport;
import com.mc.util.Util;

public class McLoginServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public McLoginServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String cookies = request.getParameter("mc");
		String username = request.getParameter("username");
		String time = request.getParameter("secret");
		cookies = calculateXh(cookies, time);
		System.out.println("cookies:" + cookies);
		System.out.println("username:" + username);
		String newurl = "http://222.24.19.201/xs_main.aspx?xh=" + username;
		String result = HttpUtil.gethttp(newurl, cookies);// �����ض����ҳ��
		result = Util.requestMogic(result, username);
		System.out.println("mcLogin");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		System.out.println("Login:" + result);
		out.write(result);

	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

	/**
	 * ���Ƚ��ܵõ���ȷ��ʱ�䣬Ȼ��ͨ����ȷ��ʱ����ܵõ���ȷ��ѧ�� �����㷨: ��ʱ�����base64���ܣ�Ȼ����Ϊ��Կ��ʱ����м���base64
	 * 
	 * @param falseXh
	 *            �ٵ�ѧ��
	 * @param time
	 *            ʱ���
	 * @return ������ʵѧ��
	 */
	private String calculateXh(String falseSession, String time) {
		// TODO Auto-generated method stub
		String session = "";
		String realTime = new Passport().jiemi(time,
				String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
		try {
			if (time.equals("iOS")) {
				session = new Passport().base64_decode(falseSession);// �ṩ��
																		// iOS�Ľӿ�
			} else {
				session = new Passport().jiemi(falseSession, realTime);
			}

			System.out.println("��ʵ����:" + session);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return session;
	}

}
