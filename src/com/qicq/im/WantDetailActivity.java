package com.qicq.im;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class WantDetailActivity extends Activity {

	TextView name;
	TextView type;
	TextView start;
	TextView end;
	TextView gender;
	TextView descrip;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.want_detail);
        
        name = (TextView) findViewById(R.id.want_detail_username);
        type = (TextView) findViewById(R.id.want_detail_type);
        start = (TextView) findViewById(R.id.want_detail_start);
        end = (TextView) findViewById(R.id.want_detail_end);
        gender = (TextView) findViewById(R.id.want_detail_gender);
        descrip = (TextView) findViewById(R.id.want_detail_descip);
        
        
    }
}

