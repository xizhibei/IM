package com.qicq.im;

import com.qicq.im.app.LBSApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity{
	EditText uid,pwd;
	Button submit;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.login);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		uid = (EditText)findViewById(R.id.uid);
		pwd = (EditText)findViewById(R.id.pwd);
		submit = (Button)findViewById(R.id.submit);

		uid.setText("145@145.com");
		pwd.setText("145");

		OnClickListener listener = new OnClickListener(){
			public void onClick(View v) {
				SearchButtonProcess(v);
			}

		};

		submit.setOnClickListener(listener);

	}
	
	protected void SearchButtonProcess(View v) {
		if(submit.equals(v)){

			LBSApp app = (LBSApp)this.getApplication();
			int ret = app.login(uid.getText().toString(), pwd.getText().toString());
	        
			if(ret == 0){
				SharedPreferences sp = this.getSharedPreferences("SP", Context.MODE_PRIVATE);
				String cookie = app.getCookie();
		        Editor editor = sp.edit();
		        editor.putString("Cookie", cookie);
		        editor.commit();
		        
		        Log.v("Write Cookie",cookie);
		        
				Toast.makeText(this, "µÇÂ¼³É¹¦£¬»¶Ó­", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(LoginActivity.this,MainActivity.class);
				
				startActivity(intent);
				finish();
			}else{
				Toast.makeText(this, "µÇÂ½Ê§°Ü", Toast.LENGTH_LONG).show();
			}
		}

	}
}
