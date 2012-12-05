package com.qicq.im.thread;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.qicq.im.api.APIManager;
import com.qicq.im.api.ChatMessage;

public class SendMessageThread extends AbstractMessageThread{

	private List<ChatMessage> msgs = new ArrayList<ChatMessage>();
	//private APIManager api = null;
	//private boolean continueSend = true;
	//private long sleeptime = 100;
	//private boolean networkConnect = true;
	//private boolean needPause = false;
	
	public SendMessageThread(APIManager api){
		super(api);
		this.api = api;
		sleeptime = 100;
	}
	public void addMsgs(ChatMessage msg){
		msgs.add(msg);
	}
	
	public void setSleepTime(long time){
		sleeptime = time;
	}
	
	@Override
	public void run() {
		Log.i("SendMessageThread","Start");
		while(continuing){
			while(!networkConnect || needPause){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			while(!msgs.isEmpty()){
				api.SendMessage(msgs.get(0));
				msgs.remove(0);
			}
			
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
