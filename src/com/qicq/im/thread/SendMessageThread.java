package com.qicq.im.thread;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.qicq.im.api.APIManager;
import com.qicq.im.api.ChatMessage;
import com.qicq.im.config.SysConfig;
import com.qicq.im.db.MsgSendTaskModel;

public class SendMessageThread extends AbstractMessageThread{

	private MsgSendTaskModel task;
	private Context context;

	public SendMessageThread(APIManager api,Context context,MsgSendTaskModel task){
		super(api);
		this.api = api;
		sleeptime = 1000;
		this.task = task;
		this.context = context;
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

			while(true){
				ChatMessage msg = task.fetchRow();
				if(msg == null)
					break;
				
				if(msg.type == ChatMessage.MESSAGE_TYPE_VOICE || msg.type == ChatMessage.MESSAGE_TYPE_IMAGE){
					int fid = api.UploadFile(msg.content);
					msg.content = String.valueOf(fid);
				}
				int ret = api.SendMessage(msg);
				if(ret == 0){
					task.delete(msg.mid);
					Intent intent = new Intent(SysConfig.BROADCAST_SEND_MSG_ACTION);  
					intent.putExtra("mid", msg.mid);
					context.sendBroadcast(intent);
				}
			}
			//			while(!msgs.isEmpty()){
			//				int ret = api.SendMessage(msgs.get(0));
			//				if(ret == 0)
			//					msgs.remove(0);
			//			}

			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				Log.e("SendMessageThread",e.getMessage());
			}
		}
	}

}
