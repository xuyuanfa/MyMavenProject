package com.xxx.util.io.client.nio2;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * xuyf:封装NioTcpClient，扩展功能，为写操作添加结果状态；
 * 
 */
public class NioWriteFuture {

	private final NioTcpClient client;
	private boolean isDone = false;
	private boolean isSuccess = false;

	private final ConcurrentLinkedQueue<INioWriteFutureListener> listeners = new ConcurrentLinkedQueue<INioWriteFutureListener>();

	public NioWriteFuture(NioTcpClient client) {
		this.client = client;
	}

	public NioTcpClient getClient() {
		return client;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public void addListener(INioWriteFutureListener listener) {
		listeners.add(listener);
	}

	public void notifyListeners() throws Exception {
		for (INioWriteFutureListener listener : listeners) {
			listener.operationComplete(this);
		}
	}
}
