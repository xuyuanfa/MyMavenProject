/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.xxx.util.io.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
	private final static Logger logger = LoggerFactory.getLogger(EchoClientHandler.class);
    private final ByteBuf firstMessage;
    private int headSize;
    private boolean isLongConnect;

    /**
     * Creates a client-side handler.
     */
    public EchoClientHandler() {
        firstMessage = Unpooled.buffer(EchoClient.SIZE);
//        for (int i = 0; i < firstMessage.capacity(); i ++) {
//            firstMessage.writeByte((byte) i);
//        }
        firstMessage.writeBytes("00000003123".getBytes());
    }

    public EchoClientHandler(int headSize, boolean isLongConnect) {
        firstMessage = Unpooled.buffer(EchoClient.SIZE);
        firstMessage.writeBytes("00000003123".getBytes());
    	this.headSize = headSize;
    	this.isLongConnect = isLongConnect;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //ctx.writeAndFlush(firstMessage);
        if(isLongConnect){
     	   startHeartBeatThread(ctx);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

    	ByteBuf in = (ByteBuf) msg;
    	System.out.println(in.toString(CharsetUtil.US_ASCII));

//    	ByteBuf in2 = ByteBufUtil.threadLocalDirectBuffer();
//    	System.out.println(in2.toString(CharsetUtil.US_ASCII));
        //ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
       ctx.flush();
       if(!isLongConnect){
    	   ctx.close();
       }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }


	/**
	 * 启动心跳线程
	 */
	public void startHeartBeatThread(final ChannelHandlerContext ctx) {
		final Timer heartBeatTimer = new Timer();
		TimerTask heartBeatTask = new TimerTask() {
			@Override
			public void run() {
				if (!ctx.channel().isWritable()) {
					heartBeatTimer.cancel();
					logger.info("Socket通讯已关闭，取消发送心跳包");
					return;
				}
				logger.info("Socket通讯发送心跳包");
				if (headSize == EchoClient.HEAD_SIZE_8) {
				    ByteBuf firstMessage = Unpooled.buffer(EchoClient.SIZE);
				    firstMessage.writeBytes("00000000".getBytes());
					ctx.writeAndFlush(firstMessage);
				} else if (headSize == EchoClient.HEAD_SIZE_6) {
				    ByteBuf firstMessage = Unpooled.buffer(EchoClient.SIZE);
				    firstMessage.writeBytes("000000".getBytes());
					ctx.writeAndFlush(firstMessage);
				}
			}
		};
		heartBeatTimer.schedule(heartBeatTask, 5000, 5000);
	}
}