package com.qicq.im.api;

import java.util.Date;

public class ChatMessage {

	public static final int MESSAGE_FROM = 0;
	public static final int MESSAGE_TO = 1;
	
	public static final int MESSAGE_TYPE_TEXT = 0;
	public static final int MESSAGE_TYPE_HELLO = 1;
	public static final int MESSAGE_TYPE_IMAGE = 2;
	public static final int MESSAGE_TYPE_VOICE = 3;
	public static final int MESSAGE_TYPE_REQUEST = 4;

	public int direction;
	public String content;//if type is image or audio, content is filename/path
	public String targetId;
	public int time;
	public boolean isStored = false;
	public boolean isReaded = true;
	
	public int audioTime = 0;
	
	public int type;
	
	public ChatMessage(){
		
	}
	
	public static ChatMessage fromReciver(String content,String targetId,int type,int time){
		ChatMessage msg = new ChatMessage();
		msg.direction = MESSAGE_FROM;
		msg.content = content;
		msg.targetId = targetId;
		msg.time = time;
		msg.isReaded = false;
		return msg;
	}
	
	public static ChatMessage fromSender(int type,String content,String targetId){
		ChatMessage msg = new ChatMessage();
		msg.direction = MESSAGE_FROM;
		msg.type = type;
		msg.content = content;
		msg.targetId = targetId;
		Date date = new Date();
		msg.time = (int) (date.getTime() / 1000);
		return msg;
	}
	
	public static ChatMessage fromDatabase(int direction, String content,String targetId,int type,int time){
		ChatMessage msg = new ChatMessage();
		msg.direction = direction;
		msg.content = content;
		msg.targetId = targetId;
		msg.type = type;
		msg.time = time;
		msg.isStored = true;
		return msg;
	}
	
	public String getDateString(){
		Date date = new Date();
		date.setTime(time);
		return date.toLocaleString();
	}
}
