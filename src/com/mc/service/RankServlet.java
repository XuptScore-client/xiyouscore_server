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

		String falseXh = URLDecoder.decode(request.getParameter("data"));// 通过时间进行加密之后的学号
		String time = URLDecoder.decode(request.getParameter("viewstate"));// 客户端产生的时间
																			// 通过
																			// 密钥进行机密
		System.out.println(falseXh);
		String IMEI = URLDecoder
				.decode(request.getParameter("content") == null ? "" : request
						.getParameter("content"));// 用户IMEI
		/**
		 * 求出真实学号
		 */
		HashMap<String, String> xhAndXnMap = calculateXh(falseXh, time);
		String realXh = xhAndXnMap.get("xh");// 学号
		String realIMEI = getRealIMEI(IMEI, time);
		dbUpdate(IMEI, realXh, realIMEI);
		String xn = xhAndXnMap.get("xn");// 学年
		String xq = xhAndXnMap.get("xq");// 学期
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		String result = "";
		if (realXh.length() == XUEHAOLENGTH) {
			// 通过学号求得成绩
			/**
			 * 先判断是否有该专业的文件。例如 04113*.txt
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
			String filename = realXh.substring(0, 5);// 截取 学好的前5位，即就是专业的文件名
			String professionalFilePath = averageScoreFolderPath + filename
					+ xn + xq + ".txt";// 专业该学年文件的目录
			File proFessionalFile = new File(professionalFilePath);
			String rank = "";
			/**
			 * 在这里可以改进下 之前查询成绩的信息。 如果用户查成绩没有查询排名，则在开始就计算平均成绩 并将该用户的成绩加入 txt 中。
			 */
			if (proFessionalFile.exists()) {// 专业该学年目录存在说明
											// 该专业已经全部查询过。 然后
											// 查找文件中是否包含该学号，如果包含则直接查询索引，否则直接查询该学生成绩插入即可，求得排名
				rank = getRank(realXh, professionalFilePath);
				// 该学号不在 文件
				if (rank.equals("")) {
					// 计算该学号成绩，并插入文件
					// 文件加密过
					String destFile = root_path + "temp\\"
							+ Passport.jiami(realXh);// 临时文件名 用base64进行加密
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
							.getSomeoneAverageScore(destFile);// 求得成绩
					HashMap<String, Float> noSortAverageScoreMap = new HashMap<String, Float>();
					noSortAverageScoreMap = readTxtFile(professionalFilePath);
					// 将该学号和成绩加入map中
					noSortAverageScoreMap.put(realXh, averageScore);
					String data = MapToData(noSortAverageScoreMap);
					writeAverageScoreFile(data, proFessionalFile);
					rank = getRank(realXh, professionalFilePath);
				}
			} else {// 目录不存在，则表明是这个专业的第一个人查成绩的。 所以要进行全局 计算平均成绩
				proFessionalFile.createNewFile();// 创建文件
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
			List<Map.Entry<String, Float>> data = sortMap(noSortAverageScoreMap);// 对map的values
																					// 进行排序
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
		 * 更新数据库
		 */
		if (!DBUtil.isUserRank(realXh)) {
			System.out.println("更新");
			DBUtil.updateUserRank(realXh);
		}
		if (!IMEI.equals("")) {
			DBUtil.updateUserDevIMEI(realXh, realIMEI);
		}
	}

	private void testJiami(String filename, String realXh) {
		if (!FileEncryptAndDecrypt.readFileLastByte(filename, realXh.length())
				.equals(realXh)) {// 未加密
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
		 * 读取文件 查询是否包含该学号
		 */
		HashMap<String, Float> noSortAverageScoreMap = readTxtFile(professionalFilePath);
		List<Map.Entry<String, Float>> data = sortMap(noSortAverageScoreMap);// 对map的values
																				// 进行排序

		String rank = "";
		int swap = data.size();
		// 找出该学号的排名, 升序排名
		for (Map.Entry<String, Float> mapping : data) {
			if (mapping.getKey().equals(realXh)) {
				rank = Integer.toString(swap);
			}
			swap -= 1;
		}
		return rank;
	}

	/**
	 * 读取文件 排序好的内容 读取到hashmap 是否会乱序
	 * 
	 * @param filePath
	 */
	public static HashMap<String, Float> readTxtFile(String filePath) {
		HashMap<String, Float> data = new HashMap<String, Float>();
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String txtMement = "";
				String lineTxt = "";
				while ((lineTxt = bufferedReader.readLine()) != null) {
					txtMement += lineTxt;
				}
				read.close();
				// 将字符串直接解析成hashmap
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
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}

		return data;

	}

	/**
	 * 计算该学号所在专业的所有人的成绩，并且将这个map写到文件中
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
		 * 对成绩进行解密
		 */
		HashMap<String, String> realFileList = new HashMap<String, String>();
		for (File object : fileList) {
			String path = object.getAbsolutePath();
			String xh = object.getAbsolutePath().substring(
					path.lastIndexOf("\\") + 1, path.lastIndexOf("."));
			String destFile = root_path + "temp\\" + Passport.jiami(xh);// 临时文件名
																		// 用base64进行加密
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
		 * 获取 该专业 该学年 该学期的所有综合，并将这个保存到文件 文件名 命名为 专业号+学年+学期.txt
		 */
		@SuppressWarnings("unchecked")
		HashMap<String, Float> allAverageScore = new AverageScore(xn, xq,
				realFileList).getAverageScore();// 04113129:90
		String data = MapToData(allAverageScore);
		writeAverageScoreFile(data, proFessionalFile);
	}

	private String MapToData(HashMap<String, Float> allAverageScore) {
		List<Map.Entry<String, Float>> data = sortMap(allAverageScore);// 对map的values
																		// 进行排序
		String xhs = "";// 所有学号
		String scores = "";// 所有chengji

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
	 * 对map的values 进行排序
	 * 
	 * @param allAverageScore
	 * @return
	 */
	@SuppressWarnings("unused")
	private synchronized List<Map.Entry<String, Float>> sortMap(
			HashMap<String, Float> allAverageScore) {
		// 这里将map.entrySet()转换成list
		@SuppressWarnings("unchecked")
		List<Map.Entry<String, Float>> list = new ArrayList<Map.Entry<String, Float>>(
				(Collection<? extends Entry<String, Float>>) allAverageScore
						.entrySet());
		// 然后通过比较器来实现排序
		Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
			// 升序排序
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
	 * 冒泡排序算法
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
	 * 将 该专业 该学年 该学期的成绩 所有综合成绩写进文件
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
			// 对文件进行加密
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ArrayList GetFilenameList(String baseDIR, String fileName) {
		// 在此目录中找文件
		// 找扩展名为txt的文件
		ArrayList resultList = new ArrayList();
		FileSearcher.findFiles(baseDIR, fileName, resultList);
		return resultList;
	}

	/**
	 * 首先解密得到正确的时间，然后通过正确的时间解密得到正确的学号 加密算法: 对时间进行base64加密，然后作为密钥对时间进行加密base64
	 * 
	 * @param falseXh
	 *            假的学号
	 * @param time
	 *            时间点
	 * @return 返回真实学号
	 */
	private HashMap<String, String> calculateXh(String falseXh, String time) {
		// TODO Auto-generated method stub
		String realXh = "";
		String realTime = new Passport().jiemi(time,
				String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
		HashMap<String, String> xhAndXnMap = new HashMap<String, String>();// 学号
																			// 学年
																			// 学期
		try {
			if (time.equals("iOS")) {
				realXh = new Passport().base64_decode(falseXh);// 提供给 iOS的接口
			} else {
				realXh = new Passport().jiemi(falseXh, realTime);
			}

			System.out.println("真实数据:" + realXh);
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
	 * 得到真实imei
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
