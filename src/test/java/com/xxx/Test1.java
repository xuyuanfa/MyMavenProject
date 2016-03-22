package com.xxx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Test1 {
	private static Map<String, Object> map = new HashMap();

	/**
	 * @param args
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws ParseException, InterruptedException {
		/*
		 * Class<?> parent = java.io.OutputStream.class; // Class<?> child =
		 * java.io.BufferedOutputStream.class; Object child = new
		 * BufferedOutputStream(null); System.out.println(child.getClass());
		 * System.out.println(parent.isAssignableFrom(child.getClass()));
		 * System.
		 * out.println(OutputStream.class.isAssignableFrom(BufferedOutputStream
		 * .class));
		 * 
		 * double a = 35000; double b = 50000; double count = a / b;
		 * System.out.println(count);
		 */
		// BigDecimal c = BigDecimal.valueOf(50000);
		// BigDecimal d = BigDecimal.valueOf(499999);
		// System.out.println(d.divide(c,BigDecimal.ROUND_UP).intValue());
		// System.out.println(d.intValue());
		//
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// System.out.println(sdf.format(sdf.parse("2015-01-01 23:23:23")));
		//
		// String inta = "0.001";
		// System.out.println(Double.parseDouble(inta));
		//

		// try {
		// // int a[][] = new int[1024*1024*1024][1024*1024*1024];
		// // System.out.println(a.length);
		// new Throwable();
		// } catch (Throwable e) {
		// e.printStackTrace();
		// System.out.println(e.getMessage());
		// System.out.println(e.getMessage());
		// }
		// System.out.println("123");

		// 测试正则表达式
		// testMatches();
		// ThreadTest1 threadTest1 = new Test1().new ThreadTest1("1");
		// ThreadTest1 threadTest2 = new Test1().new ThreadTest1("2");
		// threadTest1.start();;
		// threadTest2.start();
		// Thread.sleep(1000);
		// threadTest2.notifyPrint();

		// System.out.println(String.format("%.2f", 2.0));
		// try {
		// FileOutputStream fs = new FileOutputStream(new
		// File("C:/bank_login/test.txt"));
		// fs.close();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		//System.out.println(getInstance().toString());
//		if("33.jpg".matches("[.*\\.jpg$|11]")){
//			System.out.println(123);
//		}
		
//		String str = ",,,, ";
//		String s[] = str.split(",");
//		System.out.println(s.length );
//		System.out.println(s[0].equals("") );
		
//		int a[] = new int[Integer.MAX_VALUE];
//		System.out.println(a.length);
		
		System.out.println(new SecureRandom().nextLong());
	}

	/**
	 * 内部类，用于实现lzay机制
	 */
	private static class SingletonHolder {
		/** 单例变量 */
		private static Test1 instance = new Test1();
		static {
			System.out.println("123");
		}
	}

	/**
	 * 获取单例对象实例
	 * 
	 * @return 单例对象
	 */
	public static Test1 getInstance() {
		return SingletonHolder.instance;
	}

	class ThreadTest1 extends Thread {
		String str = null;

		public ThreadTest1(String str) {
			this.str = str;
		}

		public void print() throws InterruptedException {
			System.out.println(str);
		}

		public void notifyPrint() {
			synchronized (this) {
				notify();
			}
		}

		@Override
		public void run() {
			try {
				synchronized (this) {
					wait();
				}
				print();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void testMatches() {
		String abc = "001234";
		System.out.println(abc.matches("\\d{6}"));
		int respMsgLen = Integer.parseInt(abc);
		System.out.println(respMsgLen);
	}

}
