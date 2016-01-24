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

/**
 * 上传自己的坐标，并且 返回给用户 指定距离内的 其他用户
 * 
 * @author Administrator
 */
public class UnloadingLocation extends HttpServlet {
	public UnloadingLocation() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String result = "error";
		String username = request.getParameter("username");// 用户账号
		String location = request.getParameter("location");// 坐标
		String select_sql = "select count(*) from user_location where username = ?";// 查询数据库中时候有这个用户的坐标
		String insert_location = "insert into user_location (username,location) values(?,?)";// 插入新的用户数据
		String update_location = "update user_location set location = ? where username = ?";// 更新用户的坐标
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement select_user = conn.prepareStatement(select_sql);// 查询是否存在这个用户
			select_user.setString(1, username);
			ResultSet seleclt_user_set = select_user.executeQuery();
			while (seleclt_user_set.next()) {
				if (seleclt_user_set.getString(1).equals("0")) {// 没有这个用户
					// 则插入新用户
					PreparedStatement insert_user = conn
							.prepareStatement(insert_location);
					insert_user.setString(1, username);
					insert_user.setString(2, location);
					insert_user.execute();
					insert_user.close();
				} else {
					// 有这个用户,则更新用户的数据
					PreparedStatement update_user = conn
							.prepareStatement(update_location);
					update_user.setString(1, location);
					update_user.setString(2, username);
					update_user.execute();
					update_user.close();
				}
				result = "success";// 成功
			}
			seleclt_user_set.close();
			select_user.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DBUtil.closeConn(conn);
		;
		PrintWriter out = response.getWriter();
		out.print(result);

	}

}
