package com.qicq.im.thread;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.qicq.im.api.APIManager;
import com.qicq.im.api.ChatMessage;

public class SendMessageThread extends AbstractMessageThread{

	private List<ChatMessage> msgs = new ArrayList<ChatMessage>();
	
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
					Log.e("SendMessageThread",e.getMessage());
				}
			}
			while(!msgs.isEmpty()){
				int ret = api.SendMessage(msgs.get(0));
				if(ret == 0)
					msgs.remove(0);
			}
			
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				Log.e("SendMessageThread",e.getMessage());
			}
		}
	}

}
