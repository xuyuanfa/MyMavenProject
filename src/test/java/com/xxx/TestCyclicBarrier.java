package com.xxx;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class TestCyclicBarrier {

	public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
		CyclicBarrier barrier = new CyclicBarrier(2, new Runnable() {
			@Override
			public void run() {
				System.out.println("finished");
			}
		});
		barrier.await();
		System.out.println("1");
	}

}
