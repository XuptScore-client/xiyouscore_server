package com.mc.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import model.Herf;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.InputTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.tags.SelectTag;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;

import com.mc.jsonutil.JSONUtil;

import dao.HerfDAO;

public class HtmlUtil1 {

	// ���ظñ�ǩ������
	public static String getHtmlTag(HttpURLConnection httpURLConnection,
			String s) {
		NodeList nodes;
		String result = "error";
		try {
			Parser parser = new Parser(httpURLConnection);
			NodeFilter filter = new TagNameFilter(s);
			nodes = parser.extractAllNodesThatMatch(filter);
			if (nodes != null) {
				for (int i = 0; i < nodes.size(); i++) {
					Node textnode = (Node) nodes.elementAt(i);
					result = textnode.toPlainTextString();
				}
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * System.out.println("==============fdf==========================");
		 * System.out.println(result);
		 */
		return result;
	}

	/**
	 * ��ȡinput�е�ֵ
	 * 
	 * @param html
	 * @param name
	 *            input��name��ֵ
	 * @return
	 */
	public static String getHtmlInput(String html, String name) {
		String result = "server_preserve";// ������ ά��
		// ����Parser������ݴ����ַ�����ָ���ı���
		Parser parser = Parser.createParser(html, "GBK");
		// ����HtmlPage����HtmlPage(Parser parser)
		HtmlPage page = new HtmlPage(parser);
		try {
			// HtmlPage extends visitor,Apply the given visitor to the current
			// page.
			parser.visitAllNodesWith(page);
		} catch (ParserException e1) {
			e1 = null;
		}
		// ���еĽڵ�
		NodeList nodelist = page.getBody();
		// ����һ���ڵ�filter���ڹ��˽ڵ�
		NodeFilter filter = new TagNameFilter("input");
		// �õ����й��˺���Ҫ�Ľڵ�
		nodelist = nodelist.extractAllNodesThatMatch(filter, true);
		for (int i = 0; i < nodelist.size(); i++) {
			// System.out.println("\n");
			LinkTag link = (LinkTag) nodelist.elementAt(i);

			// InputTag inputTag
			if (link.getAttribute("name").equals(name)) {// get�� ��һ��post����Ҫ�õĲ���
				result = link.getAttribute("value");
			}
		}
		return result;
	}

	/**
	 * ����OrFilter���÷� ��ȡ input ����select�е�ֵ
	 * 
	 * @param html
	 * @param name
	 *            Ҫ��ȡ��input nameֵ
	 */
	public static String getInput(String html, String name) {
		String result = "";
		NodeFilter inputFilter = new NodeClassFilter(InputTag.class);
		NodeFilter selectFilter = new NodeClassFilter(SelectTag.class);
		Parser myParser;
		NodeList nodeList = null;
		try {
			Parser parser = new Parser();
			parser.setInputHTML(html);
			parser.setEncoding(parser.getEncoding());
			OrFilter lastFilter = new OrFilter();
			lastFilter.setPredicates(new NodeFilter[] { selectFilter,
					inputFilter });
			nodeList = parser.parse(lastFilter);
			for (int i = 0; i <= nodeList.size(); i++) {
				if (nodeList.elementAt(i) instanceof InputTag) {
					InputTag tag = (InputTag) nodeList.elementAt(i);
					/*
					 * System.out.println("OrFilter tag name is :" +
					 * tag.getTagName() + " ,tag value is:" +
					 * tag.getAttribute("value"));
					 */
					if (tag.getAttribute("name").equals(name)) {
						result = tag.getAttribute("value");
					}
				}
				/*
				 * if (nodeList.elementAt(i) instanceof SelectTag) { SelectTag
				 * tag = (SelectTag) nodeList.elementAt(i); NodeList list =
				 * tag.getChildren(); for (int j = 0; j < list.size(); j++) {
				 * OptionTag option = (OptionTag) list.elementAt(j);
				 * System.out.println("OrFilter Option" +
				 * option.getOptionText()); } }
				 */
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * ��ȡ herf
	 * 
	 * @param html
	 * @param herfName
	 * @return
	 */
	// ͬ�����Ի�ñ��⡢��ַ������ַ��������
	public static String getHERF(String html, int flag) { // ��ȡ��Ӧ �������Ƶ�herf

		String result = "server_preserve";// ������ ά��
		// ����Parser������ݴ����ַ�����ָ���ı���
		Parser parser = Parser.createParser(html, "GBK");
		// ����HtmlPage����HtmlPage(Parser parser)
		HtmlPage page = new HtmlPage(parser);
		try {
			// HtmlPage extends visitor,Apply the given visitor to the current
			// page.
			parser.visitAllNodesWith(page);
		} catch (ParserException e1) {
			e1 = null;
		}
		// ���еĽڵ�
		NodeList nodelist = page.getBody();
		// ����һ���ڵ�filter���ڹ��˽ڵ�
		NodeFilter filter = new TagNameFilter("A");
		// �õ����й��˺���Ҫ�Ľڵ�
		nodelist = nodelist.extractAllNodesThatMatch(filter, true);
		List<Herf> listHerf = new ArrayList<Herf>();// json��ʽ
		for (int i = 0; i < nodelist.size(); i++) {
			// System.out.println("\n");
			LinkTag link = (LinkTag) nodelist.elementAt(i);

			// �����û�����
			if (flag == 1) {// ��������
				// herf = herf + link.getStringText()+"_"
				// +link.getAttribute("href") + ";";
				// ����json��ʽ������
				/*
				 * Herf herf = new Herf(); herf.setTittle(link.getStringText());
				 * herf.setHerf(link.getAttribute("href")); listHerf.add(herf);
				 */
				if (link.getStringText().equals("�ɼ���ѯ")) {
					// �����û�����
					result = link.getAttribute("href").split("&")[1].split("=")[1];// ����
				}

			} else {
				if ("here".equals(link.getStringText())) {// ��¼
					// ��������
					// System.out.println(link.getStringText());
					result = link.getAttribute("href");
				}
			}
		}
		/*
		 * if (flag == 1) {// �����е�herf���ϳ�Json���� HerfDAO herfDAO = new HerfDAO();
		 * herfDAO.setListHerf(listHerf); result = JSONUtil.toJSON(herfDAO); }
		 */

		return result;
	}

	/**
	 * ��ȡtittle
	 */
	public static String getHtmlTittle(String html) {
		String tittle = "error";
		try {
			Parser parser = new Parser(html);
			/*
			 * Parser parser = new Parser( httpURLConnection);
			 */
			parser.setEncoding("utf8");
			HtmlPage htmlPage = new HtmlPage(parser);
			parser.visitAllNodesWith(htmlPage);
			tittle = htmlPage.getTitle();
			// System.out.println(tittle);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("==============fdf==========================");
		// System.out.println(tittle);
		return tittle;
	}

	/**
	 * ��ȡbody
	 */
	public static String getHtmlBody(String html) {
		String body = "error";
		try {
			Parser parser = new Parser(html);
			/*
			 * Parser parser = new Parser( httpURLConnection);
			 */
			parser.setEncoding("utf8");
			HtmlPage htmlPage = new HtmlPage(parser);
			parser.visitAllNodesWith(htmlPage);
			body = htmlPage.getBody().asString();
			body = body.replaceAll("[��\\t\\n\\r\\f(&nbsp;|gt) ]+", " ");
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * System.out.println("==============fdf==========================");
		 * System.out.println(body);
		 */
		return body;
	}

	/**
	 * д�ļ�
	 */

	public static void writeHtml(String html) {
		String s = html;
		FileWriter fw = null;
		File f = new File(FilePathUtil.htmlPath);
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			fw = new FileWriter(f);
			BufferedWriter out = new BufferedWriter(fw);
			out.write(s, 0, s.length() - 1);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("end");
	}

	/**
	 * ����tr
	 */
	public static String parseHtmlTR(String html, String displaymode) {
		String body = "";
		try {
			Parser parser = new Parser(html);
			/*
			 * Parser parser = new Parser( httpURLConnection);
			 */
			parser.setEncoding("utf8");
			NodeList nodeList = parser
					.extractAllNodesThatMatch(new TagNameFilter("TR"));
			System.out.println("��ӡ������Ϣ");
			if (displaymode.equals("NEWS")) {// ��ҳ
				for (int i = 10; i < 18; i++) { // for (int i = 0; i <
					// nodeList.size(); i++) { ��ȡ��ҳ
					// ������Ϣ �ӵ�10 �� ��17��
					// ��ȡtr
					TableRow trt = (TableRow) nodeList.elementAt(i);
					// ��ȡtr�ӽڵ������
					// System.out.println(trt.toPlainTextString());

					body = body + trt.toPlainTextString();
					body = body.replaceAll("[��\\t\\r\\f(&nbsp;|gt) ]+", " ");
					body = body + "\n";
				}
			}
			if (displaymode.equals("PAID")) {// �ɷѼ�¼
				for (int i = 19; i < 24; i++) { // for (int i = 0; i <
					// nodeList.size(); i++) { ��ȡ��ҳ
					// ������Ϣ �ӵ�10 �� ��17��
					// ��ȡtr
					TableRow trt = (TableRow) nodeList.elementAt(i);
					// ��ȡtr�ӽڵ������
					// System.out.println(trt.toPlainTextString());
					body = body + trt.toPlainTextString() + "\n";
					body = body.replaceAll("[��\\t\\r\\f(&nbsp;|gt) ]+", " ");
					body = body + "\n";
				}
			}
			if (displaymode.equals("LD")) {// ��¼��¼
				for (int i = 19; i < 24; i++) { // for (int i = 0; i <
					// nodeList.size(); i++) { ��ȡ��ҳ
					// ������Ϣ �ӵ�10 �� ��17��
					// ��ȡtr
					TableRow trt = (TableRow) nodeList.elementAt(i);
					// ��ȡtr�ӽڵ������
					body = body + trt.toPlainTextString() + "\n";
					body = body.replaceAll("[��\\t\\r\\f(&nbsp;|gt) ]+", " ");
					body = body + "\n";
				}
			}
			if (displaymode.equals("UD")) {// ��ϸ����
				for (int i = 21; i < nodeList.size() - 1; i++) { // for (int i =
																	// 0; i <
					// nodeList.size(); i++) { ��ȡ��ҳ
					// ������Ϣ �ӵ�10 �� ��17��
					// ��ȡtr
					TableRow trt = (TableRow) nodeList.elementAt(i);
					// ��ȡtr�ӽڵ������
					body = body + trt.toPlainTextString() + "\n";
					body = body.replaceAll("[��\\t\\r\\f(&nbsp;|gt) ]+", " ");
					body = body + "\n";
				}
			}

			if (displaymode.equals("")) {
				for (int i = 0; i < nodeList.size(); i++) { // for (int i = 0; i
															// <
					// nodeList.size(); i++) { ��ȡ��ҳ
					// ������Ϣ �ӵ�10 �� ��17��
					// ��ȡtr
					TableRow trt = (TableRow) nodeList.elementAt(i);
					// ��ȡtr�ӽڵ������
					// System.out.println(trt.toPlainTextString());

					body = body + trt.toPlainTextString();
					body = body.replaceAll("[��\\t\\r\\f(&nbsp;|gt) ]+", " ");
					body = body + "\n";
				}
			}
			// System.out.println(body);
			System.out.println("===================================");
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * System.out.println("==============fdf==========================");
		 * System.out.println(body);
		 */
		return body;
	}

	/**
	 * ɾ��Html��ǩ����ȡscript�е�ֵ
	 * 
	 * @param inputString
	 * @return
	 */
	public static String htmlRemoveTag(String inputString) {
		if (inputString == null)
			return null;
		String htmlStr = inputString; // ��html��ǩ���ַ���
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		try {
			// ����script��������ʽ{��<script[^>]*?>[\\s\\S]*?<\\/script>
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
			// ����style��������ʽ{��<style[^>]*?>[\\s\\S]*?<\\/style>
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
			String regEx_html = "<[^>]+>"; // ����HTML��ǩ��������ʽ
			/*
			 * p_script = Pattern.compile(regEx_script,
			 * Pattern.CASE_INSENSITIVE); m_script = p_script.matcher(htmlStr);
			 * htmlStr = m_script.replaceAll(""); // ����script��ǩ
			 */p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // ����style��ǩ
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // ����html��ǩ
			textStr = htmlStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return textStr;// �����ı��ַ���
	}
}
