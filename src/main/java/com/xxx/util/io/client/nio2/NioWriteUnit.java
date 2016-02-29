package com.xxx.util.io.client.nio2;

import java.nio.ByteBuffer;

/**
 * xuyf:封装有结果状态的写操作对象和写操作数据，作为一个单元对象；
 * 
 */
public class NioWriteUnit {

	private final NioWriteFuture future;
	private final ByteBuffer buffer;

	public NioWriteUnit(NioWriteFuture future, ByteBuffer buffer) {
		this.future = future;
		this.buffer = buffer;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public NioWriteFuture getFuture() {
		return future;
	}

}
