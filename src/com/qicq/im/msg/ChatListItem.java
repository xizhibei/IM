package com.qicq.im.msg;

import com.qicq.im.api.ChatMessage;
import com.qicq.im.api.User;

public class ChatListItem {
	public static final int CHATLIST_ITEM_NORMAL = 1;
	public static final int CHATLIST_ITEM_HELLO = 2;
	public static final int CHATLIST_ITEM_REQUEST = 4;
	
	public static final String HELLO_ID = "-1";
	public static final String REQUEST_ID = "-2";
	
	public int type = 1;
	
	public int unreadCount = 0;
	//public String targetId;
	
	public ChatMessage msg;
	public User user;
	
	public ChatListItem(ChatMessage msg,User user){
		setCountAndMsg(msg);		
		this.user = user;
		//this.targetId = String.valueOf(user.uid);
	}
	
	public static ChatListItem fromDatabase(ChatMessage msg,User user,int count){
		ChatListItem tmp = new ChatListItem(msg,user);
		tmp.unreadCount = count;
		return tmp;
	}
	
	public void setCountAndMsg(ChatMessage msg){
		this.msg = msg;
		if(msg.direction == ChatMessage.MESSAGE_FROM && !msg.isReaded)
			unreadCount++;
	}
}
