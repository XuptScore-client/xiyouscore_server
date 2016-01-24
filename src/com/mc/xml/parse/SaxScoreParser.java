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
 * ����xml
 * 
 * @author Administrator 2014-7-21
 */
public class SaxScoreParser implements ScoreParse {

	public List<XueKeScore> parse(InputStream is) throws Exception {
		// TODO Auto-generated method stub
		SAXParserFactory factory = SAXParserFactory.newInstance(); // ȡ��SAXParserFactoryʵ��
		SAXParser parser = factory.newSAXParser(); // ��factory��ȡSAXParserʵ��
		MyHandler handler = new MyHandler();// ʵ�����Զ���Handler
		parser.parse(is, handler); // �����Զ���Handler�������������
		return handler.getTableScores();
	}

	// ��Ҫ��дdefaultHandler�ķ���
	private class MyHandler extends DefaultHandler {

		private List<XueKeScore> tableScores;
		private XueKeScore tableScore;
		private StringBuilder builder;

		// ���ؽ�����õ���Book���󼯺�
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
			builder.setLength(0);// ���ַ���������Ϊ0 �Ա����¿�ʼ��ȡԪ���ڵ��ַ��ڵ�
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
			builder.append(ch, start, length);// ����ȡ���ַ�����׷�ӵ�builder��
		}

	}
}
