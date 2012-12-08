package com.qicq.im.api;

import java.io.File;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.baidu.mapapi.GeoPoint;
import com.qicq.im.overlayitem.PeopleOverlayItem;

public class User {
	
	public static final int USER_TYPE_NONE = -1;
	public static final int USER_TYPE_ME = 0;
	public static final int USER_TYPE_BEFOLLOWED = 1;
	public static final int USER_TYPE_FANS = 2;
	public static final int USER_TYPE_FRIEND = 3;
	public static final int USER_TYPE_BLACK_LIST = 4;
	public static final int USER_TYPE_NEARBY = 8;
	
	public int type;
	public int uid;
	public String name;
	public String sex;
	public int age;
	public String regdate;
	public String lastupdate;
	public String serverAvatarUrl;
	public String localAvatarPath;

	public int lat;
	public int lng;
	public float distance;

	public static User fromLogin(int uid,String name,String sex,int age,String regdate,String lastupdate,
			int lat,int lng,float distance,String avatar,String localavatar){
		User u = new User(USER_TYPE_ME,uid,name,sex,age,regdate,lastupdate,
				lat,lng,distance,avatar,localavatar);
		return u;
	}
	
	public static User fromNearby(int uid,String name,String sex,int age,String regdate,String lastupdate,
			int lat,int lng,float distance,String avatar,String localavatar){
		User u = new User(USER_TYPE_NEARBY,uid,name,sex,age,regdate,lastupdate,
				lat,lng,distance,avatar,localavatar);
		return u;
	}
	
	public static User fromFriendList(int type,int uid,String name,String sex,int age,String regdate,String lastupdate,
			int lat,int lng,float distance,String avatar,String localavatar){
		User u = new User(type,uid,name,sex,age,regdate,lastupdate,
				lat,lng,distance,avatar,localavatar);
		return u;
	}

	public User(int type,int uid,String name,String sex,int age,String regdate,String lastupdate,
			int lat,int lng,float distance,String avatar,String localavatar){
		this.uid = uid;
		this.name = name;
		this.sex = sex;
		this.age = age;
		this.regdate = regdate;
		this.lastupdate = lastupdate;
		this.lat = lat;
		this.lng = lng;
		this.distance = distance;
		this.serverAvatarUrl = avatar;
		this.localAvatarPath = localavatar;
	}

	public Drawable getAvatar(){
		Bitmap bitmap = BitmapFactory.decodeFile(localAvatarPath);
		return new BitmapDrawable(bitmap);
	}
	public PeopleOverlayItem toOverlayItem(){
		Drawable d = null;
		if(localAvatarPath != null){
			File file = new File(localAvatarPath);
			if(file.exists()){
				Bitmap bitmap = BitmapFactory.decodeFile(localAvatarPath);
				d = new BitmapDrawable(bitmap);
				
				int h = d.getIntrinsicHeight() / 2;
				int w = d.getIntrinsicWidth() / 2;
				
				d.setBounds(-w, -h, w, h);
			}
		}
		return new PeopleOverlayItem(new GeoPoint(lat,lng),sex,name + " " + lastupdate + " " + regdate,d);
	}
}
