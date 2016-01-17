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
		 * String username = request.getParameter("username"); //�������ݿ�汾
		 * DBUtil.updateUserVersion(username, version);
		 */
		System.out.println("version");
		Connection connection = DBUtil.openConnection();//
		String query = "select * from apk_version order by id DESC limit 1";// ��ѯ���һ������
		String update = "update apk_version set times=? where version=?";// �������ش���

		if (connection == null) {
			return;
		}
		try {
			PreparedStatement query_last = connection.prepareStatement(query); // ��ѯ���һ������
			ResultSet rs_query = query_last.executeQuery();
			PreparedStatement update_last = connection.prepareStatement(update);// �������ش���

			while (rs_query.next()) {
				if (version.compareTo("1.0.11") == 0) {// ���
														// С��1.0.2,������1.0.2�ָ�ǩ��
					result = "/manager/apk/queryscore1.0.3.apk|1.0.3|���Ӳ����ɼ�\n\n�������������İ汾���ͣ����ǻ�ͨ��������������أ������֮��ͬ��ж�أ�Ȼ���ٰ�װ|";
					// result = "no";//1.0.11�İ汾���û��Ȳ��������µİ汾����һ��ʱ�� �ٸ���
				} else {
					if (version.compareTo("1.0.11") < 0) {
						result = "/manager/apk/queryscore1.0.11.apk|1.0.11|�������棬���²�����ʾ����|";
					} else {
						result = judgeVersion(version,
								rs_query.getString("version"), rs_query);
					}
				}
				if (!result.equals("no")) {// �������ش���
					String times = rs_query.getString("times");// ��ȡĿǰ���ش���
					times = String.valueOf(Integer.valueOf(times) + 1);// ������1
					update_last.setString(1, times);// ��������ӵ� sql
					update_last.setString(2, rs_query.getString("version"));// ��Ӧ�İ汾
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
	 * �ж��Ƿ����°汾, ���� url �汾�� ��������
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
