package com.qicq.im.msg;

import java.util.EventListener;
import java.util.List;

import com.qicq.im.api.ChatMessage;

public interface MsgRcvListener extends EventListener {
	public void onMsgRcved(MsgRcvEvent e,List<ChatMessage> msgs);
}
