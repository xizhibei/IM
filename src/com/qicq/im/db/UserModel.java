package com.qicq.im.db;

import java.util.ArrayList;
import java.util.List;

import com.qicq.im.api.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class UserModel extends AbstractModel{
	public UserModel(Context context, String uid) {
		super(context, uid);
		
		tableName = "user";
	}
	
	public void updateAll(List<User> users){
		for(User u : users){
			updateUser(u);
		}
	}
	
	public List<User> fetchAll(String sql){
		Cursor c = db.rawQuery(sql, null);		
		List<User> users = new ArrayList<User>();
		
		int Idx_uid = c.getColumnIndex("uid");
		int Idx_type = c.getColumnIndex("type");
		int Idx_name = c.getColumnIndex("name");
		int Idx_sex = c.getColumnIndex("sex");
		int Idx_age = c.getColumnIndex("age");
		int Idx_regdate = c.getColumnIndex("regdate");
		int Idx_lastupdate = c.getColumnIndex("lastupdate");
		int Idx_serverAvatarUrl = c.getColumnIndex("serverAvatarUrl");
		int Idx_localAvatarPath = c.getColumnIndex("localAvatarPath");
		int Idx_lat = c.getColumnIndex("lat");
		int Idx_lng = c.getColumnIndex("lng");
		int Idx_distance = c.getColumnIndex("distance");
		
		while(c.moveToNext()){
			users.add( new User(c.getInt(Idx_type),
					c.getInt(Idx_uid), 
					c.getString(Idx_name), 
					c.getString(Idx_sex), 
					c.getInt(Idx_age), 
					c.getString(Idx_regdate), 
					c.getString(Idx_lastupdate), 
					c.getInt(Idx_lat), 
					c.getInt(Idx_lng), 
					c.getFloat(Idx_distance), 
					c.getString(Idx_serverAvatarUrl), 
					c.getString(Idx_localAvatarPath)
					));
		}
		c.close();
		Log.v("UserModel",sql + " outcome: " + users.size());
		return users;
	}
	
	public List<User> fetchAllFans(){
		return fetchAll("select * from " + tableName + " where type & " + User.USER_TYPE_FANS + " <> 0");
	}
	
	public List<User> fetchAllFollowed(){
		return fetchAll("select * from " + tableName + " where type & " + User.USER_TYPE_BEFOLLOWED + " <> 0");
	}
	
	public List<User> fetchAllFriends(){
		return fetchAll("select * from " + tableName + " where type & " + User.USER_TYPE_FRIEND  + " = " + User.USER_TYPE_FRIEND);
	}
	
	public List<User> fetchAllNearby(){
		return fetchAll("select * from " + tableName + " where type = " + User.USER_TYPE_NEARBY);
	}
	
	public void updateUser(User u){
		ContentValues cv = new ContentValues();
		cv.put("type",u.type);
        cv.put("name",u.name);
        cv.put("sex",u.sex);
        cv.put("age",u.age);
        cv.put("regdate",u.regdate);
        cv.put("lastupdate",u.lastupdate);
        cv.put("serverAvatarUrl",u.serverAvatarUrl);
        cv.put("localAvatarPath",u.localAvatarPath);
        cv.put("lat",u.lat);
        cv.put("lng",u.lng);
        cv.put("distance",u.distance);
        
		Cursor c = db.rawQuery("select * from " + tableName + " where uid = " + u.uid,null);
		if(!c.moveToNext()){
			cv.put("uid", u.uid);
			long ret = db.insert(tableName, null,cv);
			Log.v("UserModel","Insert return " + ret);
		}else{
			int ret = db.update(tableName, cv, "uid = " + u.uid, null);
			Log.v("UserModel","Update affected return " + ret);
		}
		c.close();
	}
	
	public User getUser(String uid){
		Cursor c = db.rawQuery("select * from " + tableName + " where uid = " + uid, null);
		if(!c.moveToNext()){
			c.close();
			Log.v("UserModel","Fail to get user " + uid);
			return null;
		}
		
		int Idx_uid = c.getColumnIndex("uid");
		int Idx_type = c.getColumnIndex("type");
		int Idx_name = c.getColumnIndex("name");
		int Idx_sex = c.getColumnIndex("sex");
		int Idx_age = c.getColumnIndex("age");
		int Idx_regdate = c.getColumnIndex("regdate");
		int Idx_lastupdate = c.getColumnIndex("lastupdate");
		int Idx_serverAvatarUrl = c.getColumnIndex("serverAvatarUrl");
		int Idx_localAvatarPath = c.getColumnIndex("localAvatarPath");
		int Idx_lat = c.getColumnIndex("lat");
		int Idx_lng = c.getColumnIndex("lng");
		int Idx_distance = c.getColumnIndex("distance");
		
		User u = new User(c.getInt(Idx_type),
				c.getInt(Idx_uid), 
				c.getString(Idx_name), 
				c.getString(Idx_sex), 
				c.getInt(Idx_age), 
				c.getString(Idx_regdate), 
				c.getString(Idx_lastupdate), 
				c.getInt(Idx_lat), 
				c.getInt(Idx_lng), 
				c.getFloat(Idx_distance), 
				c.getString(Idx_serverAvatarUrl), 
				c.getString(Idx_localAvatarPath)
				);
		c.close();
		return u;
	}

}
