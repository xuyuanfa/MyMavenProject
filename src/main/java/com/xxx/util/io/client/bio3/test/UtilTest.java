package com.xxx.util.io.client.bio3.test;

/**
 * 
 * @author xuyf
 * 
 */
public class UtilTest {
	public static void main(String[] args) {
		// ConnectUtil util = ConnectUtil.getInstance("127.0.0.1", 8000);
		try {
			for (int i = 0; i < 1; i++) {
				// byte[] bytes = util.sendAndRcv("123".getBytes(), "test");
				// System.out.println(new String(bytes));
				new SocketShortConnectTestThread().start();
			}
			for (int i = 0; i < 1; i++) {
				new SocketLongConnectTestThread().start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
