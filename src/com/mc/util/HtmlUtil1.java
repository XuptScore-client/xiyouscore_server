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

	// 返回该标签的内容
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
	 * 获取input中的值
	 * 
	 * @param html
	 * @param name
	 *            input中name得值
	 * @return
	 */
	public static String getHtmlInput(String html, String name) {
		String result = "server_preserve";// 服务器 维护
		// 创建Parser对象根据传给字符串和指定的编码
		Parser parser = Parser.createParser(html, "GBK");
		// 创建HtmlPage对象HtmlPage(Parser parser)
		HtmlPage page = new HtmlPage(parser);
		try {
			// HtmlPage extends visitor,Apply the given visitor to the current
			// page.
			parser.visitAllNodesWith(page);
		} catch (ParserException e1) {
			e1 = null;
		}
		// 所有的节点
		NodeList nodelist = page.getBody();
		// 建立一个节点filter用于过滤节点
		NodeFilter filter = new TagNameFilter("input");
		// 得到所有过滤后，想要的节点
		nodelist = nodelist.extractAllNodesThatMatch(filter, true);
		for (int i = 0; i < nodelist.size(); i++) {
			// System.out.println("\n");
			LinkTag link = (LinkTag) nodelist.elementAt(i);

			// InputTag inputTag
			if (link.getAttribute("name").equals(name)) {// get到 下一次post请求要用的参数
				result = link.getAttribute("value");
			}
		}
		return result;
	}

	/**
	 * 测试OrFilter的用法 获取 input 或者select中的值
	 * 
	 * @param html
	 * @param name
	 *            要获取的input name值
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
	 * 获取 herf
	 * 
	 * @param html
	 * @param herfName
	 * @return
	 */
	// 同样可以获得标题、地址，但地址不够完整
	public static String getHERF(String html, int flag) { // 获取对应 链接名称的herf

		String result = "server_preserve";// 服务器 维护
		// 创建Parser对象根据传给字符串和指定的编码
		Parser parser = Parser.createParser(html, "GBK");
		// 创建HtmlPage对象HtmlPage(Parser parser)
		HtmlPage page = new HtmlPage(parser);
		try {
			// HtmlPage extends visitor,Apply the given visitor to the current
			// page.
			parser.visitAllNodesWith(page);
		} catch (ParserException e1) {
			e1 = null;
		}
		// 所有的节点
		NodeList nodelist = page.getBody();
		// 建立一个节点filter用于过滤节点
		NodeFilter filter = new TagNameFilter("A");
		// 得到所有过滤后，想要的节点
		nodelist = nodelist.extractAllNodesThatMatch(filter, true);
		List<Herf> listHerf = new ArrayList<Herf>();// json格式
		for (int i = 0; i < nodelist.size(); i++) {
			// System.out.println("\n");
			LinkTag link = (LinkTag) nodelist.elementAt(i);

			// 返回用户姓名
			if (flag == 1) {// 所有链接
				// herf = herf + link.getStringText()+"_"
				// +link.getAttribute("href") + ";";
				// 返回json格式的数据
				/*
				 * Herf herf = new Herf(); herf.setTittle(link.getStringText());
				 * herf.setHerf(link.getAttribute("href")); listHerf.add(herf);
				 */
				if (link.getStringText().equals("成绩查询")) {
					// 返回用户姓名
					result = link.getAttribute("href").split("&")[1].split("=")[1];// 姓名
				}

			} else {
				if ("here".equals(link.getStringText())) {// 登录
					// 链接名称
					// System.out.println(link.getStringText());
					result = link.getAttribute("href");
				}
			}
		}
		/*
		 * if (flag == 1) {// 将所有的herf整合成Json数据 HerfDAO herfDAO = new HerfDAO();
		 * herfDAO.setListHerf(listHerf); result = JSONUtil.toJSON(herfDAO); }
		 */

		return result;
	}

	/**
	 * 获取tittle
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
	 * 获取body
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
			body = body.replaceAll("[　\\t\\n\\r\\f(&nbsp;|gt) ]+", " ");
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
	 * 写文件
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
	 * 解析tr
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
			System.out.println("打印个人信息");
			if (displaymode.equals("NEWS")) {// 首页
				for (int i = 10; i < 18; i++) { // for (int i = 0; i <
					// nodeList.size(); i++) { 获取首页
					// 个人信息 从第10 行 到17行
					// 获取tr
					TableRow trt = (TableRow) nodeList.elementAt(i);
					// 获取tr子节点的内容
					// System.out.println(trt.toPlainTextString());

					body = body + trt.toPlainTextString();
					body = body.replaceAll("[　\\t\\r\\f(&nbsp;|gt) ]+", " ");
					body = body + "\n";
				}
			}
			if (displaymode.equals("PAID")) {// 缴费记录
				for (int i = 19; i < 24; i++) { // for (int i = 0; i <
					// nodeList.size(); i++) { 获取首页
					// 个人信息 从第10 行 到17行
					// 获取tr
					TableRow trt = (TableRow) nodeList.elementAt(i);
					// 获取tr子节点的内容
					// System.out.println(trt.toPlainTextString());
					body = body + trt.toPlainTextString() + "\n";
					body = body.replaceAll("[　\\t\\r\\f(&nbsp;|gt) ]+", " ");
					body = body + "\n";
				}
			}
			if (displaymode.equals("LD")) {// 登录记录
				for (int i = 19; i < 24; i++) { // for (int i = 0; i <
					// nodeList.size(); i++) { 获取首页
					// 个人信息 从第10 行 到17行
					// 获取tr
					TableRow trt = (TableRow) nodeList.elementAt(i);
					// 获取tr子节点的内容
					body = body + trt.toPlainTextString() + "\n";
					body = body.replaceAll("[　\\t\\r\\f(&nbsp;|gt) ]+", " ");
					body = body + "\n";
				}
			}
			if (displaymode.equals("UD")) {// 详细资料
				for (int i = 21; i < nodeList.size() - 1; i++) { // for (int i =
																	// 0; i <
					// nodeList.size(); i++) { 获取首页
					// 个人信息 从第10 行 到17行
					// 获取tr
					TableRow trt = (TableRow) nodeList.elementAt(i);
					// 获取tr子节点的内容
					body = body + trt.toPlainTextString() + "\n";
					body = body.replaceAll("[　\\t\\r\\f(&nbsp;|gt) ]+", " ");
					body = body + "\n";
				}
			}

			if (displaymode.equals("")) {
				for (int i = 0; i < nodeList.size(); i++) { // for (int i = 0; i
															// <
					// nodeList.size(); i++) { 获取首页
					// 个人信息 从第10 行 到17行
					// 获取tr
					TableRow trt = (TableRow) nodeList.elementAt(i);
					// 获取tr子节点的内容
					// System.out.println(trt.toPlainTextString());

					body = body + trt.toPlainTextString();
					body = body.replaceAll("[　\\t\\r\\f(&nbsp;|gt) ]+", " ");
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
	 * 删除Html标签，获取script中的值
	 * 
	 * @param inputString
	 * @return
	 */
	public static String htmlRemoveTag(String inputString) {
		if (inputString == null)
			return null;
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		try {
			// 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
			// 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			/*
			 * p_script = Pattern.compile(regEx_script,
			 * Pattern.CASE_INSENSITIVE); m_script = p_script.matcher(htmlStr);
			 * htmlStr = m_script.replaceAll(""); // 过滤script标签
			 */p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签
			textStr = htmlStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return textStr;// 返回文本字符串
	}
}
