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

import com.mc.util.HtmlUtil;
import com.mc.util.HttpUtil;
/**
 * 修改密码
 * @author Administrator
 *
 */
public class ChangePasswd extends HttpServlet {

	
	public ChangePasswd() {
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

		
		
		String session = request.getParameter("session");//获取session
		String url = request.getParameter("url")
				 + "&gnmkdm="
				+ request.getParameter("gnmkdm");
		System.out.println(url);
		/**
		 * 先进行get请求，获取html中 __VIEWSTATE参数的值
		 */
		String get_result = HttpUtil.gethttp(HttpUtil.IP + url, session);// 查询为通过的学科
		// get请求
		String viewstate = HtmlUtil.getInput(get_result, "__VIEWSTATE");
		viewstate = URLEncoder.encode(viewstate);// 必须使用编码之后的字符串
		
		String old_password = request.getParameter("old_password").trim();//原密码
		String new_password = request.getParameter("new_password").trim();//新密码
		Map<String, String> map = new HashMap<String, String>();
		System.out.println(viewstate);
		map.put("__VIEWSTATE", viewstate);
		map.put("TextBox2", old_password);
		map.put("TextBox3", new_password);
		map.put("TextBox4", new_password);
		map.put("Button1", "%D0%DE++%B8%C4");
		String post_result = HttpUtil.http(HttpUtil.IP + url, map, session);//修改密码
	//	System.out.println("script:"+HtmlUtil.htmlRemoveTag(post_result));
		String script = HtmlUtil.htmlRemoveTag(post_result);
		
//		if (!script.trim().equals("ERROR - 出错啦！")) {
		PrintWriter out = response.getWriter();
			if (script.split("'")[1].equals("修改成功！")) {
				
				out.print("success change");
			}else {
				out.print("password error");//原密码错误
			}
//		}
	}

}
