package com.xxx.util.io.client.nio2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * xuyf:NIO客户端，管理Socket连接和操作，一个Client配对一个通道，不能同时发起请求
 * （一是因为未知远程服务器是否会按请求顺序返回结果、二是当前类字段绑定请求对象信息）；
 * 当前设计，一次处理一个请求等待响应后才提交下一个请求，信号量控制；
 * TODO 待加设计（单独类），连续提交请求，通过响应结果中的流水号配置请求；
 */
public class NioTcpClient {

	private final static Logger log = LoggerFactory.getLogger(NioTcpClient.class);
	private final static AtomicLong gClientId = new AtomicLong(0);

	public static int HEAD_SIZE_6 = 6;
	public static int HEAD_SIZE_8 = 8;

	private final ConcurrentHashMap<String, Object> attachments = new ConcurrentHashMap<String, Object>();
	private final List<NioWriteUnit> pendingWriteUnits = new LinkedList<NioWriteUnit>();
	private final long clientId;
	private final NioManager nioManager;
	private final INioHandler handler;
	private String host = "";
	private int port;
	private int headSize;
	private boolean isLongConnect = false;
	private SocketChannel socketChannel;
	private final NioBuffer nioBuffer = new NioBuffer();// 缓存响应结果片段，在处理器中调用

	private CountDownLatch connectLatch = null;
	private Integer receiveLength;// 预接收报文长度
	
	private byte[] rcvResult; // 用于接收长连接回调返回响应结果
	private CountDownLatch cdlRcv = new CountDownLatch(1);

	// 信号量控制不允许并发处理请求，不限并发接收请求
	private final Semaphore semaphore = new Semaphore(1, true);
	
	public NioTcpClient(final NioManager nioManager, INioHandler handler) {
		clientId = gClientId.incrementAndGet();
		this.nioManager = nioManager;
		this.handler = handler;
	}

	final public Object putAttachment(String key, Object value) {
		return attachments.put(key, value);
	}

	final public Object getAttachment(String key) {
		return attachments.get(key);
	}

	private boolean hostIsSame(String other) {
		if (other == null)
			return false;
		else
			return (host.compareTo(other) == 0);
	}

	private boolean isSame(String other, int port) {
		return hostIsSame(other) && this.port == port;
	}

	final public String getHost() {
		return host;
	}

	final public int getPort() {
		return port;
	}

	final public int getHeadSize() {
		return headSize;
	}

	public boolean isLongConnect() {
		return isLongConnect;
	}

	public void setLongConnect(boolean isLongConnect) {
		this.isLongConnect = isLongConnect;
	}

	final public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	final public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	final public boolean isConnected() {
		return this.socketChannel != null && this.socketChannel.isConnected();
	}

	public NioBuffer getNioBuffer() {
		return nioBuffer;
	}

	public Integer getReceiveLength() {
		return receiveLength;
	}

	public void setReceiveLength(Integer receiveLength) {
		this.receiveLength = receiveLength;
	}

	
	public Semaphore getSemaphore() {
		return semaphore;
	}

	/**
	 * 异常需处理事件
	 * 
	 * @param e
	 * @throws Exception
	 */
	final public void handleException(Exception e) throws Exception {
		if (handler != null) {
			handler.onExceptionHappened(this, e);
		}
	}

	/**
	 * 连接成功后需处理事件
	 * 
	 * @throws Exception
	 */
	final public void handleConnected() throws Exception {
		connectLatch.countDown();
		if (handler != null) {
			handler.onConnected(this);
		}
	}

	/**
	 * 断开连接需处理事件
	 * 
	 * @throws Exception
	 */
	final public void handleDisconnected() throws Exception {
		connectLatch = null;
		if (handler != null) {
			handler.onDisconnected(this);
		}
	}

	/**
	 * 接收数据成功后需处理事件
	 * 
	 * @param buffer
	 * @throws Exception
	 */
	final public void handleDataReceived(ByteBuffer buffer) throws Exception {
		if (handler != null) {
			handler.onDataReceived(this, buffer);
		}
	}

	/**
	 * 发送数据成功后需处理事件
	 * 
	 * @param unit
	 * @throws Exception
	 */
	final public void handleWriteSuccess(NioWriteUnit unit) throws Exception {
		NioWriteFuture future = unit.getFuture();
		future.setDone(true);
		future.setSuccess(true);
	}

	/**
	 * 发送数据失败后需处理事件
	 * 
	 * @param unit
	 * @param e
	 * @throws Exception
	 */
	final public void handleWriteFailure(NioWriteUnit unit, Exception e) throws Exception {
		NioWriteFuture future = unit.getFuture();
		future.setDone(true);
		future.setSuccess(false);
		if (handler != null) {
			handler.onExceptionHappened(this, e);
		}
	}

	/**
	 * 长连接或短连接
	 * 
	 * @param host
	 *            域名或IP
	 * @param port
	 *            端口
	 * @param headSize
	 *            报文头长度
	 * @param isLongConnect
	 *            是否长连接
	 */
	final public void connect(String host, int port, int headSize, boolean isLongConnect) {
		if (isSame(host, port)) {// 同一个client不能多次连接同一个服务
			if (socketChannel != null && socketChannel.isConnected()) {
				log.warn("connection of {}:{} is already connected", host, port);
				return;
			}
		} else {
			// 保持长连接时，同一socketAddress只有一个长连接
			if (socketChannel != null && socketChannel.isConnected()) {
				this.disconnect();
			}
		}
		this.host = host;
		this.port = port;
		this.headSize = headSize;
		this.isLongConnect = isLongConnect;
		connectLatch = new CountDownLatch(1);
		nioManager.connect(this);
	}

