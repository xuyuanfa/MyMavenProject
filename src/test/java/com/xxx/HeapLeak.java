package com.xxx;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class HeapLeak {
	static Map map = new HashMap();
	static Map map2 = new HashMap();
	public static void main(String[] args) {
		/*
		 * try { ArrayList list = new ArrayList(); while (true) { list.add(new
		 * HeapLeak.method()); } } catch (Throwable e) {
		 * System.out.println("123"); System.out.println(e.getMessage());
		 * e.fillInStackTrace(); }
		 */

		ScheduledExecutorService ses = Executors.newScheduledThreadPool(5);
		ses.execute(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
//						list.add(new HeapLeak.method());
						map.put(UUID.randomUUID(),UUID.randomUUID());
					}
				} catch (Throwable e) {
					System.out.println("第一个线程挂了");
					System.out.println(e.getMessage());
					e.fillInStackTrace();
				}
			}
		});
		ses.execute(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
//						list.add(new HeapLeak.method());
						Thread.sleep(100);
						System.out.println("2...");
						map2.put(UUID.randomUUID(),UUID.randomUUID());
					}
				} catch (Throwable e) {
					System.out.println("第二个线程挂了");
					System.out.println(e.getMessage());
					e.fillInStackTrace();
				}
			}
		});
		ses.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					System.out.println("3");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	static class method {

	}
}
