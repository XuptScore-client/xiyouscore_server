package com.mc.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 计算 文件的时间 如果超出 100天 就重新 请求 学校的服务器
 * 
 * @author Administrator
 * 
 */
public class CalculateFileTime {

	private static final long DIFFERENT_DAY = 10;// 差的天数
	private static final long DIFFERENT_TIME = 1;// 差的秒数

	/**
	 * 是否重新请求服务器
	 * 
	 * @param Ifile
	 * @return
	 */
	public static Boolean isRequest(File Ifile) {
		long modifiedTime = Ifile.lastModified();
		Date date = new Date(modifiedTime);
		SimpleDateFormat mode_sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
		String dd = mode_sdf.format(date);// 文件的修改时间

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentTime = new Date();
		// 将截取到的时间字符串转化为时间格式的字符串
		Date beginTime = null;
		try {
			beginTime = sdf.parse(dd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 默认为毫秒，除以1000是为了转换成秒
		long interval = (currentTime.getTime() - beginTime.getTime()) / 1000;// 秒
		long day = interval / (24 * 3600);// 天
		long hour = interval % (24 * 3600) / 3600;// 小时
		long minute = interval % 3600 / 60;// 分钟
		long second = interval % 60;// 秒
		System.out.println("两个时间相差：" + day + "天" + hour + "小时" + minute + "分"
				+ second + "秒");
		return day > DIFFERENT_DAY ? true : false;
	}

	/**
	 * 比较两个时间的差值
	 * 
	 * @param last_time
	 *            老的时间
	 * @param new_time
	 *            最新时间
	 * @return
	 */
	public static Boolean isRequest(File Ifile, long DIFFERENT_TIME) {
		long modifiedTime = Ifile.lastModified();
		Date date = new Date(modifiedTime);
		SimpleDateFormat mode_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dd = mode_sdf.format(date);// 文件的修改时间
		/* Date currentTime=new Date(); */
		// 将截取到的时间字符串转化为时间格式的字符串
		Date beginTime = null;
		try {
			beginTime = mode_sdf.parse(dd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* Date currentTime=new Date(); */
		// 将截取到的时间字符串转化为时间格式的字符串
		// 默认为毫秒，除以1000是为了转换成秒
		Date currentTime = new Date();
		long interval = (currentTime.getTime() - beginTime.getTime()) / 1000;// 秒
		long day = interval / (24 * 3600);// 天
		long hour = interval % (24 * 3600) / 3600;// 小时
		long minute = interval % 3600 / 60;// 分钟
		long second = interval % 60;// 秒
		System.out.println("两个时间相差：" + hour + "小时  +  " + DIFFERENT_TIME);
		return hour > DIFFERENT_TIME ? true : false;
	}
}
