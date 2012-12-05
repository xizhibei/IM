package com.qicq.im.config;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class UserConfig {

	private SharedPreferences sp;
	private Editor editor;
	
	private final static String FIELD_COOKIE = "Cookie";
	private final static String FIELD_UID = "Uid";
	
	private final static String FIELD_FRIEND_UPDATE_TIME = "friend_upate";
	private final static String FIELD_NEARBY_UPDATE_TIME = "nearby_update";
	
	private final static String FIELD_SHOW_Notification = "show_notification";
	private final static String FIELD_NEED_SOUND = "needSound";
	private final static String FIELD_NEED_VIBRATION = "needVibration";
	
	private final static String NULL = null;
	private final static int UPDATE_INTERVAL = 900;//15 minutes
	
	public UserConfig(Context context,String filename){
		sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	
	public static String getCookie(Context context,String filename){
		SharedPreferences sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
		return sp.getString(FIELD_COOKIE, NULL);
	}
	
	public static void setCookie(Context context,String filename,String cookie) {
		SharedPreferences sp = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		if(cookie == null)
			editor.remove(FIELD_COOKIE);
		else
			editor.putString(FIELD_COOKIE, cookie);
        editor.commit();
	}
	
	public String getCookie() {
		return sp.getString(FIELD_COOKIE, NULL);
	}

	public void setCookie(String cookie) {
		if(cookie == null)
			editor.remove(FIELD_COOKIE);
		else
			editor.putString(FIELD_COOKIE, cookie);
        editor.commit();
	}

	public String getUid() {
		return sp.getString(FIELD_UID, NULL);
	}

	public void setUid(String uid) {
        editor.putString(FIELD_UID, uid);
        editor.commit();
	}
	
	public boolean isShowNotification(){
		return sp.getBoolean(FIELD_SHOW_Notification, true);
	}
	
	public void setShowNotification(boolean show){
		editor.putBoolean(FIELD_SHOW_Notification, show);
        editor.commit();
	}
	
	public boolean isNeedSound(){
		return sp.getBoolean(FIELD_NEED_SOUND, true);
	}
	
	public void setNeedSound(boolean show){
		editor.putBoolean(FIELD_NEED_SOUND, show);
        editor.commit();
	}
	
	public boolean isNeedVibration(){
		return sp.getBoolean(FIELD_NEED_VIBRATION, true);
	}
	
	public void setNeedVibration(boolean show){
		editor.putBoolean(FIELD_NEED_VIBRATION, show);
        editor.commit();
	}
	
	private boolean isNeedUpdate(String key){
		int oldtime = sp.getInt(key, -1);
		if(oldtime == -1)
			return true;
		Date date = new Date();
		Log.v("Userconfig","Time compare "+ date.getTime() / 1000 + " " + oldtime);
		return date.getTime() / 1000 - oldtime > UPDATE_INTERVAL;
	}
	
	private void setUpdate(String key){
		Date date = new Date();
        editor.putInt(key, (int) (date.getTime() / 1000));
        editor.commit();
	}
	
	public boolean isFriendNeedUpdate() {
		return isNeedUpdate(FIELD_FRIEND_UPDATE_TIME);		
	}

	public void setFriendUpdate() {
		setUpdate(FIELD_FRIEND_UPDATE_TIME);
	}
	
	public boolean isNearbyNeedUpdate() {
		return isNeedUpdate(FIELD_NEARBY_UPDATE_TIME);		
	}

	public void setNearbyUpdate() {
		setUpdate(FIELD_NEARBY_UPDATE_TIME);
	}
}
