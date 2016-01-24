package com.mc.thread;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ThreadMain {
	public static Map<String, MyThread> threadPool = new HashMap<String, MyThread>();// ����һ���̳߳�

	/***
	 * ��ֹ�����߳�
	 */
	public static void stopAll() {
		Iterator<MyThread> threads = threadPool.values().iterator();
		while (threads.hasNext()) {
			threads.next().stopThread();
		}
		threadPool.clear();
	}

	public static void thread_request(String session, String filename,
			String url, ThreadMain threadMain) {
		// ʵ���������̻߳�ȡ�������Ϊ68ʱ��ֹͣ���̣������
		MyThread thread1 = new MyThreadOne(session, filename, url, threadMain);// ʵ�����߳�1
		MyThread thread2 = new MyThreadTwo(session, filename, url, threadMain);// ʵ�����߳�2
		threadPool.put("thread1", thread1);// ���߳�1�����̳߳���
		threadPool.put("thread2", thread2);// ���߳�2�����̳߳���
		thread1.start();// ����
		thread2.start();
	}

}
