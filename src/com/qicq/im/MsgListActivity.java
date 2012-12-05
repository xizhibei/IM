package com.qicq.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qicq.im.api.ChatMessage;
import com.qicq.im.api.User;
import com.qicq.im.app.LBSApp;
import com.qicq.im.msg.MsgRcvEvent;
import com.qicq.im.msg.MsgRcvListener;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

@SuppressLint("HandlerLeak")
public class MsgListActivity extends ListActivity{

	class MsgListItem{
		int count = 0;
		String showmsg;
		MsgListItem(int count,String showmsg){
			this.count = count;
			this.showmsg = showmsg;
		}
	}
	private Map<String,Map<String, Object>> msglist = new HashMap<String,Map<String, Object>>();
	private List<Map<String, Object>> showmsglist = new ArrayList<Map<String, Object>>();
	private Map<String,MsgListItem> msgItem = new HashMap<String,MsgListItem>();

	private LBSApp app;
	private SimpleAdapter adapter;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			adapter.notifyDataSetChanged();
		}
	};
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (LBSApp)this.getApplication();

		adapter = new SimpleAdapter(this,showmsglist,R.layout.msg_item,
				new String[]{"name","info","img","time"},
				new int[]{R.id.name,R.id.info,R.id.img,R.id.time});
		setListAdapter(adapter);


		getListView().setOnItemClickListener( new OnItemClickListener(){

			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				//friendlist.get(position);
				Toast.makeText(MsgListActivity.this,
						"Pos: " + position, Toast.LENGTH_LONG).show();

				Intent i = new Intent(MsgListActivity.this,MsgActivity.class);
				User f = app.getFriends(false).get(position);
				i.putExtra("name", f.name);
				i.putExtra("uid", String.valueOf(f.uid));
				MsgListActivity.this.startActivity(i);
			}

		});

		app.addMsgRcvListener(m);
	}
	
	private MsgRcvListener m = new MsgRcvListener(){

		public void onMsgRcved(MsgRcvEvent e, List<ChatMessage> msgs) {
			msgItem.clear();
			for(ChatMessage m : msgs){
				String id = m.targetId;
				if(!msgItem.containsKey(id)){
					msgItem.put(id, new MsgListItem(1,m.content) );
				}else{
					msgItem.get(id).count++;
					msgItem.get(id).showmsg = m.content;
				}
			}

			for(String id : msgItem.keySet()){
				if(msglist.containsKey(id)){
					int count = (Integer) msglist.get(id).get("count");
					msglist.get(id).put("count", msgItem.get(id).count + count);
				}else{
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("name", "New follower!!!");
					map.put("info", msgItem.get(id).showmsg);
					map.put("img", R.drawable.avatar);
					map.put("time", "3Сʱǰ");
					map.put("count", 1);
					msglist.put(id, map);
				}
			}
			showmsglist.clear();
			for(Map<String,Object> map : msglist.values())
				showmsglist.add(map);

			mHandler.sendMessage(new Message());
		}
	};
	
	public void onDestroy(){
		app.removeMsgRcvListener(m);
		super.onDestroy();
		//remove listener and store info into db
	}

}
