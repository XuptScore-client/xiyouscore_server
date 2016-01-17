package model;

public class RankList {

	private String xh;
	private String name;
	private String score;

	public String getXh() {
		return xh;
	}

	public void setXh(String xh) {
		this.xh = xh;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	/*
	 * public RankList(Builder builder){ xh = builder.getBuilderXh(); name =
	 * builder.getBuilderName(); score = builder.getBuilderScore(); }
	 * 
	 * public static class Builder{ private String builderXh; private String
	 * builderName; private String builderScore; public String getBuilderXh() {
	 * return builderXh; } public Builder setBuilderXh(String builderXh) {
	 * this.builderXh = builderXh; return this; } public String getBuilderName()
	 * { return builderName; } public Builder setBuilderName(String builderName)
	 * { this.builderName = builderName; return this; } public String
	 * getBuilderScore() { return builderScore; } public Builder
	 * setBuilderScore(String builderScore) { this.builderScore = builderScore;
	 * return this; } public RankList create(){ return new RankList(this); }
	 * 
	 * }
	 */
}
