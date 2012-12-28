package com.qicq.im.db;

import java.util.ArrayList;
import java.util.List;

import com.qicq.im.api.ChatMessage;
import com.qicq.im.api.User;
import com.qicq.im.msg.ChatListItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class ChatListModel extends AbstractModel {

	public ChatListModel(Context context, String uid) {
		super(context, uid);
		tableName = "chatlist";
	}

	public void insertAll(List<ChatListItem> msgs) {
		for (ChatListItem m : msgs) {
			ContentValues cv = new ContentValues();			
			cv.put("time", m.msg.time);
			cv.put("content", m.msg.content);
			cv.put("type", m.msg.type);
			cv.put("direction", m.msg.direction);
			cv.put("audiotime", m.msg.audioTime);
			cv.put("count", m.unreadCount);
			
			
			Cursor c = db.rawQuery("select * from " + tableName + " where targetid = " + m.msg.targetId, null);
			if(c.moveToNext()){
				int ret = db.update(tableName, cv,"targetid = " + m.msg.targetId,null);
				Log.v("ChatListModel","Rows affected " + ret);
			}else{
				cv.put("targetid", m.msg.targetId);
				long ret = db.insert(tableName, null, cv);
				Log.v("ChatListModel","Row number insert  " + ret);
			}
			c.close();
		}
	}

	public void updateNewMsg(List<ChatMessage> msgs){
		for(ChatMessage m : msgs){
			Cursor c = db.rawQuery("select count from " + tableName + " where targetid = " + m.targetId, null);
			int count = 1;
			if(c.moveToNext())
				count = c.getInt(0) + 1;
			ContentValues cv = new ContentValues();

			cv.put("time", m.time);
			cv.put("content", m.content);
			cv.put("type", m.type);
			cv.put("direction", m.direction);
			cv.put("audiotime", m.audioTime);
			cv.put("count", count);

			if(count == 1){
				cv.put("targetid", m.targetId);
				db.insert(tableName, null, cv);
			}
			else
				db.update(tableName, cv, "targetid = " + m.targetId, null);
			c.close();
		}
	}

	public List<ChatListItem> fetchAll() {
		Cursor c = db.rawQuery("select * from " + tableName
				+ ",user where uid = targetid", null);
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

		while (c.moveToNext()) {// int direction, String content,String
			// targetId,boolean isStored
			msgs.add(ChatListItem.fromDatabase(ChatMessage.fromDatabase(
					c.getInt(0),
					c.getInt(1), 
					c.getString(2),
					c.getString(3), 
					c.getInt(4), 
					c.getInt(5),
					c.getInt(6)
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
}
