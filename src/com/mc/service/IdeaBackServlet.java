package com.mc.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

public class IdeaBackServlet extends HttpServlet {

	public IdeaBackServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String msg = URLDecoder.decode(request.getParameter("data"));
		response.getWriter().print(sendMail(msg) ? "success" : "error");
	}

	public boolean sendMail(final String msg) {
		System.out.println(msg);
		MultiPartEmail email = new MultiPartEmail();
		/*
		 * EmailAttachment attachment = new EmailAttachment();
		 * 
		 * String pathAll = email_attach .getText().toString();
		 * attachment.setPath(pathAll);
		 * 
		 * attachment .setDisposition(EmailAttachment.ATTACHMENT);
		 * attachment.setDescription("不错！");
		 * 
		 * //解决附件名中文乱码 String fujian = pathAll.substring(pathAll
		 * .lastIndexOf("/") + 1); System.out.println("附件名:"+fujian ); try {
		 * attachment.setName(MimeUtility.encodeText(fujian)); } catch
		 * (UnsupportedEncodingException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		try {

			// 设置发送主机的服务器地址
			email.setHostName("smtp.126.com");
			// 设置收件人邮箱
			email.addTo("ideaback_mc@126.com");
			// 发件人邮箱
			email.setFrom("uu9923@126.com");
			// 如果要求身份验证，设置用户名、密码，分别为发件人在邮件服务器上注册的用户名和密码
			email.setAuthentication("uu9923@126.com", "uz31415926");
			// 设置邮件的主题
			email.setSubject("xupcScore ideaback");

			// 邮件正文消息
			email.setMsg(msg);

			// email.attach(attachment);

			email.send();
		} catch (EmailException e1) {
			// TODO Auto-generated catch
			// block
			e1.printStackTrace();
			return false;
		}
		return true;

	}
}
