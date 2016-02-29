package com.xxx.util.io.client.bio;

public interface SocketLongConnect extends SocketConnect{


	/**
	 * 发送请求接收响应数据，通过回调方法返回响应数据，用于长连接
	 * 
	 * @param reqMsgBytes
	 * @param txCode
	 * @param callback
	 * @throws Exception
	 */
	public void sendAndRcv(byte[] reqMsgBytes, String txCode, ISocketBIOCallBack callback) throws Exception;

	public boolean isConnected();
	
}
