package com.mc.xml.parse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import model.XueKeScore;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 解析xml
 * 
 * @author Administrator 2014-7-21
 */
public class SaxScoreParser implements ScoreParse {

	public List<XueKeScore> parse(InputStream is) throws Exception {
		// TODO Auto-generated method stub
		SAXParserFactory factory = SAXParserFactory.newInstance(); // 取得SAXParserFactory实例
		SAXParser parser = factory.newSAXParser(); // 从factory获取SAXParser实例
		MyHandler handler = new MyHandler();// 实例化自定义Handler
		parser.parse(is, handler); // 根据自定义Handler规则解析输入流
		return handler.getTableScores();
	}

	// 需要重写defaultHandler的方法
	private class MyHandler extends DefaultHandler {

		private List<XueKeScore> tableScores;
		private XueKeScore tableScore;
		private StringBuilder builder;

		// 返回解析后得到的Book对象集合
		public List<XueKeScore> getTableScores() {
			return tableScores;
		}

		@Override
		public void startDocument() throws SAXException {
			tableScores = new ArrayList<XueKeScore>();
			builder = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// TODO Auto-generated method stub
			if (qName.equals("Table")) {
				tableScore = new XueKeScore();
			}
			// System.out.println("localname:"+localName);
			builder.setLength(0);// 将字符长度设置为0 以便重新开始读取元素内的字符节点
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// TODO Auto-generated method stub
			if (qName.equals("KCDM")) {
				tableScore.setKcdm(builder.toString());
			}
			if (qName.equals("XN")) {
				tableScore.setXn(builder.toString());
			}
			if (qName.equals("XQ")) {
				tableScore.setXq(builder.toString());
			}
			if (qName.equals("KCMC")) {
				tableScore.setKcmc(builder.toString());
			}
			if (qName.equals("KCXZ")) {
				tableScore.setKcxz(builder.toString());
			}
			if (qName.equals("XF")) {
				tableScore.setXf(builder.toString());
			}
			if (qName.equals("PSCJ")) {
				tableScore.setPscj(builder.toString());
			}
			if (qName.equals("QMCJ")) {
				tableScore.setQmcj(builder.toString());
			}
			if (qName.equals("CJ")) {
				tableScore.setCj(builder.toString());
			}
			if (qName.equals("XYMC")) {
				tableScore.setXymc(builder.toString());
			}
			if (qName.equals("BKCJ")) {
				tableScore.setBkcj(builder.toString());
			}
			if (qName.equals("Table")) {
				tableScores.add(tableScore);
			}

		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			builder.append(ch, start, length);// 将读取的字符数组追加到builder中
		}

	}
}
