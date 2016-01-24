package com.mc.db;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.mc.util.Passport;
import com.mc.util.StaticVARUtil;

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
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		DBUtil dbUtil = new DBUtil();
		updateUserPassword("04113129", "chaoking1993");
		/*
		 * Connection conn = dbUtil.openConnection();
		 * 
		 * Statement state = conn.createStatement(); ResultSet rs =
		 * state.executeQuery
		 * ("select version,times from apk_version order by id DESC limit 1");
		 * try { while (rs.next()) { // BLOB blob = (BLOB)
		 * rs.getBlob("attachment"); System.out.println("版本:" +
		 * rs.getString(1)); } } catch (Exception e) { e.printStackTrace(); }
		 * dbUtil.closeConn(conn);
		 */
	}

	/**
	 * 查询用户是否存在
	 */
	public static boolean isHaveUser(String username) {
		String sql = "select count(*) from users where username = ?";
		Connection conn = DBUtil.openConnection();
		boolean result = true;
		if (conn == null) {
			return false;
		}
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) == 0) {
					result = false;
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
	public static void insertUser(String username, String password) {
		String sql = "insert into users(username,password,last_time) values (?,?,?)";
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, Passport.jiami(password));
			ps.setString(3, StaticVARUtil.getTime());
			ps.execute();
			ps.close();
			// 更新ip表，将用户名插入

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConn(conn);
	}

	/**
	 * mc插入 下拉图片
	 * 
	 * @param isPoll
	 */
	public static void insertPollDownView(String time, String isPoll,
			String scaletype) {
		String sql = "insert into polldownimage(time,isPoll,scaletype) values (?,?,?)";
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, time);
			ps.setString(2, isPoll);
			ps.setString(3, scaletype);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConn(conn);
	}

	/**
	 * 获取最新 图片时间和是否下载
	 * 
	 * @param isPoll
	 * @return
	 */
	public static String getPollDownViewTimeIspoll(String filePath) {
		String sql = "select time,isPoll,scaletype from polldownimage  order by polldownimage_id DESC limit 1";
		Connection conn = DBUtil.openConnection();
		String result = "no";
		if (conn == null) {
			return "0|0|0";
		}
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet ts = ps.executeQuery();
			if (ts.next()) {
				result = new File(filePath + "/" + ts.getString(1) + ".jpg")
						.exists() ? ts.getString(1) + "|" + ts.getString(2)
						+ "|" + ts.getString(3) : "0|0";

			}
			ts.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DBUtil.closeConn(conn);
		return result;
	}

	/**
	 * 获取公告
	 */
	public static String return_notice() {
		String query = "select notice from notice order by notice_id DESC limit 1";// 查询最后一条数据
		Connection conn = DBUtil.openConnection();
		String notice = "";
		try {
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				notice = rs.getString(1);
			}
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DBUtil.closeConn(conn);
		return notice;
	}

	/**
	 * 获取用户登录的次数
	 */
	public static String updateLoginTimes(String username) {
		String sql = "select times from users where username = ?";
		Connection conn = DBUtil.openConnection();
		String times = "0";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				times = rs.getString("times");
				// 转化为int
				String insert = "update users set times = ? where username = ?";
				String new_times = String.valueOf(Integer.valueOf(times) + 1);
				PreparedStatement update_ps = conn.prepareStatement(insert);
				update_ps.setString(1, new_times);
				update_ps.setString(2, username);
				update_ps.execute();
				update_ps.close();
			}
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DBUtil.closeConn(conn);
		return times;
	}

	/**
	 * 更新用户
	 */
	public static void updateUserPassword(String username, String password) {
		String sql = "update xuptscore.users set password = ?,last_time=? where username = ?";
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			password = Passport.jiami(password);// 加密
			ps.setString(1, password);
			ps.setString(2, StaticVARUtil.getTime());
			ps.setString(3, username);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConn(conn);
	}

	/**
	 * 更新用户rank 记录
	 */
	public static void updateUserRank(String username) {
		String sql = "update xuptscore.users set isRank = '1' where username = ?";
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConn(conn);
	}

	/**
	 * 插入 用户所对应的设备序列号
	 */
	public static boolean isUserRank(String username) {
		String sql = "select isRank from xuptscore.users where username = ?";
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString(1).equals("1")) {// 已经排名
					ps.close();
					DBUtil.closeConn(conn);
					return true;
				}
			}
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConn(conn);
		return false;
	}

	/**
	 * 插入 用户所对应的设备序列号
	 */
	public static void updateUserDevIMEI(String username, String imei) {
		String sql = "update xuptscore.users set imei = ? where username = ?";
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, imei);
			ps.setString(2, username);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DBUtil.closeConn(conn);
	}

	/**
	 * 更新用户 使用版本 记录
	 */
	public static void updateUserVersion(String username, String version) {
		String sql = "update xuptscore.users set version = ? where username = ?";
		Connection conn = DBUtil.openConnection();
		if (conn == null) {
			return;
		}
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, version);
			ps.setString(2, username);
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
	public static void insertUserName(String username, String name) {
		/**
		 * 如果是第一次登陆则更新， 修改Bug
		 */
		if (isHaveName(username, name)) {
			String sql = "update users set name = ? where username = ?";
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

	/**
	 * 插入用户版本
	 */
	public static void insertUserVersion(String username, String name,
			String version) {
		if (isHaveName(username, name)) {
			String sql = "update users set version = ? where username = ?";
			Connection conn = DBUtil.openConnection();
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, version);
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

	/**
	 * 判断用户名是否为空 也就是 为空是第一次登陆
	 * 
	 * @param username
	 * @return
	 */
	public static boolean isHaveName(String username, String name) {
		String sql = "select name from users where username = ?";
		String nameCountSql = "select count(*) from users where name = ?";
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString(1) == null
						|| (rs.getString(1) != null && rs.getString(1).equals(
								""))) {// 第一次登陆
					PreparedStatement nameCountPs = conn
							.prepareStatement(nameCountSql);
					nameCountPs.setString(1, name);// 如果这个用户名已经是垃圾，存在于其他学号中。
					ResultSet nameCountSqlRs = nameCountPs.executeQuery();
					while (nameCountSqlRs.next()) {
						if (name != null || name.equals("")
								|| nameCountSqlRs.getInt(1) == 0) {
							return true;
						}
					}
				} else {
					return false;
				}
			}
			ps.close();
			DBUtil.closeConn(conn);
			return false;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 判断是不是第一次登陆
	 * 
	 * @param username
	 *            学号
	 * @return
	 */
	public static boolean isFirstLogin(String username) {
		String sql = "select times from users  where username = ?";
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString(1).equals("1")) {// 第一次登陆
					return true;
				} else {
					return false;
				}
			}
			ps.close();
			DBUtil.closeConn(conn);
			return false;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 修复BUG 查询用户名和学号是否对应。
	 */
	public static boolean checkXhAndName(String name, String username) {
		String sql = "select count(*) from users where name = ? and username like ?";
		Connection conn = DBUtil.openConnection();
		if (conn == null) {
			return true;
		}
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {

				if (rs.getInt(1) != 0) {// 不存在这个用户，被攻击
					ps.close();
					rs.close();
					DBUtil.closeConn(conn);
					return true;
				}
			}
			ps.close();
			rs.close();
			DBUtil.closeConn(conn);
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 在本地数据库验证 用户名和密码
	 */
	/**
	 * 判断用户名是否为空 也就是 为空是第一次登陆
	 * 
	 * @param username
	 * @return
	 */
	public static boolean judge_usernameAndPassW(String username,
			String password) {
		String sql = "select password from users where username = ? ";
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (new Passport().jiemi(rs.getString(1)).equals(password)) {// 用户密码验证成功
					ps.close();
					DBUtil.closeConn(conn);
					return true;
				} else {
					ps.close();
					DBUtil.closeConn(conn);
					return false;
				}
			}
			ps.close();
			DBUtil.closeConn(conn);
			return false;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 通过学号 得到 姓名
	 * 
	 * @param username
	 * @return
	 */
	public static String getName(String username) {
		String name = "待定";
		String sql = "select name from users where username = ? ";
		Connection conn = DBUtil.openConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				name = rs.getString(1);
			}
			ps.close();
			DBUtil.closeConn(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}
}
