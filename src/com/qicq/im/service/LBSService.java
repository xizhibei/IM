package com.qicq.im.service;

import java.lang.Thread.State;
import java.util.List;
import com.qicq.im.MainActivity;
import com.qicq.im.R;
import com.qicq.im.api.APIManager;
import com.qicq.im.api.ChatMessage;
import com.qicq.im.api.User;
import com.qicq.im.config.SysConfig;
import com.qicq.im.config.UserConfig;
import com.qicq.im.db.DBUtil;
import com.qicq.im.msg.MsgRcvEvent;
import com.qicq.im.msg.MsgRcvListener;
import com.qicq.im.thread.NetworkMonitorThread;
import com.qicq.im.thread.RcvMessageThread;
import com.qicq.im.thread.SendMessageThread;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * 
 * @author Xu Zhipei
 * @Date 2012-12-13
 *
 */
@SuppressLint("HandlerLeak")
public class LBSService extends Service {

	public RcvMessageThread rcvMsgThread = null;
	public SendMessageThread sendMsgThread = null;
	private NetworkMonitorThread networkMonitorThread = null;

	public APIManager api = null;
	public UserConfig userConfig;
	private LBSBinder binder = null;

	private NotificationManager notificationManager;

	public String talkingToId = null;
	
	public DBUtil dbUtil;

	public class LBSBinder extends Binder {
		public LBSService getService() {
			return LBSService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v("Service","onBind");
		if (null == binder)
			binder = new LBSBinder();

		if(userConfig == null)
			userConfig = new UserConfig(this,"config");

		String uid = userConfig.getUid();
		if(uid != null && dbUtil == null)
			initDatabase(userConfig.getUid());

		if(sendMsgThread == null)
			sendMsgThread = new SendMessageThread(this,api,dbUtil);
		if(rcvMsgThread == null)
			rcvMsgThread = new RcvMessageThread(api);
		
		rcvMsgThread.addMsgRcvListener(new MsgRcvListener() {

			public void onMsgRcved(MsgRcvEvent e, List<ChatMessage> msgs) {
				if (userConfig.isShowNotification()) {
					ChatMessage m = msgs.get(msgs.size() - 1);
					if(!m.targetId.equals(talkingToId)){
						User u = getUser(m.targetId);
						showNotification(R.drawable.card,"ÐÂÏûÏ¢",u.name,m.content);
					}
				}
				dbUtil.insertAllMsg(msgs);
				dbUtil.updateChatListNewMsg(msgs);
				for(ChatMessage m : msgs){
					getUser(m.targetId);//Just update database to match chatlist
				}
			}
		});
		
		String cookie = userConfig.getCookie();
		if(cookie != null){
			api.setCookie(cookie);
			if(rcvMsgThread.getState() == State.NEW)
				rcvMsgThread.start();
			if(sendMsgThread.getState() == State.NEW)
				sendMsgThread.start();
		}
				
		if(networkMonitorThread == null)
			networkMonitorThread = new NetworkMonitorThread(this);

		networkMonitorThread.addNetworkStateListener(new NetworkStateListener(){

			public void onNetworkUnconnected() {
				if(rcvMsgThread.isAlive())
					rcvMsgThread.setNetworkState(false);
				if(sendMsgThread.isAlive())
					sendMsgThread.setNetworkState(false);
			}

			public void onNetworkConnected() {
				if(rcvMsgThread.isAlive())
					rcvMsgThread.setNetworkState(true);
				if(sendMsgThread.isAlive())
					sendMsgThread.setNetworkState(true);
			}

		});
		networkMonitorThread.start();
		
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("Service","onCreate");
		if(api == null)
			api = new APIManager(SysConfig.API_SERVER_ADDR);		
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.v("Service","onStart id = " + startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.v("Service","onStartCommand id = " + startId + " flag = " + flags);
		return super.onStartCommand(intent, flags, startId);
	}

	public void initDatabase(String uid){
		dbUtil = new DBUtil(getApplicationContext(),uid);
	}
	public void closeDatabase(){
		dbUtil.close();
	}

	@Override
	public void onDestroy() {
		Log.v("LBSService","Destory");
		rcvMsgThread.setStop();
		sendMsgThread.setStop();
		networkMonitorThread.setStop();
		super.onDestroy();
	}

	public LBSBinder getBinder() {
		return binder;
	}

	public void showNotification(int icon, String tickertext, String title,
			String content) {
		Notification notification = new Notification(icon, tickertext,
				System.currentTimeMillis());

		int defaults = Notification.DEFAULT_LIGHTS;
		if(userConfig.isNeedVibration())
			defaults = defaults | Notification.DEFAULT_VIBRATE;
		if(userConfig.isNeedSound())
			defaults = defaults | Notification.DEFAULT_SOUND;

		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		Intent i = new Intent(this,MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pt = PendingIntent.getActivity(this, 0,i, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(this, title, content, pt);
		notificationManager.notify(R.string.app_name, notification);

	}

	public User getUser(){
		return getUser(userConfig.getUid());
	}

	public User getUser(String uid){
		User u = dbUtil.getUser(uid);
		if(u == null || userConfig.isFriendNeedUpdate()){
			User tmp = api.getUser(uid);
			if(tmp != null){
				u = tmp;
				dbUtil.updateUser(tmp);
				userConfig.setFriendUpdate();
			}
		}
		return u;
	}
}
