package com.xxx.util.io.client.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectUtil {

	private final static Logger logger = LoggerFactory.getLogger(ConnectUtil.class);


	// public static void main(String[] args) throws Exception {
	// NioTcpClient socket = getInstance("127.0.0.1", 8000, HEAD_SIZE_8, false);
	// socket.write("00000003123".getBytes());
	// }

	/**
	 * 获取短连接实例
	 * 
	 * @param host
	 * @param port
	 * @param headSize
	 * @return
	 * @throws IOException
	 */
	private static NioTcpClient getInstance(String host, int port, int headSize) throws IOException {
		return getInstance(host, port, headSize, false);
	}

	/**
	 * 获取连接实例，指定长短连接
	 * 
	 * @param host
	 * @param port
	 * @param headSize
	 * @param isLongConnect
	 * @return
	 * @throws IOException
	 */
	private static NioTcpClient getInstance(String host, int port, int headSize, boolean isLongConnect) throws IOException {
		final NioManager nioManager = new NioManager();

		INioHandler handler = new INioHandler() {
			@Override
			public void onConnected(NioTcpClient client) throws Exception {
				logger.info(client + " was connected");
				if (client.isLongConnect()) {
					// 发送心跳包
					client.startHeartBeatThread();
				}
			}

			@Override
			public void onDisconnected(NioTcpClient client) throws Exception {
				client.getNioBuffer().clear();
				nioManager.shutdown();
				logger.info(client + " was disconnected");
			}

			@Override
			public void onDataReceived(NioTcpClient client, ByteBuffer buffer) throws Exception {
				logger.info("received " + buffer.remaining() + " bytes by " + client);
				client.getNioBuffer().addBuffer(buffer);

				if (client.getReceiveLength() == null && client.getNioBuffer().readableByteSize() >= client.getHeadSize()) {
					byte[] lengthBytes = client.getNioBuffer().readBytes(client.getHeadSize());
					client.setReceiveLength(Integer.parseInt(new String(lengthBytes)));
				}
				if(client.getNioBuffer().readableByteSize() >= client.getReceiveLength()){
					client.getCallback().setRcvResult(client.getNioBuffer().readBytes(client.getReceiveLength()));
					client.setReceiveLength(null);
					if (!client.isLongConnect()) {
						client.disconnect();
					}
				}
			}

			@Override
			public void onExceptionHappened(NioTcpClient client, Exception e) throws Exception {
				logger.error("", e);
				client.disconnect();
			}

			
		};

		NioTcpClient socket = new NioTcpClient(nioManager, handler);
		socket.connect(host, port, headSize, isLongConnect);
		return socket;
	}

	private static Map<String, NioTcpClient> connectUtilMap = new HashMap<String, NioTcpClient>();

	/**
	 * 获取连接实例
	 * 
	 * @param isLongConnect
	 * @param headSize
	 * @param host
	 * @param port
	 * @return
	 * @throws IOException
	 */
	public static NioTcpClient getInstance(boolean isLongConnect, int headSize, String host, int port) throws IOException {
		if (isLongConnect && connectUtilMap.get(host + port) != null) {
			return connectUtilMap.get(host + port);
		}
		NioTcpClient client = null;
		try {
			if (isLongConnect) {
				// 加锁，防止多线程同时建立多个长连接；缩小锁范围；
				synchronized (connectUtilMap) {
					if(connectUtilMap.get(host + port) != null){
						return connectUtilMap.get(host + port);
					}
					client = getInstance(host, port, headSize, true);
					connectUtilMap.put(host + port, client);
				}
			} else {
				client = getInstance(host, port, headSize);
			}
		} catch (IOException e) {
			logger.error("", e);
			throw e;
		}
		return client;
	}

}
