package com.qicq.im.api;

import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ChatMessage {

	public static final int MESSAGE_FROM = 0;
	public static final int MESSAGE_TO = 1;
	
	public static final int MESSAGE_TYPE_TEXT = 0;
	public static final int MESSAGE_TYPE_HELLO = 1;
	public static final int MESSAGE_TYPE_IMAGE = 2;
	public static final int MESSAGE_TYPE_VOICE = 3;
	public static final int MESSAGE_TYPE_REQUEST = 4;
	
	public static final int MESSAGE_STATE_READY = 0;
	public static final int MESSAGE_STATE_SEND = 1;
	public static final int MESSAGE_STATE_FAIL = 2;

	public int mid;
	public int direction;
	public String content;//if type is image or audio, content is filename/path
	public String targetId;
	public int time;
	public boolean isStored = false;
	public boolean isReaded = true;
	public int sendState;
	
	public int audioTime = 0;
	public int type;
	
	public ChatMessage(){
		
	}
	
	public static ChatMessage fromReciver(String content,String targetId,int type,int time,int audiotime){
		ChatMessage msg = new ChatMessage();
		msg.direction = MESSAGE_FROM;
		msg.content = content;
		msg.targetId = targetId;
		msg.type = type;
		msg.time = time;
		msg.isReaded = false;
		msg.audioTime = audiotime;
		return msg;
	}
	
	public static ChatMessage fromSender(int type,String content,String targetId){
		ChatMessage msg = new ChatMessage();
		msg.direction = MESSAGE_TO;
		msg.type = type;
		msg.content = content;
		msg.targetId = targetId;
		Date date = new Date();
		msg.time = (int) (date.getTime() / 1000);
		msg.sendState = MESSAGE_STATE_READY;
		return msg;
	}
	
	public static ChatMessage fromDatabase(int mid,int direction, String content,String targetId,int type,int time,int audiotime,int sendstate){
		ChatMessage msg = new ChatMessage();
		msg.mid = mid;
		msg.direction = direction;
		msg.content = content;
		msg.targetId = targetId;
		msg.type = type;
		msg.time = time;
		msg.isStored = true;
		msg.audioTime = audiotime;
		msg.sendState = sendstate;
		return msg;
	}
	
	public String getDateString(){
		Date date = new Date();
		date.setTime(time);
		return date.toLocaleString();
	}
	
	public Bitmap getBitmap(){
		Bitmap tmp = BitmapFactory.decodeFile(content);
		return Bitmap.createBitmap(tmp, 0, 0, 200, 200);
	}
}
