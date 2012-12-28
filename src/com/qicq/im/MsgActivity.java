package com.qicq.im;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.qicq.im.Utilities.Utility;
import com.qicq.im.api.ChatMessage;
import com.qicq.im.app.LBSApp;
import com.qicq.im.config.SysConfig;
import com.qicq.im.msg.ChattingAdapter;
import com.qicq.im.msg.MsgRcvEvent;
import com.qicq.im.msg.MsgRcvListener;
import com.qicq.im.popwin.LBSToast;

@SuppressLint("HandlerLeak")
public class MsgActivity extends Activity {

	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;

	protected static final String TAG = "MsgActivity";
	private ChattingAdapter chatHistoryAdapter;
	private List<ChatMessage> messages = new ArrayList<ChatMessage>();

	private ListView chatHistoryLv;
	private Button sendBtn;
	private EditText textEditor;
	private ImageView sendImageIv;
	private ImageView captureImageIv;
	private View recording;


	private TextView friendName;
	private String friendUID = null;
	private LBSApp app;

	String audioFileName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatting);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.chatting_title_bar);
		chatHistoryLv = (ListView) findViewById(R.id.chatting_history_lv);

		initListView();

		app = (LBSApp)this.getApplication();
		friendUID = this.getIntent().getStringExtra("uid");
		setAdapterForThis();

		sendBtn = (Button) findViewById(R.id.send_button);
		textEditor = (EditText) findViewById(R.id.text_editor);
		sendImageIv = (ImageView) findViewById(R.id.send_image);
		captureImageIv = (ImageView) findViewById(R.id.capture_image);
		sendBtn.setOnClickListener(l);
		sendImageIv.setOnClickListener(l);
		captureImageIv.setOnClickListener(l);

		friendName = (TextView) findViewById(R.id.chatting_contact_name);
		friendName.setText(this.getIntent().getCharSequenceExtra("name"));

		File file = new File(SysConfig.AUDIO_PATH);
		if(!file.exists())
			file.mkdirs();

		recording = findViewById(R.id.recording);
		recording.setOnTouchListener(new View.OnTouchListener() {
			private int timeCount;
			private Timer timer = new Timer(true);
			private TimerTask task = new TimerTask(){
				@Override
				public void run() {
					timeCount++;
					handler.sendMessage(new Message()); 
				}

			};

			private final Handler handler = new Handler(){  
				public void handleMessage(Message msg) {
					timeTextView.setText(String.valueOf(timeCount));
				}    
			};
			private TextView timeTextView;
			private PopupWindow menuWindow = null;

			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();

				switch (action) {
				case MotionEvent.ACTION_UP:
					v.setBackgroundResource(R.drawable.hold_to_talk_normal);
					if (menuWindow != null)
						menuWindow.dismiss();
					//Log.d(TAG, "---onTouchEvent action:ACTION_UP");
					stopRecording();
					if( timeCount <= 2 )
						LBSToast.makeText(MsgActivity.this, "时间不足一秒", Toast.LENGTH_LONG);
					else{
						ChatMessage msg = ChatMessage.fromSender(ChatMessage.MESSAGE_TYPE_VOICE, audioFileName, friendUID);
						msg.audioTime = timeCount;
						sendMessage(msg);
					}
					break;
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundResource(R.drawable.hold_to_talk_pressed);
					ViewGroup root = (ViewGroup) findViewById(R.id.chat_root);
					View view = LayoutInflater.from(MsgActivity.this).inflate(R.layout.audio_recorder_ring, null);
					menuWindow = new PopupWindow(view, 200, 200);
					timeTextView = (TextView)view.findViewById(R.id.recorder_ring_time);
					// @+id/recorder_ring
					view.findViewById(R.id.recorder_ring).setVisibility(View.VISIBLE);
					view.setBackgroundResource(R.drawable.pls_talk);
					menuWindow.showAtLocation(root, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
					//Log.d(TAG, "---onTouchEvent action:ACTION_DOWN");
					// AudioRecorder recorder=new AudioRecorder();
					audioFileName = SysConfig.AUDIO_PATH + Utility.getRandomFileName() + ".amr";
					startRecording(audioFileName);
					timeCount = 0;
					timer.schedule(task, 0,1000);
					break;
				}

				return true;
			}
		});


		app.addMsgRcvListener(m);
		app.setTalkingToId(friendUID);
		
		//register receiver for msg feedback
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SysConfig.BROADCAST_SEND_MSG_ACTION);  
		registerReceiver(receiver, intentFilter); 
	}

	private BroadcastReceiver receiver = new BroadcastReceiver(){  
		@Override
		public void onReceive(Context context, Intent intent) {
			int mid = intent.getIntExtra("mid", -1);
			Log.v("MsgActivity","recive send msg feed back " + mid);
			//TODO set the msg send state success
		}
	};

	private void initListView(){
		chatHistoryLv.setOnItemLongClickListener(new OnItemLongClickListener(){

			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {				
				return false;
			}

		});
		chatHistoryLv.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				ChatMessage msg = (ChatMessage)chatHistoryAdapter.getItem(position);
				if(msg.type == ChatMessage.MESSAGE_TYPE_VOICE){
					startPlaying(msg.content);
				}
			}

		});
	}
	private void startPlaying(String audioName) {
		if(mPlayer == null)
			mPlayer = new MediaPlayer();
		else
			mPlayer.reset();
		try {
			mPlayer.setDataSource(audioName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e("Record audio", "prepare() failed");
		}
	}

	private void startRecording(String audioName) {
		if(mRecorder == null)
			mRecorder = new MediaRecorder();
		else
			mRecorder.reset();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mRecorder.setOutputFile(audioName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		try {
			mRecorder.prepare();
			mRecorder.start();
		} catch (IOException e) {
			Log.e("Record audio", "prepare() failed " + e.getMessage());
		}

	}
	private void stopRecording() {
		mRecorder.stop();
		//        mRecorder.reset();
		//        mRecorder.release();
		//        mRecorder = null;
	}

	private MsgRcvListener m = new MsgRcvListener(){

		public void onMsgRcved(MsgRcvEvent e, List<ChatMessage> msgs) {
			for(ChatMessage m : msgs){
				Log.v("New msg for "+m.targetId,m.content);
				if(m.targetId.equals(friendUID)){
					messages.add(m);						
				}
			}
			mHandler.sendMessage(new Message());
		}

	};
	
	@Override
	public void onDestroy(){
		unregisterReceiver(receiver);
		
		app.removeMsgRcvListener(m);
		app.setTalkingToId(null);
		//app.saveAllMsg(messages);
		if(mRecorder != null)
			mRecorder.release();
		if(mPlayer != null)
			mPlayer.release();
		super.onDestroy();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			chatHistoryAdapter.notifyDataSetChanged();
		}
	};

	// 设置adapter
	private void setAdapterForThis() {
		List<ChatMessage> tmp = app.getAllMsg(friendUID);
		if(tmp.size() != 0)
			messages.addAll(tmp);
		chatHistoryAdapter = new ChattingAdapter(this, messages);
		chatHistoryLv.setAdapter(chatHistoryAdapter);
	}

	/**
	 * 按键时间监听
	 */
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
			String path = SysConfig.IMAGE_CAPTURE_PATH + Utility.getRandomFileName() + ".jpg";
			Uri out = Uri.fromFile(new File(path));
			if(requestCode == 1){				
				startPhotoZoom(out,out);
			}else if(requestCode == 2){
				Uri uri = data.getData();
				startPhotoZoom(uri,out);			
			}else if(requestCode == 3){
				Log.v("MsgActivity","Send msg " + path);
				ChatMessage msg = ChatMessage.fromSender(ChatMessage.MESSAGE_TYPE_IMAGE, path,friendUID);
				sendMessage(msg);
			}
		}
	}

	private View.OnClickListener l = new View.OnClickListener() {

		public void onClick(View v) {

			if (v.getId() == sendBtn.getId()) {
				String str = textEditor.getText().toString();
				String sendStr;
				sendStr = str.trim().replaceAll("\r", "").replaceAll("\t", "").replaceAll("\n", "").replaceAll("\f", "");
				if (!sendStr.equals("")) {
					ChatMessage msg = ChatMessage.fromSender(ChatMessage.MESSAGE_TYPE_TEXT, sendStr,friendUID);
					sendMessage(msg);
				}
				textEditor.setText("");

			} else if (v.getId() == sendImageIv.getId()) {
				Intent i = new Intent();
				i.setType("image/*");
				i.setAction(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE); 
				startActivityForResult(i, 2);
			} else if (v.getId() == captureImageIv.getId()) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File file = new File(SysConfig.IMAGE_CAPTURE_PATH);
				if(!file.exists())
					file.mkdirs();
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(file, "tmp.jpg") ));
				startActivityForResult(intent, 1);
			}
		}
	};

	private void sendMessage(ChatMessage msg){
		messages.add(msg);
		chatHistoryAdapter.notifyDataSetChanged();
		app.sendMessage(msg);
	}
}
