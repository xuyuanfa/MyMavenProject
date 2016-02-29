package com.xxx.util.io.client.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioDemo implements ISocketCallBack{

	private final static Logger log = LoggerFactory.getLogger(NioDemo.class);

	private byte[] rcvResult; // 用于接收长连接回调返回响应结果
	private CountDownLatch cdlRcv = new CountDownLatch(1);

	public static void main(String[] args) throws Exception{
		for(int i=0;i<1000;i++){
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						NioDemo demo = new NioDemo();
						NioTcpClient client = ConnectUtil.getInstance(true, 8, "127.0.0.1", 8000);
						synchronized (client) {
							client.write("00000003123".getBytes(), demo);
							byte[] respMsgBytes = demo.getRcvResult();
							System.out.println(new String(respMsgBytes));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		
//		for(int i=0;i<1;i++){
//			System.out.println("+++++++++yuwujian:::"+i+"    end++++++++++++++");
//			test();
//			System.out.println("+++++++++yuwujian:::"+i+"    end++++++++++++++");
//		}
	}
	
	public static void test() throws IOException {
		log.info("app started");
		final NioManager nioManager = new NioManager();
//	    final String host = "www.baidu.com";
//	    final int port = 80;
		final String host = "localhost";
	    final int port = 8000;
	    final NioBuffer nioBuffer = new NioBuffer();
	    
	    
	    INioHandler handler = new INioHandler() {
	    	
	    	//按需求编写具体请求数据
	        @Override
	        public void onConnected(NioTcpClient client) throws Exception {
	            log.info("{} was connected", client);
	            String getRequest = "00000003123";
	            byte[] data = getRequest.getBytes(Charset.forName("UTF-8"));
	            client.write(data);
	        }

	        @Override
	        public void onDisconnected(NioTcpClient client) throws Exception {
	            log.info("{} was disconnected", client);
	            byte[] bytes = nioBuffer.readBytes(nioBuffer.readableByteSize());
	            String str = new String(bytes, Charset.forName("utf-8"));
	            nioBuffer.clear();
	            log.info(str);
	            nioManager.shutdown();
	        }

	        @Override
	        public void onDataReceived(NioTcpClient client, ByteBuffer buffer)
	                throws Exception {
	            log.info("received {} bytes by {}", buffer.remaining(), client);
	            nioBuffer.addBuffer(buffer);
	            response(client,nioBuffer);
	        }

			@Override
			public void onExceptionHappened(NioTcpClient client, Exception e)
					throws Exception {
				log.error("", e);
	            client.disconnect();
			}
	    };
	    
	    NioTcpClient socket = new NioTcpClient(nioManager, handler);
	    socket.connect(host, port, 8);
	    System.out.println("+++++yuwujian11111111+++++++");
//	    NioTcpClient socket2 = new NioTcpClient(nioManager, handler);
//	    socket2.connect(host, port);
//	    System.out.println("+++++yuwujian1222222222222+++++++");
	}
	
	private static void response(NioTcpClient client, NioBuffer buffer){
		try{
			byte[] lengthBytes=new byte[8];
			int length=-1;
			if(buffer.readableByteSize()>=8){
				lengthBytes=buffer.readBytes(8);
//				buffer.get(lengthBytes, 0, 8);
				length=Integer.parseInt(new String(lengthBytes));
				if(length==buffer.readableByteSize()){
					client.disconnect();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}


	@Override
	public void setRcvResult(byte[] bytes) {
		rcvResult = bytes;
		cdlRcv.countDown();
	}

	public byte[] getRcvResult() throws Exception {
		cdlRcv.await();
		cdlRcv = new CountDownLatch(1);
		byte[] _rcvResult = rcvResult;
		rcvResult = null;
		return _rcvResult;
	}

}
