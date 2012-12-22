package com.qicq.im.thread;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.qicq.im.api.APIManager;

public class FileDownloadThread extends AbstractMessageThread{

	private List<String> files = new ArrayList<String>();
	
	public FileDownloadThread(APIManager api){
		super(api);
		this.api = api;
		sleeptime = 100;
	}
	public void addMsgs(String file){
		files.add(file);
	}
	
	public void setSleepTime(long time){
		sleeptime = time;
	}
	
	@Override
	public void run() {
		Log.i("FileUploadThread","Start");
		while(continuing){
			while(!networkConnect || needPause){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			while(!files.isEmpty()){
				int ret = api.UploadFile(files.get(0));
				if(ret == 0)
					files.remove(0);
			}
			
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
