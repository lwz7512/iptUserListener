package com.ybcx.ipintu.task;

import java.util.List;

import org.json.JSONObject;

public interface TaskListener {
	
	void onPreExecute(GenericTask task);
	void onPostExecute(GenericTask task, TaskResult result);
	void onProgressUpdate(GenericTask task, Object param);
	void onCancelled(GenericTask task);
	//添加�?��返回列表数据的接�?
	//lwz7512 @ 2011/08/08
	void deliverRetrievedList(List<Object> results);
	//添加�?��服务端返回字符串的接口，这个比较常用
	//lwz7512 @ 2011/08/10
	void deliverResponseString(String response);
	//添加�?��服务端返回JSON对象的接�?
	//lwz7512 @ 2011/08/10
	void deliveryResponseJson(JSONObject json);
	
}
