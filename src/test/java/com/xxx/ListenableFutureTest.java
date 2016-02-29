package com.xxx;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * Created by hupeng on 2014/9/24.
 */
public class ListenableFutureTest {

	public static void main(String[] args) throws InterruptedException {
		ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

		final ListenableFuture<String> future = pool.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
//				Thread.sleep(1000 * 2);
				return "Task done !";
			}
		});
		Thread.sleep(1000 * 2);
		// future.addListener(new Runnable() {
		// @Override
		// public void run() {
		// try {
		// final String contents = future.get();
		// System.out.println(contents);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// } catch (ExecutionException e) {
		// e.printStackTrace();
		// }
		// }
		// }, MoreExecutors.sameThreadExecutor());

		Futures.addCallback(future, new FutureCallback<String>() {
			@Override
			public void onSuccess(String result) {
				System.out.println(result);
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		});

		Thread.sleep(5 * 1000); // wait for task done

		pool.shutdown();
	}
}