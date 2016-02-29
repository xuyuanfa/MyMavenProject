package com.xxx.util.io.client.bio;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author xuyf
 * 
 */
public class ConnectUtil {

	private static Map<String, SocketConnect> connectUtilMap = new HashMap<String, SocketConnect>();

	/**
	 * 获取短连接实例，短连接中可以不需要报文头长度
	 * 
	 * @param isLongConnect
	 * @param IPAddress
	 * @param port
	 * @return
	 */
	public static SocketConnect getInstance(String IPAddress, int port) {
		return getInstance(false, 0, IPAddress, port);
	}

	/**
	 * 获取连接实例
	 * 
	 * @param isLongConnect
	 * @param headSize
	 * @param IPAddress
	 * @param port
	 * @return
	 */
	public static SocketConnect getInstance(boolean isLongConnect, int headSize, String IPAddress, int port) {
		
		if (isLongConnect) {
			SocketLongConnect socketLongConnectClient = (SocketLongConnect) connectUtilMap.get(IPAddress + port);
			// 双重校验锁，多线程并发同时执行通过了第一层的判断，则必须在下面加锁而且内部再次判断
			// 连接正常则返回，否则继续执行创建对象，创建的对象在保存Map时会覆盖旧的对象
			if (socketLongConnectClient == null || !socketLongConnectClient.isConnected()) {
				synchronized (connectUtilMap) {
					socketLongConnectClient = (SocketLongConnect) connectUtilMap.get(IPAddress + port);
					if(socketLongConnectClient == null || !socketLongConnectClient.isConnected()){
						socketLongConnectClient = new SocketLongConnectUtil(headSize, IPAddress, port);
						connectUtilMap.put(IPAddress + port, socketLongConnectClient);
					}
				}
			}
			return socketLongConnectClient;
		} else {
			return new SocketShortConnectUtil(headSize, IPAddress, port);
		}
	}

}
