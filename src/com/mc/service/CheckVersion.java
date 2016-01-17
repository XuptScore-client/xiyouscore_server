package com.mc.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mc.db.DBUtil;

public class CheckVersion extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public CheckVersion() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String result = "";
		String version = request.getParameter("version");
		/*
		 * String username = request.getParameter("username"); //跟新数据库版本
		 * DBUtil.updateUserVersion(username, version);
		 */
		System.out.println("version");
		Connection connection = DBUtil.openConnection();//
		String query = "select * from apk_version order by id DESC limit 1";// 查询最后一条数据
		String update = "update apk_version set times=? where version=?";// 更新下载次数

		if (connection == null) {
			return;
		}
		try {
			PreparedStatement query_last = connection.prepareStatement(query); // 查询最后一条数据
			ResultSet rs_query = query_last.executeQuery();
			PreparedStatement update_last = connection.prepareStatement(update);// 更新下载次数

			while (rs_query.next()) {
				if (version.compareTo("1.0.11") == 0) {// 如果
														// 小于1.0.2,更新至1.0.2恢复签名
					result = "/manager/apk/queryscore1.0.3.apk|1.0.3|增加补考成绩\n\n声明：由于您的版本过低，我们会通过浏览器进行下载，请更新之先同意卸载，然后再安装|";
					// result = "no";//1.0.11的版本的用户先不更新最新的版本，过一段时间 再更新
				} else {
					if (version.compareTo("1.0.11") < 0) {
						result = "/manager/apk/queryscore1.0.11.apk|1.0.11|美化界面，更新部分显示问题|";
					} else {
						result = judgeVersion(version,
								rs_query.getString("version"), rs_query);
					}
				}
				if (!result.equals("no")) {// 更新下载次数
					String times = rs_query.getString("times");// 获取目前下载次数
					times = String.valueOf(Integer.valueOf(times) + 1);// 次数加1
					update_last.setString(1, times);// 将次数添加到 sql
					update_last.setString(2, rs_query.getString("version"));// 对应的版本
					update_last.execute();
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DBUtil.closeConn(connection);
		}
		DBUtil.closeConn(connection);

		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.print(result);
	}

	/**
	 * 判断是否有新版本, 返回 url 版本号 更新内容
	 */
	private static String judgeVersion(String oldVersion, String newVersion,
			ResultSet getVersion) {
		try {
			return oldVersion.compareTo(newVersion) < 0 ? getVersion
					.getString("url")
					+ "|"
					+ getVersion.getString("version")
					+ "|" + getVersion.getString("content") : "no";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
}
