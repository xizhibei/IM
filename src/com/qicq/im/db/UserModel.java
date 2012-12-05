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
		while(c.moveToNext()){
			users.add( new User(
					c.getInt(0),
					c.getInt(1),
					c.getString(2),
					c.getString(3),
					c.getInt(4),
					c.getString(5),
					c.getString(6),
					c.getInt(7),
					c.getInt(8),
					c.getFloat(9),
					c.getString(10),
					c.getString(11)
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
        
		Cursor c = db.rawQuery("select count(*) from " + tableName + " where uid = " + u.uid,null);
		if(c.moveToNext() && c.getInt(0) == 0){
			cv.put("uid", u.uid);
			db.insert(tableName, null,cv);
		}else{
			db.update(tableName, cv, "uid = " + u.uid, null);
		}
		c.close();
	}
	
	public User getUser(String uid){
		Cursor c = db.rawQuery("select * from " + tableName + " where uid = " + uid, null);
		if(c.moveToNext() && c.getCount() == 0){
			c.close();
			return null;
		}
		
		User u = new User(
				c.getInt(0),
				c.getInt(1),
				c.getString(2),
				c.getString(3),
				c.getInt(4),
				c.getString(5),
				c.getString(6),
				c.getInt(7),
				c.getInt(8),
				c.getFloat(9),
				c.getString(10),
				c.getString(11)
				);
		c.close();
		return u;
	}

}
