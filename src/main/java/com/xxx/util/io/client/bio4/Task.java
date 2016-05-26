package com.xxx.util.io.client.bio4;

import java.util.concurrent.Callable;

public class Task implements Callable<byte[]> {
	SocketConnect client;
	public byte[] reqMsgBytes;
	public String txCode;

	public Task(byte[] reqMsgBytes, String txCode, SocketConnect client) {
		this.reqMsgBytes = reqMsgBytes;
		this.txCode = txCode;
		this.client = client;
	}

	@Override
	public byte[] call() throws Exception {
		return client.sendAndRcv(this.reqMsgBytes, this.txCode);
	}

}
