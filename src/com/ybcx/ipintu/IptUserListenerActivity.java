package com.ybcx.ipintu;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ybcx.ipintu.db.Applycant;
import com.ybcx.ipintu.db.UserCacheImpl;
import com.ybcx.ipintu.task.GenericTask;
import com.ybcx.ipintu.task.SendTask;
import com.ybcx.ipintu.task.TaskAdapter;
import com.ybcx.ipintu.task.TaskListener;
import com.ybcx.ipintu.task.TaskParams;
import com.ybcx.ipintu.task.TaskResult;

public class IptUserListenerActivity extends Activity {
	 private static final String TAG = "IptUserListenerActivity";
	 
	 private UserCacheImpl cache;
	 
	 private ListView user_lv;
	 private IptUserAdapter user_adptr;
	 
	 private Applycant currentUser;
	 
	// Task
	 private GenericTask mSendTask;
	 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //请求系统添加进度条到标题栏
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        //设置视图
        setContentView(R.layout.main);
        
        cache = new UserCacheImpl(this.getApplicationContext());
        
        user_lv = (ListView)findViewById(R.id.userlist);
        user_adptr = new IptUserAdapter(this.getApplicationContext());
        user_lv.setAdapter(user_adptr);
        
        user_lv.setOnItemClickListener(listener);
    }
    
    public void onResume(){
    	super.onResume();
    	
    	List<Applycant> users = cache.getNewApplycants();
    	user_adptr.refresh(users);
    }
    
    private OnItemClickListener listener = new OnItemClickListener() {
    	@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		currentUser = (Applycant) user_adptr.getItem(position);
    		processUser();
    	}
    };
    
	private void processUser() {
		if(currentUser==null) return;
		
		//任务执行时，不许弹出对话框
		if (mSendTask != null
				&& mSendTask.getStatus() == GenericTask.Status.RUNNING)
			return;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(
				IptUserListenerActivity.this);
		builder.setTitle(getText(R.string.examine_user));
		builder.setMessage(getText(R.string.current_applycants)+currentUser.account);
		builder.setPositiveButton(getText(R.string.accept_applycants), okListener);
		builder.setNegativeButton(getText(R.string.decline_applycants), cancelListener);

		Dialog dialog = builder.create();
		dialog.show();
	}
	
	private final DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			sendAccept(true);
		}
	};
	
	private final DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			sendAccept(false);
			dialog.dismiss();
		}
	};
	
	private void sendAccept(boolean isAccept){
		if (mSendTask != null
				&& mSendTask.getStatus() == GenericTask.Status.RUNNING)
			return;
		
		mSendTask = new SendTask();
		mSendTask.setListener(mSendTaskListener);
		TaskParams params = new TaskParams();
		if(isAccept){
			params.put("mode", SendTask.TYPE_ACCEPT);			
		}else{
			params.put("mode", SendTask.TYPE_REFUSE);
		}
		mSendTask.execute(params);
	}
	
	private TaskListener mSendTaskListener = new TaskAdapter() {
		@Override
		public void onPreExecute(GenericTask task) {	
			setProgressBarIndeterminateVisibility(true);// 执行前使进度条可见
		}
		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			setProgressBarIndeterminateVisibility(false); // 执行完后使进度条隐藏			
		}
		public void deliverResponseString(String response) {
			deleteAcceptedUser();
		}
		
	};
	
	private void deleteAcceptedUser(){
		cache.deleteAcceptApplycant(currentUser.id);
		List<Applycant> users = cache.getNewApplycants();
    	user_adptr.refresh(users);
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy.");

		if (mSendTask != null
				&& mSendTask.getStatus() == GenericTask.Status.RUNNING) {
			// Doesn't really cancel execution (we let it continue running).
			// See the SendTask code for more details.
			mSendTask.cancel(true);
		}

		mSendTask = null;
		
		super.onDestroy();
	}
    
}