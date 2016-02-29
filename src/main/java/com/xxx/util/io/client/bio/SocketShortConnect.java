package com.xxx.util.io.client.bio;

public interface SocketShortConnect  extends SocketConnect{

	/**
	 * 发送请求接收响应数据，直接返回响应数据，用于短连接
	 * 
	 * @param reqMsgBytes
	 * @param txCode
	 * @return
	 * @throws Exception
	 */
	public byte[] sendAndRcv(byte[] reqMsgBytes, String txCode) throws Exception;

}
