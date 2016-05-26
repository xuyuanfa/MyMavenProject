package com.xxx;

import java.awt.Color;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

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
		
//		System.out.println(new SecureRandom().nextLong());

//		System.out.println("123+456".split("[+]").length);
//		System.out.println("123+456".split("\\+").length);
//		System.out.println(Test1.class.getResource("/").getPath());
		
//		SynchronousQueue<String> syncProgress=new SynchronousQueue<>(true);
//		System.out.println(syncProgress.size());
//		syncProgress.put("123");
//		System.out.println(syncProgress.size());
		
//		List<String> list = new ArrayList<String>(Arrays.asList("","","","",""));
//		System.out.println(list.size());
//		Collections.fill(list, "0");
//		System.out.println(list.get(0));
//		System.out.println(list.size());
//		list.subList(0, 3);
//		System.out.println(list.size());

//		Set<String> set = new HashSet();
//		System.out.println(set.size());
//		set.add(null);
//		System.out.println(set.size());
//		
//		List<String> list = Collections.nCopies(10,"0");
//		System.out.println(list.size());
//		list.add(null);
//		System.out.println(list.size());
		
//		Test1 test1 = SingletonHolder.instance;
//		System.out.println(test1.hashCode());
		
//		System.out.println(String.valueOf(Calendar.getInstance().getTimeInMillis()).substring(4));
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
//		Date startDate = sdf.parse("201512");
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(startDate);
//		calendar.add(Calendar.MONTH, 1);
//		Date endDate = calendar.getTime();
//		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd HHmmSS");
//		System.out.println(sdf2.format(startDate));
//		System.out.println(sdf2.format(endDate));
		
//		System.out.println(new Date().getTime());
//		System.out.println(System.currentTimeMillis());
//		System.out.println();
		
//		BigDecimal i = BigDecimal.valueOf(1.45);
//		BigDecimal j = BigDecimal.valueOf(1.55);
//		System.out.println(i.add(j));
		
//		Optional<Integer> op = Optional.absent();
//		System.out.println(op.isPresent());
//		ImmutableSet.of("a", "b", "c");
//		ImmutableMap.of("a", 1, "b", 2, 2,"d");
//
//		ImmutableSet.builder()
//		            .add(new Color(0, 191, 255))
//		            .build();

		System.out.println("阿萨德发松岛枫".length());
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
