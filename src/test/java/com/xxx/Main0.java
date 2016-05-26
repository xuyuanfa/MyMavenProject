package com.xxx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main0 {

	static List list = Collections.synchronizedList(new ArrayList());
	public static void main(String[] args) {
		Thread thread1 = new Thread( new Runnable() {
			@Override
			public void run() {
				test1();
			}
		});
		
		thread1.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(list.add(1));
	}

	public synchronized static void test1() {
		//synchronized (list) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("1");
		//}
	}
	
	
}
