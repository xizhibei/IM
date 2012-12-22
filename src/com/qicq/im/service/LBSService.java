package com.qicq.im.service;

import java.util.List;
import com.qicq.im.MainActivity;
import com.qicq.im.R;
import com.qicq.im.api.APIManager;
import com.qicq.im.api.ChatMessage;
import com.qicq.im.api.User;
import com.qicq.im.config.UserConfig;
import com.qicq.im.db.ChatListModel;
import com.qicq.im.db.MsgModel;
import com.qicq.im.db.UserModel;
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

	public User user = null;

	public RcvMessageThread rcvMsgThread = null;
	public SendMessageThread sendMsgThread = null;
	private NetworkMonitorThread networkMonitorThread = null;

	public APIManager api = null;
	public UserConfig userConfig;
	private LBSBinder binder = null;

	private NotificationManager notificationManager;

	public String talkingToId = null;
	//private boolean isStarted = false;

	public UserModel userModel;
	public MsgModel msgModel;
	public ChatListModel chatListModel;

	public class LBSBinder extends Binder {
		public LBSService getService() {
			return LBSService.this;
		}
	}

	//	private Handler handler = new Handler() {
	//		public void handleMessage(Message msg) {
	//
	//		};
	//	};

	@Override
	public IBinder onBind(Intent intent) {
		Log.v("Service","onBind");
		if (null == binder)
			binder = new LBSBinder();

		if(userConfig == null)
			userConfig = new UserConfig(this,"config");

		String cookie = userConfig.getCookie();
		if(cookie != null)
			api.setCookie(cookie);

		String uid = userConfig.getUid();
		if(uid != null && !isDatabaseOpened())
			initDatabase(userConfig.getUid());

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("Service","onCreate");
		api = new APIManager(getResources().getString(R.string.HOSTADDR));

		rcvMsgThread = new RcvMessageThread(api);
		sendMsgThread = new SendMessageThread(api);
		networkMonitorThread = new NetworkMonitorThread(this);

		rcvMsgThread.addMsgRcvListener(new MsgRcvListener() {

			public void onMsgRcved(MsgRcvEvent e, List<ChatMessage> msgs) {
				if (userConfig.isShowNotification()) {
					ChatMessage m = msgs.get(msgs.size() - 1);
					if(!m.targetId.equals(talkingToId)){
						User u = getUser(m.targetId);
						showNotification(R.drawable.card,"ÐÂÏûÏ¢",u.name,m.content);
					}
				}
				msgModel.insertAll(msgs);
				chatListModel.updateNewMsg(msgs);
				for(ChatMessage m : msgs){
					getUser(m.targetId);//Just update database to match chatlist
				}
			}
		});

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
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.v("Service","onStart");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.v("Service","onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	public boolean isDatabaseOpened(){
		return userModel != null && msgModel != null && chatListModel != null;
	}

	public void initDatabase(String uid){
		userModel = new UserModel(this,uid);
		msgModel = new MsgModel(this,uid);
		chatListModel = new ChatListModel(this,uid);
	}

	public void closeDatabase(){
		userModel.getDB().close();
		userModel = null;
		msgModel = null;
		chatListModel = null;
	}

	@Override
	public void onDestroy() {		
		rcvMsgThread.setStop();
		sendMsgThread.setStop();
		networkMonitorThread.setStop();
		super.onDestroy();
	}



	public LBSBinder getBinder() {
		return binder;
	}

	public void sendMessage(ChatMessage msg){
		sendMsgThread.addMsgs(msg);
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

	//	public boolean isStarted() {
	//		return isStarted;
	//	}
	//
	//	public void setStarted(boolean isStarted) {
	//		this.isStarted = isStarted;
	//	}

	public User getUser(){
		return getUser(userConfig.getUid());
	}

	public User getUser(String uid){
		User u = userModel.getUser(uid);
		if(u == null || userConfig.isFriendNeedUpdate()){
			User tmp = api.getUser(uid);
			if(tmp != null){
				u = tmp;
				userModel.updateUser(tmp);
			}
		}
		return u;
	}
}
