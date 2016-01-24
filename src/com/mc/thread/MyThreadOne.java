package com.mc.thread;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mc.util.ChaXunChengJiUtil;

public class MyThreadOne extends MyThread {
	private boolean isOK = true;
	private String session;
	private String filename;
	private String url;
	private ThreadMain threadMain;

	public MyThreadOne(String session, String filename, String url,
			ThreadMain threadMain) {
		this.session = session;
		this.filename = filename;
		this.url = url;
		this.threadMain = threadMain;
	}

	public void stopThread() {
		this.isOK = false;
	}

	@Override
	public void run() {
		while (isOK) {
			try {
				System.out.println("1并发请求" + filename);
				new ChaXunChengJiUtil().requestHttpGetXML(session, filename,
						url, filename);
				if (new File(filename).exists()) {
					threadMain.stopAll();
					System.out.println("1中断线程");
					break;
				}
			} catch (StringIndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 未评价教师会出现异常

		}
	}
}
