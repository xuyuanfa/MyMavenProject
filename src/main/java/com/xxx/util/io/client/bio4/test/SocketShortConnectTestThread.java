package com.xxx.util.io.client.bio4.test;

import com.xxx.util.io.client.bio3.ReqRespData;
import com.xxx.util.io.client.bio3.SocketConnect;
import com.xxx.util.io.client.bio3.SocketConnectClient;


/**
 * 
 * @author xuyf
 * 
 */
public class SocketShortConnectTestThread extends Thread {

	@Override
	public void run() {
		try {
			SocketConnect client = new SocketConnectClient("127.0.0.1", 8000, 8, false);
//			byte[] bytes = client.sendAndRcv("00000003123".getBytes(), "test");
			
			ReqRespData reqRespData = new ReqRespData("00000003123".getBytes(), "test");
			byte[] bytes = client.sendAndRcv(reqRespData);
			
//			client.send(reqRespData);
//			byte[] bytes = client.receive(reqRespData);
			
			System.out.println(new String(bytes));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Thread.sleep(1000);
	}

}
