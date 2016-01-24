package com.mc.service;

import java.io.IOException;
import java.io.PrintWriter;
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
 * 用户使用 好友功能时必须进行注册， 服务器获取 用户注册信息之后 将 users表中的is_regist更改为1 表示该用户已经注册 注意：得考虑
 * api安全，不能被随意调用并更改
 * 
 * @author Administrator
 * 
 */
public class RegistMassageServlet extends HttpServlet {

	public RegistMassageServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * String key = request.getParameter("session");//该数据是将用户学号和时间进行加密
		 * 通过base64 // System.out.println(sessionID); String username =
		 * request.getParameter("username"); String password =
		 * request.getParameter("password"); // String txtSecretCode =
		 * request.getParameter("txtSecretCode");//验证码
		 * 
		 * Map<String, String> map = new HashMap<String, String>();
		 * map.put("__VIEWSTATE",
		 * "dDw1MjQ2ODMxNzY7Oz799QJ05KLrvCwm73IGbcfJPI91Aw==");
		 * map.put("TextBox1", username); map.put("TextBox2", password);
		 * map.put("RadioButtonList1", "%D1%A7%C9%FA");// 学生 map.put("Button1",
		 * "%B5%C7%C2%BC"); // 登录请求，返回 该用户请求其他链接所有地址。 String result =
		 * HttpUtil.http(HttpUtil.BASE_URL + HttpUtil.LOGIN_URL, map,
		 * sessionID);// post请求 // System.out.println(result); // 正则表达式
		 * 获取script中的数据 result = HtmlUtil.htmlRemoveTag(result); //
		 * System.out.println("result:" + result); String newurl =
		 * result.split("\\;")[0].split("\\'")[1];
		 * 
		 * //System.out.println(newurl); if (newurl.equals("密码错误！！")) {// 密码错误
		 * result = "error"; } else { if (newurl.equals("用户名不存在！！")) { result =
		 * "no_user"; } else { // 登录成功 插入数据库 try { if
		 * (!DBUtil.isHaveUser(username)) {// 如果不存在，则插入新用户
		 * DBUtil.insertUser(username, password); } else {
		 * DBUtil.updateUserPassword(username, password); }
		 * DBUtil.updateLoginTimes(username);// 更新用户登录次数 } catch (Exception e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); } //
		 * System.out.println("result:"+result+"\n newurl:"+newurl); newurl =
		 * HttpUtil.BASE_URL + newurl; result = HttpUtil.gethttp(newurl,
		 * sessionID);// 返回重定向的页面 result = HtmlUtil.getHERF(result, 1);//
		 * 获取所有的herf ， 比如 查询成绩的herf // System.out.println("打印:" + result); } }
		 * 
		 * // 将用户账号 插入到 ip中
		 * 
		 * response.setCharacterEncoding("utf-8"); PrintWriter out =
		 * response.getWriter(); out.write(result);
		 */
	}

}
