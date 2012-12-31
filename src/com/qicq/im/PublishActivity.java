package com.qicq.im;

import java.util.ArrayList;
import java.util.Calendar;

import com.qicq.im.api.Demand;
import com.qicq.im.app.LBSApp;

import android.os.Bundle;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class PublishActivity extends Activity {

	private Spinner sex = null;
	private Spinner activity = null;
	private TextView startTimeView = null;
	private TextView endTimeView = null;
	private Button submit = null;
	private EditText detailEditText = null;

	private Calendar c;
	private int startH;
	private int startM;
	private int endH;
	private int endM;

	private ArrayList<String> activityItems = new ArrayList<String>();
	private ArrayList<String> sexItems = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publish);

		sex = (Spinner)findViewById(R.id.sexSpinner);
		activity = (Spinner)findViewById(R.id.activitySpinner);
		startTimeView = (TextView)findViewById(R.id.startTimeShow);
		endTimeView = (TextView)findViewById(R.id.endTimeShow);
		submit = (Button)findViewById(R.id.submit);
		detailEditText = (EditText) findViewById(R.id.detailEditText);

		c = Calendar.getInstance();
		startTimeView.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
		endTimeView.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + (15 + c.get(Calendar.MINUTE)));
		
		startTimeView.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				new TimePickerDialog(
						PublishActivity.this,
						new TimePickerDialog.OnTimeSetListener(){
							public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
								startTimeView.setText(hourOfDay + ":" + minute);
								startH = hourOfDay;
								startM = minute;
							}

						},
						c.get(Calendar.HOUR_OF_DAY),
						c.get(Calendar.MINUTE),
						true).show();
			}

		});

		endTimeView.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				new TimePickerDialog(
						PublishActivity.this,
						new TimePickerDialog.OnTimeSetListener(){
							public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
								endTimeView.setText(hourOfDay + ":" + minute);
								endH = hourOfDay;
								endM = minute;
							}

						},
						c.get(Calendar.HOUR_OF_DAY),
						c.get(Calendar.MINUTE) + 15,
						true).show();
			}

		});

		//		endTime.setIs24HourView(true);
		//		startTime.setIs24HourView(true);
		//		endTime.setCurrentMinute((endTime.getCurrentMinute() + 15) % 60);
		//		if((endTime.getCurrentMinute() + 15) >= 60)
		//			endTime.setCurrentHour((endTime.getCurrentHour() + 1) % 24);

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

		submit.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				SearchButtonProcess(v);
			}
		});
	}

	protected void SearchButtonProcess(View v) {
		if(v.equals(submit)){
			int start = startH * 60 + startM;
			int end = endH * 60 + endM;
			if(start >= end){
				Toast.makeText(this, "Error on start or end", Toast.LENGTH_LONG).show();
				return;
			}


			LBSApp app = (LBSApp)this.getApplication();
			int ret = app.PublishDemands(
					Demand.fromSender(
							activityItems.get(activity.getSelectedItemPosition()), 
							startH, 
							startM, 
							endH, 
							endM, 
							sex.getSelectedItemPosition(),
							detailEditText.getText().toString())
					);

			if(ret == 0){	
				Toast.makeText(this, "发布成功", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, "发布失败"+ret, Toast.LENGTH_LONG).show();
			}
		}

	}
}
