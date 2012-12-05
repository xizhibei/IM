package com.qicq.im.popwin;

import com.qicq.im.R;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class NearbyOptionPopupWindow extends PopupWindow{
	private ViewGroup root;
	private int gender;
	private String updateTime;
	private Button submit;
	
	public NearbyOptionPopupWindow(Context context){
		super(((Activity) context).getLayoutInflater().inflate(R.layout.nearby_option_pop, null),
				LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		
		root = (ViewGroup) (((Activity) context).findViewById(R.id.main_root));
		View popView = ((Activity) context).getLayoutInflater().inflate(R.layout.nearby_option_pop, null);
		popView.findViewById(R.id.nearby_option_root).setVisibility(View.VISIBLE);
		popView.setBackgroundResource(R.drawable.pls_talk);
		
		setContentView(popView);
		//setHeight(LayoutParams.WRAP_CONTENT);
		//setWidth(LayoutParams.WRAP_CONTENT);
		
		submit = (Button)popView.findViewById(R.id.submit);
		
		
		RadioGroup g = (RadioGroup)popView.findViewById(R.id.nearby_option_gender);
		final RadioButton all=(RadioButton)popView.findViewById(R.id.gender_all);  
		final RadioButton male=(RadioButton)popView.findViewById(R.id.gender_male);  
		final RadioButton female=(RadioButton)popView.findViewById(R.id.gender_female);
		
		g.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == all.getId())
					gender = 0;
				else if(checkedId == male.getId())
					gender = 1;
				else if(checkedId == female.getId())
					gender = 2;
			}

		});
		
		RadioGroup t = (RadioGroup)popView.findViewById(R.id.nearby_option_time);
		final RadioButton m15=(RadioButton)popView.findViewById(R.id.time_15m);  
		final RadioButton h1=(RadioButton)popView.findViewById(R.id.time_1h);
		
		t.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == m15.getId())
					updateTime = "15m";
				else if(checkedId == h1.getId())
					updateTime = "1h";
			}

		});
		
	}
	
	public void show(){
		showAtLocation(root, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	public int getGender() {
		return gender;
	}

	public String getUpdateTime() {
		return updateTime;
	}
	
	public void setGender(int gender) {
		this.gender = gender;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	public void setOnOk(OnClickListener l){
		submit.setOnClickListener(l);
	}
}
