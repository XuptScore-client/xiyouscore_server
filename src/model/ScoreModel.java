package model;

import java.util.List;

/**
 * xn为最外层的
 * 
 * @author Administrator 2014-7-22
 */
public class ScoreModel {

	private String xn;
	private List<XueKeScore> list_xueKeScore;

	public String getXn() {
		return xn;
	}

	public void setXn(String xn) {
		this.xn = xn;
	}

	public List<XueKeScore> getList_xueKeScore() {
		return list_xueKeScore;
	}

	public void setList_xueKeScore(List<XueKeScore> listXueKeScore) {
		list_xueKeScore = listXueKeScore;
	}

}
