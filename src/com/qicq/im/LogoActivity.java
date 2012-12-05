package com.qicq.im;

import com.qicq.im.app.LBSApp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LogoActivity extends Activity{
	
	Button login = null;
	Button reg = null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.logo);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		
		LBSApp app = (LBSApp)this.getApplication();
//		if(app.isUserLogin()){
//			Intent intent = new Intent(LogoActivity.this,MainActivity.class);
//			startActivity(intent);
//			finish();
//		}
		if(!app.isNeedLogin()){			
			Toast.makeText(this, "ª∂”≠ªÿ¿¥", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(LogoActivity.this,MainActivity.class);
			startActivity(intent);
			finish();
		}
		
		login = (Button)findViewById(R.id.login);
		reg = (Button)findViewById(R.id.reg);

		OnClickListener listener = new OnClickListener(){
			public void onClick(View v) {
				SearchButtonProcess(v);
			}

		};
		
		login.setOnClickListener(listener);
		reg.setOnClickListener(listener);
		
//		ImageView iv = (ImageView)findViewById(R.id.logo_bg);
//		
//		AlphaAnimation aa = new AlphaAnimation(0.1f,1.0f);
//		aa.setDuration(1000);
//		iv.startAnimation(aa);
//		
//		aa.setAnimationListener(new AnimationListener(){
//			public void onAnimationEnd(Animation arg0) {
//				//Intent intent = new Intent(LogoActivity.this,TestActivity.class);
//				Intent intent = new Intent(LogoActivity.this,MainActivity.class);
//				startActivity(intent);
//				finish();
//			}
//			public void onAnimationRepeat(Animation arg0) {				
//			}
//
//			public void onAnimationStart(Animation arg0) {
//			}			
//		});
	}
	protected void SearchButtonProcess(View v) {
		if(v.equals(login)){
			Intent intent = new Intent(LogoActivity.this,LoginActivity.class);
			startActivity(intent);
			finish();
		}
		else if(v.equals(reg)){
			Intent intent = new Intent(LogoActivity.this,RegActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
