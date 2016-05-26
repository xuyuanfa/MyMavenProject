package com.xxx.util.io.client.bio4;

public enum SocketLongConnectClientEnum {
	testConnect(new SocketConnectClient("127.0.0.1", 8000, 8, true), "测试");

	SocketConnectClient client;
	String name = null;

	SocketLongConnectClientEnum(SocketConnectClient client, String name) {
		this.client = client;
		this.name = name;
	}

	public SocketConnect getClient() throws Exception {
		if (!client.isConnected()) {
			// 内部已加锁
			client.connect();
		}
		return client;
	}
}
