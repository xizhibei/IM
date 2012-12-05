package com.qicq.im;

import com.qicq.im.app.LBSApp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegActivity extends Activity{

	EditText name,email,pwd;
	Button submit;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.reg);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		name = (EditText)findViewById(R.id.name);
		email = (EditText)findViewById(R.id.email);
		pwd = (EditText)findViewById(R.id.pwd);
		submit = (Button)findViewById(R.id.submit);

		OnClickListener listener = new OnClickListener(){
			public void onClick(View v) {
				SearchButtonProcess(v);
			}

		};

		submit.setOnClickListener(listener);

	}
	protected void SearchButtonProcess(View v) {
		if(v.equals(submit)){
			LBSApp app = (LBSApp)this.getApplication();
			int ret = app.UserReg(name.getText().toString(), email.getText().toString(), pwd.getText().toString());


			if(ret == 0){
				Toast.makeText(this, "×¢²á³É¹¦£¬»¶Ó­", Toast.LENGTH_LONG).show();

				Intent intent = new Intent(RegActivity.this,LoginActivity.class);
				startActivity(intent);
				finish();
			}else{
				Toast.makeText(this, "×¢²áÊ§°Ü"+ret, Toast.LENGTH_LONG).show();
			}
		}

	}

}
