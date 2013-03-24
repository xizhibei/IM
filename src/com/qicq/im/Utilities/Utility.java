package com.qicq.im.Utilities;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

public class Utility {
	public static boolean isServiceRunning(Context context, String className) {

		boolean isRunning = false;
		ActivityManager activityManager =
				(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceList
		= activityManager.getRunningServices(Integer.MAX_VALUE);

		if (!(serviceList.size() > 0)) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	public static boolean isNetworkAvailable(Context context) {  

		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
		if (connectivity == null) {  
			Log.e("ConnectivityManager", "Null error!!!");
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();  
			if (info != null) {  
				for (int i = 0; i < info.length; i++) {  
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {  
						return true;  
					}  
				}  
			}  
		}  
		return false;  
	}
	
	public static int getDatetime(){
		return (int) (new Date().getTime()/1000);
	}
	
	public static String getMD5(String str){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] b = str.getBytes("UTF8");  
	        byte[] hash = md.digest(b);  
	        String pwd = Base64.encodeToString( hash,Base64.CRLF);
	        return pwd;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  
        return null;
	}
	
	public static String getRandomFileName(){
		Date date = new Date();
		return String.valueOf(date.getTime());
	}
}
