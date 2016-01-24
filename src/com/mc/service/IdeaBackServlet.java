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
		 * attachment.setDescription("����");
		 * 
		 * //����������������� String fujian = pathAll.substring(pathAll
		 * .lastIndexOf("/") + 1); System.out.println("������:"+fujian ); try {
		 * attachment.setName(MimeUtility.encodeText(fujian)); } catch
		 * (UnsupportedEncodingException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		try {

			// ���÷��������ķ�������ַ
			email.setHostName("smtp.126.com");
			// �����ռ�������
			email.addTo("ideaback_mc@126.com");
			// ����������
			email.setFrom("uu9923@126.com");
			// ���Ҫ�������֤�������û��������룬�ֱ�Ϊ���������ʼ���������ע����û���������
			email.setAuthentication("uu9923@126.com", "uz31415926");
			// �����ʼ�������
			email.setSubject("xupcScore ideaback");

			// �ʼ�������Ϣ
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
