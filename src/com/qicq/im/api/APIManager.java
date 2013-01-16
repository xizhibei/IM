package com.qicq.im.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import com.qicq.im.config.SysConfig;


import android.util.Log;

public class APIManager extends WebManager{

	public final static int ERROR_IN_JSON = -1;
	public final static int ERROR_NO_DATA = -2;
	public final static int ERROR_ARG = -3;
	public final static int ERROR_ENCODER = -4;	

	public APIManager(String addr) {
		super(addr);
		File dir = new File(SysConfig.ALBUM_PATH);
		if(!dir.exists()){
			dir.mkdirs();
			Log.v("Dir","Create" + SysConfig.ALBUM_PATH);
		}
	}

	public int UserReg(String name,String email,String pwd){
		String tmp;
		try {
			tmp = "name=" + URLEncoder.encode(name,"UTF-8") + "&email=" + email+ "&pwd=" + pwd;
			tmp = PostData(addr + "/user/reg", tmp);

			if(tmp != null){
				Log.v("Recive data",tmp);
				JSONObject json;
				try {
					json = new JSONObject(tmp);
					return json.getInt("errno");
				} catch (JSONException e) {
					e.printStackTrace();
					return ERROR_IN_JSON;
				}
			}else{
				return ERROR_NO_DATA;
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		return ERROR_ENCODER;
	}

	private User parseUser(JSONObject p,int type) throws JSONException{
		String avatarPath;
		String avatarUrl = p.getString("avatar");

		File f = new File(avatarUrl);
		avatarPath = SysConfig.ALBUM_PATH + f.getName();
		//TODO download file from server

		File tmp = new File(avatarPath);
		if(!tmp.exists()){
			DownloadFile(addr + avatarUrl,avatarPath);
		}else{
			long modTime = tmp.lastModified();
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Log.v(avatarPath,dateFormat.format(new Date(modTime)));
		}

		User u = User.fromFriendList(
				type,
				p.getInt("uid"),
				p.getString("name"),
				p.getString("sex"),
				p.getInt("age"),
				p.getString("regdate"),
				p.getString("locupdate"),
				p.getInt("lat"),
				p.getInt("lng"),
				(float) p.getDouble("distance"),
				avatarUrl,
				avatarPath);
		if(p.has("a_name")){
			Demand d = Demand.fromReciver(
					p.getInt("did"),
					p.getInt("uid"),
					p.getString("a_name"), 
					p.getInt("starttime"), 
					p.getInt("expiretime"), 
					p.getInt("sextype"), 
					p.getString("detail"));
			u.demand = d;
		}
		return u;
	}

	public User UserLogin(String email,String pwd){
		String tmp = "email=" + email + "&pwd=" + pwd;

		tmp = PostData(addr + "/user/login", tmp);

		if(tmp != null){
			Log.v("Recive data",tmp);
			try {
				JSONObject p = new JSONObject(tmp);
				if(p.getInt("errno") == 0){
					return parseUser(p,User.USER_TYPE_ME);
				}
				else
					return null;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}else
			return null;
	}

	public int UserUpdate(String sex,String phone){
		return 0;
	}

	public int LocationUpdate(int lat,int lng){
		String tmp = PostData(addr + "/loc/update", "latitude="+ lat + "&longitude=" + lng);
		if(tmp != null){
			Log.v("LocationUpdate Recive data",tmp);
			try {
				JSONObject json = new JSONObject(tmp);
				return json.getInt("errno");
			} catch (JSONException e) {
				e.printStackTrace();
				return ERROR_IN_JSON;
			}
		}else
			return ERROR_NO_DATA;
	}

	public List<User> NearbyPeople(){
		String tmp = GetData(addr + "/loc/nearby");
		List<User> list = new ArrayList<User>();
		if(tmp != null){
			Log.v("NearbyPeople Recive data",tmp);
			try {
				JSONObject json = new JSONObject(tmp);

				if(json.getInt("errno") == 0){
					int count = json.getInt("count");
					for(int i = 0;i < count;i++){
						JSONObject p = json.getJSONObject(String.valueOf(i));
						list.add(parseUser(p,User.USER_TYPE_NEARBY | p.getInt("type")));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.v("NearbyPeople",e.toString());
			}
		}else{
			Log.v("NearbyPeople","No data!");
		}
		return list;
	}

	public List<User> AllMyFriend(){
		String tmp = GetData(addr + "/friend/list");
		List<User> list = new ArrayList<User>();
		if(tmp != null){
			Log.v("AllMyFriend Recive data",tmp);
			try {
				JSONObject json = new JSONObject(tmp);

				if(json.getInt("errno") == 0){
					int count = json.getInt("count");
					for(int i = 0;i < count;i++){
						JSONObject p = json.getJSONObject(String.valueOf(i));

						list.add(parseUser(p,p.getInt("type")));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.v("get friend",e.toString());
			}
		}else{
			Log.v("get friend","No data!");
		}
		return list;
	}

	public User getUser(String uid){
		String tmp;
		if(uid != null)
			tmp = PostData(addr + "/user/info","uid=" + uid);
		else
			tmp = GetData(addr + "/user/info");

		if(tmp != null){
			Log.v("getUser Recive data",tmp);
			try {
				JSONObject json = new JSONObject(tmp);

				if(json.getInt("errno") == 0){
					return parseUser(json,json.getInt("type"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.v("getUser",e.toString());
			}
		}else{
			Log.v("getUser","No data!");	
		}
		return null;
	}

	public int PublishDemands(Demand d){
		String tmp;
		try {
			tmp = "name=" + URLEncoder.encode(d.name,"UTF-8")
					+ "&endH=" + d.endH
					+ "&endM=" + d.endM
					+ "&startH=" + d.startH
					+ "&startM=" + d.startM
					+ "&sextype=" + d.sexType
					+ "&detail=" + URLEncoder.encode(d.detail,"UTF-8");
			tmp = PostData(addr + "/want/publish", tmp);
			if(tmp != null){
				Log.v("Recive data",tmp);
				try {
					JSONObject json = new JSONObject(tmp);
					return json.getInt("errno");
				} catch (JSONException e) {
					e.printStackTrace();
					return ERROR_IN_JSON;
				}
			}else
				return ERROR_NO_DATA;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return ERROR_ENCODER;
	}

	public int SendMessage(ChatMessage msg){
		String tmp;
		try {
			tmp = "type=" + msg.type + "&audiotime=" + msg.audioTime + "&rcvId="+msg.targetId+"&content=" + URLEncoder.encode(msg.content,"UTF-8");
			tmp = PostData(addr + "/msg/send", tmp);
			if(tmp != null){
				Log.v("SendMessage Recive data",tmp);
				JSONObject json;
				try {
					json = new JSONObject(tmp);
					return json.getInt("errno");
				} catch (JSONException e) {
					e.printStackTrace();
					return ERROR_IN_JSON;
				}
			}else{
				return ERROR_NO_DATA;
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return ERROR_ENCODER;
	}

	public int SendRequestForDemand(int did,int targetId){
		String tmp;
		tmp = "type=" + ChatMessage.MESSAGE_TYPE_REQUEST + "&audiotime=0&rcvId="+targetId+"&content=";
		tmp = PostData(addr + "/msg/send", tmp);
		if(tmp != null){
			Log.v("SendRequest ","SendRequestForDemand Recive data: "+tmp);
			JSONObject json;
			try {
				json = new JSONObject(tmp);
				return json.getInt("errno");
			} catch (JSONException e) {
				e.printStackTrace();
				return ERROR_IN_JSON;
			}
		}else{
			return ERROR_NO_DATA;
		}
	}

	public List<ChatMessage> RcvMessage(){
		String tmp = GetData(addr + "/msg/new");
		List<ChatMessage> list = new ArrayList<ChatMessage>();
		if(tmp != null){
			Log.v("RcvMessage Recive data",tmp);
			try {
				JSONObject json = new JSONObject(tmp);

				if(json.getInt("errno") == 0){
					int count = json.getInt("count");
					for(int i = 0;i < count;i++){
						JSONObject p = json.getJSONObject(String.valueOf(i));

						list.add(ChatMessage.fromReciver(
								p.getString("content"),
								p.getString("sendid"),
								p.getInt("type"),
								p.getInt("time"),
								p.getInt("audiotime"))
								);
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
				Log.v("RcvMessage",e.toString());
			}
		}else{
			Log.v("RcvMessage","No data!");
		}
		return list;
	}

	public List<LocationCluster> GetLocationCluster(int ltLat, int ltLng, 
			int rbLat, int rbLng, int gender, int agelevel,String updatetime){
		String data = String.format("ltLat=%d&ltLng=%d&rbLat=%d&rbLng=%d&gender=%d&ageLevel=%d&updatetime=%s", 
				ltLat,ltLng,rbLat,rbLng,gender,agelevel,updatetime);
		String tmp = PostData(addr + "/loc/cluster",data);
		List<LocationCluster> list = new ArrayList<LocationCluster>();
		if(tmp != null){			
			Log.v("GetLocationCluster Recive data",tmp);
			try {
				JSONObject json = new JSONObject(tmp);

				if(json.getInt("errno") == 0){
					int count = json.getInt("count");
					for(int i = 0;i < count;i++){
						JSONObject p = json.getJSONObject(String.valueOf(i));

						list.add(new LocationCluster(
								p.getInt("lat"),
								p.getInt("lng"),
								p.getInt("radix"))
								);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.v("GetLocationCluster",e.toString());
			}
		}else{
			Log.v("GetLocationCluster","No data!");
		}
		return list;
	}

	/**
	 * 
	 * @param filename
	 * @return the fid in server
	 */
	public int UploadFile(String filename){
		String contentType;
		if(filename.endsWith("png")){
			contentType = "image/png";
		}else if(filename.endsWith("jpg")){
			contentType = "image/jpg";
		}else if(filename.endsWith("gif")){
			contentType = "image/gif";
		}else if(filename.endsWith("amr")){
			contentType = "audio/amr";
		}else
			return ERROR_ARG;

		String tmp = UploadFile(addr + "/file/upload", filename, contentType);

		if(tmp != null){
			Log.v("UploadFile Recive data",tmp);
			JSONObject json;
			try {

				json = new JSONObject(tmp);
				if(json.getInt("errno") == 0)
					return json.getInt("fid");
				else
					return ERROR_NO_DATA;
			} catch (JSONException e) {
				e.printStackTrace();
				return ERROR_IN_JSON;
			}
		}else{
			return ERROR_NO_DATA;
		}
	}
}














