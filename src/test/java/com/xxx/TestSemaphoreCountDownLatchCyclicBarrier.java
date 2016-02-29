package com.xxx;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class TestSemaphoreCountDownLatchCyclicBarrier {
	public static void main(String[] args) {
		// 测试消耗时间
		testSemaphore();
		testCountDownLatch();
		testCyclicBarrier();
	}
	
	
	public static void testSemaphore(){
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {

		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);

		// 允许同时放行的线程数
		start = System.currentTimeMillis();
		Semaphore semaphore = new Semaphore(1);
		for (int i = 0; i < 10000000; i++) {
			try {
				semaphore.acquire();
				semaphore.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	

	public static void testCountDownLatch(){
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {

		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);

		// 计数器减一，计数器的数量为0时，放行，不可复用
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			try {
				// 不可复用，必须每次创建
				CountDownLatch countDownLatch = new CountDownLatch(1);
				countDownLatch.countDown();
				countDownLatch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	public static void testCyclicBarrier(){
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {

		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);

		// 线程就位达到设定的数目时，同时放行，可复用
		start = System.currentTimeMillis();
		CyclicBarrier cyclicBarrier = new CyclicBarrier(1);
		for (int i = 0; i < 10000000; i++) {
			try {
				cyclicBarrier.await();
			} catch (BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		end = System.currentTimeMillis();
		System.out.println(end - start);
	}
}
