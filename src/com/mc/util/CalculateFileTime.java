package com.mc.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ���� �ļ���ʱ�� ������� 100�� ������ ���� ѧУ�ķ�����
 * 
 * @author Administrator
 * 
 */
public class CalculateFileTime {

	private static final long DIFFERENT_DAY = 10;// �������
	private static final long DIFFERENT_TIME = 1;// �������

	/**
	 * �Ƿ��������������
	 * 
	 * @param Ifile
	 * @return
	 */
	public static Boolean isRequest(File Ifile) {
		long modifiedTime = Ifile.lastModified();
		Date date = new Date(modifiedTime);
		SimpleDateFormat mode_sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
		String dd = mode_sdf.format(date);// �ļ����޸�ʱ��

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentTime = new Date();
		// ����ȡ����ʱ���ַ���ת��Ϊʱ���ʽ���ַ���
		Date beginTime = null;
		try {
			beginTime = sdf.parse(dd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Ĭ��Ϊ���룬����1000��Ϊ��ת������
		long interval = (currentTime.getTime() - beginTime.getTime()) / 1000;// ��
		long day = interval / (24 * 3600);// ��
		long hour = interval % (24 * 3600) / 3600;// Сʱ
		long minute = interval % 3600 / 60;// ����
		long second = interval % 60;// ��
		System.out.println("����ʱ����" + day + "��" + hour + "Сʱ" + minute + "��"
				+ second + "��");
		return day > DIFFERENT_DAY ? true : false;
	}

	/**
	 * �Ƚ�����ʱ��Ĳ�ֵ
	 * 
	 * @param last_time
	 *            �ϵ�ʱ��
	 * @param new_time
	 *            ����ʱ��
	 * @return
	 */
	public static Boolean isRequest(File Ifile, long DIFFERENT_TIME) {
		long modifiedTime = Ifile.lastModified();
		Date date = new Date(modifiedTime);
		SimpleDateFormat mode_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dd = mode_sdf.format(date);// �ļ����޸�ʱ��
		/* Date currentTime=new Date(); */
		// ����ȡ����ʱ���ַ���ת��Ϊʱ���ʽ���ַ���
		Date beginTime = null;
		try {
			beginTime = mode_sdf.parse(dd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* Date currentTime=new Date(); */
		// ����ȡ����ʱ���ַ���ת��Ϊʱ���ʽ���ַ���
		// Ĭ��Ϊ���룬����1000��Ϊ��ת������
		Date currentTime = new Date();
		long interval = (currentTime.getTime() - beginTime.getTime()) / 1000;// ��
		long day = interval / (24 * 3600);// ��
		long hour = interval % (24 * 3600) / 3600;// Сʱ
		long minute = interval % 3600 / 60;// ����
		long second = interval % 60;// ��
		System.out.println("����ʱ����" + hour + "Сʱ  +  " + DIFFERENT_TIME);
		return hour > DIFFERENT_TIME ? true : false;
	}
}
