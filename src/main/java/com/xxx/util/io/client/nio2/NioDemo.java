package com.xxx.util.io.client.nio2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioDemo {

	private final static Logger log = LoggerFactory.getLogger(NioDemo.class);


	public static void main(String[] args){
		for (int i = 0; i < 100; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						NioDemo demo = new NioDemo();
						NioTcpClient client = ConnectUtil.getInstance(true, 8, "127.0.0.1", 8000);
						// 想办法去掉锁，用对象封装回调-----已用信号量控制
//						synchronized (client) {
//							client.write("00000003123".getBytes());
//							byte[] respMsgBytes = client.readRespData();
//							System.out.println(new String(respMsgBytes));
//						}
						
						byte[] respMsgBytes = client.writeAndRead("00000003123".getBytes());
						System.out.println(new String(respMsgBytes));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}
