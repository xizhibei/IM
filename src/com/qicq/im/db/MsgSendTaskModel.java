package com.qicq.im.db;

import java.util.List;

import com.qicq.im.api.ChatMessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class MsgSendTaskModel extends AbstractModel{

	public MsgSendTaskModel(Context context, String uid) {
		super(context, uid);
		tableName = "msgsendtask";
	}

	public void insertAll(List<ChatMessage> msgs){
		for(ChatMessage m : msgs){
			if(m.isStored)
				continue;
			insert(m);
		}
	}
	
	public int insert(ChatMessage m){
		ContentValues cv = new ContentValues();
		cv.put("mid",m.mid);
		cv.put("targetid",m.targetId);
		cv.put("time",m.time);
		cv.put("content",m.content);
		cv.put("type",m.type);
		cv.put("direction",m.direction);
		cv.put("audiotime",m.audioTime);
		cv.put("sendstate",m.sendState);
		
		return (int) db.insert(tableName, null,cv);
	}

	public ChatMessage fetchRow(){
		Cursor c = db.rawQuery("select * from " + tableName + " limit 0,1", null);
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
	
	public void delete(int mid){
		db.delete(tableName, "mid = " + mid,null);
	}
}
