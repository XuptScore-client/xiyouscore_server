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
 * �û�ʹ�� ���ѹ���ʱ�������ע�ᣬ ��������ȡ �û�ע����Ϣ֮�� �� users���е�is_regist����Ϊ1 ��ʾ���û��Ѿ�ע�� ע�⣺�ÿ���
 * api��ȫ�����ܱ�������ò�����
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
		 * String key = request.getParameter("session");//�������ǽ��û�ѧ�ź�ʱ����м���
		 * ͨ��base64 // System.out.println(sessionID); String username =
		 * request.getParameter("username"); String password =
		 * request.getParameter("password"); // String txtSecretCode =
		 * request.getParameter("txtSecretCode");//��֤��
		 * 
		 * Map<String, String> map = new HashMap<String, String>();
		 * map.put("__VIEWSTATE",
		 * "dDw1MjQ2ODMxNzY7Oz799QJ05KLrvCwm73IGbcfJPI91Aw==");
		 * map.put("TextBox1", username); map.put("TextBox2", password);
		 * map.put("RadioButtonList1", "%D1%A7%C9%FA");// ѧ�� map.put("Button1",
		 * "%B5%C7%C2%BC"); // ��¼���󣬷��� ���û����������������е�ַ�� String result =
		 * HttpUtil.http(HttpUtil.BASE_URL + HttpUtil.LOGIN_URL, map,
		 * sessionID);// post���� // System.out.println(result); // ������ʽ
		 * ��ȡscript�е����� result = HtmlUtil.htmlRemoveTag(result); //
		 * System.out.println("result:" + result); String newurl =
		 * result.split("\\;")[0].split("\\'")[1];
		 * 
		 * //System.out.println(newurl); if (newurl.equals("������󣡣�")) {// �������
		 * result = "error"; } else { if (newurl.equals("�û��������ڣ���")) { result =
		 * "no_user"; } else { // ��¼�ɹ� �������ݿ� try { if
		 * (!DBUtil.isHaveUser(username)) {// ��������ڣ���������û�
		 * DBUtil.insertUser(username, password); } else {
		 * DBUtil.updateUserPassword(username, password); }
		 * DBUtil.updateLoginTimes(username);// �����û���¼���� } catch (Exception e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); } //
		 * System.out.println("result:"+result+"\n newurl:"+newurl); newurl =
		 * HttpUtil.BASE_URL + newurl; result = HttpUtil.gethttp(newurl,
		 * sessionID);// �����ض����ҳ�� result = HtmlUtil.getHERF(result, 1);//
		 * ��ȡ���е�herf �� ���� ��ѯ�ɼ���herf // System.out.println("��ӡ:" + result); } }
		 * 
		 * // ���û��˺� ���뵽 ip��
		 * 
		 * response.setCharacterEncoding("utf-8"); PrintWriter out =
		 * response.getWriter(); out.write(result);
		 */
	}

}
