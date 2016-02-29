package com.xxx.util.io.client.netty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectUtil {

	private final static Logger logger = LoggerFactory.getLogger(ConnectUtil.class);

	private static Map<String, EchoClient> connectUtilMap = new HashMap<String, EchoClient>();

	/**
	 * 获取连接实例
	 * 
	 * @param isLongConnect
	 * @param headSize
	 * @param host
	 * @param port
	 * @return
	 * @throws Exception 
	 */
	public static EchoClient getInstance(String host, int port, int headSize, boolean isLongConnect) throws Exception {
		EchoClient client = connectUtilMap.get(host + port);
		if (isLongConnect && client != null) {
			if(client.getChannel().isOpen()){
				return client;
			}
		}
		
		if (isLongConnect) {
			// 加锁，防止多线程同时建立多个长连接；缩小锁范围；
			synchronized (connectUtilMap) {
				if(client != null && client.getChannel().isOpen()){
					return client;
				}
				client = new EchoClient(host, port, headSize, true);
				client.doOpen();
				connectUtilMap.put(host + port, client);
			}
		} else {
			client = new EchoClient(host, port, headSize, false);
			client.doOpen();
		}
		return client;
	}

}
