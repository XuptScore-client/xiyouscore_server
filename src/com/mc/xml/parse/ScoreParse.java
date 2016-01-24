package com.mc.xml.parse;

import java.io.InputStream;
import java.util.List;

import model.XueKeScore;

/**
 * ����xml�Ľӿ�
 * 
 * @author Administrator 2014-7-21
 */
public interface ScoreParse {

	/**
	 * ���������� �õ� tablescore���󼯺�
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public List<XueKeScore> parse(InputStream is) throws Exception;
	/**
	 * ���л�TableScore���󼯺� �õ�xml��ʽ���ַ���
	 * 
	 * @param TableScores
	 * @return
	 * @throws Exception
	 */
	// public String serialize(List<TableScore> TableScores) throws Exception;
}
