package com.xxx.util.io.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for(int i=0;i<10;i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						EchoClient client = ConnectUtil.getInstance("127.0.0.1", 8000, 8, true);
						ByteBuf firstMessage = Unpooled.buffer(EchoClient.SIZE);
						firstMessage.writeBytes("00000003123".getBytes());
						client.getChannel().writeAndFlush(firstMessage);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}).start();
		}
		
	}

}
