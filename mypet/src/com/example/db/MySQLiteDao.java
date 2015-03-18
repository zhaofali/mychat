package com.example.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.bean.ChatMessage;
import com.example.bean.ChatMessage.Type;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MySQLiteDao {
	
	private Context context;
	private MySQLiteOpenHelper dbHelper;
	
	/**
	 * default construtor
	 * @param context
	 */
	public MySQLiteDao(Context context){
		this.setContext(context);
		dbHelper = new MySQLiteOpenHelper(context);
	}
	
	
	public void insert(ChatMessage msg){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		StringBuffer sb = new StringBuffer();
		sb.append("insert into chat_message (message,create_time,type) values('");
		sb.append(msg.getMsg());
		sb.append("','");
		sb.append(msg.getDateStr());
		sb.append("',");
		sb.append(msg.getType() == Type.INPUT ? 1 : 0);
		sb.append(")");
		String sql = sb.toString();
		
		db.execSQL(sql);
		
		db.close();
		
		Log.d("db","insert "+msg.getMsg() +" @ "+msg.getDateStr());
	}
	
	public List<ChatMessage> query(String creatTime){
		
		List<ChatMessage> list = new ArrayList<ChatMessage>();
		
		StringBuffer sb  =  new StringBuffer();
		
		sb.append("select message,create_time,type from chat_message where create_time < '"+creatTime+"' order by _id desc limit 10");
		
		String sql = sb.toString();
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor cur = db.rawQuery(sql, null);
		
		while(cur.moveToNext()){
			ChatMessage msg = new ChatMessage();
			msg.setMsg(cur.getString(cur.getColumnIndex("message")));
			msg.setDateStr(cur.getString(cur.getColumnIndex("create_time")));
			msg.setType(cur.getInt(cur.getColumnIndex("type"))==1 ? Type.INPUT : Type.OUTPUT);
			msg.setName(cur.getInt(cur.getColumnIndex("type"))==1 ? "±¦±¦" : "¸ç¸ç");
			list.add(msg);
		}
		
		db.close();
		
		Log.d("db","query "+list.size() +" ÌõÊý¾Ý ");
		
		Collections.reverse(list);
		return list;
	}


	public Context getContext() {
		return context;
	}


	public void setContext(Context context) {
		this.context = context;
	}
}
