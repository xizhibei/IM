package com.qicq.im.popwin;

import com.qicq.im.R;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class LBSToast extends Toast{
	public LBSToast(Context context){
		super(context);
	}
	
	public LBSToast(Context context,CharSequence content){
		super(context);
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		   View layout = inflater.inflate(R.layout.lbstoast,
		     (ViewGroup) ((Activity) context).findViewById(R.id.toast_root));
		   TextView text = (TextView) layout.findViewById(R.id.toast_content);
		   text.setText(content);
		   
		   this.setGravity(Gravity.CENTER, 0, 0);
		   this.setView(layout);
		   this.setDuration(Toast.LENGTH_LONG);
	}
	
	public LBSToast(Context context,CharSequence content, int duration) {
		super(context);
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		   View layout = inflater.inflate(R.layout.lbstoast,
		     (ViewGroup) ((Activity) context).findViewById(R.id.toast_root));
		   TextView text = (TextView) layout.findViewById(R.id.toast_content);
		   text.setText(content);
		   
		   this.setGravity(Gravity.CENTER, 0, 0);
		   this.setView(layout);
		   this.setDuration(duration);
	}
	
	public static LBSToast makeText(Context context,CharSequence text, int duration){
		return new LBSToast(context,text,duration);
	}

}
