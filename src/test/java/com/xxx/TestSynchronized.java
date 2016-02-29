package com.xxx;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 实例证明同一线程共用同一个锁不会阻塞，不同线程共用同一个锁会阻塞
 *
 */
public class TestSynchronized {

//	Lock lock = new ReentrantLock();
	public static void main(String[] args) {

		//测试同步锁的耗时
		testSynchronized();
		
		final TestSynchronized test = new TestSynchronized();
		for(int i = 0; i < 2; i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					test.test1();
				}
			}).start();
		}
	}

	public static void testSynchronized() {
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {

		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);

		start = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			synchronized (Test1.class) {

			}
		}
		end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	public  void test1(){
//		lock.lock();
		synchronized(TestSynchronized.class){
			System.out.println("test1占用TestSynchronized锁");
			test2();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		lock.unlock();
	}
	public synchronized void test2(){
//		lock.lock();
		synchronized(TestSynchronized.class){
			System.out.println("test2可共用TestSynchronized锁");
		}
//		lock.unlock();
	}
}
