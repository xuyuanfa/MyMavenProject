package com.xxx.util.io.client.bio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * 
 * @author xuyf
 *
 */
public class SocketShortConnectUtil implements SocketShortConnect{
	private static final Logger logger = Logger.getLogger(SocketShortConnectUtil.class);

	private String IPAddress;
	private int port;
	private int headSize;
	
	/**
	 * 短连接中可以不需要报文头长度
	 * @param IPAddress
	 * @param port
	 */
	public SocketShortConnectUtil(String IPAddress, int port) {
		this.IPAddress = IPAddress;
		this.port = port;
	}
	
	public SocketShortConnectUtil(int headSize, String IPAddress, int port) {
		this.IPAddress = IPAddress;
		this.port = port;
		this.headSize = headSize;
	}

	public byte[] sendAndRcv(byte[] reqMsgBytes, String txCode) throws Exception {
		Socket socket = null;
		BufferedOutputStream os = null;
		BufferedInputStream is = null;
		ByteArrayOutputStream bos = null;
		try {
			socket = new Socket(IPAddress, port);
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

}
