package com.xxx.util.io.client.bio3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * Socket连接客户端，内部保持一个长连接（懒加载），也可用短连接
 */
public class SocketShortConnectImpl {
	private static final Logger logger = Logger.getLogger(SocketShortConnectImpl.class);

	public static int BUFFER_SIZE = 1024;

	private String host;
	private int port;
	private int headSize;

	public SocketShortConnectImpl(String host, int port, int headSize) {
		try {
			this.host = host;
			this.port = port;
			this.headSize = headSize;

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 短连接发送请求，直接等待响应数据
	 * 
	 * @param reqMsgBytes
	 * @param txCode
	 * @return
	 * @throws Exception
	 */
	public byte[] sendAndRcvWithShortConnect(byte[] reqMsgBytes, String txCode) throws Exception {
		Socket socket = null;
		BufferedOutputStream os = null;
		BufferedInputStream is = null;
		ByteArrayOutputStream bos = null;
		try {
			socket = new Socket(host, port);
			logger.debug(socket.toString());
			logger.info("Socket通讯发送数据(" + txCode + ")...");
			bos = new ByteArrayOutputStream();
			os = new BufferedOutputStream(socket.getOutputStream());
			os.write(reqMsgBytes);
			os.flush();
			socket.shutdownOutput();

			logger.info("Socket通讯接收数据(" + txCode + ")...");
			byte[] tmp = new byte[BUFFER_SIZE];
			is = new BufferedInputStream(socket.getInputStream());
			int readLen = 0;
			while ((readLen = is.read(tmp)) != -1) {
				bos.write(tmp, 0, readLen);
				tmp = new byte[BUFFER_SIZE];
			}
			socket.shutdownInput();
		} catch (Exception e) {
			logger.error("", e);
			throw e;
		} finally {
			if (os != null) {
				os.close();
			}
			if (is != null) {
				is.close();
			}
			if (socket != null) {
				socket.close();
			}
		}
		return bos.toByteArray();
	}

	/**
	 * 短连接发送请求，直接等待响应数据，通过封装请求响应对象返回响应结果
	 */
	public void sendAndRcvWithShortConnect(ReqRespData reqRespData) throws Exception {
		byte bytes[] = sendAndRcvWithShortConnect(reqRespData.getReqMsgBytes(), reqRespData.getTxCode());
		reqRespData.setRespMsgBytes(bytes);
	}

}
