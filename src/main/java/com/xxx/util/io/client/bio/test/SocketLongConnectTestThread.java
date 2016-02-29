package com.xxx.util.io.client.bio.test;

import java.util.concurrent.CountDownLatch;

import com.xxx.util.io.client.bio.ConnectUtil;
import com.xxx.util.io.client.bio.ISocketBIOCallBack;
import com.xxx.util.io.client.bio.SocketLongConnect;

/**
 * 
 * @author xuyf
 * 
 */
public class SocketLongConnectTestThread extends Thread implements ISocketBIOCallBack {
	final SocketLongConnect util2 = (SocketLongConnect) ConnectUtil.getInstance(true, 8, "127.0.0.1", 8000);
	byte[] rcvResult;
	CountDownLatch cdlRcv = new CountDownLatch(1);

	@Override
	public void run() {
		byte[] bytes2;
		try {
			util2.sendAndRcv("00000003123".getBytes(), "test", this);
			bytes2 = getRcvResult();
			System.out.println(new String(bytes2));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Thread.sleep(1000);
	}

	public void setRcvResult(byte[] bytes) {
		rcvResult = bytes;
		cdlRcv.countDown();
	}

	public byte[] getRcvResult() throws Exception {
		cdlRcv.await();
		cdlRcv = new CountDownLatch(1);
		byte[] _rcvResult = rcvResult;
		rcvResult = null;
		return _rcvResult;
	}

}
