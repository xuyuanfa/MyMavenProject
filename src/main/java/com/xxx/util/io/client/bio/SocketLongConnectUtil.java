package com.xxx.util.io.client.bio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

/**
 * 
 * @author xuyf 不支持单实例（子多线程）并发，必须一个请求等待一个响应； 如果有子多线程发起请求，需要在外部调用位置加锁处理；
 */
public class SocketLongConnectUtil implements SocketLongConnect {
	private static final Logger logger = Logger.getLogger(SocketLongConnectUtil.class);

	private Socket socket = null;
	private String IPAddress;
	private int port;
	private int headSize;
	private boolean isSocketReading = false;

	private LinkedBlockingQueue<Object[]> threadQueue = new LinkedBlockingQueue<Object[]>();

	public SocketLongConnectUtil(int headSize, String IPAddress, int port) {
		try {
			this.IPAddress = IPAddress;
			this.port = port;
			this.headSize = headSize;
			
			initSockeyConnect();
			// 启动消息队列处理线程
			startMsgQueueThread();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 启动消息队列处理线程
	 */
	private void startMsgQueueThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						// 阻塞队列，当队列为空时，阻塞等待
						Object[] obj = threadQueue.take();
						byte[] reqMsgBytes = (byte[]) obj[0];
						String txCode = (String) obj[1];
						ISocketBIOCallBack callback = (ISocketBIOCallBack) obj[2];
						doSendAndRcv(reqMsgBytes, txCode, callback);
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}).start();
	}

	/**
	 * 长连接中： 用消息队列处理请求； 用回调方法返回结果；
	 */
	public void doSendAndRcv(byte[] reqMsgBytes, String txCode, ISocketBIOCallBack callback) throws Exception {
		BufferedOutputStream os = null;
		BufferedInputStream is = null;
		ByteArrayOutputStream bos = null;
		try {
			initSockeyConnect();
			logger.debug(socket.toString());
			logger.info("Socket通讯发送数据(" + txCode + ")...");
			bos = new ByteArrayOutputStream();
			os = new BufferedOutputStream(socket.getOutputStream());
			os.write(reqMsgBytes);
			os.flush();

			// 防止心跳包同时往Socket通道写数据
			isSocketReading = true;
			
			logger.info("Socket通讯接收数据(" + txCode + ")...");
			is = new BufferedInputStream(socket.getInputStream());
			byte[] tmp = new byte[headSize];
			int readLen = is.read(tmp);
			if (readLen == -1) {
				logger.info("长连接流返回-1，关闭Socket");
				callback.setRcvResult(bos.toByteArray());
				if(os != null)
					os.close();
				if(is != null)
					is.close();
				socket.close();
				return;
			}
			String msg = new String(tmp);
			// logger.info("客户端收到报文长度开头：" + msg);
			if (readLen != headSize || !msg.matches("\\d{" + headSize + "}")) {
				throw new Exception("报文格式有误，非" + headSize + "位报文长度开头");
			}
			bos.write(tmp, 0, readLen);

			int alreadyReadLen = 0;
			int headLen = Integer.parseInt(msg);
			int remainLen = headLen % BUFFER_SIZE;
			int times = headLen / BUFFER_SIZE + (remainLen > 0 ? 1 : 0);
			for (int i = 0; i < times; i++) {
				if (i == times - 1 && remainLen > 0) {
					tmp = new byte[remainLen];
				} else {
					tmp = new byte[BUFFER_SIZE];
				}
				readLen = is.read(tmp);
				if (readLen == -1) {
					logger.info("长连接流返回-1，关闭Socket");
					socket.close();
					return;
				}
				if (readLen != tmp.length) {
					logger.info("从数据流中读取数据不完整");
					// TODO 未确定是否会发生此情况，如果会，下面调整循环处理
					int unreadLen = headLen - alreadyReadLen - readLen;
					remainLen = unreadLen % BUFFER_SIZE;
					times = i + unreadLen / BUFFER_SIZE + (remainLen > 0 ? 1 : 0);
				}
				bos.write(tmp, 0, readLen);
				alreadyReadLen += readLen;
			}

			callback.setRcvResult(bos.toByteArray());
		} catch (Exception e) {
			logger.error("", e);
			callback.setRcvResult(e.getMessage().getBytes());
			throw e;
		} finally {
			isSocketReading = false;
			// 不能关闭流，否则socket会被关闭
			// IOUtils.closeOutputStreamQuietly(os);
			// IOUtils.closeInputStreamQuietly(is);
		}
	}

	/**
	 * 消息队列处理请求
	 */
	public void sendAndRcv(byte[] reqMsgBytes, String txCode, ISocketBIOCallBack callback) throws Exception {
		threadQueue.put(new Object[] { reqMsgBytes, txCode, callback });
	}

	/**
	 * 建立Socket连接，如已连接，返回旧连接
	 * 
	 * @throws Exception
	 */
	private Socket initSockeyConnect() throws Exception {
		if (socket == null || !socket.isConnected() || socket.isClosed()) {
			synchronized (SocketLongConnectUtil.class) {
				if (socket == null || !socket.isConnected() || socket.isClosed()) {
					socket = new Socket(IPAddress, port);
					startHeartBeatThread();
				}
			}
		}
		return socket;
	}
	
	public boolean isConnected(){
		if(socket != null && socket.isConnected()){
			return true;
		}
		return false;
	}
	/**
	 * 启动心跳线程
	 */
	private void startHeartBeatThread() {
		final Timer heartBeatTimer = new Timer();
		TimerTask heartBeatTask = new TimerTask() {
			@Override
			public void run() {
				if(isSocketReading){
					return ;
				}
				
				if (socket == null || !socket.isConnected() || socket.isClosed() || socket.isOutputShutdown()) {
					heartBeatTimer.cancel();
					logger.info("Socket通讯已关闭，取消发送心跳包");
					return;
				}
				logger.info("Socket通讯发送心跳包");
				BufferedOutputStream os = null;
				try {
					os = new BufferedOutputStream(socket.getOutputStream());
					if (headSize == HEAD_SIZE_8) {
						os.write("00000000".getBytes("UTF-8"));
					} else if (headSize == HEAD_SIZE_6) {
						os.write("000000".getBytes("UTF-8"));
					}
					os.flush();
				} catch (UnsupportedEncodingException e) {
					logger.error("", e);
				} catch (IOException e) {
					logger.error("", e);
					try {
						if (os != null) {
							os.close();
						}
					} catch (IOException e1) {

					}
				}
			}
		};
		heartBeatTimer.schedule(heartBeatTask, 10000, 10000);
	}
}
