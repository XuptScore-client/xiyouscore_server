package com.mc.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mc.db.DBUtil;
import com.mc.util.HtmlUtil;
import com.mc.util.HttpUtil;

public class LoginServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public LoginServlet() {
		super();
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
this.doPost(request, response);
		
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		
		String sessionID= request.getParameter("session");
		//System.out.println(sessionID);
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		//	String txtSecretCode = request.getParameter("txtSecretCode");//��֤��
		
		Map<String, String> map = new HashMap<String, String>();
		/*map.put("txtUserName", username);
		map.put("TextBox2", password);
		map.put("txtSecretCode", txtSecretCode);
		map.put("__VIEWSTATE", "dDwyODE2NTM0OTg7Oz4I0N5Oc1v%2BYbV62wJKyVYkP7ZEOg==");
		map.put("RadioButtonList1", "%D1%A7%C9%FA");//ѧ��
		map.put("Button1", "");
		map.put("lbLanguage", "");
		map.put("hidPdrs", "");
		map.put("hidsc", "");*/
		map.put("__VIEWSTATE", "dDw1MjQ2ODMxNzY7Oz799QJ05KLrvCwm73IGbcfJPI91Aw==");
		map.put("TextBox1", username);
		map.put("TextBox2", password);
		map.put("RadioButtonList1", "%D1%A7%C9%FA");//ѧ��
		map.put("Button1", "%B5%C7%C2%BC");
		String result =  HttpUtil.http(HttpUtil.LOGIN_URL, map,sessionID);//post����
		//������ʽ ��ȡscript�е�����
		result = HtmlUtil.htmlRemoveTag(result);
//		System.out.println(result);
		String newurl = result.split("\\;")[0].split("\\'")[1];
//		System.out.println(newurl);
		if (newurl.equals("������󣡣�")) {//�������
			result = "error";
		}else {
			//��¼�ɹ� �������ݿ�
			if (!DBUtil.isHaveUser(username)) {//��������ڣ���������û�
				DBUtil.insertUser(username, password);
			}
			//System.out.println("result:"+result+"\n newurl:"+newurl);
			newurl = HttpUtil.IP+newurl;
			result = HttpUtil.gethttp(newurl, sessionID);// �����ض����ҳ��
			result = HtmlUtil.getHERF(result,1);// ��ȡ���е�herf �� ���� ��ѯ�ɼ���herf
//			System.out.println("��ӡ:" + result);
		}
		
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.write(result);
	}
	
	
	 /** 
     * ����������req©���İ���ַ�ֱ���滻��ȫ���ַ� 
     *  
     * @param s 
     * @return 
     */  
    private static String reqEncode(String s) {  
        if (s == null || "".equals(s)) {  
            return s;  
        }  
        StringBuilder sb = new StringBuilder(s.length() + 16);  
        for (int i = 0; i < s.length(); i++) {  
            char c = s.charAt(i);  
            switch (c) {  
            case '\'':  
                sb.append("&prime;");// &acute;");  
                break;  
            case '��':  
                sb.append("&prime;");// &acute;");  
                break;  
            case '\"':  
                sb.append("&quot;");  
                break;  
            case '��':  
                sb.append("&quot;");  
                break;  
            case '&':  
                sb.append("��");  
                break;  
            case '#':  
                sb.append("��");  
                break;  
            case '\\':  
                sb.append('��');  
                break;  
  
            default:  
                sb.append(c);  
                break;  
            }  
        }  
        return sb.toString();  
    }  
}