	/**
	 * 短连接
	 * 
	 * @author xuyf
	 * @param host
	 *            域名或IP
	 * @param port
	 *            端口
	 * @param headSize
	 *            报文头长度
	 */
	final public void connect(String host, int port, int headSize) {
		if (isSame(host, port)) {// 同一个client不能多次连接同一个服务
			if (socketChannel != null && socketChannel.isConnected()) {
				log.warn("connection of {}:{} is already connected", host, port);
				return;
			}
		} else {
			// 保持长连接时，同一socketAddress只有一个长连接
			if (socketChannel != null && socketChannel.isConnected()) {
				this.disconnect();
			}
		}
		this.host = host;
		this.port = port;
		this.headSize = headSize;
		connectLatch = new CountDownLatch(1);
		nioManager.connect(this);
	}

	/**
	 * 保证正确读写之后才关闭，必须控制顺序，例如在INioHandler的onDataReceived()中调用。
	 * 
	 * @author xuyf
	 */
	final public void disconnect() {
		synchronized (pendingWriteUnits) {
			pendingWriteUnits.clear();
		}
		nioManager.disconnect(this);
	}

	/**
	 * 发送数据，字节数组
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	final public NioWriteFuture write(byte[] data) throws IOException, InterruptedException {
		semaphore.acquire();
		if (!isConnected()) {
			if (connectLatch != null) {
				// 等待连接，限定10秒内依然未连接成功则跳出等待
				connectLatch.await(10, TimeUnit.SECONDS);
				if (!isConnected()) {
					throw new IOException("connection is not open");
				}
			} else {
				throw new IOException("connection is not open");
			}
		}
		NioWriteFuture future = new NioWriteFuture(this);
		ByteBuffer buffer = ByteBuffer.wrap(data);
		NioWriteUnit unit = new NioWriteUnit(future, buffer);
		synchronized (pendingWriteUnits) {
			pendingWriteUnits.add(unit);
		}
		nioManager.write(this);
		return future;
	}

	/**
	 * 发送数据，NioBuffer
	 * 
	 * @param buffer
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	final public NioWriteFuture write(NioBuffer buffer) throws IOException, InterruptedException {
		if (!isConnected()) {
			throw new IOException("connection is not open");
		}
		byte[] data = buffer.readBytes(buffer.readableByteSize());
		return write(data);
	}
	
	/**
	 * 发送心跳包数据（远程接口无响应数据）
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	final private void writeHeartData(byte[] data) throws IOException, InterruptedException {
		NioWriteFuture future = new NioWriteFuture(this);
		ByteBuffer buffer = ByteBuffer.wrap(data);
		NioWriteUnit unit = new NioWriteUnit(future, buffer);
		synchronized (pendingWriteUnits) {
			pendingWriteUnits.add(unit);
		}
		nioManager.write(this);
	}
	
	/**
	 * 获取待发送数据的封装对象
	 * 
	 * @return
	 */
	final public NioWriteUnit getOneWriteUnit() {
		synchronized (pendingWriteUnits) {
			if (pendingWriteUnits.size() <= 0)
				return null;
			else
				return pendingWriteUnits.remove(0);
		}
	}

	final public void putBackWriteUnitOnTop(NioWriteUnit unit) {
		synchronized (pendingWriteUnits) {
			pendingWriteUnits.add(0, unit);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("NioTcpClient-" + clientId);
		if (socketChannel != null && socketChannel.isConnected()) {
			try {
				sb.append(" " + socketChannel.getLocalAddress());
			} catch (IOException e) {
				log.error("getLocalAddress", e);
			}
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 启动心跳线程
	 */
	public void startHeartBeatThread() {
		final Timer heartBeatTimer = new Timer();
		TimerTask heartBeatTask = new TimerTask() {
			@Override
			public void run() {
				if (!isConnected()) {
					heartBeatTimer.cancel();
					log.info("Socket通讯已关闭，取现发送心跳包");
					return;
				}
				log.info("Socket通讯发送心跳包");
				try {
					if (getHeadSize() == HEAD_SIZE_8) {
						writeHeartData("00000000".getBytes("UTF-8"));
					} else if (getHeadSize() == HEAD_SIZE_6) {
						writeHeartData("000000".getBytes("UTF-8"));
					}
				} catch (IOException e) {
					log.error("", e);
				} catch (InterruptedException e) {
					log.error("", e);
				}
			}
		};
		heartBeatTimer.schedule(heartBeatTask, 5000, 5000);
	}
	

	public void setRcvResult(byte[] bytes) {
		rcvResult = bytes;
		cdlRcv.countDown();
	}

	public byte[] getRcvResult() throws InterruptedException {
		cdlRcv.await();
		cdlRcv = new CountDownLatch(1);
		semaphore.release();
		byte[] _rcvResult = rcvResult;
		rcvResult = null;
		return _rcvResult;
	}
	
	/**
	 * 获取响应数据
	 * @return
	 * @throws InterruptedException
	 */
	public byte[] readRespData() throws InterruptedException{
		return getRcvResult();
	}

	/**
	 * 发送数据，NioReqRespData
	 */
	final public byte[] writeAndRead(byte[] data) throws IOException, InterruptedException {
		write(data);
		return getRcvResult();
	}
	

	
	
	
	
}
