package com.mc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用到的变量
 * 
 * @author Administrator
 * 
 */
public class StaticVARUtil {

	public static final String[] xn = { "2012-2013", "2013-2014", "2014-2015",
			"2015-2016" };

	/**
	 * 获取系统时间
	 */
	public static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH/mm/ss");
		System.out.println();
		return sdf.format(new Date());
	}

	public static String getTime1() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		System.out.println();
		return sdf.format(new Date());
	}

	public static void main(String[] args) {
		System.out.println(getTime());
	}
}
