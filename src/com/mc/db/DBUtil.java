package com.mc.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.mc.util.Passport;

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
		try {
			InputStream in = DBUtil.class
					.getResourceAsStream("/configDB.properties");
			p.load(in);
			String driver = p.getProperty("oracleDriver");
			String url = p.getProperty("oracleURL");
			String username = p.getProperty("oracleUser");
			String password = p.getProperty("oraclePassword");
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
			System.out.println("关闭数据库连接");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		//DBUtil dbUtil = new DBUtil();
//		updateUserPassword("04113129","chaoking1993");
		/*Connection conn = dbUtil.openConnection();
		
		Statement state = conn.createStatement();
		ResultSet rs = state.executeQuery("select version,times from apk_version order by id DESC limit 1");
		try {
			while (rs.next()) {
				// BLOB blob = (BLOB) rs.getBlob("attachment");
				System.out.println("版本:" + rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dbUtil.closeConn(conn);*/
	}
	
	/**
	 * 查询用户是否存在
	 */
	public static boolean isHaveUser(String username){
		String sql  = "select count(*) from users where username = ?";
		Connection conn = DBUtil.openConnection();
		boolean result = true;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					result =  false;
				}
			}
			ps.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DBUtil.closeConn(conn);
		}
		DBUtil.closeConn(conn);
		return result;
	}
	/**
	 * 插入用户
	 */
	public static void insertUser(String username,String password){
		String sql  = "insert into users(username,password) values (?,?)";
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, Passport.jiami(password));
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DBUtil.closeConn(conn);
	}
	
	/**
	 * 更新用户
	 */
	public static void updateUserPassword(String username,String password){
		String sql  = "update xuptscore.users set password = ? where username = ?";
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			System.out.println(Passport.jiami(password));
			 password = Passport.jiami(password);//加密
			ps.setString(1, password);
			ps.setString(2, username);
			System.out.println("更新成功");
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DBUtil.closeConn(conn);
	}
	/**
	 * 插入用户名字
	 */
	public static void insertUserName(String username,String name){
		String sql  = "update users set name = ? where username = ?";
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, username); 
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DBUtil.closeConn(conn);
	}
}
