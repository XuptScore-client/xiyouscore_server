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
		String type = request.getParameter("type");// 获取上传类型
		System.out.println(type);

		String path = "";
		String filename = "";
		if (type.equals("JPEG")) {
			// 获取文件上传需要保存的路径，user_photo文件夹需存在。
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
		// 获得磁盘文件条目工厂。
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 设置暂时存放文件的存储室，这个存储室可以和最终存储文件的文件夹不同。因为当文件很大的话会占用过多内存所以设置存储室。
		factory.setRepository(new File(path));
		// 设置缓存的大小，当上传文件的容量超过缓存时，就放到暂时存储室。
		factory.setSizeThreshold(1024 * 1024);
		// 上传处理工具类（高水平API上传处理？）
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List<FileItem> list;
			list = (List<FileItem>) upload.parseRequest(request);
			for (FileItem item : list) {
				// 获取表单属性名字。
				String name = item.getFieldName();
				// 如果获取的表单信息是普通的文本信息。即通过页面表单形式传递来的字符串。
				if (item.isFormField()) {
					// 获取用户具体输入的字符串，
					String value = item.getString();
					request.setAttribute(name, value);
				}
				// 如果传入的是非简单字符串，而是图片，音频，视频等二进制文件。
				else {
					// 获取路径名
					if (!type.equals("JPEG")) {
						String value = item.getName();
						// 取到最后一个反斜杠。
						int start = value.lastIndexOf("\\");
						// 截取上传文件的 字符串名字。+1是去掉反斜杠。
						filename = value.substring(start + 1);
					}
					request.setAttribute(name, filename);

					/*
					 * 第三方提供的方法直接写到文件中。 item.write(new File(path,filename));
					 */
					// 收到写到接收的文件中。
					OutputStream out = new FileOutputStream(new File(path,
							filename));
					InputStream in = item.getInputStream();

					int length = 0;
					byte[] buf = new byte[1024];
					System.out.println("获取文件总量的容量:" + item.getSize());

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
		pstmt.setString(1, "user_photo/" + filename);// 将用户头像
		// 路径插入到数据库
		pstmt.setString(2, username);// 该用户
		pstmt.execute();
		return filename;

	}
}
