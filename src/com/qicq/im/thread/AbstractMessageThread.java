package com.qicq.im.thread;

import android.util.Log;

import com.qicq.im.api.APIManager;

public abstract class AbstractMessageThread extends Thread{
	protected APIManager api = null;
	protected long sleeptime = 1000;
	protected static boolean networkConnect = true;
	protected boolean needPause = false;
	protected boolean continuing = true;
	
	public AbstractMessageThread(APIManager api){
		this.api = api;
	}
	
	public void setNeedPause(boolean needPause){
		this.needPause = needPause;
	}
	public void setStop(){
		continuing = false;
	}
	
	public synchronized void setNetworkState(boolean state){
		networkConnect = state;
		if(state)
			Log.i("MessageThread","Enabled");
		else
			Log.i("MessageThread","Disabled");
	}
	
	public void setSleepTime(long time){
		sleeptime = time;
	}
}
