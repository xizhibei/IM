package com.qicq.im;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qicq.im.app.LBSApp;
import com.qicq.im.config.SysConfig;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ConfigActivity extends ListActivity{

	LBSApp app;
	//ListView configLV = null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		//setContentView(R.layout.config);

		//configLV = (ListView)findViewById(R.id.configListView);

		SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.configitem,
				new String[]{"title","info","img"},
				new int[]{R.id.title,R.id.info,R.id.img});
		setListAdapter(adapter);
		app = (LBSApp)ConfigActivity.this.getApplication();

		getListView().setOnItemClickListener( new OnItemClickListener(){

			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				if(position == 0){

					app.logout();

					Intent intent = new Intent(ConfigActivity.this,LoginActivity.class);

					startActivity(intent);
					finish();
				}else if(position == 1){
					Intent mIntent = new Intent("/");
					ComponentName comp = new ComponentName("com.android.settings",
							"com.android.settings.WirelessSettings");
					mIntent.setComponent(comp);
					mIntent.setAction("android.intent.action.VIEW");
					startActivity(mIntent);
				}else if(position == 2){
					Intent i = new Intent();
					i.setType("image/*");
					i.setAction(Intent.ACTION_GET_CONTENT);
					i.addCategory(Intent.CATEGORY_OPENABLE); 
					startActivityForResult(i, 1);
				}else if(position == 3){
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File file = new File(SysConfig.IMAGE_CAPTURE_PATH);
					if(!file.exists())
						file.mkdirs();
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(file, "tmp.jpg") ));
					startActivityForResult(intent, 2);
				}

			}

		});
	}

	public void startPhotoZoom(Uri uri,Uri out) {  
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");  
		intent.putExtra("crop", "true");  
		intent.putExtra("aspectX", 1);  
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 640);  
		intent.putExtra("outputY", 640);
		intent.putExtra("scale", true); 
		intent.putExtra("noFaceDetection", true); 
		intent.putExtra("output", out); 
		startActivityForResult(intent, 3);  
	}  

	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		super.onActivityResult(requestCode, resultCode, data); 
		if(resultCode == RESULT_OK){
			String path = SysConfig.IMAGE_CAPTURE_PATH + "tmp.jpg";
			Uri out = Uri.fromFile(new File(path));
			if(requestCode == 1){				
				startPhotoZoom(out,out);
			}else if(requestCode == 2){
				Uri uri = data.getData();
				startPhotoZoom(uri,out);
				//				String [] proj={MediaStore.Images.Media.DATA}; 
				//				Cursor cursor = managedQuery( uri,proj,null,null,null);
				//
				//				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
				//				cursor.moveToFirst(); 
				//				path = cursor.getString(column_index);				
			}else if(requestCode == 3){
				Log.v("ConfigActivity","Begin to upload " + path);
				app.UploadFile(path);
			}
		}
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();

		map = new HashMap<String, Object>();
		map.put("title", "Logout");
		map.put("info", "logout");
		map.put("img", R.drawable.icon);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "Network");
		map.put("info", "newwork");
		map.put("img", R.drawable.icon);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "Upload Exist");
		map.put("info", "Upload image");
		map.put("img", R.drawable.icon);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "Upload Toke");
		map.put("info", "Upload image");
		map.put("img", R.drawable.icon);
		list.add(map);

		for(int i = 0;i < 10;i++){
			map = new HashMap<String, Object>();
			map.put("title", "No." + i);
			map.put("info", "test");
			map.put("img", R.drawable.icon);
			list.add(map);
		}

		return list;
	}
}
