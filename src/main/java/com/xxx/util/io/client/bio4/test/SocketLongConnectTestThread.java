package com.xxx.util.io.client.bio4.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import com.xxx.util.io.client.bio4.SocketConnect;
import com.xxx.util.io.client.bio4.SocketConnectClient;
import com.xxx.util.io.client.bio4.SocketLongConnectClientEnum;
import com.xxx.util.io.client.bio4.Task;

/**
 * 
 * @author xuyf
 * 
 */
public class SocketLongConnectTestThread extends Thread {
	static final ExecutorService executor = Executors.newCachedThreadPool();
	@Override
	public void run() {
		try {

			SocketConnect client = SocketLongConnectClientEnum.testConnect.getClient();
//			byte[] bytes2 = client.sendAndRcv("00000003123".getBytes(), "test");
			
//			ReqRespData reqRespData = new ReqRespData("00000003123".getBytes(), "test");
//			byte[] bytes2 = client.sendAndRcv(reqRespData);
			
//			client.send(reqRespData);
//			byte[] bytes2 = client.receive(reqRespData);

			Task task = new Task("00000003123".getBytes(), "test", client);
			FutureTask<byte[]> futureTask = new FutureTask<byte[]>(task);
			executor.submit(futureTask);
			System.out.println(Thread.currentThread().getName() + "：" + new String(futureTask.get()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Thread.sleep(1000);
	}


}
