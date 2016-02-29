package com.xxx.util.io.client.bio3.test;

import com.xxx.util.io.client.bio3.ReqRespData;
import com.xxx.util.io.client.bio3.SocketConnect;
import com.xxx.util.io.client.bio3.SocketLongConnectClientEnum;

/**
 * 
 * @author xuyf
 * 
 */
public class SocketLongConnectTestThread extends Thread {
	@Override
	public void run() {
		try {
			SocketConnect client = SocketLongConnectClientEnum.testConnect.getClient();
//			byte[] bytes2 = client.sendAndRcv("00000003123".getBytes(), "test");
			
			ReqRespData reqRespData = new ReqRespData("00000003123".getBytes(), "test");
			byte[] bytes2 = client.sendAndRcv(reqRespData);
			
//			client.send(reqRespData);
//			byte[] bytes2 = client.receive(reqRespData);
			
			System.out.println(new String(bytes2));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Thread.sleep(1000);
	}


}
