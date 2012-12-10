package com.qicq.im;

import java.util.List;
import com.qicq.im.api.ChatMessage;
import com.qicq.im.api.User;
import com.qicq.im.app.LBSApp;
import com.qicq.im.msg.ChatListItem;
import com.qicq.im.msg.MsgListAdapter;
import com.qicq.im.msg.MsgRcvEvent;
import com.qicq.im.msg.MsgRcvListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("HandlerLeak")
public class MsgListActivity extends Activity{

	private MsgListAdapter msgListAdapter;
	private LBSApp app;
	
	private ListView listView;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			msgListAdapter.notifyDataSetChanged();
		}
	};
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_list);

		app = (LBSApp)this.getApplication();

		listView = (ListView)findViewById(R.id.msg_list_listView);

		initMsgListAdapter();
		listView.setAdapter(msgListAdapter);
		msgListAdapter.notifyDataSetChanged();
		
		listView.setOnItemClickListener( new OnItemClickListener(){

			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				//friendlist.get(position);
				Toast.makeText(MsgListActivity.this,
						"Pos: " + position, Toast.LENGTH_LONG).show();

				Intent i = new Intent(MsgListActivity.this,MsgActivity.class);
				User f = ((ChatListItem)msgListAdapter.getItem(position)).user;
				
				i.putExtra("name", f.name);
				i.putExtra("uid", String.valueOf(f.uid));
				MsgListActivity.this.startActivity(i);
			}

		});

		app.addMsgRcvListener(m);
	}
	
	private MsgRcvListener m = new MsgRcvListener(){

		public void onMsgRcved(MsgRcvEvent e, List<ChatMessage> msgs) {
			for(ChatMessage m : msgs){
				User user = app.getUser(m.targetId);
				msgListAdapter.addItem(new ChatListItem(m,user));
			}
			mHandler.sendMessage(new Message());
		}
	};
	
	private void initMsgListAdapter(){
		msgListAdapter = new MsgListAdapter(this);
		List<ChatListItem> tmp = app.getAllChattingList();
		msgListAdapter.addItemDirectly(tmp);
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onStop(){
		super.onResume();
	}
	
	@Override
	public void onDestroy(){
		app.removeMsgRcvListener(m);
		app.saveAllChattingList(msgListAdapter.getItems());
		super.onDestroy();
	}

}
