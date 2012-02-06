package com.ybcx.ipintu.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserCacheImpl {
	
	private static final String TAG = "UserCacheImpl";

	private SQLiteDatabase ptdb;
	
	public UserCacheImpl(Context ctxt){
		IpintuDB ipt = IpintuDB.getInstance(ctxt);
		ptdb = ipt.getDb(true);
	}
	

	public void cacheNewApplycants(List<Applycant> users){
		Query q = new Query(ptdb);
		try {
			ptdb.beginTransaction();
			// 不分顺序插入
			for (Applycant user : users) {
				long result = -1;
				// 检查是否有重复记录，没有才入库，否则发生约束冲突
				if (!checkRecordExist(ApplycantsTable.TABLE_NAME, user.id)) {
					result = q.into(ApplycantsTable.TABLE_NAME)
							.values(applycantsToContentValues(user)).insert();
				}
				if (-1 == result) {
					Log.e(TAG, "cann't insert the applycant : "
							+ user.id);
				} else {					
					Log.v(TAG, String.format(
							"Insert a applycant into database : %s",
							user.id));
				}
			}
			ptdb.setTransactionSuccessful();
		} finally {
			ptdb.endTransaction();
		}
	}
	
	public List<Applycant> getNewApplycants(){
		List<Applycant> list = new ArrayList<Applycant>();
		Query q = new Query(ptdb);
		Cursor c = q
				.from(ApplycantsTable.TABLE_NAME)
				.orderBy(
						ApplycantsTable.Columns.APPLYTIME
								+ " DESC").select();
		try {
			while (c.moveToNext()) {
				list.add(cursorToApplycant(c));
			}
		} finally {
			c.close();
		}

		return list;

	}
	
	public void deleteAcceptApplycant(String id){
		Log.d(TAG, "to delete applycant: " + id);
		Query del = new Query(ptdb);
		del.from(ApplycantsTable.TABLE_NAME)
				.where(ApplycantsTable.Columns.ID + "=?", id)
				.delete();
	}
	
	
	private boolean checkRecordExist(String table, String id) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM ").append(table).append(" WHERE ")
				.append(" id=?");

		Cursor c = ptdb.rawQuery(sql.toString(), new String[] { id });
		boolean exist = false;
		if (c.moveToFirst()) {
			exist = c.getInt(0) > 0;
		}
		c.close();
		return exist;

	}
	
	private ContentValues applycantsToContentValues(Applycant user) {
		ContentValues v = new ContentValues();
		v.put(ApplycantsTable.Columns.ID, user.id);		
		v.put(ApplycantsTable.Columns.ACCOUNT, user.account);		
		v.put(ApplycantsTable.Columns.APPLYREASON, user.applyReason);		
		v.put(ApplycantsTable.Columns.APPLYTIME, user.applyTime);		

		return v;
	}
	
	private Applycant cursorToApplycant(Cursor c) {
		Applycant user = new Applycant();
		user.id = c.getString(c.getColumnIndex(ApplycantsTable.Columns.ID));
		user.account = c.getString(c.getColumnIndex(ApplycantsTable.Columns.ACCOUNT));
		user.applyReason = c.getString(c.getColumnIndex(ApplycantsTable.Columns.APPLYREASON));
		user.applyTime = c.getString(c.getColumnIndex(ApplycantsTable.Columns.APPLYTIME));
		
		return user;
	}
	
}
