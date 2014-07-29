package com.mc.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ���� �ļ���ʱ��  �������  100��  ������ ���� ѧУ�ķ�����
 * @author Administrator
 *
 */
public class CalculateFileTime {

	private static final int DIFFERENT_DAY = 100;//�������
	/**
	 * �Ƿ��������������
	 * @param Ifile
	 * @return
	 */
	public static Boolean isRequest(File Ifile){
		long modifiedTime = Ifile.lastModified();
        Date date=new Date(modifiedTime);
        SimpleDateFormat mode_sdf=new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        String dd=mode_sdf.format(date);//�ļ����޸�ʱ��
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  Date currentTime=new Date();
		  //����ȡ����ʱ���ַ���ת��Ϊʱ���ʽ���ַ���
		  Date beginTime = null;
		try {
			beginTime = sdf.parse(dd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  //Ĭ��Ϊ���룬����1000��Ϊ��ת������
		  long interval=(currentTime.getTime()-beginTime.getTime())/1000;//��
		  long day=interval/(24*3600);//��
		  long hour=interval%(24*3600)/3600;//Сʱ
		  long minute=interval%3600/60;//����
		  long second=interval%60;//��
		  System.out.println("����ʱ����"+day+"��"+hour+"Сʱ"+minute+"��"+second+"��");
	      return day>DIFFERENT_DAY?true:false;
	}
}
