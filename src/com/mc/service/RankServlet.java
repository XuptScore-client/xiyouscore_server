package com.mc.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.RankList;
import model.RankModel;

import com.mc.db.DBUtil;
import com.mc.jsonutil.JSONUtil;
import com.mc.util.AverageScore;
import com.mc.util.BASE64;
import com.mc.util.FileEncryptAndDecrypt;
import com.mc.util.FileSearcher;
import com.mc.util.Passport;

import dao.ScoreModelDAO;

public class RankServlet extends HttpServlet {

	private static int XUEHAOLENGTH = 8;

	/**
	 * Constructor of the object.
	 */
	public RankServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String falseXh = URLDecoder.decode(request.getParameter("data"));// ͨ��ʱ����м���֮���ѧ��
		String time = URLDecoder.decode(request.getParameter("viewstate"));// �ͻ��˲�����ʱ��
																			// ͨ��
																			// ��Կ���л���
		System.out.println(falseXh);
		String IMEI = URLDecoder
				.decode(request.getParameter("content") == null ? "" : request
						.getParameter("content"));// �û�IMEI
		/**
		 * �����ʵѧ��
		 */
		HashMap<String, String> xhAndXnMap = calculateXh(falseXh, time);
		String realXh = xhAndXnMap.get("xh");// ѧ��
		String realIMEI = getRealIMEI(IMEI, time);
		dbUpdate(IMEI, realXh, realIMEI);
		String xn = xhAndXnMap.get("xn");// ѧ��
		String xq = xhAndXnMap.get("xq");// ѧ��
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		String result = "";
		if (realXh.length() == XUEHAOLENGTH) {
			// ͨ��ѧ����óɼ�
			/**
			 * ���ж��Ƿ��и�רҵ���ļ������� 04113*.txt
			 */
			String root_path = request.getSession().getServletContext()
					.getRealPath(request.getRequestURI());
			root_path = root_path.substring(0, root_path.lastIndexOf("xupt"))
					+ "student_score\\";
			String averageScoreFolderPath = root_path + "averageScore\\";
			File averageScoreFolder = new File(averageScoreFolderPath);
			if (!averageScoreFolder.exists()) {
				averageScoreFolder.mkdirs();
			}
			String filename = realXh.substring(0, 5);// ��ȡ ѧ�õ�ǰ5λ��������רҵ���ļ���
			String professionalFilePath = averageScoreFolderPath + filename
					+ xn + xq + ".txt";// רҵ��ѧ���ļ���Ŀ¼
			File proFessionalFile = new File(professionalFilePath);
			String rank = "";
			/**
			 * ��������ԸĽ��� ֮ǰ��ѯ�ɼ�����Ϣ�� ����û���ɼ�û�в�ѯ���������ڿ�ʼ�ͼ���ƽ���ɼ� �������û��ĳɼ����� txt �С�
			 */
			if (proFessionalFile.exists()) {// רҵ��ѧ��Ŀ¼����˵��
											// ��רҵ�Ѿ�ȫ����ѯ���� Ȼ��
											// �����ļ����Ƿ������ѧ�ţ����������ֱ�Ӳ�ѯ����������ֱ�Ӳ�ѯ��ѧ���ɼ����뼴�ɣ��������
				rank = getRank(realXh, professionalFilePath);
				// ��ѧ�Ų��� �ļ�
				if (rank.equals("")) {
					// �����ѧ�ųɼ����������ļ�
					// �ļ����ܹ�
					String destFile = root_path + "temp\\"
							+ Passport.jiami(realXh);// ��ʱ�ļ��� ��base64���м���
					try {

						if (new File(destFile).exists()) {
							;
						} else {
							String root_filePath = root_path + realXh + ".xml";
							testJiami(root_filePath, realXh);
							if (!new File(destFile).exists()) {
								FileEncryptAndDecrypt.decrypt(root_filePath,
										destFile, realXh.length());
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					float averageScore = new AverageScore(xn, xq, true)
							.getSomeoneAverageScore(destFile);// ��óɼ�
					HashMap<String, Float> noSortAverageScoreMap = new HashMap<String, Float>();
					noSortAverageScoreMap = readTxtFile(professionalFilePath);
					// ����ѧ�źͳɼ�����map��
					noSortAverageScoreMap.put(realXh, averageScore);
					String data = MapToData(noSortAverageScoreMap);
					writeAverageScoreFile(data, proFessionalFile);
					rank = getRank(realXh, professionalFilePath);
				}
			} else {// Ŀ¼�����ڣ�����������רҵ�ĵ�һ���˲�ɼ��ġ� ����Ҫ����ȫ�� ����ƽ���ɼ�
				proFessionalFile.createNewFile();// �����ļ�
				try {
					CalculateAllAverageScore(xn, xq, root_path, filename,
							proFessionalFile, realXh);
					rank = getRank(realXh, professionalFilePath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			HashMap<String, Float> noSortAverageScoreMap = new HashMap<String, Float>();
			noSortAverageScoreMap = readTxtFile(professionalFilePath);
			List<Map.Entry<String, Float>> data = sortMap(noSortAverageScoreMap);// ��map��values
																					// ��������
			RankModel rankModel = new RankModel();
			rankModel.setRank(rank);
			List<RankList> rankLists = new ArrayList<RankList>();
			for (Map.Entry<String, Float> mapping : data) {
				RankList rankList = new RankList();

				rankList.setXh(mapping.getKey());
				rankList.setScore(Float.toString(mapping.getValue()));
				rankList.setName(DBUtil.getName(mapping.getKey()));
				rankLists.add(rankList);
			}
			rankModel.setRankList(rankLists);
			result = JSONUtil.toJSON(rankModel);
		} else {
			result = "error";
		}
		out.write(result);

	}

	private void dbUpdate(String IMEI, String realXh, String realIMEI) {
		/**
		 * �������ݿ�
		 */
		if (!DBUtil.isUserRank(realXh)) {
			System.out.println("����");
			DBUtil.updateUserRank(realXh);
		}
		if (!IMEI.equals("")) {
			DBUtil.updateUserDevIMEI(realXh, realIMEI);
		}
	}

	private void testJiami(String filename, String realXh) {
		if (!FileEncryptAndDecrypt.readFileLastByte(filename, realXh.length())
				.equals(realXh)) {// δ����
			try {
				FileEncryptAndDecrypt.encrypt(filename, realXh);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String getRank(String realXh, String professionalFilePath) {
		/**
		 * ��ȡ�ļ� ��ѯ�Ƿ������ѧ��
		 */
		HashMap<String, Float> noSortAverageScoreMap = readTxtFile(professionalFilePath);
		List<Map.Entry<String, Float>> data = sortMap(noSortAverageScoreMap);// ��map��values
																				// ��������

		String rank = "";
		int swap = data.size();
		// �ҳ���ѧ�ŵ�����, ��������
		for (Map.Entry<String, Float> mapping : data) {
			if (mapping.getKey().equals(realXh)) {
				rank = Integer.toString(swap);
			}
			swap -= 1;
		}
		return rank;
	}

	/**
	 * ��ȡ�ļ� ����õ����� ��ȡ��hashmap �Ƿ������
	 * 
	 * @param filePath
	 */
	public static HashMap<String, Float> readTxtFile(String filePath) {
		HashMap<String, Float> data = new HashMap<String, Float>();
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// ���ǵ������ʽ
				BufferedReader bufferedReader = new BufferedReader(read);
				String txtMement = "";
				String lineTxt = "";
				while ((lineTxt = bufferedReader.readLine()) != null) {
					txtMement += lineTxt;
				}
				read.close();
				// ���ַ���ֱ�ӽ�����hashmap
				String[] xhAndScore = txtMement.split("\\|");
				if (xhAndScore.length == 1) {
					xhAndScore = txtMement.split("\\|");
				}
				String[] xh_array = xhAndScore[0].split(",");
				String[] score_array = xhAndScore[1].split(",");
				for (int i = 0; i < xh_array.length; i++) {
					data.put(xh_array[i], Float.parseFloat(score_array[i]));
				}

			} else {
				System.out.println("�Ҳ���ָ�����ļ�");
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}

		return data;

	}

	/**
	 * �����ѧ������רҵ�������˵ĳɼ������ҽ����mapд���ļ���
	 * 
	 * @param xn
	 * @param xq
	 * @param root_path
	 * @param filename
	 * @param proFessionalFile
	 * @throws Exception
	 */
	private synchronized void CalculateAllAverageScore(String xn, String xq,
			String root_path, String filename, File proFessionalFile,
			String realXh) throws Exception {
		@SuppressWarnings("unchecked")
		ArrayList<File> fileList = GetFilenameList(root_path, filename
				+ "*.xml");

		/**
		 * �Գɼ����н���
		 */
		HashMap<String, String> realFileList = new HashMap<String, String>();
		for (File object : fileList) {
			String path = object.getAbsolutePath();
			String xh = object.getAbsolutePath().substring(
					path.lastIndexOf("\\") + 1, path.lastIndexOf("."));
			String destFile = root_path + "temp\\" + Passport.jiami(xh);// ��ʱ�ļ���
																		// ��base64���м���
			try {
				if (new File(destFile).exists()) {
					;
				} else {
					testJiami(path, xh);
					FileEncryptAndDecrypt.decrypt(path, destFile,
							realXh.length());
				}
				realFileList.put(xh, destFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/**
		 * ��ȡ ��רҵ ��ѧ�� ��ѧ�ڵ������ۺϣ�����������浽�ļ� �ļ��� ����Ϊ רҵ��+ѧ��+ѧ��.txt
		 */
		@SuppressWarnings("unchecked")
		HashMap<String, Float> allAverageScore = new AverageScore(xn, xq,
				realFileList).getAverageScore();// 04113129:90
		String data = MapToData(allAverageScore);
		writeAverageScoreFile(data, proFessionalFile);
	}

	private String MapToData(HashMap<String, Float> allAverageScore) {
		List<Map.Entry<String, Float>> data = sortMap(allAverageScore);// ��map��values
																		// ��������
		String xhs = "";// ����ѧ��
		String scores = "";// ����chengji

		for (Map.Entry<String, Float> mapping : data) {
			if (mapping.getValue() != 0) {
				xhs += mapping.getKey() + ",";
				scores += Float.toString(mapping.getValue()) + ",";
			}
		}
		xhs = xhs.substring(0, xhs.lastIndexOf(","));
		scores = scores.substring(0, scores.lastIndexOf(","));
		String txtData = xhs + "|" + scores;
		return txtData;
	}

	/**
	 * ��map��values ��������
	 * 
	 * @param allAverageScore
	 * @return
	 */
	@SuppressWarnings("unused")
	private synchronized List<Map.Entry<String, Float>> sortMap(
			HashMap<String, Float> allAverageScore) {
		// ���ｫmap.entrySet()ת����list
		@SuppressWarnings("unchecked")
		List<Map.Entry<String, Float>> list = new ArrayList<Map.Entry<String, Float>>(
				(Collection<? extends Entry<String, Float>>) allAverageScore
						.entrySet());
		// Ȼ��ͨ���Ƚ�����ʵ������
		Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
			// ��������
			public int compare(Entry<String, Float> o1, Entry<String, Float> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}

		});
		/*
		 * HashMap<String, Float> map = new Link<String, Float>(); for
		 * (Map.Entry<String, Float> mapping : list) { map.put(mapping.getKey(),
		 * mapping.getValue()); }
		 */
		return list;

	}

	/**
	 * ð�������㷨
	 * 
	 * @param args
	 * @return
	 */
	private synchronized float[] bubbleSort(float[] args) {
		for (int i = 0; i < args.length - 1; i++) {
			for (int j = i + 1; j < args.length; j++) {
				if (args[i] > args[j]) {
					float temp = args[i];
					args[i] = args[j];
					args[j] = temp;
				}
			}
		}
		return args;
	}

	/**
	 * �� ��רҵ ��ѧ�� ��ѧ�ڵĳɼ� �����ۺϳɼ�д���ļ�
	 * 
	 * @param data
	 * @param filePath
	 */
	private void writeAverageScoreFile(String data, File saveFile) {
		try {
			Writer outStream = new OutputStreamWriter(new FileOutputStream(
					saveFile), "UTF-8");
			outStream.write(data);
			outStream.close();
			// ���ļ����м���
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ArrayList GetFilenameList(String baseDIR, String fileName) {
		// �ڴ�Ŀ¼�����ļ�
		// ����չ��Ϊtxt���ļ�
		ArrayList resultList = new ArrayList();
		FileSearcher.findFiles(baseDIR, fileName, resultList);
		return resultList;
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
			if (time.equals("iOS")) {
				realXh = new Passport().base64_decode(falseXh);// �ṩ�� iOS�Ľӿ�
			} else {
				realXh = new Passport().jiemi(falseXh, realTime);
			}

			System.out.println("��ʵ����:" + realXh);
			String[] xhAndXn = new String(realXh).split("\\|");
			xhAndXnMap.put("xh", xhAndXn[0]);
			xhAndXnMap.put("xn", xhAndXn[1]);
			xhAndXnMap.put("xq", xhAndXn[2]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xhAndXnMap;
	}

	/**
	 * �õ���ʵimei
	 * 
	 * @param falseIMEI
	 * @param time
	 * @return
	 */
	private String getRealIMEI(String falseIMEI, String time) {
		String realTime = new Passport().jiemi(time,
				String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
		try {
			Long.parseLong(new Passport().jiemi(falseIMEI, realTime));
			return new Passport().jiemi(falseIMEI, realTime);
		} catch (Exception e) {
			// TODO: handle exception
			return "error";
		}
	}

}
