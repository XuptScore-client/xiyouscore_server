package com.mc.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mc.db.DBUtil;
import com.mc.util.CalculateFileTime;

public class NoticeServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public NoticeServlet() {
		super();
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
		// System.out.println(sessionID);
		String username = request.getParameter("username");
		// String txtSecretCode = request.getParameter("txtSecretCode");//��֤��
		String root_path = request.getSession().getServletContext()
				.getRealPath(request.getRequestURI());
		root_path = root_path.substring(0, root_path.lastIndexOf("xupt"))
				+ "student_score/";
		File file = new File(root_path);
		if (!file.exists()) {
			file.mkdirs();// �������� ѧ�� xml���ļ���
		}
		String filename = root_path + username + ".xml";// �ļ���
		String result = "";
		if (new File(filename).exists()/*
										 * &&!CalculateFileTime.isRequest(new
										 * File(filename))
										 */) {// ���û� ���� ��ѯ��300Сʱ����
			result = "1";
		} else {
			result = DBUtil.return_notice();
		}
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.write(result);

	}

}
