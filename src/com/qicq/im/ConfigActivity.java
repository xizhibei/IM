package com.qicq.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qicq.im.app.LBSApp;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ConfigActivity extends ListActivity{

	//ListView configLV = null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		//setContentView(R.layout.config);

		//configLV = (ListView)findViewById(R.id.configListView);

		SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.configitem,
				new String[]{"title","info","img"},
				new int[]{R.id.title,R.id.info,R.id.img});
		setListAdapter(adapter);
		
		getListView().setOnItemClickListener( new OnItemClickListener(){

			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				if(position == 0){
			        LBSApp app = (LBSApp)ConfigActivity.this.getApplication();
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
					Uri uri = Uri.parse("file://data/data/com.qicq.im/databases/1034.db"); 
					Intent it = new Intent(Intent.ACTION_VIEW,uri); 
					startActivity(it); 
				}
				
			}

		});
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
		map.put("title", "Stored data");
		map.put("info", "Stored data");
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
