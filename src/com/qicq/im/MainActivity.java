package com.qicq.im;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity {
	
	//private LBSApp app;
	
	private class TabView extends LinearLayout {
		ImageView imageView;

		public TabView(Context c, int drawable, int drawableselec) {
			super(c);
			imageView = new ImageView(c);
			StateListDrawable listDrawable = new StateListDrawable();
			listDrawable.addState(SELECTED_STATE_SET, this.getResources()
					.getDrawable(drawableselec));
			listDrawable.addState(ENABLED_STATE_SET, this.getResources()
					.getDrawable(drawable));
			imageView.setImageDrawable(listDrawable);
			imageView.setBackgroundColor(Color.TRANSPARENT);
			setGravity(Gravity.CENTER);
			addView(imageView);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.tab);
		TabHost tabHost = getTabHost();
		
		//app = (LBSApp)this.getApplication();
		
		TabView view = null;

		view = new TabView(this, R.drawable.location,
				R.drawable.location_enabled);
		view.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.tab_bg));
		TabSpec recentContactSpec = tabHost.newTabSpec("Location");
		recentContactSpec.setIndicator(view);
		Intent recentContactIntent = new Intent(this, NearbyActivity.class);
		recentContactSpec.setContent(recentContactIntent);

		view = new TabView(this, R.drawable.msg, R.drawable.msg_enabled);
		view.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.tab_bg));
		TabSpec contactBookSpec = tabHost.newTabSpec("Message");
		contactBookSpec.setIndicator(view);
		Intent contactBookIntent = new Intent(this, MsgListActivity.class);
		contactBookSpec.setContent(contactBookIntent);

		view = new TabView(this, R.drawable.people, R.drawable.people_enabled);
		view.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.tab_bg));
		TabSpec smsMessageSpec = tabHost.newTabSpec("Friend");
		smsMessageSpec.setIndicator(view);
		Intent smsMessageIntent = new Intent(this, FriendActivity.class);
		smsMessageSpec.setContent(smsMessageIntent);

		view = new TabView(this, R.drawable.config, R.drawable.config_enabled);
		view.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.tab_bg));
		TabSpec settingSpec = tabHost.newTabSpec("Setting");
		settingSpec.setIndicator(view);
		Intent settingIntent = new Intent(this, ConfigActivity.class);
		settingSpec.setContent(settingIntent);

		tabHost.addTab(recentContactSpec);
		tabHost.addTab(contactBookSpec);
		tabHost.addTab(smsMessageSpec);
		tabHost.addTab(settingSpec);
		
		tabHost.setCurrentTab(0);	
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//app.unbindService();
	}
}
