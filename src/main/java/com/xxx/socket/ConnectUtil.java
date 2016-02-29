package com.xxx.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectUtil {
	private static Socket socket = null;
	private static BufferedOutputStream os = null;
	private static BufferedInputStream is = null;

	public synchronized static byte[] sendAndRcv(byte[] reqMsgBytes, String txCode) throws Exception {
		initSockeyConnect();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			System.out.println("Socket通讯发送数据(" + txCode + ")");
			os.write(reqMsgBytes);
			os.flush();

			//Thread.sleep(100);
			System.out.println("Socket通讯接收数据(" + txCode + ")");
			byte[] tmp = new byte[1024];
			int readLen = 0;
			while(is.available() == 0){
				Thread.sleep(10);
			}
			while (is.available() > 0 && (readLen = is.read(tmp)) != -1) {
				// is.read(tmp);
				bos.write(tmp, 0, readLen);
				tmp = new byte[1024];
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 不能关闭流，否则socket会被关闭
			// if(os != null)os.close();
			// if(is != null)is.close();
			// System.out.println(socket.isConnected());
			// System.out.println(socket.isClosed());
		}
		return bos.toByteArray();
	}

	/**
	 * 
	 * @throws Exception
	 */
	public static void initSockeyConnect() throws Exception {
		if (socket == null || !socket.isConnected() || socket.isClosed()) {
			synchronized (ConnectUtil.class) {
				if (socket == null || !socket.isConnected()) {
					String IPAddress = "127.0.0.1";
					int port = 8000;
					socket = new Socket(IPAddress, port);
					os = new BufferedOutputStream(socket.getOutputStream());
					is = new BufferedInputStream(new DataInputStream(socket.getInputStream()));
					// startHeartBeatThread();
				}
			}
		}
	}

	private static void startHeartBeatThread() {
		// 启动心跳线程
		Timer heartBeatTimer = new Timer();
		TimerTask heartBeatTask = new TimerTask() {
			@Override
			public void run() {
				System.out.println("Socket通讯发送心跳包");
				try {
					BufferedOutputStream os = new BufferedOutputStream(socket.getOutputStream());
					os.write("00000000".getBytes());
					os.flush();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		heartBeatTimer.schedule(heartBeatTask, 20000, 20000);
	}

	public static void main(String[] args) {
		try {
			for (int i = 0; i < 1; i++) {
				byte[] bytes = ConnectUtil.sendAndRcv("123".getBytes("UTF-8"), "test");
				System.out.println(new String(bytes));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
}
