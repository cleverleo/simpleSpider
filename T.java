package net.cleverleo.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class T {
	private static ExecutorService list = Executors.newFixedThreadPool(5);
	private static int runing = 0;

	public static void add(ThreadRun task) {
		T.list.submit(task);
	}

	public static void shutdown() {
		T.list.shutdown();
	}

	public static synchronized void lockInc() {
		T.runing++;
	}

	public static synchronized void lockDec() {
		T.runing--;
	}

	public static void waitDone() {
		try {
			Thread.sleep(100);
			while (T.runing != 0) {
				Thread.sleep(100);
			}
		} catch (InterruptedException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}

	}

	public static int runCount() {
		return T.runing;
	}
}
