package com.xxx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private void outputToFile(InputStream is) {
		File file = null;
		InputStream inputStream = is;
		FileOutputStream fs = null;
		try {

			file = new File("D:\\download\\bcm_p\\test.csv");
			fs = new FileOutputStream(file);

			int byteread = 0;
			byte[] buffer = new byte[1024];
			while ((byteread = inputStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fs != null) {
				try {
					fs.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 复用InputStream
	 * @param is
	 */
	private void outputToFile2(InputStream is) {
		File file = null;
		InputStream inputStream = null;
		FileOutputStream fs = null;
		try {
			// 转为ByteArrayOutputStream，重用InputStream
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];  
			int len;  
			while ((len = is.read(buf)) > -1 ) {  
			    baos.write(buf, 0, len);  
			}  
			baos.flush(); 
			// 还原InputStream
			is = new ByteArrayInputStream(baos.toByteArray());  
			
			inputStream = new ByteArrayInputStream(baos.toByteArray());  
			
			file = new File("D:\\download\\citic\\test.xls");
			fs = new FileOutputStream(file);

			int byteread = 0;
			byte[] buffer = new byte[1024];
			while ((byteread = inputStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fs != null) {
				try {
					fs.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
