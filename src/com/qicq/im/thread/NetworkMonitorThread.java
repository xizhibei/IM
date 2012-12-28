package com.qicq.im.thread;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Vector;

import com.qicq.im.config.SysConfig;
import com.qicq.im.service.NetworkStateListener;

import android.content.Context;
import android.util.Log;

public class NetworkMonitorThread extends Thread{

	private Vector<NetworkStateListener> listeners = new Vector<NetworkStateListener>();
	private boolean continueMonit = true;
	private boolean isConnect = true;
	//private Context context = null;
	private long sleeptime = 10000;
	private String addr;

	public NetworkMonitorThread(Context context){
		//this.context = context;
		addr = SysConfig.API_SERVER_ADDR;
		addr = addr.substring(7);
	}

	public void setStop(){
		continueMonit = false;
	}

	public void setSleepTime(long time){
		sleeptime = time;
	}

	public synchronized void addNetworkStateListener(NetworkStateListener e)
	{
		listeners.addElement(e);
	}

	public synchronized void removeNetworkStateListener(NetworkStateListener e)
	{
		listeners.removeElement(e);
	}

	@SuppressWarnings("unchecked")
	protected void notifyAllEventUnconnect()
	{
		Vector<NetworkStateListener> tempVector = null;
		synchronized(this)
		{
			tempVector=(Vector<NetworkStateListener>)listeners.clone();

			for(int i=0;i<tempVector.size();i++)
			{
				NetworkStateListener l = (NetworkStateListener)tempVector.elementAt(i);
				l.onNetworkUnconnected();
			}
		}

	}
	@SuppressWarnings("unchecked")
	protected void notifyAllEventConnect()
	{
		Vector<NetworkStateListener> tempVector = null;
		synchronized(this)
		{
			tempVector=(Vector<NetworkStateListener>)listeners.clone();

			for(int i=0;i<tempVector.size();i++)
			{
				NetworkStateListener l = (NetworkStateListener)tempVector.elementAt(i);
				l.onNetworkConnected();
			}
		}

	}

	boolean isReachable(){
		try {
			return InetAddress.getByName(addr).isReachable(3000);
		} catch (IOException e) {
			Log.e("NetworkMonitorThread",e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void run() {		
		while(continueMonit){
			if(! isReachable() ){
				if(isConnect){
					Log.e("Network state","Disconnected");
					notifyAllEventUnconnect();				
				}
				isConnect = false;
			}else{
				if(!isConnect){
					Log.e("Network state","Connected");
					notifyAllEventConnect();					
				}
				isConnect = true;
			}
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
