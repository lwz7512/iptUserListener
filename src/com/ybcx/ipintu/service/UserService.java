/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ybcx.ipintu.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.ybcx.ipintu.IptUserListenerActivity;
import com.ybcx.ipintu.R;
import com.ybcx.ipintu.db.Applycant;
import com.ybcx.ipintu.db.UserCacheImpl;
import com.ybcx.ipintu.task.GenericTask;
import com.ybcx.ipintu.task.RetrieveUserTask;
import com.ybcx.ipintu.task.TaskAdapter;
import com.ybcx.ipintu.task.TaskListener;
import com.ybcx.ipintu.task.TaskParams;
import com.ybcx.ipintu.task.TaskResult;


public class UserService extends Service {
    private static final String TAG = "UserService";

    private WakeLock mWakeLock;
    private NotificationManager mNotificationManager;


    private GenericTask mRetrieveTask;

    private UserCacheImpl cache;

    @Override
    public void onCreate() {
        Log.v(TAG, "Server Created");
        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWakeLock.acquire();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //先计划下一次任务
        schedule(UserService.this);        
       
        cache = new UserCacheImpl(this.getApplicationContext());
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {       
        Log.d(TAG, "Start Once");
        requestNewUsers();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service Destroyed!.");

        if (mRetrieveTask != null
                && mRetrieveTask.getStatus() == GenericTask.Status.RUNNING) {
            mRetrieveTask.cancel(true);
        }        
        mWakeLock.release();
        super.onDestroy();
    }
  
 
    private void requestNewUsers() {
    	    	
    	if (mRetrieveTask != null
                && mRetrieveTask.getStatus() == GenericTask.Status.RUNNING) {
            return;
        } else {
        	
        	DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        	Log.d(TAG, "TO Retrieve new applycants at: "+df.format(new Date()));
            
        	//执行前先检查网络状况
        	if(!isNetworkAvailable(UserService.this)) return;
        	
            mRetrieveTask = new RetrieveUserTask();                        
            mRetrieveTask.setListener(mRetrieveTaskListener);
            mRetrieveTask.execute(new TaskParams());
        }
    	
    }

    private TaskListener mRetrieveTaskListener = new TaskAdapter() {
        @Override
        public void onPostExecute(GenericTask task, TaskResult result) { 
        	if(result == TaskResult.OK){
        		Log.d(TAG, "applycants retrieved successfully...");
        	}else if(result == TaskResult.FAILED){
        		Log.e(TAG, "applycants retrieve failed!");
        	}else if (result == TaskResult.IO_ERROR) {
        		Log.e(TAG, "network is ungeilivable!");
        	} else if (result == TaskResult.JSON_PARSE_ERROR) {
        		Log.e(TAG, "json data parse error!");
        	}
            //***STOP SERVICE!***
            //任务完成后就停止销毁，等待AlarmManager下一次创建
            Log.d(TAG, "to stop UserService...");
            stopSelf();
        }
        @Override
        public void deliverRetrievedList(List<Object> applycants){
        	Log.i(TAG, "New applycants numer: "+applycants.size());
        	//CHECK USER NUMBER...THEN SEND NOTIFICATION...
        	int num = applycants.size();
        	if(num>0){
        		composeNotification(num);
        		cacheApplycants(applycants);
        	}
        }
       
    };
    
    private void cacheApplycants(List<Object> applycants){
    	ArrayList<Applycant> users = new ArrayList<Applycant>();
    	for(Object user : applycants){
    		users.add((Applycant) user);
    	}
    	cache.cacheNewApplycants(users);
    }
    
    private void composeNotification(int num){
    	//展开消息，启动主活动
    	Intent it = new Intent(this, IptUserListenerActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, it, 0);
		
		String title = getText(R.string.admin_name).toString();
		String content = getText(R.string.new_applycants).toString()+num;
		int icon = R.drawable.head32;
		//发通知
		sendNotification(contentIntent, R.string.new_applycants, icon, content, title, content);
    }

    public static void schedule(Context context) {
    	
    	Intent intent = new Intent(context, UserService.class);
        PendingIntent pending = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
       
        Calendar c = new GregorianCalendar();
        int min = c.get(Calendar.MINUTE);
        
        //check inteval
        int interval = 2;
        
        if(min % interval > 0){
        	//变成整10分钟数
        	min += (interval-min%interval); 
        }else{
        	min += interval;
        }
        //设置下一个服务执行时间
        c.set(Calendar.MINUTE, min); 
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

         DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
         Log.d(TAG, "Schedule, next run at " + df.format(c.getTime()));

         AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
         //先停止上一个任务
         alarm.cancel(pending);
        //计划下一个任务
         alarm.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pending);
        
    }

    public static void unschedule(Context context) {
        Intent intent = new Intent(context, UserService.class);
        PendingIntent pending = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "Cancelling alarms.");
        alarm.cancel(pending);
    }
    
    private void sendNotification(PendingIntent intent, int notificationId, int notifyIconId, String tickerText, String title, String text) {
    	
        Notification notification = new Notification(notifyIconId, tickerText,
                 System.currentTimeMillis());

        notification.setLatestEventInfo(this, title, text, intent);

        notification.flags = Notification.FLAG_AUTO_CANCEL
                 | Notification.FLAG_ONLY_ALERT_ONCE
                 | Notification.FLAG_SHOW_LIGHTS;
        //油绿：光润而浓绿的颜色
        notification.ledARGB = 0xFF00BC12;
        notification.ledOnMS = 5000;
        notification.ledOffMS = 5000;
        
        notification.defaults |= Notification.DEFAULT_SOUND;       

         mNotificationManager.notify(notificationId, notification);
     }

    
	public static boolean isNetworkAvailable(Context mContext) {
		boolean flag = false;
		try{
			//需要添加uses-permission, android.permission.ACCESS_NETWORK_STATE			
			ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) {
				flag = false;
			} else {
				flag = connectivity.getActiveNetworkInfo().isAvailable();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}