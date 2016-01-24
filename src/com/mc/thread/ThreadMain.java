package com.mc.thread;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ThreadMain {
	public static Map<String, MyThread> threadPool = new HashMap<String, MyThread>();// 定义一个线程池

	/***
	 * 终止所有线程
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
		// 实例化两个线程获取到随机数为68时就停止进程，并输出
		MyThread thread1 = new MyThreadOne(session, filename, url, threadMain);// 实例化线程1
		MyThread thread2 = new MyThreadTwo(session, filename, url, threadMain);// 实例化线程2
		threadPool.put("thread1", thread1);// 将线程1放入线程池中
		threadPool.put("thread2", thread2);// 将线程2放入线程池中
		thread1.start();// 运行
		thread2.start();
	}

}
