package com.mc.service;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NotificationServlet extends HttpServlet {

	private final int MARKTYPE = 1;// �����Ƿ�������
	private final int DETAILTYPE = 2;// ��ȡ��ϸ����

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
