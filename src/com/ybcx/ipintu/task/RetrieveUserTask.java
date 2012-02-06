package com.ybcx.ipintu.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.ybcx.ipintu.db.Applycant;
import com.ybcx.ipintu.http.HttpException;
import com.ybcx.ipintu.http.Response;
import com.ybcx.ipintu.http.SimpleHttpClient;
import com.ybcx.ipintu.service.ApiConsts;



public class RetrieveUserTask extends GenericTask {

	private static String TAG = "SimpleTask";
	//存放整理好的评论
    private List<Object> retrievedApplycants;
	
	private SimpleHttpClient client;
	
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		//初始化客户端
        client = new SimpleHttpClient(ApiConsts.adminUser);
	}
	
	
	@Override
	protected TaskResult _doInBackground(TaskParams... params) {
		String retrieveApplycantMethod = "getApplicant";
		JSONArray jsApplycants = null;
		try {
			ArrayList<BasicNameValuePair> nvs = new ArrayList<BasicNameValuePair>();
			BasicNameValuePair methodParam = new BasicNameValuePair("method",
					retrieveApplycantMethod);			
			nvs.add(methodParam);			
			// 所有的请求，除了下载下载图片文件，都采用post的方式提交
			Response resp = client.post(ApiConsts.serviceUrl, nvs, null, false);
			String jsonStr = resp.asString();
			Log.d(TAG, ">>> json users: " + jsonStr);
			
			//转成对象
			jsApplycants = new JSONArray(jsonStr);
			//保存下来
			jsonApplycantsToObjs(jsApplycants);			
			
		} catch (HttpException e) {			
			e.printStackTrace();
			return TaskResult.FAILED;
		} catch (JSONException e) {
			e.printStackTrace();
			return TaskResult.JSON_PARSE_ERROR;
		}
		return TaskResult.OK;
	}
	
	private void jsonApplycantsToObjs(JSONArray jsApplycants){
		retrievedApplycants = new ArrayList<Object>();
		try{
			for(int i=0;i<jsApplycants.length();i++){
				Applycant user = Applycant.parseJsonToObj(jsApplycants.getJSONObject(i));	
				Log.d(TAG, "User applyTime: "+user.applyTime);
				retrievedApplycants.add(user);
			}			
		}catch(JSONException e){
			e.printStackTrace();
			Log.w(TAG, ">>> json Array getJSONObject Exception!");
		}		
	}
	

	@Override
	protected void _onPostExecute(TaskResult result) {
		if(result==TaskResult.OK){
			if(this.getListener()!=null && retrievedApplycants!=null){
    			//回调监听方法传结果
    			this.getListener().deliverRetrievedList(retrievedApplycants);
    		}else{
    			//listener is null or retrieved pics is null!
    			Log.d(TAG, "listener is null or retrieved applycants is null!");
    		}
		}

	}

}
