package com.xxx.util.io.client.bio2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 请求响应数据对象
 */
public class ReqRespData {
	public byte[] reqMsgBytes;
	public String txCode;
	public byte[] respMsgBytes;
	CountDownLatch countDownLatch = new CountDownLatch(1);

	public ReqRespData(byte[] reqMsgBytes, String txCode) {
		this.reqMsgBytes = reqMsgBytes;
		this.txCode = txCode;
	}

	public byte[] getRespMsgBytes() throws InterruptedException {
		if (!countDownLatch.await(60, TimeUnit.SECONDS)) {
			throw new InterruptedException("等待响应数据超时");
		}
		return respMsgBytes;
	}

	public void setRespMsgBytes(byte[] respMsgBytes) {
		this.respMsgBytes = respMsgBytes;
		countDownLatch.countDown();
	}

	public byte[] getReqMsgBytes() {
		return reqMsgBytes;
	}

	public String getTxCode() {
		return txCode;
	}

}