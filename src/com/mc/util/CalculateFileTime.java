package com.mc.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 计算 文件的时间  如果超出  100天  就重新 请求 学校的服务器
 * @author Administrator
 *
 */
public class CalculateFileTime {

	private static final int DIFFERENT_DAY = 100;//差的天数
	/**
	 * 是否重新请求服务器
	 * @param Ifile
	 * @return
	 */
	public static Boolean isRequest(File Ifile){
		long modifiedTime = Ifile.lastModified();
        Date date=new Date(modifiedTime);
        SimpleDateFormat mode_sdf=new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        String dd=mode_sdf.format(date);//文件的修改时间
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  Date currentTime=new Date();
		  //将截取到的时间字符串转化为时间格式的字符串
		  Date beginTime = null;
		try {
			beginTime = sdf.parse(dd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  //默认为毫秒，除以1000是为了转换成秒
		  long interval=(currentTime.getTime()-beginTime.getTime())/1000;//秒
		  long day=interval/(24*3600);//天
		  long hour=interval%(24*3600)/3600;//小时
		  long minute=interval%3600/60;//分钟
		  long second=interval%60;//秒
		  System.out.println("两个时间相差："+day+"天"+hour+"小时"+minute+"分"+second+"秒");
	      return day>DIFFERENT_DAY?true:false;
	}
}
