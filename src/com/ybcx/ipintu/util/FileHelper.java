package com.ybcx.ipintu.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.util.EncodingUtils;

import android.os.Environment;

/**
 * 对SD卡文件的管理
 * 
 * @author ch.linghu
 * 
 */
public class FileHelper {
	private static final String TAG = "FileHelper";

	private static final String BASE_PATH = "iptuser";

	public static File getBasePath() throws IOException {
		// 如果SD卡不可用返回空
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return null;
		}
		File basePath = new File(Environment.getExternalStorageDirectory(),
				BASE_PATH);

		if (!basePath.exists()) {
			if (!basePath.mkdirs()) {
				throw new IOException(String.format("%s cannot be created!",
						basePath.toString()));
			}
		}

		if (!basePath.isDirectory()) {
			throw new IOException(String.format("%s is not a directory!",
					basePath.toString()));
		}

		return basePath;
	}
	
	// 读在/mnt/sdcard/目录下面的文件
	public static String readFileSdcard(String fileName) {
		ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
		String res = null;
		try {
			FileInputStream inStream = new FileInputStream(fileName);	
	        int len=-1; 
	        byte[] buffer =new byte[1024];       
	        while( (len=inStream.read(buffer))!=-1 ){ 
	            outputStream.write(buffer, 0, len); 
	        }
	        byte[] data = outputStream.toByteArray(); 
	        
	        inStream.close(); 
	        outputStream.close(); 
	        
	        res = new String(data);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static int getFileLength(String fileName){
		int total = 0;
		try {
			FileInputStream inStream = new FileInputStream(fileName);	
	        int len=-1; 
	        byte[] buffer =new byte[1024];       
	        while( (len=inStream.read(buffer))!=-1 ){ 
	        	total += len;
	        }	       	      
	        inStream.close(); 	        	        	       	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}

	// 写在/mnt/sdcard/目录下面的文件
	public static void writeFileSdcard(String fileName, String message) {
		try {
			// FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);
			FileOutputStream fout = new FileOutputStream(fileName);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
