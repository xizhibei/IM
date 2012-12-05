package com.qicq.im;

import java.util.ArrayList;

import com.qicq.im.app.LBSApp;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class PublishActivity extends Activity {

	private Spinner sex = null;
	private Spinner activity = null;
	private TimePicker startTime = null;
	private TimePicker endTime = null;
	private Button submit = null;

	private ArrayList<String> activityItems = new ArrayList<String>();
	private ArrayList<String> sexItems = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publish);

		sex = (Spinner)findViewById(R.id.sexSpinner);
		activity = (Spinner)findViewById(R.id.activitySpinner);
		startTime = (TimePicker)findViewById(R.id.startTimePicker);
		endTime = (TimePicker)findViewById(R.id.endTimePicker);
		submit = (Button)findViewById(R.id.submit);

		endTime.setIs24HourView(true);
		startTime.setIs24HourView(true);
		endTime.setCurrentMinute((endTime.getCurrentMinute() + 15) % 60);
		if((endTime.getCurrentMinute() + 15) >= 60)
			endTime.setCurrentHour((endTime.getCurrentHour() + 1) % 24);

		activityItems.add("看电影");
		activityItems.add("吃饭");
		activityItems.add("KTV");
		activityItems.add("拼车");
		activityItems.add("逛街");

		activity.setAdapter(new BaseAdapter(){

			public int getCount() {
				return activityItems.size();
			}

			public Object getItem(int position) {
				return null;
			}

			public long getItemId(int position) {
				return 0;
			}

			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout ll = new LinearLayout(PublishActivity.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				TextView tv = new TextView(PublishActivity.this);
				tv.setText(activityItems.get(position));
				tv.setTextSize(27);
				tv.setTextColor(Color.BLACK);
				ll.addView(tv);
				return ll;
			}

		});
		activity.setOnItemSelectedListener( new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
			}

			public void onNothingSelected(AdapterView<?> arg0) {				
			}

		});


		sexItems.add("男");
		sexItems.add("女");
		sexItems.add("均可");
		sex.setAdapter(new BaseAdapter(){

			public int getCount() {
				return sexItems.size();
			}

			public Object getItem(int position) {
				return null;
			}

			public long getItemId(int position) {
				return 0;
			}

			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout ll = new LinearLayout(PublishActivity.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				TextView tv = new TextView(PublishActivity.this);
				tv.setText(sexItems.get(position));
				tv.setTextSize(27);
				tv.setTextColor(Color.BLACK);
				ll.addView(tv);
				return ll;
			}

		});
		sex.setOnItemSelectedListener( new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
			}

			public void onNothingSelected(AdapterView<?> arg0) {				
			}

		});

		OnClickListener listener = new OnClickListener(){
			public void onClick(View v) {
				SearchButtonProcess(v);
			}
		};

		submit.setOnClickListener(listener);
	}

	protected void SearchButtonProcess(View v) {
		if(v.equals(submit)){
			int start = startTime.getCurrentHour() * 60 + startTime.getCurrentMinute();
			int end = endTime.getCurrentHour() * 60 + endTime.getCurrentMinute();
			if(start > end){
				Toast.makeText(this, "Error on start or end", Toast.LENGTH_LONG).show();
				return;
			}


			LBSApp app = (LBSApp)this.getApplication();
			int ret = app.PublishDemands(
					activityItems.get(activity.getSelectedItemPosition()), 
					startTime.getCurrentHour().toString(), 
					startTime.getCurrentMinute().toString(), 
					endTime.getCurrentHour().toString(), 
					endTime.getCurrentMinute().toString(), 
					String.valueOf(sex.getSelectedItemPosition()) 
					);

			if(ret == 0){	
				Toast.makeText(this, "发布成功", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, "发布失败"+ret, Toast.LENGTH_LONG).show();
			}
		}

	}
}
