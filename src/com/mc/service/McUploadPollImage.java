package com.mc.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.mc.db.DBUtil;
import com.mc.util.StaticVARUtil;

public class McUploadPollImage extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public McUploadPollImage() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// ��ô����ļ���Ŀ������
		DiskFileItemFactory factory = new DiskFileItemFactory();
		String isPoll = request.getParameter("ispoll");
		String scaleType = request.getParameter("scaletype");
		String time = StaticVARUtil.getTime1();
		DBUtil.insertPollDownView(time, isPoll, scaleType);
		String path = request.getSession().getServletContext()
				.getRealPath("/image");
		System.out.println("path:" + path);
		if (!new File(path).exists()) {
			new File(path).mkdir();
		}
		// ������ʱ����ļ��Ĵ洢�ң�����洢�ҿ��Ժ����մ洢�ļ����ļ��в�ͬ����Ϊ���ļ��ܴ�Ļ���ռ�ù����ڴ��������ô洢�ҡ�
		factory.setRepository(new File(path));
		// ���û���Ĵ�С�����ϴ��ļ���������������ʱ���ͷŵ���ʱ�洢�ҡ�
		factory.setSizeThreshold(1024 * 1024);
		// �ϴ��������ࣨ��ˮƽAPI�ϴ�������
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List<FileItem> list;
			list = (List<FileItem>) upload.parseRequest(request);
			for (FileItem item : list) {
				// ��ȡ���������֡�
				String name = item.getFieldName();
				// �����ȡ�ı���Ϣ����ͨ���ı���Ϣ����ͨ��ҳ�����ʽ���������ַ�����
				if (item.isFormField()) {
					// ��ȡ�û�����������ַ�����
					String value = item.getString();
					request.setAttribute(name, value);
				}
				// ���������ǷǼ��ַ���������ͼƬ����Ƶ����Ƶ�ȶ������ļ���
				else {
					// ��ȡ·����
					String value = item.getName();
					// ȡ�����һ����б�ܡ�
					int start = value.lastIndexOf("\\");
					// ��ȡ�ϴ��ļ��� �ַ������֡�+1��ȥ����б�ܡ�
					String filename = time + ".jpg";
					request.setAttribute(name, filename);

					/*
					 * �������ṩ�ķ���ֱ��д���ļ��С� item.write(new File(path,filename));
					 */
					// �յ�д�����յ��ļ��С�
					OutputStream out = new FileOutputStream(new File(path,
							filename));
					InputStream in = item.getInputStream();

					int length = 0;
					byte[] buf = new byte[1024];
					System.out.println("��ȡ�ļ�����������:" + item.getSize());

					while ((length = in.read(buf)) != -1) {
						out.write(buf, 0, length);
					}
					in.close();
					out.close();
				}
			}
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
