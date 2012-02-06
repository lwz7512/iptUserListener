package com.ybcx.ipintu.db;

import org.json.JSONException;
import org.json.JSONObject;


public class Applycant {

	public String id;
	public String account;
	public String applyReason;
	public String applyTime;
	
	public static  Applycant parseJsonToObj(JSONObject json) throws JSONException{
		Applycant user = new Applycant();
		user.id =json.optString("id");
		user.account =json.optString("account");
		user.applyReason =json.optString("applyReason");
		user.applyTime =json.optString("applyTime");
		
		return user;
	}
	
}
