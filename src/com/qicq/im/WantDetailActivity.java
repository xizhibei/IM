package com.qicq.im;


import com.qicq.im.api.User;
import com.qicq.im.app.LBSApp;
import com.qicq.im.popwin.LBSToast;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WantDetailActivity extends Activity {

	TextView name;
	TextView type;
	TextView start;
	TextView end;
	TextView gender;
	TextView descrip;
	TextView userinfo;
	ImageView avatar;
	Button request;

	User user;
	User me;
	private LBSApp app;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.want_detail);
		app = (LBSApp) this.getApplication();

		name = (TextView) findViewById(R.id.want_detail_username);
		type = (TextView) findViewById(R.id.want_detail_type);
		start = (TextView) findViewById(R.id.want_detail_start);
		end = (TextView) findViewById(R.id.want_detail_end);
		gender = (TextView) findViewById(R.id.want_detail_gender);
		descrip = (TextView) findViewById(R.id.want_detail_descip);
		userinfo = (TextView) findViewById(R.id.want_detail_user_gender_and_age);
		request = (Button) findViewById(R.id.want_detail_request);

		avatar = (ImageView)findViewById(R.id.want_detail_avatar);

		String uid = getIntent().getStringExtra("uid");
		user = app.getUser(uid);
		me = app.getUser();

		name.setText(user.name);
		type.setText(user.demand.name);
		start.setText(user.demand.getStartTime());
		end.setText(user.demand.getExpireTime());
		gender.setText(user.demand.getSextype());
		descrip.setText(user.demand.detail);
		avatar.setImageDrawable(user.getAvatar());
		userinfo.setText(user.age + " " + user.sex);

		avatar.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				Intent i = new Intent(WantDetailActivity.this, UserDetailActivity.class);
				i.putExtra("uid", String.valueOf(user.uid));
				WantDetailActivity.this.startActivity(i);
			}
		});
		request.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(user.uid != me.uid)
					new SendRequestTask().execute(user.demand.did,user.uid);
			}

		});
	}

	private class SendRequestTask extends AsyncTask<Integer, Void, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			return app.sendRequestForDemand(params[0],params[1]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(result == 0)
				LBSToast.makeText(WantDetailActivity.this, "发送成功", Toast.LENGTH_LONG).show();
			else
				LBSToast.makeText(WantDetailActivity.this, "发送失败", Toast.LENGTH_LONG).show();
		}

	}

}

