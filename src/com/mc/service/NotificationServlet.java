package com.mc.service;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NotificationServlet extends HttpServlet {

	private final int MARKTYPE = 1;// 请求是否有新闻
	private final int DETAILTYPE = 2;// 获取详细内容

	/**
	 * Constructor of the object.
	 */
	public NotificationServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String type = request.getParameter("type");
		if (type.equals(MARKTYPE + "")) {

		}

	}

}
