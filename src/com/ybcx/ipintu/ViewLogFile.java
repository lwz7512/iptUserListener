package com.ybcx.ipintu;

import java.io.File;
import java.io.IOException;

import com.ybcx.ipintu.service.UserService;
import com.ybcx.ipintu.util.FileHelper;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewLogFile extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.log);
		
		TextView tv = (TextView) this.findViewById(R.id.logtxt);
		
		try {
			File logdir = FileHelper.getBasePath();
			File logFile = new File(logdir, UserService.LOGFILENAME);
			if(!logFile.exists()) return;
			
			String logFilePath = logFile.getAbsolutePath();
			String logContent = FileHelper.readFileSdcard(logFilePath);
			tv.setText(logContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
