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
import com.mc.util.HttpUtil;

public class GetUserPhoto extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public GetUserPhoto() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String url = "no_photo";
		String username = request.getParameter("username");
		String select_user_photo = "select photo from users where username = ?";
		Connection conn = DBUtil.openConnection();
		if (conn == null) {
			return;
		}
		try {
			PreparedStatement ps = conn.prepareStatement(select_user_photo);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				url = rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DBUtil.closeConn(conn);
		PrintWriter out = response.getWriter();
		out.print(url);
	}

}
