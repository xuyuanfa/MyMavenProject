package com.xxx.util.io.client.test;

/**
 * xuyf:为写操作添加监听器，监听是否操作完成；
 * 未实现。
 */
public interface INioWriteFutureListener {
	public void operationComplete(NioWriteFuture future) throws Exception;
}
