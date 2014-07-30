package com.mc.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Oracel database connection
 * 
 * @author mc
 * 
 */
public class DBUtil {

	/**
	 * Get the Connection object
	 * 
	 * @return
	 */
	public static synchronized Connection openConnection() {
		Properties p = new Properties();
		Connection c = null;
		ResultSet rs = null;
		try {
			InputStream in = DBUtil.class
					.getResourceAsStream("/configDB.properties");
			p.load(in);
			String driver = p.getProperty("oracleDriver");
			String url = p.getProperty("oracleURL");
			String username = p.getProperty("oracleUser");
			String password = p.getProperty("oraclePassword");
			System.out
					.println("fsfasfdsf" + driver + url + username + password);
			Class.forName(driver).newInstance();
			c = DriverManager.getConnection(url, username, password);
			System.out.println("successful connection");
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * close Connection
	 * 
	 * @param conn
	 */
	public static void closeConn(Connection conn) {
		try {
			if(conn!=null)
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		DBUtil dbUtil = new DBUtil();
		Connection conn = dbUtil.openConnection();
		Statement state = conn.createStatement();
		ResultSet rs = state.executeQuery("select version from apk_version");
		try {
			while (rs.next()) {
				// BLOB blob = (BLOB) rs.getBlob("attachment");
				System.out.println("°æ±¾:" + rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dbUtil.closeConn(conn);
	}
}
