package com.qicq.im.Utilities;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
}
