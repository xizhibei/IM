package com.qicq.im.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	
	private static final int Version = 1;
	private static final String CreateMsgTable = 
			"CREATE TABLE IF NOT EXISTS msg (`mid` integer PRIMARY KEY AUTOINCREMENT,`direction` integer, `content` text,`targetid` integer,`type` integer ,`time` integer,`audiotime` integer )";
	private static final String CreateUserTable = 
			"CREATE TABLE IF NOT EXISTS user " +
			"(`uid` integer PRIMARY KEY AUTOINCREMENT," +
			"`type` integer , " +
			"`name` text , " +
			"`sex` text ," +
			"`age` integer ," +
			"`regdate` text ," +
			"`lastupdate` text ," +
			"`serverAvatarUrl` text ," +
			"`localAvatarPath` text ," +
			"`lat` integer ," +
			"`lng` integer ," +
			"`distance` float)";
	
	private static final String CreateClusterTable = 
			"CREATE TABLE IF NOT EXISTS cluster (`cid` integer PRIMARY KEY AUTOINCREMENT,`latitude` integer ,`longtitude` integer ,`radix` integer )";
	
	private static final String CreateChatListTable = 
			"CREATE TABLE IF NOT EXISTS chatlist (`id` integer PRIMARY KEY AUTOINCREMENT,`direction` integer, `content` text,`targetid` integer,`type` integer ,`time` integer,`audiotime` integer ,`count` integer )";
	
	private static final String CreateSendTaskTable = 
			"CREATE TABLE IF NOT EXISTS msgsendtask (`mid` integer PRIMARY KEY AUTOINCREMENT,`direction` integer, `content` text,`targetid` integer,`type` integer ,`time` integer,`audiotime` integer )";
	
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
		db.execSQL(CreateMsgTable);
		db.execSQL(CreateUserTable);
		db.execSQL(CreateClusterTable);
		db.execSQL(CreateChatListTable);
		db.execSQL(CreateSendTaskTable);
	}
	
}
