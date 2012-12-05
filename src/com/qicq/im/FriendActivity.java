package com.qicq.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qicq.im.api.User;
import com.qicq.im.app.LBSApp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FriendActivity extends ListActivity{
	List<Map<String, Object>> friendlist;
	private LBSApp app;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (LBSApp)this.getApplication();
		friendlist = getData();

		SimpleAdapter adapter = new SimpleAdapter(this,friendlist,R.layout.friend_item,
				new String[]{"name","info","img","time"},
				new int[]{R.id.name,R.id.info,R.id.img,R.id.time});
		setListAdapter(adapter);


		this.getListView().setOnItemClickListener( new OnItemClickListener(){

			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				//friendlist.get(position);
				Toast.makeText(FriendActivity.this,
						"Pos: " + position, Toast.LENGTH_LONG).show();

				Intent i = new Intent(FriendActivity.this,MsgActivity.class);
				User f = app.getFriends(false).get(position);
				i.putExtra("name", f.name);
				i.putExtra("uid", String.valueOf(f.uid));
				FriendActivity.this.startActivity(i);
			}

		});
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		List<User> flist = app.getFans(false);
		for(User u : flist){
			map = new HashMap<String, Object>();
			map.put("name", u.name);
			map.put("info", u.regdate);
			map.put("img", R.drawable.avatar);
			map.put("time", u.lastupdate);
			list.add(map);
		}

		return list;
	}
}
