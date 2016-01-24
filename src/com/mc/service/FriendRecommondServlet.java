package com.mc.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mc.util.Passport;

public class FriendRecommondServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FriendRecommondServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);

	}

	@SuppressWarnings("deprecation")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String falseData = URLDecoder.decode(request.getParameter("data"));// ͨ��ʱ����м���֮���ѧ��
		String time = URLDecoder.decode(request.getParameter("viewstate"));// �ͻ��˲�����ʱ��
																			// ͨ��
																			// ��Կ���л���
		HashMap<String, String> xhAndXnMap = calculateXh(falseData, time);
		String realXh = xhAndXnMap.get("xh");// ѧ��
		String realKcdm = xhAndXnMap.get("kcdm");
		String realScore = xhAndXnMap.get("score");
		String xn = xhAndXnMap.get("xn");// ѧ��
		String xq = xhAndXnMap.get("xq");// ѧ��

	}

	/**
	 * ���Ƚ��ܵõ���ȷ��ʱ�䣬Ȼ��ͨ����ȷ��ʱ����ܵõ���ȷ��ѧ�� �����㷨: ��ʱ�����base64���ܣ�Ȼ����Ϊ��Կ��ʱ����м���base64
	 * 
	 * @param falseXh
	 *            �ٵ�ѧ��
	 * @param time
	 *            ʱ���
	 * @return ������ʵѧ��
	 */
	private HashMap<String, String> calculateXh(String falseXh, String time) {
		// TODO Auto-generated method stub
		String realXh = "";
		String realTime = new Passport().jiemi(time,
				String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
		HashMap<String, String> xhAndXnMap = new HashMap<String, String>();// ѧ��
																			// ѧ��
																			// ѧ��
		try {
			realXh = new Passport().jiemi(falseXh, realTime);
			System.out.println("��ʵ����:" + realXh);
			String[] xhAndXn = new String(realXh).split("\\|");
			xhAndXnMap.put("xh", xhAndXn[0]);
			xhAndXnMap.put("kcdm", xhAndXn[1].split("\\|")[0]);
			xhAndXnMap.put("xn", xhAndXn[1].split("\\|")[1]);
			xhAndXnMap.put("xq", xhAndXn[1].split("\\|")[2]);
			xhAndXnMap.put("score", xhAndXn[2]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xhAndXnMap;
	}

}
