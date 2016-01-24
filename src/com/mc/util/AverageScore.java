package com.mc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * AverageScore
 * 
 * @author Administrator
 * 
 */
public class AverageScore {
	private String XN;
	private String XQ;
	private HashMap<String, String> XMLFileName;

	/**
	 * ������רҵ������
	 * 
	 * @param XN
	 * @param XQ
	 * @param XMLFileName
	 */
	public AverageScore(String XN, String XQ,
			HashMap<String, String> XMLFileName) {
		this.XN = XN + "-" + String.valueOf(Integer.parseInt(XN) + 1);
		this.XQ = XQ;
		this.XMLFileName = XMLFileName;
	}

	/**
	 * ��ĳ���˵�����
	 * 
	 * @param XN
	 * @param XQ
	 */
	public AverageScore(String XN, String XQ, boolean isOneXN) {
		this.XN = isOneXN ? XN + "-" + String.valueOf(Integer.parseInt(XN) + 1)
				: XN;
		this.XQ = XQ;
	}

	/**
	 * prase xml ,AverageScore HashMap
	 * 
	 * @param XN
	 * @param XQ
	 * @param XMLFileName
	 *            xml's name
	 * @return Through xn .xq and xh calculate averagescore.
	 * @throws Exception
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public HashMap getAverageScore() throws Exception {
		HashMap<String, Float> averageScoreMap = new HashMap<String, Float>();
		for (String xh : XMLFileName.keySet()) {// read list xml
			// ��ȡ ѧ��
			String xmlFileName = XMLFileName.get(xh);
			System.out.println(xh + " " + xmlFileName);

			averageScoreMap.put(xh, getSomeoneAverageScore(xmlFileName));
		}

		return averageScoreMap;
	}

	/**
	 * ��ĳ��ѧ�ŵĳɼ�
	 * 
	 * @param XMLFileName
	 *            �ļ���
	 * @return
	 */
	public float getSomeoneAverageScore(String XMLFileName) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		NodeList nodeList = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new FileInputStream(new File(
					XMLFileName)));// ����Ϊ stream ֱ���������д���
			nodeList = document.getElementsByTagName("Table");
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		float cjSum = 0;// �ɼ�*ѧ�ֵ��ܺ�
		float xfSum = 0;// ѧ���ܺ�
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			NodeList childList = element.getChildNodes();
			float cj = 0;// �ɼ�*ѧ��
			float xf = 0;// ѧ��
			for (int j = 0; j < childList.getLength(); j++) {
				Node node = childList.item(j);
				if (node.getNodeName().trim().equals("XN")
						&& (node.getTextContent().trim().equals(XN) == false)) {
					break;
				}
				if (node.getNodeName().trim().equals("XQ")
						&& (node.getTextContent().trim().equals(XQ) == false)) {
					break;
				}
				if (node.getNodeName().equals("#text")) {
					continue;
				}
				if (node.getNodeName().trim().equals("XF")) {
					xf = Float.parseFloat(node.getTextContent().trim());
				}
				if (node.getNodeName().trim().equals("CJ")) {
					try {
						cj = Integer.parseInt(node.getTextContent().trim());
					} catch (Exception e) {
						String score = node.getTextContent().trim();
						if (score.equals("����")) {
							cj = 90;
						} else if (score.equals("�е�")) {
							cj = 80;
						} else if (score.equals("����")) {
							cj = 60;
						}
					}
				}

			}
			cjSum = cjSum + cj * xf;
			xfSum = xfSum + xf;
		}
		// �޸� û�гɼ����ֵ�NaN
		float result = 0;
		if (cjSum == 0.0 & xfSum == 0.0) {// û�и�ѧ��ĳɼ�
			;
		} else {

			result = cjSum / xfSum;
			BigDecimal bg = new BigDecimal(result);
			result = (float) bg.setScale(2, BigDecimal.ROUND_HALF_UP)
					.doubleValue();
		}
		return result;
	}

	private float getSomeoneCJ(String kcdm, String xmlFilepath) {
		float cj = 0;// �ɼ�*ѧ��
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		NodeList nodeList = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new FileInputStream(new File(
					xmlFilepath)));// ����Ϊ stream ֱ���������д���
			nodeList = document.getElementsByTagName("Table");
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			NodeList childList = element.getChildNodes();

			float xf = 0;// ѧ��
			for (int j = 0; j < childList.getLength(); j++) {
				Node node = childList.item(j);
				if (node.getNodeName().trim().equals("XN")
						&& (node.getTextContent().trim().equals(XN) == false)) {
					break;
				}
				if (node.getNodeName().trim().equals("XQ")
						&& (node.getTextContent().trim().equals(XQ) == false)) {
					break;
				}
				if (node.getNodeName().equals("#text")) {
					continue;
				}

				if (node.getNodeName().trim().equals("CJ")) {
					try {
						cj = Integer.parseInt(node.getTextContent().trim());
					} catch (Exception e) {
						String score = node.getTextContent().trim();
						if (score.equals("����")) {
							cj = 90;
						} else if (score.equals("�е�")) {
							cj = 80;
						} else if (score.equals("����")) {
							cj = 60;
						}
					}
				}

			}
		}
		return cj;
	}
}
