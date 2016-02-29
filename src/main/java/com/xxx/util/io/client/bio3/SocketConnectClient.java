package com.xxx.util.io.client.bio3;

import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * Socket连接客户端
 */
public class SocketConnectClient implements SocketConnect {
	private static final Logger logger = Logger.getLogger(SocketConnectClient.class);

	private boolean longConnect;

	private SocketLongConnectImpl socketLongConnect = null;
	private SocketShortConnectImpl socketShortConnect = null;

	public SocketConnectClient(String host, int port, int headSize, boolean longConnect) {
		try {
			this.longConnect = longConnect;

			if (longConnect) {
				socketLongConnect = new SocketLongConnectImpl(host, port, headSize);
			} else {
				socketShortConnect = new SocketShortConnectImpl(host, port, headSize);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public byte[] sendAndRcv(byte[] reqMsgBytes, String txCode) throws Exception {
		byte[] bytes = null;
		if (longConnect) {
			ReqRespData reqRespData = new ReqRespData(reqMsgBytes, txCode);
			socketLongConnect.getThreadQueue().put(reqRespData);
			bytes = reqRespData.getRespMsgBytes();
		} else {
			bytes = socketShortConnect.sendAndRcvWithShortConnect(reqMsgBytes, txCode);
		}
		return bytes;
	}

	@Override
	public byte[] sendAndRcv(ReqRespData reqRespData) throws Exception {
		if (reqRespData == null) {
			throw new Exception("请求对象为null");
		}
		return sendAndRcv(reqRespData.getReqMsgBytes(), reqRespData.getTxCode());
	}

	@Override
	public void send(ReqRespData reqRespData) throws Exception {
		if (longConnect) {
			socketLongConnect.getThreadQueue().put(reqRespData);
		} else {
			socketShortConnect.sendAndRcvWithShortConnect(reqRespData);
		}
	}

	@Override
	public byte[] receive(ReqRespData reqRespData) throws Exception {
		return reqRespData.getRespMsgBytes();
	}

	@Override
	public boolean isConnected() {
		if (socketLongConnect != null) {
			Socket socket = socketLongConnect.getSocket();
			if (socket != null && socket.isConnected()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Boolean connect() throws Exception {
		if (socketLongConnect != null) {
			return socketLongConnect.connect();
		}
		return null;
	}
}
