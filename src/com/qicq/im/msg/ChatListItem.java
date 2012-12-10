package com.qicq.im.msg;

import com.qicq.im.api.ChatMessage;
import com.qicq.im.api.User;

public class ChatListItem {
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
