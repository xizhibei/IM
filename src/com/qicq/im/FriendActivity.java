package com.qicq.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qicq.im.api.User;
import com.qicq.im.app.LBSApp;
import com.qicq.im.view.PullToRefreshListView;
import com.qicq.im.view.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FriendActivity extends Activity{
	List<Map<String, Object>> friendlist;
	private LBSApp app;
	SimpleAdapter adapter;
	PullToRefreshListView listView;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.friend_list);
		app = (LBSApp)this.getApplication();
		friendlist = getData();
		
		listView = (PullToRefreshListView) findViewById(R.id.friend_listview);

		adapter = new SimpleAdapter(this,friendlist,R.layout.friend_item,
				new String[]{"name","info","img","time"},
				new int[]{R.id.name,R.id.info,R.id.img,R.id.time});
		listView.setAdapter(adapter);

		listView.setOnRefreshListener(new OnRefreshListener() {
		    public void onRefresh() {
		        new GetDataTask().execute();
		    }
		});
		
		listView.setOnItemClickListener( new OnItemClickListener(){

			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				//friendlist.get(position);
				Toast.makeText(FriendActivity.this,
						"Pos: " + position, Toast.LENGTH_LONG).show();

				Intent i = new Intent(FriendActivity.this,MsgActivity.class);
				Map<String, Object> tmp = friendlist.get(position);
				i.putExtra("name", (String)tmp.get("name"));
				i.putExtra("uid", String.valueOf(tmp.get("uid")));
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
			map.put("uid", u.uid);
			map.put("name", u.name);
			map.put("info", u.regdate);
			map.put("img", R.drawable.avatar);
			map.put("time", u.lastupdate);
			list.add(map);
		}

		return list;
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, List<User>> {
	    @Override
	    protected void onPostExecute(List<User> result) {
	    	
	    	friendlist = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = null;
			
			for(User u : result){
				map = new HashMap<String, Object>();
				map.put("uid", u.uid);
				map.put("name", u.name);
				map.put("info", u.regdate);
				map.put("img", R.drawable.avatar);
				map.put("time", u.lastupdate);
				friendlist.add(map);
			}
	        // Call onRefreshComplete when the list has been refreshed.
			listView.onRefreshComplete();
	        
	        super.onPostExecute(result);
	    }

		@Override
		protected List<User> doInBackground(Void... arg0) {
			return app.getFans(true);
		}
	}
}
