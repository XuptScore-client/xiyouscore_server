package com.mc.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mc.jsonutil.JSONUtil;

import dao.XueKeScoreDAO;

import model.XueKeScore;

public class JSONutil {

	private static String xn = "/NewDataSet/Table/XN";// ѧ��
	private static String xq = "/NewDataSet/Table/XQ";// ѧ��
	private static String kcmc = "/NewDataSet/Table/KCMC";// �γ�
	private static String kcxz = "/NewDataSet/Table/KCXZ";// �γ�����
	private static String xf = "/NewDataSet/Table/XF";// ѧ��
	private static String pscj = "/NewDataSet/Table/PSCJ";// ƽʱ�ɼ�
	private static String qmcj = "/NewDataSet/Table/QMCJ";// ��ĩ�ɼ�
	private static String cj = "/NewDataSet/Table/CJ";// ���ճɼ�
	private static String xymc = "/NewDataSet/Table/XYMC";// ѧԺ

	/**
	 * ����xml ����json���ݸ�ʽ
	 * 
	 * @param str
	 * @return
	 */
	public static String get_JSON(String str) {
		String result = "";

		SAXReader saxReader = new SAXReader();

		try {
			ByteArrayInputStream in = new ByteArrayInputStream(
					str.getBytes("utf-8"));
			org.dom4j.Document document = saxReader.read(in);
			Element root = document.getRootElement();
			List xnArray = root.selectNodes(xn);
			List xqArray = root.selectNodes(xq);
			List kcmcArray = root.selectNodes(kcmc);
			List kcxzArray = root.selectNodes(kcxz);
			List xfArray = root.selectNodes(xf);
			List pscjArray = root.selectNodes(pscj);
			List qmcjArray = root.selectNodes(qmcj);
			List cjArray = root.selectNodes(cj);
			List xymcArray = root.selectNodes(xymc);
			Element[] xnArrays = new Element[xnArray.size()];
			xnArray.toArray(xnArrays);
			Element[] xqArrays = new Element[xnArray.size()];
			xqArray.toArray(xqArrays);
			Element[] kcmcArrays = new Element[xnArray.size()];
			kcmcArray.toArray(kcmcArrays);
			Element[] kcxzArrays = new Element[xnArray.size()];
			kcxzArray.toArray(kcxzArrays);
			Element[] xfArrays = new Element[xnArray.size()];
			xfArray.toArray(xfArrays);
			Element[] pscjArrays = new Element[xnArray.size()];
			pscjArray.toArray(pscjArrays);
			Element[] qmcjArrays = new Element[xnArray.size()];
			qmcjArray.toArray(qmcjArrays);
			Element[] cjArrays = new Element[xnArray.size()];
			cjArray.toArray(cjArrays);
			Element[] xymcArrays = new Element[xnArray.size()];
			xymcArray.toArray(xymcArrays);

			List<XueKeScore> list = new ArrayList<XueKeScore>();
			for (int i = 0; i < xymcArrays.length; i++) {
				XueKeScore xueKeScore = new XueKeScore();
				xueKeScore.setXn(xnArrays[i].getText());
				xueKeScore.setXq(xqArrays[i].getText());
				xueKeScore.setKcmc(kcmcArrays[i].getText());
				xueKeScore.setKcxz(kcxzArrays[i].getText());
				xueKeScore.setXf(xfArrays[i].getText());
				xueKeScore.setPscj(pscjArrays[i].getText());
				xueKeScore.setQmcj(qmcjArrays[i].getText());
				xueKeScore.setCj(cjArrays[i].getText());
				xueKeScore.setXymc(xymcArrays[i].getText());
				list.add(xueKeScore);
			}
			XueKeScoreDAO xueKeScoreDAO = new XueKeScoreDAO();
			xueKeScoreDAO.setlKeScores(list);
			result = JSONUtil.toJSON(xueKeScoreDAO);// ת��Ϊjson����
			return result;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * �õ��ɼ�
	 * 
	 * @param str
	 * @return
	 */
	public static String getChengji(String str) {
		String result = "";
		str = toStringS(str);
		try {
			Pattern p = Pattern.compile("<NewDataSet>(.*?)</NewDataSet>");// ����ѧ�Ƴɼ���table
			Matcher m = p.matcher(str);
			String string = "";
			while (m.find()) {
				string = m.group();// ��������table��ǩ�ĳɼ�
			}
			System.out.println("��һ��" + string);
			result = return_json(string);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	private static String toStringS(String str) {
		String dest;
		// \\t|\r
		Pattern p = Pattern.compile("\n|\t");
		Matcher m = p.matcher(str);
		dest = m.replaceAll(",");
		return dest;
	}

	/**
	 * ����table��ǩ�еĳɼ��������ϳ�json���ݸ�ʽ
	 * 
	 * @param s
	 * @return
	 */
	private static String return_json(String s) {
		String json = "";
		List<XueKeScore> listKeScores = new ArrayList<XueKeScore>();
		Pattern p = Pattern.compile("<Table(.*?)</Table>");// ��ȡ����ĳɼ�
		Matcher m = p.matcher(s);
		while (m.find()) {
			String string = m.group();
			XueKeScore xueKeScore = new XueKeScore();
			xueKeScore.setXn(getPattern("<XN>(.*?)</XN>", string));
			xueKeScore.setXq(getPattern("<XQ>(.*?)</XQ>", string));
			xueKeScore.setKcmc(getPattern("<KCMC>(.*?)</KCMC>", string));
			xueKeScore.setKcxz(getPattern("<KCXZ>(.*?)</KCXZ>", string));
			xueKeScore.setXf(getPattern("<XF>(.*?)</XF>", string));
			xueKeScore.setPscj(getPattern("<PSCJ>(.*?)</PSCJ>", string));
			xueKeScore.setQmcj(getPattern("<QMCJ>(.*?)</QMCJ>", string));
			xueKeScore.setCj(getPattern("<CJ>(.*?)</CJ>", string));
			xueKeScore.setXymc(getPattern("<XYMC>(.*?)</XYMC>", string));
			listKeScores.add(xueKeScore);
		}
		XueKeScoreDAO xueKeScoreDAO = new XueKeScoreDAO();
		xueKeScoreDAO.setlKeScores(listKeScores);
		json = JSONUtil.toJSON(xueKeScoreDAO);// ת��Ϊjson����
		return json;
	}

	/**
	 * ���� regex ������ʽ�������ַ���
	 * 
	 * @param regex
	 * @param str
	 * @return
	 */
	private static String getPattern(String regex, String str) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		String result = "";
		while (m.find()) {
			result = m.group(1);
		}
		return result;
	}
}
