package com.xxx.util.io.client.bio3;

public interface SocketConnect {
	/*
	 * 思路：
	 * 长短连接实现在同一个类中，提供两种调用发送数据的方法：
	 * a.封装发送数据和等待接收数据，短连接直接等待，长连接通过锁存器控制
	 * b.分开发送数据和接收数据，长短连接都采用回调，通过锁存器控制---创建请求响应数据对象，代替回调处理
	 * 
	 * 长连接：
	 * 采用阻塞消息队列（FIFO），接收发送请求，确保按顺序处理请求；
	 * 采用工作线程（消费者），利用线程池保证工作线程稳定不挂，处理队列中的请求；
	 * 多个长连接客户端，每一个长连接客户端对应一个工作线程，实现延迟创建多个长连接客户端，解决单一长连接客户端超时等待阻塞问题；
	 * 回调机制，封装发送请求和回调对象，确保工作线程的处理响应结果对应发起请求；
	 * 
	 * 短连接：
	 * 每个请求创建短连接客户端，直接处理发送请求，同步等待响应结果；
	 * 
	 * 关键点：封装请求响应对象，取消回调机制
	 */
	
	/**
	 * 发送请求接收响应数据（短连接直接等待，长连接通过锁存器控制响应数据）
	 */
	public byte[] sendAndRcv(byte[] reqMsgBytes, String txCode) throws Exception;

	/**
	 * 发送请求接收响应数据（短连接直接等待，长连接通过锁存器控制响应数据）
	 */
	public byte[] sendAndRcv(ReqRespData reqRespData) throws Exception;

	/**
	 * 发送请求（通过锁存器控制响应数据）
	 */
	public void send(ReqRespData reqRespData) throws Exception;

	/**
	 * 获取响应数据（通过锁存器控制响应数据）
	 */
	public byte[] receive(ReqRespData reqRespData) throws Exception;

	/**
	 * 判断长连接中Socket对象是否正常连接
	 */
	public boolean isConnected();

	/**
	 * 建立长连接Socket，返回连接结果
	 */
	public Boolean connect() throws Exception;
}
