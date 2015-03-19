package com.example.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.example.bean.ChatMessage;
import com.example.bean.ChatMessage.Type;
import com.example.bean.CommonException;
import com.example.bean.Message;
import com.example.bean.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HttpUtils
{
	private static String API_KEY = "d15d73af7004ab8570489f7b77e579ba";
	private static String URL = "http://www.tuling123.com/openapi/api";

	/**
	 * 发�?�?��消息，并得到返回的消�?
	 * @param msg
	 * @return
	 */
	public static ChatMessage sendMsg(String msg)
	{
		ChatMessage message = new ChatMessage();
		String url = setParams(msg);
		String res = doGet(url);
		Gson gson = new Gson();
		Result result = gson.fromJson(res, Result.class);
		
		if (result.getCode() > 400000 || result.getText() == null
				|| result.getText().trim().equals(""))
		{
			message.setMsg("该功能等待开�?..");
		}else
		{
			message.setMsg(result.getText());
		}
		message.setType(Type.INPUT);
		message.setDate(new Date());
		message.setName("宝宝");
		return message;
	}

	/**
	 * 拼接Url
	 * @param msg
	 * @return
	 */
	private static String setParams(String msg)
	{
		try
		{
			msg = URLEncoder.encode(msg, "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return URL + "?key=" + API_KEY + "&info=" + msg;
	}

	/**
	 * Get请求，获得返回数�?
	 * @param urlStr
	 * @return
	 */
	private static String doGet(String urlStr)
	{
		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try
		{
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5 * 1000);
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == 200)
			{
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[128];

				while ((len = is.read(buf)) != -1)
				{
					baos.write(buf, 0, len);
				}
				baos.flush();
				return baos.toString();
			} else
			{
				throw new CommonException("服务器连接错误！");
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			throw new CommonException("服务器连接错误！");
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			try
			{
				if (baos != null)
					baos.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			conn.disconnect();
		}

	}
	
	/**
	 * 向服务器存储发送的信息
	 * @param msg
	 */
	public static void storeMsg(ChatMessage msg){
		URL url = null;
		HttpURLConnection con = null;
		
		try {
			url = new URL(getParams(msg));
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			con.setRequestMethod("GET");
			
			if(con.getResponseCode() != 200){
				Log.d("httpUtil", "请求失败");
				throw new CommonException("存储服务器连接错误!");
			}
		} catch (Exception e) {
			Log.e("httpUtil", e.getMessage());
			throw new CommonException("存储服务器连接错误!");
		} finally{
			con.disconnect();
		}
	}
	
	private static String getParams(ChatMessage msg){
		String result = C.SERVER;
		StringBuffer sb = new StringBuffer();
		sb.append("?name=");
		sb.append(URLEncoder.encode(msg.getName()));
		sb.append("&message=");
		sb.append(URLEncoder.encode(msg.getMsg()));
		sb.append("&dateStr=");
		sb.append(msg.getDateStr());
		sb.append("&type=1&action=store");
		return result+sb.toString();
	}
	
	/**
	 * 从存储服务器获取未读消息
	 * @param name
	 * @return
	 */
	public static List<ChatMessage> getMessageFromServer(String name){
		List<ChatMessage> list = new ArrayList<ChatMessage>();
		URL url = null;
		HttpURLConnection con = null;
		String respond = "";
		try {
			url = new URL(C.SERVER+"?action=get&name="+URLEncoder.encode(name));
			con = (HttpURLConnection) url.openConnection();
			con.setReadTimeout(5 * 1000);
			con.setConnectTimeout(5 * 1000);
			con.setRequestMethod("GET");
			
			if(con.getResponseCode() != 200){
				throw new CommonException("存储服务器连接错误！");
			}
			respond = getRespondJson(con.getInputStream());
			
			Log.d("json", respond);
			
			Gson gson = new Gson();  
			
			List<Message>  listMsg = gson.fromJson(respond, new TypeToken<List<Message>>() {  
            }.getType()); 
			
			Iterator<Message> it = listMsg.iterator();
			while(it.hasNext()){
				ChatMessage m = new ChatMessage();
				Message msg = it.next();
				if(msg.getMessage().trim() == ""){
					continue;
				}
				m.setDateStr(msg.getDateStr());
				m.setMsg(msg.getMessage());
				m.setType(Type.INPUT);
				m.setName("宝宝");
				list.add(m);
			}
		} catch (Exception e) {
			ChatMessage message = new ChatMessage();
			message.setName("宝宝");
			message.setDateStr(new SimpleDateFormat("yyyy-MM-ddHH:mm:ss").format(new Date()));
			message.setMsg("服务器挂了啊");
			list.add(message);
			return list;
		} finally{
			if(con != null)
			con.disconnect();
		}
		if(list != null && list.size() == 0){
			ChatMessage message = new ChatMessage();
			message.setName("宝宝");
			message.setDateStr(new SimpleDateFormat("yyyy-MM-ddHH:mm:ss").format(new Date()));
			message.setMsg("没有留言");
			list.add(message);
		}
		return list;
	}
	
	/**
	 * 将返回的流组成字符串
	 * @param inputStream
	 * @return
	 * @throws IOException 
	 */
	private static String getRespondJson(InputStream in) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		int length = -1;
		byte[] buff = new byte[1024];
		
		while((length = in.read(buff)) != -1){
			bos.write(buff, 0, length);
		}
		bos.flush();
		return bos.toString();
	}

}
