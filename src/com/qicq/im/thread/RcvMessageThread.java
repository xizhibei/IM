package com.qicq.im.thread;

import java.util.List;
import java.util.Vector;

import android.util.Log;

import com.qicq.im.api.APIManager;
import com.qicq.im.api.ChatMessage;
import com.qicq.im.msg.MsgRcvEvent;
import com.qicq.im.msg.MsgRcvListener;

public class RcvMessageThread extends AbstractMessageThread{
	private Vector<MsgRcvListener> listeners = new Vector<MsgRcvListener>();
	//private boolean continueRcv = true;
	//private APIManager api = null;
	public final static long MAX_TIME = 128000;
	public final static long MIN_TIME = 1000;
	//private long sleeptime = 1000;
	//private boolean networkConnect = true;
	//private boolean needPause = false;
	
	public RcvMessageThread(APIManager api){
		super(api);
		this.api = api;
	}
	
	public synchronized void addMsgRcvListener(MsgRcvListener e)
    {
		listeners.addElement(e);
    }
    
    public synchronized void removeMsgRcvListener(MsgRcvListener e)
    {
    	listeners.removeElement(e);
    }
    
    @SuppressWarnings("unchecked")
	protected void notifyAllEvent(List<ChatMessage> msgs)
    {
        Vector<MsgRcvListener> tempVector = null;
        MsgRcvEvent e = new MsgRcvEvent(this);
        synchronized(this)
        {
            tempVector=(Vector<MsgRcvListener>)listeners.clone();
            
            for(int i=0;i<tempVector.size();i++)
            {
            	MsgRcvListener l = (MsgRcvListener)tempVector.elementAt(i);
                l.onMsgRcved(e,msgs);
            }
        }
        
    }
    
    @Override
	public void run() {
    	Log.i("RcvMessageThread","Start");
		while(continuing){
			while(!networkConnect || needPause){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			List<ChatMessage> msgs = api.RcvMessage();
			
			if(msgs.size() != 0){
				notifyAllEvent(msgs);
				sleeptime /= 2;
				if(sleeptime < MIN_TIME)
					sleeptime = MIN_TIME;
			}else{
				sleeptime *= 2;
				if(sleeptime > MAX_TIME)
					sleeptime = MAX_TIME;
			}
				
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
