package com.qicq.im.db;

import java.util.ArrayList;
import java.util.List;

import com.qicq.im.api.ChatMessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class MsgModel extends AbstractModel{

	public MsgModel(Context context, String uid) {
		super(context, uid);
		tableName = "msg";
	}
	
	public void insertAll(List<ChatMessage> msgs){
		for(ChatMessage m : msgs){
			if(m.isStored)
				continue;
			ContentValues cv = new ContentValues();        
            cv.put("targetid",m.targetId);
            cv.put("time",m.time);
            cv.put("content",m.content);
            cv.put("type",m.type);
            cv.put("direction",m.direction);
            
			db.insert(tableName, null,cv);

		}
	}
	
	public List<ChatMessage> fetchAll(String targetId){
		Cursor c = db.rawQuery("select * from " + tableName + " where targetid = " + targetId + " order by time asc", null);
		List<ChatMessage> msgs = new ArrayList<ChatMessage>();
		while(c.moveToNext()){//int direction, String content,String targetId,boolean isStored
			msgs.add(ChatMessage.fromDatabase(
					c.getInt(0),
					c.getString(1),
					c.getString(2),
					c.getInt(3),
					c.getInt(4)));
		}
		c.close();
		return msgs;
	}

}
