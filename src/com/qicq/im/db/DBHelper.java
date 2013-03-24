package com.qicq.im.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	
	private static final int Version = 1;
	private static final String MsgTable = 
			"CREATE TABLE IF NOT EXISTS msg (`mid` integer PRIMARY KEY AUTOINCREMENT,`direction` integer, `content` text,`targetid` integer,`type` integer ,`time` integer,`audiotime` integer,`sendstate` integer)";
	private static final String UserTable = 
			"CREATE TABLE IF NOT EXISTS user " +
			"(`type` integer , `uid` integer PRIMARY KEY,`name` text , `sex` text ,`age` integer ," +
			"`regdate` text ,`lastupdate` text ,`lat` integer ,`lng` integer ,`distance` float," +
			"`serverAvatarUrl` text ,`localAvatarPath` text,`did` integer,`localUpdatetime` integer)";
	
	private static final String ChatListTable = 
			"CREATE TABLE IF NOT EXISTS chatlist (`id` integer PRIMARY KEY AUTOINCREMENT,`direction` integer, `content` text,`targetid` integer,`type` integer ,`time` integer,`audiotime` integer,`sendstate` integer ,`count` integer )";
	
	private static final String SendTaskTable = 
			"CREATE TABLE IF NOT EXISTS msgsendtask (`mid` integer PRIMARY KEY AUTOINCREMENT,`direction` integer, `content` text,`targetid` integer,`type` integer ,`time` integer,`audiotime` integer,`sendstate` integer )";
	
	private static final String DemandTable = 
			"CREATE TABLE IF NOT EXISTS demand (`did` integer PRIMARY KEY,`uid` integer,`name` text, `startTime` integer,`expireTime` integer,`sexType` integer,`detail` text )";
	
//	private static final String ActivityTable = 
//			"CREATE TABLE IF NOT EXISTS activity (`aid` integer PRIMARY KEY,`name` text, `path` text,`count` integer)";
	
	public DBHelper(Context context,String DBname) {
		super(context, DBname, null, Version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createAllTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public void createAllTables(SQLiteDatabase db){
		db.execSQL(MsgTable);
		db.execSQL(UserTable);
		db.execSQL(ChatListTable);
		db.execSQL(SendTaskTable);
		db.execSQL(DemandTable);
	}	
}
