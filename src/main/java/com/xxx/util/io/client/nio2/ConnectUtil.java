package com.xxx.util.io.client.nio2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

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
	private static NioTcpClient getInstance(String host, int port, int headSize) throws Exception {
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
	private static NioTcpClient getInstance(String host, int port, int headSize, boolean isLongConnect) throws Exception {
		final NioManager nioManager = NioManager.getInstance();
		final CountDownLatch waitConnected = new CountDownLatch(1);
		INioHandler handler = new INioHandler() {
			@Override
			public void onConnected(NioTcpClient client) throws Exception {
				logger.info(client + " was connected");
				waitConnected.countDown();
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
				if (client.getNioBuffer().readableByteSize() >= client.getReceiveLength()) {
					client.setRcvResult(client.getNioBuffer().readBytes(client.getReceiveLength()));
					client.setReceiveLength(null);
					if (!client.isLongConnect()) {
						client.disconnect();
					}
				}
			}

			@Override
			public void onExceptionHappened(NioTcpClient client, Exception e) throws Exception {
				logger.error("", e);
				connectUtilMap.remove(client.getHost() + client.getHost());
				client.getNioBuffer().clear();
				client.disconnect();
				client.setRcvResult(null);
			}

		};

		NioTcpClient socket = new NioTcpClient(nioManager, handler);
		socket.connect(host, port, headSize, isLongConnect);
		waitConnected.await();
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
	public static NioTcpClient getInstance(boolean isLongConnect, int headSize, String host, int port) throws Exception {

		if (isLongConnect) {
			NioTcpClient client = connectUtilMap.get(host + port);
			// 双重校验锁，多线程并发同时执行通过了第一层的判断，则必须在下面加锁而且内部再次判断
			// 连接正常则返回，否则继续执行创建对象，创建的对象在保存Map时会覆盖旧的对象
			if (client == null || !client.isConnected()) {
				synchronized (connectUtilMap) {
					client = connectUtilMap.get(host + port);
					if (client == null || !client.isConnected()) {
						// 因为是异步建立连接，此处返回的Socket可能未建立连接-----已处理
						client = getInstance(host, port, headSize, true);
						connectUtilMap.put(host + port, client);
					}
				}
			}
			return client;
		} else {
			return getInstance(host, port, headSize);
		}
	}

}
