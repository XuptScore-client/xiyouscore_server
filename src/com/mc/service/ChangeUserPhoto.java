package com.mc.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

public class ChangeUserPhoto extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public ChangeUserPhoto() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String result = "error";
		String type = request.getParameter("type");// ��ȡ�ϴ�����
		System.out.println(type);

		String path = "";
		String filename = "";
		if (type.equals("JPEG")) {
			// ��ȡ�ļ��ϴ���Ҫ�����·����user_photo�ļ�������ڡ�
			path = request.getSession().getServletContext()
					.getRealPath("/user_photo");
			try {
				filename = savePhotoToDB(request.getParameter("username"));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (type.equals("txt")) {
			path = request.getSession().getServletContext()
					.getRealPath("/crash_infos");
		} else if (type.equals("devsdk")) {
			path = request.getSession().getServletContext()
					.getRealPath("/user_devsdk_infos");
		} else if (type.equals("loginmsg")) {
			path = request.getSession().getServletContext()
					.getRealPath("/loginMsg");
			System.out.println("loginmsg");
		}

		System.out.println("path:" + path);
		result = SaveFile(request, result, type, path, filename);
		PrintWriter out = response.getWriter();
		out.write(result);
	}

	private String SaveFile(HttpServletRequest request, String result,
			String type, String path, String filename)
			throws FileNotFoundException, IOException {
		if (!new File(path).exists()) {
			new File(path).mkdir();
		}
		// ��ô����ļ���Ŀ������
		DiskFileItemFactory factory = new DiskFileItemFactory();
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
					if (!type.equals("JPEG")) {
						String value = item.getName();
						// ȡ�����һ����б�ܡ�
						int start = value.lastIndexOf("\\");
						// ��ȡ�ϴ��ļ��� �ַ������֡�+1��ȥ����б�ܡ�
						filename = value.substring(start + 1);
					}
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
					result = filename;
				}
			}
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private String savePhotoToDB(String username) throws SQLException {
		String update_user_photo = "update users set photo = ? where username = ?";
		Connection conn = DBUtil.openConnection();
		String filename = username + System.currentTimeMillis() + ".JPEG";
		PreparedStatement pstmt = conn.prepareStatement(update_user_photo);
		pstmt.setString(1, "user_photo/" + filename);// ���û�ͷ��
		// ·�����뵽���ݿ�
		pstmt.setString(2, username);// ���û�
		pstmt.execute();
		return filename;

	}
}
