package com.mc.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mc.util.ChaXunChengJiUtil;

public class ScoreStatisticalServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScoreStatisticalServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String session = request.getParameter("session");
		String url = request.getParameter("url");

		String httpResult = new ChaXunChengJiUtil().scoreHttpResult(session,
				url, true);
		System.out.println(httpResult);
	}

}
