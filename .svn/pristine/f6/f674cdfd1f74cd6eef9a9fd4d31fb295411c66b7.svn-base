package com.mc.util;

public class UnicodeEncoder {

	/**
	 * 获取字符串的unicode编码序列
	 * 
	 * @param s
	 *            string
	 * @return unicode编码后的字符串
	 */
	public static String toUNICODE(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {

			if (s.charAt(i) < 256)// ASC11表中的字符码值不够4位,补00
			{
				sb.append("\\u00");
			} else {
				sb.append("\\u");
			}
			// System.out.println(Integer.toHexString(s.charAt(i)));
			sb.append(Integer.toHexString(s.charAt(i)));
		}
		return sb.toString();
	}

	/**
	 * 根据unicode编码序列获取字符串,直接输出即可
	 * 
	 * @param unicodeValues
	 *            unicode码值序列,e.g. "\u0063\u0064"
	 * @return 解码后的字符串序列
	 */
	public static String toCharSequence(String unicodeValues) {

		return unicodeValues;
	}

	public static void main(String[] args) {
		System.out.println(toUNICODE("贾睿"));
		System.out.println(toCharSequence("\u0061\u0062\u6c49\u5b57"));
	}

}
