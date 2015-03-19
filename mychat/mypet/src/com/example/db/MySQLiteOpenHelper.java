package com.example.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	

	public MySQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	
	/**
	 * default construct
	 * @param context
	 */
	public MySQLiteOpenHelper(Context context){
		super(context, "chat", null, 1);
	}
	
	
	@Override
	//INTEGER PRIMARY KEY AUTOINCREMENT
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE chat_message (_id INTEGER PRIMARY KEY AUTOINCREMENT,message VARCHAR(255),create_time VARCHAR(255),type INT)";
		db.execSQL(sql);
		//db.execSQL("create table user(id int,name varchar(20))");
		Log.d("database", "create database");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		Log.d("database", "update database");
	}

}
