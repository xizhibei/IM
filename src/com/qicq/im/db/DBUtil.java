package com.qicq.im.db;

import java.util.ArrayList;
import java.util.List;

import com.qicq.im.api.ChatMessage;
import com.qicq.im.api.Demand;
import com.qicq.im.api.User;
import com.qicq.im.msg.ChatListItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBUtil {
	protected static DBHelper helper = null;
	protected static SQLiteDatabase db = null;
	public DBUtil(Context context,String uid){
		if(uid == null)
			throw new IllegalArgumentException("Uid is null!!!");
		if(helper == null){
			helper = new DBHelper(context,uid + ".db");
			db = helper.getWritableDatabase();
		}
	}
	
	public void close(){
		if(db != null)
			db.close();
	}

	/*******************Msg table begin********************/
	public void insertAllMsg(List<ChatMessage> msgs){
		for(ChatMessage m : msgs){
			if(m.isStored)
				continue;
			insertMsg(m);
		}
	}
	
	/**
	 * 
	 * @param m message
	 * @return the id in the table 'msg'
	 */
	public int insertMsg(ChatMessage m){
		ContentValues cv = new ContentValues();        
		cv.put("targetid",m.targetId);
		cv.put("time",m.time);
		cv.put("content",m.content);
		cv.put("type",m.type);
		cv.put("direction",m.direction);
		cv.put("audiotime",m.audioTime);
		cv.put("sendstate",m.sendState);
		return (int) db.insert("msg", null,cv);
	}

	public List<ChatMessage> fetchAllMsg(String targetId){
		Cursor c = db.rawQuery("select * from msg where targetid = " + targetId + " order by time desc", null);
		List<ChatMessage> msgs = new ArrayList<ChatMessage>();
		while(c.moveToNext()){//int direction, String content,String targetId,boolean isStored
			msgs.add(ChatMessage.fromDatabase(
					c.getInt(0),
					c.getInt(1),
					c.getString(2),
					c.getString(3),
					c.getInt(4),
					c.getInt(5),
					c.getInt(6),
					c.getInt(7)));
		}
		c.close();
		Log.v("MsgModel","get msg size" + msgs.size() + " with uid " + targetId);
		return msgs;
	}
	
	/*******************User table begin********************/
	
	public void updateAllUser(List<User> users){
		for(User u : users){
			updateUser(u);
			if(u.demand != null)
				updateDemand(u.demand);
		}
	}
	
	private User getUser(Cursor c){
		return new User(c.getInt(0),
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
	}
	
	public User getUser(String uid){
		Cursor c = db.rawQuery("select * from user where uid = " + uid, null);
		if(!c.moveToNext()){
			c.close();
			Log.v("UserModel","Fail to get user " + uid);
			return null;
		}
		
		User u = getUser(c);
		c.close();
		return u;
	}
	
	public List<User> fetchAllUser(String sql){
		Cursor c = db.rawQuery(sql, null);		
		List<User> users = new ArrayList<User>();
		
		while(c.moveToNext()){
			users.add(getUser(c));
		}
		c.close();
		Log.v("UserModel",sql + " outcome: " + users.size());
		return users;
	}
	
	public List<User> fetchAllFans(){
		return fetchAllUser("select * from user where type & " + User.USER_TYPE_FANS + " <> 0");
	}
	
	public List<User> fetchAllFollowed(){
		return fetchAllUser("select * from user where type & " + User.USER_TYPE_BEFOLLOWED + " <> 0");
	}
	
	public List<User> fetchAllFriends(){
		return fetchAllUser("select * from user where type & " + User.USER_TYPE_FRIEND  + " = " + User.USER_TYPE_FRIEND);
	}
	
	public List<User> fetchAllNearby(){
		return fetchAllUser("select * from user where type = " + User.USER_TYPE_NEARBY);
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
        
		Cursor c = db.rawQuery("select * from user where uid = " + u.uid,null);
		if(!c.moveToNext()){
			cv.put("uid", u.uid);
			long ret = db.insert("user", null,cv);
			Log.v("UserModel","Insert return " + ret);
		}else{
			int ret = db.update("user", cv, "uid = " + u.uid, null);
			Log.v("UserModel","Update affected return " + ret);
		}
		c.close();
	}
	
	/*******************Msg send task table begin********************/
	
	public void insertAllMsgSendTask(List<ChatMessage> msgs){
		for(ChatMessage m : msgs){
			if(m.isStored)
				continue;
			insertMsgSendTask(m);
		}
	}
	
	public int insertMsgSendTask(ChatMessage m){
		ContentValues cv = new ContentValues();
		cv.put("mid",m.mid);
		cv.put("targetid",m.targetId);
		cv.put("time",m.time);
		cv.put("content",m.content);
		cv.put("type",m.type);
		cv.put("direction",m.direction);
		cv.put("audiotime",m.audioTime);
		cv.put("sendstate",m.sendState);
		
		return (int) db.insert("msgsendtask", null,cv);
	}

	public ChatMessage fetchOneMsgSendTask(){
		Cursor c = db.rawQuery("select * from msgsendtask limit 0,1", null);
		ChatMessage msg = null;
		if(c.moveToNext()){//int direction, String content,String targetId,boolean isStored
			msg = ChatMessage.fromDatabase(
					c.getInt(0),
					c.getInt(1),
					c.getString(2),
					c.getString(3),
					c.getInt(4),
					c.getInt(5),
					c.getInt(6),
					c.getInt(7));
		}
		c.close();
		return msg;
	}
	
	public void deleteMsgSendTask(int mid){
		db.delete("msgsendtask", "mid = " + mid,null);
	}
	
	/*******************ChatList table begin********************/
	public void insertAllChatList(List<ChatListItem> msgs) {
		for (ChatListItem m : msgs) {
			ContentValues cv = new ContentValues();			
			cv.put("time", m.msg.time);
			cv.put("content", m.msg.content);
			cv.put("type", m.msg.type);
			cv.put("direction", m.msg.direction);
			cv.put("audiotime", m.msg.audioTime);
			cv.put("sendstate",m.msg.sendState);
			cv.put("count", m.unreadCount);
			
			
			Cursor c = db.rawQuery("select * from chatlist where targetid = " + m.msg.targetId, null);
			if(c.moveToNext()){
				int ret = db.update("chatlist", cv,"targetid = " + m.msg.targetId,null);
				Log.v("ChatListModel","Rows affected " + ret);
			}else{
				cv.put("targetid", m.msg.targetId);
				long ret = db.insert("chatlist", null, cv);
				Log.v("ChatListModel","Row number insert  " + ret);
			}
			c.close();
		}
	}

	public void updateChatListNewMsg(List<ChatMessage> msgs){
		for(ChatMessage m : msgs){
			Cursor c = db.rawQuery("select count from chatlist where targetid = " + m.targetId, null);
			int count = 1;
			if(c.moveToNext())
				count = c.getInt(0) + 1;
			ContentValues cv = new ContentValues();

			cv.put("time", m.time);
			cv.put("content", m.content);
			cv.put("type", m.type);
			cv.put("direction", m.direction);
			cv.put("audiotime", m.audioTime);
			cv.put("sendstate",m.sendState);
			cv.put("count", count);

			if(count == 1){
				cv.put("targetid", m.targetId);
				db.insert("chatlist", null, cv);
			}
			else
				db.update("chatlist", cv, "targetid = " + m.targetId, null);
			c.close();
		}
	}

//	private final static String COLUMN_NAME_uid = "uid";
//	private final static String COLUMN_NAME_type = "type";
//	private final static String COLUMN_NAME_name = "name";
//	private final static String COLUMN_NAME_sex = "sex";
//	private final static String COLUMN_NAME_age = "age";
//	private final static String COLUMN_NAME_regdate = "regdate";
//	private final static String COLUMN_NAME_lastupdate = "lastupdate";
//	private final static String COLUMN_NAME_serverAvatarUrl = "serverAvatarUrl";
//	private final static String COLUMN_NAME_localAvatarPath = "localAvatarPath";
//	private final static String COLUMN_NAME_lat = "lat";
//	private final static String COLUMN_NAME_lng = "lng";
//	private final static String COLUMN_NAME_distance = "distance";
	
	public List<ChatListItem> fetchAllChatList() {
		Cursor c = db.rawQuery("select * from chatlist,user where uid = targetid", null);
		List<ChatListItem> msgs = new ArrayList<ChatListItem>();
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

		int Idx_count = c.getColumnIndex("count");

		while (c.moveToNext()) {
			msgs.add(ChatListItem.fromDatabase(ChatMessage.fromDatabase(
					c.getInt(0),
					c.getInt(1), 
					c.getString(2),
					c.getString(3), 
					c.getInt(4), 
					c.getInt(5),
					c.getInt(6),
					c.getInt(7)
					),new User(c.getInt(Idx_type),
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
							), 
					c.getInt(Idx_count)));
		}
		c.close();
		Log.v("ChatListModel","fetch all " + msgs.size());
		return msgs;
	}
	
	/*******************Demand table begin********************/
	
	public List<Demand> fetchAllDemand(String uid){
		Cursor c = db.rawQuery("select * from demand where uid = " + uid , null);		
		List<Demand> demands = new ArrayList<Demand>();
		
		while(c.moveToNext()){
			demands.add( Demand.fromDatabase(
					c.getInt(0),
					c.getInt(1),
					c.getString(2),
					c.getInt(3),
					c.getInt(4),
					c.getInt(5),
					c.getString(6)
					));
		}
		c.close();
		Log.v("DemandModel","outcome: " + demands.size());
		return demands;
	}
	
	public void updateDemand(Demand d){
		ContentValues cv = new ContentValues();
		cv.put("uid",d.uid);
		cv.put("name",d.name);
        cv.put("startH",d.startH);
        cv.put("startM",d.startM);
        cv.put("endH",d.endH);
        cv.put("endM",d.endM);
        cv.put("sexType",d.sexType);
        cv.put("detail",d.detail);
        
		Cursor c = db.rawQuery("select * from demand where did = " + d.did,null);
		if(!c.moveToNext()){
			cv.put("did", d.did);
			long ret = db.insert("demand", null,cv);
			Log.v("DemandModel","Insert return " + ret);
		}else{
			int ret = db.update("demand", cv, "did = " + d.did, null);
			Log.v("DemandModel","Update affected return " + ret);
		}
		c.close();
	}
	
	public void updateAllDemand(List<Demand> ds){
		for(Demand d : ds){
			updateDemand(d);
		}
	}
}
