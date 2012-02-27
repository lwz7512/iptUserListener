package com.ybcx.ipintu.task;


import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.ybcx.ipintu.http.HttpException;
import com.ybcx.ipintu.http.Response;
import com.ybcx.ipintu.http.SimpleHttpClient;
import com.ybcx.ipintu.service.ApiConsts;



public class SendTask extends GenericTask {
	private static final String TAG = "SendTask";

	// 同意
	public static final int TYPE_ACCEPT = 1;
	public static final int TYPE_REFUSE = 0;
	public static final int TYP_SENDMAIL =3;
	
	private String postResult;

	private SimpleHttpClient client;
	

	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		//初始化客户端
        client = new SimpleHttpClient(ApiConsts.adminUser);
	}
	
	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		TaskParams param = params[0];
		try {

			int mode = param.getInt("mode");
			String account = param.getString("account");
			
			ArrayList<BasicNameValuePair> nvs = new ArrayList<BasicNameValuePair>();
			BasicNameValuePair methodParam = new BasicNameValuePair("method","accept");
			BasicNameValuePair accountParam = new BasicNameValuePair("account",account);
			nvs.add(methodParam);	
			nvs.add(accountParam);	
			
			BasicNameValuePair optParam = null;
			Response resp = null;
			switch (mode) {

			case TYPE_ACCEPT:
				optParam = new BasicNameValuePair("opt","approve");
				nvs.add(optParam);			
				resp = client.post(ApiConsts.serviceUrl, nvs, null, false);
				postResult = resp.asString().trim();
				break;
			
			case TYPE_REFUSE:
				optParam = new BasicNameValuePair("opt","refuse");	
				nvs.add(optParam);			
				resp = client.post(ApiConsts.serviceUrl, nvs, null, false);
				postResult = resp.asString().trim();
				break;	
				
			case 	TYP_SENDMAIL:
				
				Log.d(TAG, "mail sended!");
				break;
			}
			
			
		} catch (HttpException e) {
			// Log.e(TAG, e.getMessage(), e);
			return TaskResult.IO_ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		}

		return TaskResult.OK;

	} // end of doInBackground

	
	protected void _onPostExecute(TaskResult result) {

		if (result == TaskResult.OK) {
			if (this.getListener() != null && postResult != null) {
				Log.d(TAG, ">>> call deliverResponseString: \n" + postResult);
				// 回调监听方法传结果
				this.getListener().deliverResponseString(postResult);
			} else {
				Log.d(TAG, "listener is null or postResult is null!");
			}
		} else {
			Log.d(TAG, "Fetching  data ERROR!");
		}

	}
	

}