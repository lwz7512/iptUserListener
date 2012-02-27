package com.ybcx.ipintu;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ybcx.ipintu.db.Applycant;
import com.ybcx.ipintu.util.DateTimeHelper;

public class IptUserAdapter extends BaseAdapter {

	private static final String TAG = "UserAdapter";
	
	private LayoutInflater mInflater;
	private List<Applycant> users;
	
	private Context ctxt;
	
	public IptUserAdapter(Context c){
		
		ctxt = c;
		mInflater = LayoutInflater.from(c);
		users = new ArrayList<Applycant>();
		
	}
	
	@Override
	public int getCount() {
		return users.size();
	}

	@Override
	public Object getItem(int position) {
		return users.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null){
			view = mInflater.inflate(R.layout.useritem, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.account = (TextView) view.findViewById(R.id.account_tv);
			holder.applyTime = (TextView) view.findViewById(R.id.applytime_tv);
			holder.applyReason = (TextView) view.findViewById(R.id.applyreason_tv);
			view.setTag(holder);
		}else{
			view = convertView;
		}
		
		ViewHolder holder = (ViewHolder) view.getTag();
		Applycant user = users.get(position);
		holder.account.setText(user.account);
		holder.applyReason.setText(user.applyReason);
		
		try {
			Log.d(TAG, "to parset time: "+user.applyTime);
			String relativeTime = DateTimeHelper.getRelativeTimeByFormatDate(user.applyTime,ctxt);
			holder.applyTime.setText(relativeTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return view;
	}
	
	
	private static class ViewHolder{
		public TextView account;
		public TextView applyTime;
		public TextView applyReason;
	}
	
	
	public void refresh(List<Applycant> users){
		this.users = users;
		this.notifyDataSetChanged();
	}

	public void clear(){
		this.users.clear();
		this.notifyDataSetChanged();
	}

}
