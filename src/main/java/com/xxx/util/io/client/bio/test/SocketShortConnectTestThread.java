package com.xxx.util.io.client.bio.test;

import com.xxx.util.io.client.bio.ConnectUtil;
import com.xxx.util.io.client.bio.SocketShortConnect;

/**
 * 
 * @author xuyf
 * 
 */
public class SocketShortConnectTestThread extends Thread {
	SocketShortConnect util = (SocketShortConnect) ConnectUtil.getInstance("127.0.0.1", 8000);

	@Override
	public void run() {
		try {
			byte[] bytes = util.sendAndRcv("00000003123".getBytes(), "test");
			System.out.println(new String(bytes));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Thread.sleep(1000);
	}

}
