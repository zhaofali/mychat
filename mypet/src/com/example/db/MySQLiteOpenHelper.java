package com.example.db;

import com.example.utils.C;

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
	 * 
	 * @param context
	 */
	public MySQLiteOpenHelper(Context context) {
		super(context, "chat", null, 1);
	}

	@Override
	// INTEGER PRIMARY KEY AUTOINCREMENT
	public void onCreate(SQLiteDatabase db) {

		db.beginTransaction();

		try {

			StringBuffer sb1 = new StringBuffer();

			sb1.append("CREATE TABLE ");
			sb1.append(C.robot);
			sb1.append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT,message VARCHAR(255),create_time VARCHAR(255),type INT);");

			StringBuffer sb2 = new StringBuffer();
			sb2.append("CREATE TABLE ");
			sb2.append(C.girl);
			sb2.append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT,message VARCHAR(255),create_time VARCHAR(255),type INT);");

			StringBuffer sb3 = new StringBuffer();
			sb3.append("CREATE TABLE ");
			sb3.append(C.wifi);
			sb3.append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT,message VARCHAR(255),create_time VARCHAR(255),type INT);");

			// String sql =
			// "CREATE TABLE "+C.robot+" (_id INTEGER PRIMARY KEY AUTOINCREMENT,message VARCHAR(255),create_time VARCHAR(255),type INT);"
			// +
			// "CREATE TABLE "+C.girl+" (_id INTEGER PRIMARY KEY AUTOINCREMENT,message VARCHAR(255),create_time VARCHAR(255),type INT)";
			db.execSQL(sb1.toString());
			db.execSQL(sb2.toString());
			db.execSQL(sb3.toString());
			db.setTransactionSuccessful();
			Log.d("database", "create database successful");
		} catch (Exception e) {
			Log.d("database", "create database unsuccessful");
		} finally {
			db.endTransaction();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		Log.d("database", "update database");
	}

}
