package com.qicq.im.msg;

import java.util.EventListener;
import java.util.List;

import com.qicq.im.api.ChatMessage;

public interface MsgRcvListener extends EventListener {
	public void onMsgRcved(List<ChatMessage> msgs);
	
	/**
	 * When special messages received, such as HELLO and demand request
	 * @param msgs
	 */
	public void onHelloMsgRcved(List<ChatMessage> msgs);
	
	public void onRequestMsgRcved(List<ChatMessage> msgs);
}
