package com.xxx.util.io.server;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * NIO服务端
 * 
 * @author xuyf
 */
public class NIOServerWithHeadLen {
	public static int BUFFER_SIZE = 1024;
	protected static int HEAD_LENGTH_6 = 6;
	protected static int HEAD_LENGTH_8 = 8;
	// 通道管理器
	private Selector selector;
	private int HEAD_SIZE = 0;

	/**
	 * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
	 * 
	 * @param port
	 *            绑定的端口号
	 * @throws IOException
	 */
	public void initServer(int headSize, int port) throws IOException {
		this.HEAD_SIZE = headSize;
		// 获得一个ServerSocket通道
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		// 设置通道为非阻塞
		serverChannel.configureBlocking(false);
		// 将该通道对应的ServerSocket绑定到port端口
		serverChannel.socket().bind(new InetSocketAddress(port));
		// 获得一个通道管理器
		this.selector = Selector.open();
		// 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
		// 当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void listen() throws IOException {
		System.out.println("服务端启动成功！");
		// 轮询访问selector
		while (true) {
			// 当注册的事件到达时，方法返回；否则,该方法会一直阻塞
			selector.select();
			// 获得selector中选中的项的迭代器，选中的项为注册的事件
			Iterator ite = this.selector.selectedKeys().iterator();
			while (ite.hasNext()) {
				SelectionKey key = (SelectionKey) ite.next();
				// 删除已选的key,以防重复处理
				ite.remove();
				try {
					// 客户端请求连接事件
					if (key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						// 获得和客户端连接的通道
						SocketChannel channel = server.accept();
						// 设置成非阻塞
						channel.configureBlocking(false);

						// 在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
						channel.register(this.selector, SelectionKey.OP_READ);

						// 获得了可读的事件
					} else if (key.isReadable()) {
						read(key);
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
					// 当客户端异常关闭Socket时，服务端捕捉异常并关闭Socket通道
					// SocketChannel channel = (SocketChannel) key.channel();
					// channel.socket().shutdownInput();
					// channel.socket().shutdownOutput();
					// channel.socket().close();
					// channel.close();
					// 只需要调用key.cancel()即可以关闭通道和Socket
					key.cancel();
				}

			}

		}
	}

	/**
	 * 处理读取客户端发来的信息 的事件
	 * 
	 * @param key
	 * @throws IOException
	 */
	public void read(SelectionKey key) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 服务器可读取消息:得到事件发生的Socket通道
		SocketChannel channel = (SocketChannel) key.channel();
		// 创建读取的缓冲区，读取报文头长度
		ByteBuffer buffer = ByteBuffer.allocate(HEAD_SIZE);
		int len = channel.read(buffer);
		// 读取不到数据，即客户端Socket已关闭
		if (len == -1) {
			System.out.println("关闭Socket");
			channel.close();
			key.cancel();
			return;
		}
		byte[] data = buffer.array();
		String msg = new String(data).trim();
		System.out.println("服务端收到报文长度：" + msg);
		buffer.clear();
		// 判断读取的长度是否为设定的报文头位数，以及是否都是数字，防转换整型出异常
		if (len != HEAD_SIZE || !msg.matches("\\d{" + HEAD_SIZE + "}")) {
			System.out.println("报文格式有误，非" + HEAD_SIZE + "位报文长度");
			channel.close();
			key.cancel();
			return;
		}

		// 转换报文头长度为整型，当为0时是心跳包内容，可忽略
		int reqLen = Integer.parseInt(msg);
		if (reqLen == 0) {
			return;
		}
		bos.write(data);

		// 根据报文头长度申请缓冲区大小，每次读取BUFFER_SIZE，直到剩余部分小于BUFFER_SIZE时仅读取剩余部分
		int allocateSize = BUFFER_SIZE;
		if (reqLen >= BUFFER_SIZE) {
			buffer = ByteBuffer.allocate(BUFFER_SIZE);
		}
		// 计算已读取的字节数，用于读取未满分配的缓冲区时，调整剩余的读取处理
		int alreadyReadLen = 0;
		int remainLen = reqLen % BUFFER_SIZE;
		int times = reqLen / BUFFER_SIZE + (remainLen > 0 ? 1 : 0);
		for (int i = 0; i < times; i++) {
			// 最后的部分数据的处理，只申请剩余部分大小的缓冲区
			if (i == times - 1 && remainLen > 0) {
				allocateSize = remainLen;
				buffer = ByteBuffer.allocate(remainLen);
			}
			len = channel.read(buffer);
			// 读取不到数据，即客户端Socket已关闭
			if (len == -1) {
				System.out.println("中断关闭Socket");
				channel.close();
				key.cancel();
				return;
			}
			// data = buffer.array();
			// msg = new String(data).trim();
			// System.out.println("服务端收到信息：" + msg);
			bos.write(buffer.array());
			// 清除缓冲区，重复使用缓冲区
			buffer.clear();

			// 当读取未满分配的缓冲区时，调整剩余的读取处理
			if (len != allocateSize) {
				System.out.println("从数据流中读取数据不完整");
				// TODO 未确定是否会发生此情况，如果会，下面调整循环处理
				int unreadLen = reqLen - alreadyReadLen - len;
				remainLen = unreadLen % BUFFER_SIZE;
				times = i + unreadLen / BUFFER_SIZE + (remainLen > 0 ? 1 : 0);
			}
			alreadyReadLen += len;
		}
		System.out.println("服务端收到信息：" + new String(bos.toByteArray()));

		// 响应客户端消息
		if (HEAD_SIZE == HEAD_LENGTH_8) {
			channel.write(ByteBuffer.wrap("00000002ok".getBytes("UTF-8")));
		} else if (HEAD_SIZE == HEAD_LENGTH_6) {
			channel.write(ByteBuffer.wrap("000002ok".getBytes("UTF-8")));
		}
		System.out.println("ok");
	}

	/**
	 * 启动服务端测试
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		NIOServerWithHeadLen server = new NIOServerWithHeadLen();
		server.initServer(HEAD_LENGTH_8, 8000);
		server.listen();
	}

}
