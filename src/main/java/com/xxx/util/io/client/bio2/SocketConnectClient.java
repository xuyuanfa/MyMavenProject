package com.xxx.util.io.client.bio2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;


/**
 * Socket连接客户端，内部保持一个长连接（懒加载），也可用短连接
 */
public class SocketConnectClient implements SocketConnect {
	private static final Logger logger = Logger.getLogger(SocketConnectClient.class);

	public static int HEAD_SIZE_6 = 6;
	public static int HEAD_SIZE_8 = 8;
	public static int BUFFER_SIZE = 1024;

	private Socket socket = null;
	private String host;
	private int port;
	private int headSize;
	private boolean longConnect;

	private LinkedBlockingQueue<ReqRespData> threadQueue = new LinkedBlockingQueue<ReqRespData>();
	// 处理请求消息队列的工作线程
	private final ExecutorService workerThread = Executors.newSingleThreadExecutor(WorkerThreadFactoryBuilder.newThreadFactory("WorkerThread"));

	// 信号量控制长连接中的读写操作同一时间仅允许一个操作
	private Semaphore semaphore = new Semaphore(1);

	public SocketConnectClient(String host, int port, int headSize, boolean longConnect) {
		try {
			this.host = host;
			this.port = port;
			this.headSize = headSize;
			this.longConnect = longConnect;

			if (longConnect) {
				startMsgQueueWorkThread();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 建立Socket连接
	 * 
	 * @throws Exception
	 */
	private void initSockeyConnect() throws Exception {
		if (socket == null || !socket.isConnected() || socket.isClosed()) {
			synchronized (SocketConnectClient.class) {
				if (socket == null || !socket.isConnected() || socket.isClosed()) {
					socket = new Socket(host, port);
					startHeartBeatThread();
				}
			}
		}
	}

	/**
	 * 工作线程工厂
	 */
	private static class WorkerThreadFactoryBuilder {

		public static ThreadFactory newThreadFactory(String threadPrefix) {
			WorkerThreadFactoryBuilder builder = new WorkerThreadFactoryBuilder(threadPrefix);
			return builder.build();
		}

		private final String threadPrefix;
		private final AtomicLong seq = new AtomicLong(0);

		private WorkerThreadFactoryBuilder(String threadPrefix) {
			this.threadPrefix = threadPrefix;
		}

		final public ThreadFactory build() {
			ThreadFactory factory = new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					Thread t = Executors.defaultThreadFactory().newThread(r);
					t.setName(threadPrefix + "-" + seq.incrementAndGet());
					return t;
				}
			};
			return factory;
		}
	}

	/**
	 * 启动消息队列处理线程
	 */
	private void startMsgQueueWorkThread() {
		workerThread.execute(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						// 阻塞队列，当队列为空时，阻塞等待
						ReqRespData reqRespData = threadQueue.take();
						doSendAndRcv(reqRespData);
					}
				} catch (Exception e) {
					logger.error("workerThread异常退出", e);
					// 重启workerThread工作线程
					startMsgQueueWorkThread();
				}
			}
		});
	}

	@Override
	public byte[] sendAndRcv(byte[] reqMsgBytes, String txCode) throws Exception {
		byte[] bytes = null;
		if (longConnect) {
			ReqRespData reqRespData = new ReqRespData(reqMsgBytes, txCode);
			threadQueue.put(reqRespData);
			bytes = reqRespData.getRespMsgBytes();
		} else {
			bytes = sendAndRcvWithShortConnect(reqMsgBytes, txCode);
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

	/**
	 * 短连接发送请求，直接等待响应数据
	 * 
	 * @param reqMsgBytes
	 * @param txCode
	 * @return
	 * @throws Exception
	 */
	private byte[] sendAndRcvWithShortConnect(byte[] reqMsgBytes, String txCode) throws Exception {
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
	private void sendAndRcvWithShortConnect(ReqRespData reqRespData) throws Exception {
		byte bytes[] = sendAndRcvWithShortConnect(reqRespData.getReqMsgBytes(), reqRespData.getTxCode());
		reqRespData.setRespMsgBytes(bytes);
	}

	/**
	 * 长连接发送请求，用消息队列处理请求； 封装请求对象返回结果；
	 */
	private void doSendAndRcv(ReqRespData reqRespData) {
		BufferedOutputStream os = null;
		BufferedInputStream is = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try {
			semaphore.acquire();
			initSockeyConnect();
			logger.debug(socket.toString());
			logger.info("Socket通讯发送数据(" + reqRespData.getTxCode() + ")...");
			os = new BufferedOutputStream(socket.getOutputStream());
			os.write(reqRespData.getReqMsgBytes());
			os.flush();

			logger.info("Socket通讯接收数据(" + reqRespData.getTxCode() + ")...");
			is = new BufferedInputStream(socket.getInputStream());
			byte[] tmp = new byte[headSize];
			int readLen = is.read(tmp);
			if (readLen == -1) {
				logger.info("长连接流返回-1，关闭Socket");
				reqRespData.setRespMsgBytes(bos.toByteArray());
				if (os != null)
					os.close();
				if (is != null)
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

			reqRespData.setRespMsgBytes(bos.toByteArray());
		} catch (Exception e) {
			logger.error("请求远程接口异常", e);
			reqRespData.setRespMsgBytes(null);
		} finally {
			semaphore.release();
			// 不能关闭流，否则socket会被关闭
			// IOUtils.closeOutputStreamQuietly(os);
			// IOUtils.closeInputStreamQuietly(is);
		}
	}

	@Override
	public void send(ReqRespData reqRespData) throws Exception {
		if (longConnect) {
			threadQueue.put(reqRespData);
		} else {
			sendAndRcvWithShortConnect(reqRespData);
		}
	}

	@Override
	public byte[] receive(ReqRespData reqRespData) throws Exception {
		return reqRespData.getRespMsgBytes();
	}

	@Override
	public boolean isConnected() {
		if (socket != null && socket.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * 连接Socket
	 * @return 连接状态
	 * @throws Exception
	 */
	public boolean connect() throws Exception {
		initSockeyConnect();
		return isConnected();
	}
	
	/**
	 * 启动心跳线程
	 */
	private void startHeartBeatThread() {
		final Timer heartBeatTimer = new Timer();
		TimerTask heartBeatTask = new TimerTask() {
			@Override
			public void run() {
				if (!semaphore.tryAcquire()) {
					// 另有操作正在进行中
					return;
				}

				try {
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
				} finally {
					semaphore.release();
				}
			}
		};
		heartBeatTimer.schedule(heartBeatTask, 10000, 10000);
	}
}
