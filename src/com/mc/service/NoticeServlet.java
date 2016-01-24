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
		// String txtSecretCode = request.getParameter("txtSecretCode");//验证码
		String root_path = request.getSession().getServletContext()
				.getRealPath(request.getRequestURI());
		root_path = root_path.substring(0, root_path.lastIndexOf("xupt"))
				+ "student_score/";
		File file = new File(root_path);
		if (!file.exists()) {
			file.mkdirs();// 创建保存 学生 xml的文件夹
		}
		String filename = root_path + username + ".xml";// 文件名
		String result = "";
		if (new File(filename).exists()/*
										 * &&!CalculateFileTime.isRequest(new
										 * File(filename))
										 */) {// 老用户 并且 查询在300小时以上
			result = "1";
		} else {
			result = DBUtil.return_notice();
		}
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.write(result);

	}

}
