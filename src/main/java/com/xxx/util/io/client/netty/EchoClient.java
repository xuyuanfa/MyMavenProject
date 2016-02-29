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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Sends one message when a connection is open and echoes back any received
 * data to the server.  Simply put, the echo client initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public final class EchoClient {
	private final static Logger logger = LoggerFactory.getLogger(EchoClient.class);

    private final String host;
    private final int port;
    private final int headSize;
    private final boolean isLongConnect;
    public static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));
	public static final int HEAD_SIZE_6 = 6;
	public static final int HEAD_SIZE_8 = 8;
	private static final EventLoopGroup group = new NioEventLoopGroup();
	private Channel channel;
	private Bootstrap bootstrap;
	
    public EchoClient(){
    	this.host = System.getProperty("host", "127.0.0.1");
    	this.port = Integer.parseInt(System.getProperty("port", "8000"));
    	this.headSize = Integer.parseInt(System.getProperty("headSize", "8"));
    	this.isLongConnect = true;
    }
    public EchoClient(String host, int port, int headSize, boolean isLongConnect){
    	this.host = host;
    	this.port = port;
    	this.headSize = headSize;
    	this.isLongConnect = isLongConnect;
    }
    
    
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public void doOpen() throws Exception {
        // Configure the client.
        try {
        	bootstrap = new Bootstrap();
        	bootstrap.group(group)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.TCP_NODELAY, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     //p.addLast(new LoggingHandler(LogLevel.INFO));
                     p.addLast(new EchoClientHandler(headSize, isLongConnect));
                 }
             });

        } finally {
            // Shut down the event loop to terminate all threads.
            //group.shutdownGracefully();
        }
    }
	
	public void doConnect() throws Exception{
        // Start the client.
        ChannelFuture f = bootstrap.connect(host, port).sync();
        channel = f.channel();
        
        
        // Wait until the connection is closed.
        //f.channel().closeFuture().sync();
	}
	
	public byte[] sendAndRec(byte[] bytes){
		ByteBuf firstMessage = Unpooled.buffer(EchoClient.SIZE);
		firstMessage.writeBytes(bytes);
		channel.writeAndFlush(firstMessage);
		
		
		return null;
	}
	
	
	

}