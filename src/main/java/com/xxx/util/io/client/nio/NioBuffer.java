package com.xxx.util.io.client.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * xuyf:个人理解，用了列表的目的： 1、分离存储报文长度和实体报文； 2、可预先接收读取响应数据，后续再由响应方法从列表中读取响应数据；
 */
public class NioBuffer {

	// TODO xuyf:此处没有加锁，除非外部控制调用此方法是安全调用，否则会有顺序异常；事实上，该方法未被调用；
	public static NioBuffer combine(byte[] data1, byte[] data2) {
		NioBuffer nioBuffer = new NioBuffer();
		nioBuffer.addBuffer(data1);
		nioBuffer.addBuffer(data2);
		return nioBuffer;
	}

	private List<ByteBuffer> bufferList = new LinkedList<ByteBuffer>();

	public int readableByteSize() {
		int size = 0;
		for (int index = 0; index < bufferList.size(); index++) {
			size += bufferList.get(index).remaining();
		}
		return size;
	}

	public byte[] readBytes(int length) throws IOException {
		if (length > readableByteSize()) {
			throw new IOException("read length exceeded NioBuffer readableByteSize");
		}
		byte[] bytes = new byte[length];
		int readIndex = 0;
		int remain = length;
		// xuyf:从头移除列表，会影响性能；代码中有他的设计必要；改ArrayList为LinkedList；
		while (true) {
			ByteBuffer buffer = bufferList.get(0);
			final int readLength = Math.min(remain, buffer.remaining());
			buffer.get(bytes, readIndex, readLength);
			readIndex += readLength;
			remain -= readLength;
			if (buffer.remaining() <= 0)
				bufferList.remove(0);
			if (remain == 0)
				break;
		}
		return bytes;
	}

	public void addBuffer(ByteBuffer buffer) {
		bufferList.add(buffer);
	}

	public void addBuffer(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		bufferList.add(buffer);
	}

	public void clear() {
		bufferList.clear();
	}
}
