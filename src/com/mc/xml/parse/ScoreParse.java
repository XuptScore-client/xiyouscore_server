package com.mc.xml.parse;

import java.io.InputStream;
import java.util.List;

import model.XueKeScore;

/**
 * 解析xml的接口
 * 
 * @author Administrator 2014-7-21
 */
public interface ScoreParse {

	/**
	 * 解析输入流 得到 tablescore对象集合
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public List<XueKeScore> parse(InputStream is) throws Exception;
	/**
	 * 序列化TableScore对象集合 得到xml形式的字符串
	 * 
	 * @param TableScores
	 * @return
	 * @throws Exception
	 */
	// public String serialize(List<TableScore> TableScores) throws Exception;
}
