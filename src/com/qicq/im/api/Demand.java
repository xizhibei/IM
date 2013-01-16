package com.qicq.im.api;

import java.util.Date;

public class Demand {
	
	public final static int DEMAND_SEX_MALE = 0;
	public final static int DEMAND_SEX_FEMALE = 1;
	public final static int DEMAND_SEX_BOTH = 2;
	
	public int did;
	public int uid;
	public String name;
	public int startTime;
	public int expireTime;
	public int startH;
	public int startM;
	public int endH;
	public int endM;
	public int sexType;
	public String detail;
	
	public Demand(){
		
	}
	
	public static Demand fromSender(String name,int startH,int startM,
			int endH,int endM,int sexType,String detail){
		Demand d = new Demand();
		d.name = name;
		d.startH = startH;
		d.startM = startM;
		d.endH = endH;
		d.endM = endM;
		d.sexType = sexType;
		d.detail = detail;
		
		Date date = new Date();
		date.setHours(startH);
		date.setMinutes(startM);
		d.startTime = (int)(date.getTime() / 1000);
		
		date.setHours(endH);
		date.setMinutes(endM);
		d.expireTime = (int)(date.getTime() / 1000);
		return d;
	}
	
	public static Demand fromReciver(int did,int uid,String name,int startTime,
			int expireTime,int sexType,String detail){
		Demand d = new Demand();
		d.did = did;
		d.uid = uid;
		d.name = name;
		d.startTime = startTime;
		d.expireTime = expireTime;	
		d.sexType = sexType;
		d.detail = detail;
		
		Date date = new Date(startTime * 1000);
		d.startH = date.getHours();
		d.startM = date.getMinutes();
		
		date = new Date(expireTime * 1000);
		d.endH = date.getHours();
		d.endM = date.getMinutes();
		return d;
	}
	
	public static Demand fromDatabase(int did,int uid,String name,int startTime,
			int expireTime,int sexType,String detail){
		Demand d = new Demand();
		d.did = did;
		d.uid = uid;
		d.name = name;
		d.sexType = sexType;
		d.detail = detail;	
		d.startTime = startTime;
		d.expireTime = expireTime;
		
		Date date = new Date(startTime * 1000);
		d.startH = date.getHours();
		d.startM = date.getMinutes();
		
		date = new Date(expireTime * 1000);
		d.endH = date.getHours();
		d.endM = date.getMinutes();
		return d;
	}
	
	public String getStartTime(){
		Date date = new Date(startTime * 1000);
		return date.toLocaleString();
	}
	
	public String getExpireTime(){
		Date date = new Date(expireTime * 1000);
		return date.toLocaleString();
	}
	
	public String getSextype(){
		if(sexType == 1)
			return "ÄÐ";
		else if(sexType == 1)
			return "Å®";
		else
			return "¾ù¿É";
	}
}
