package com.mc.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mc.db.DBUtil;
import com.mc.util.HtmlUtil;
import com.mc.util.HttpUtil;

/**
 * �޸�����
 * 
 * @author Administrator
 * 
 */
public class ChangePasswd1 extends HttpServlet {

	public ChangePasswd1() {
		super();
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String session = request.getParameter("session");// ��ȡsession

		String url = "mmxg.aspx?xh=" + request.getParameter("username")
				+ "&gnmkdm=" + "N121502";
		System.out.println(url);
		/**
		 * �Ƚ���get���󣬻�ȡhtml�� __VIEWSTATE������ֵ
		 */
		String get_result = HttpUtil.gethttp(HttpUtil.BASE_URL + url, session);// ��ѯΪͨ����ѧ��
		// get����
		String viewstate = HtmlUtil.getInput(get_result, "__VIEWSTATE");
		viewstate = URLEncoder.encode(viewstate);// ����ʹ�ñ���֮����ַ���

		String old_password = request.getParameter("old_password").trim();// ԭ����
		String new_password = request.getParameter("new_password").trim();// ������
		Map<String, String> map = new HashMap<String, String>();
		System.out.println(viewstate);
		map.put("__VIEWSTATE", viewstate);
		map.put("TextBox2", old_password);
		map.put("TextBox3", new_password);
		map.put("TextBox4", new_password);
		map.put("Button1", "%D0%DE++%B8%C4");
		String post_result = HttpUtil.http(HttpUtil.BASE_URL + url, map,
				session);// �޸�����
		// System.out.println("script:"+HtmlUtil.htmlRemoveTag(post_result));
		String script = HtmlUtil.htmlRemoveTag(post_result);
		System.out.println(script);
		// if (!script.trim().equals("ERROR - ��������")) {
		PrintWriter out = response.getWriter();
		if (script.split("'")[1].equals("�޸ĳɹ���")) {
			// �������ݿ�
			DBUtil.updateUserPassword(url.split("=")[1], new_password);
			out.print("success change");
		} else {
			out.print("password error");// ԭ�������
		}
		// }
	}

}
