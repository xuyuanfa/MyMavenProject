package com.xxx.demo.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MyTimer {
	public static void main(String[] args) {
		final List<String> list = new ArrayList<String>();
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(list != null && !list.isEmpty()){
					timer.cancel();
					System.out.println("finished timer");
					return;
				}
				list.add("test");
				System.out.println(list.size());
			}
		}, 10, 1000);
		System.out.println("finished main thread");
	}
}
