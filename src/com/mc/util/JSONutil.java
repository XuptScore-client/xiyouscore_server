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

	private static String xn = "/NewDataSet/Table/XN";// 学年
	private static String xq = "/NewDataSet/Table/XQ";// 学期
	private static String kcmc = "/NewDataSet/Table/KCMC";// 课程
	private static String kcxz = "/NewDataSet/Table/KCXZ";// 课程类型
	private static String xf = "/NewDataSet/Table/XF";// 学分
	private static String pscj = "/NewDataSet/Table/PSCJ";// 平时成绩
	private static String qmcj = "/NewDataSet/Table/QMCJ";// 期末成绩
	private static String cj = "/NewDataSet/Table/CJ";// 最终成绩
	private static String xymc = "/NewDataSet/Table/XYMC";// 学院

	/**
	 * 解析xml 返回json数据格式
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
			result = JSONUtil.toJSON(xueKeScoreDAO);// 转换为json数据
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
	 * 得到成绩
	 * 
	 * @param str
	 * @return
	 */
	public static String getChengji(String str) {
		String result = "";
		str = toStringS(str);
		try {
			Pattern p = Pattern.compile("<NewDataSet>(.*?)</NewDataSet>");// 所有学科成绩的table
			Matcher m = p.matcher(str);
			String string = "";
			while (m.find()) {
				string = m.group();// 整个包含table标签的成绩
			}
			System.out.println("第一次" + string);
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
	 * 解析table标签中的成绩，并整合成json数据格式
	 * 
	 * @param s
	 * @return
	 */
	private static String return_json(String s) {
		String json = "";
		List<XueKeScore> listKeScores = new ArrayList<XueKeScore>();
		Pattern p = Pattern.compile("<Table(.*?)</Table>");// 获取具体的成绩
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
		json = JSONUtil.toJSON(xueKeScoreDAO);// 转换为json数据
		return json;
	}

	/**
	 * 传入 regex 正则表达式，返回字符串
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
