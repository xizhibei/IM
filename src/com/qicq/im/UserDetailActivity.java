package com.qicq.im;

import com.qicq.im.api.User;
import com.qicq.im.app.LBSApp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class UserDetailActivity extends Activity {

	TextView name;
	TextView regdate;
	TextView age;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);
        
        name = (TextView) findViewById(R.id.user_detail_name);
        regdate = (TextView) findViewById(R.id.user_detail_regdate);
        age = (TextView) findViewById(R.id.user_detail_age);
        
        LBSApp app = (LBSApp)this.getApplication();
        String uid = getIntent().getStringExtra("uid");
        User user = app.getUser(uid);
        
        name.setText(user.name);
        regdate.setText(user.regdate);
        //age.setText(user.age);
    }
}

