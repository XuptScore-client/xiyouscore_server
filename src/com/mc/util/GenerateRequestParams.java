package com.mc.util;

import java.util.HashMap;

public class GenerateRequestParams {

	public static void main(String[] args) {
		String score = "01121012|2014|2";
		long time = System.currentTimeMillis();
		try {

			String time_s = new Passport()
					.jiami(String.valueOf(time), "248822");
			score = new Passport().jiami(score, String.valueOf(time));

			System.out.println("score:" + score + "  time:" + time_s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
