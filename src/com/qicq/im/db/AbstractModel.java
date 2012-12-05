package com.qicq.im.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractModel {

	protected String tableName = null;
	protected static DBHelper helper = null;
	protected static SQLiteDatabase db = null;
	public AbstractModel(Context context,String uid){
		if(helper == null){
			helper = new DBHelper(context,uid + ".db");
			db = helper.getWritableDatabase();
		}
	}
	
	public SQLiteDatabase getDB(){
		return db;
	}
	
//	public Cursor fetchAll(){
//		return db.rawQuery("select * from " + tableName, null);
//	}

	public String fetchOne(String sql){
		Cursor c = db.rawQuery(sql, null);
		if(c.moveToNext()){
			c.close();
			return c.getString(0);
		}
		else{
			c.close();
			return null;
		}
	}
}
