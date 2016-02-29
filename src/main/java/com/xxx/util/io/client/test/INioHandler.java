package com.xxx.util.io.client.test;

import java.nio.ByteBuffer;

/**
 * xuyf:封装所有的细节，由该接口的具体实现再次封装，提供给使用者；
 * 使用者只面向该接口，以及业务数据的具体格式也在该接口的具体实现中。
 */
public interface INioHandler {
	
	public void onConnected(NioTcpClient client) throws Exception;
	public void onDisconnected(NioTcpClient client) throws Exception;
	public void onDataReceived(NioTcpClient client, ByteBuffer buffer) throws Exception;
	public void onExceptionHappened(NioTcpClient client, Exception e) throws Exception;
}
