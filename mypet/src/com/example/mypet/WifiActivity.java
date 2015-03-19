package com.example.mypet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bean.ChatMessage;
import com.example.bean.ChatMessage.Type;
import com.example.my.ChatMessageAdapter;
import com.nineoldandroids.view.ViewHelper;

public class WifiActivity extends FragmentActivity {

	private ListView mChatView;

	private EditText mMsg;

	private List<ChatMessage> lMsg = new ArrayList<ChatMessage>();

	private ChatMessageAdapter mAdapter;

	private String dateStr;

	private DrawerLayout mDrawerLayout;
	
	private WifiManager.MulticastLock lock;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressLint("SimpleDateFormat")
		public void handleMessage(Message msg) {
			int size = lMsg.size();
			if (msg.what == 1) {
				ChatMessage from = (ChatMessage) msg.obj;
				if (from != null) {
					lMsg.add(from);
					mAdapter.notifyDataSetChanged();
					mChatView.setSelection(lMsg.size() - 1);
				}
			} else if (msg.what == 2) {
				@SuppressWarnings("unchecked")
				List<ChatMessage> list = (List<ChatMessage>) msg.obj;
				lMsg.addAll(0, list);
				mAdapter.notifyDataSetChanged();
				mChatView.setSelection(lMsg.size() - 1 - size);
			} else {

				dateStr = getSmallestTime(lMsg);
				if (dateStr == null) {
					dateStr = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss")
							.format(new Date());
				}
				Log.d("history", dateStr);
			}

		};
	};

	/**
	 * fuction 获取list中最小的时间
	 * 
	 * @param lMsg
	 */
	public String getSmallestTime(List<ChatMessage> lMsg) {
		String result = null;

		if (lMsg == null || lMsg.size() == 0) {
			result = null;
			return result;
		}

		result = lMsg.get(0).getDateStr();

		Iterator<ChatMessage> it = lMsg.iterator();
		while (it.hasNext()) {
			ChatMessage msg = it.next();
			if (result.compareTo(msg.getDateStr()) > 0) {
				result = msg.getDateStr();
			}
		}
		return result;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.robot_main);

		initView();
		initEvents();
		mAdapter = new ChatMessageAdapter(this, lMsg);
		mChatView.setAdapter(mAdapter);
		
		//udp 相关
		WifiManager manager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		lock= manager.createMulticastLock("test wifi");
	}

	protected void onStart() {
		super.onStart();
	}

	private void initView() {
		mChatView = (ListView) findViewById(R.id.chat_listview);
		mMsg = (EditText) findViewById(R.id.id_chat_message);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
				Gravity.RIGHT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.wifi, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.joinwifi:

			// 创建一个线程，监听udp广播信息
			ChatMessage sendmessage = new ChatMessage();
			sendmessage.setDateStr(new SimpleDateFormat("yyyy-MM-ddHH:mm:ss")
					.format(new Date()));
			sendmessage.setName("哥哥");
			sendmessage.setMsg("已加入局域网聊天");
			sendmessage.setType(Type.OUTPUT);

			Message message = Message.obtain();
			message.what = 1;
			message.obj = sendmessage;
			mHandler.sendMessage(message);

			new Thread() {
				public void run() {
					byte[] buffer = new byte[1024];
					try {
						@SuppressWarnings("resource")
						DatagramSocket server = new DatagramSocket(18888);
						DatagramPacket packet = new DatagramPacket(buffer,
								buffer.length);
						while (true) {
							lock.acquire();
							server.receive(packet);
							String s = new String(packet.getData(), 0,
									packet.getLength());
							lock.release();
							Log.d("socket", s);
							ChatMessage msg = new ChatMessage(Type.INPUT, s);
							msg.setName(packet.getAddress().toString());
							msg.setDateStr(new SimpleDateFormat(
									"yyyy-MM-ddHH:mm:ss").format(new Date()));

							Message message = Message.obtain();
							message.what = 1;
							message.obj = msg;
							mHandler.sendMessage(message);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
			}.start();

//			new Thread() {
//				public void run() {
//					DatagramSocket socket;
//					DatagramPacket packet;
//					try{
//					byte[] data = { 1, 2, 3, 4 };
//					socket = new DatagramSocket();
//					socket.setBroadcast(true); // 有没有没啥不同
//					// send端指定接受端的端口，自己的端口是随机的
//					packet = new DatagramPacket(data, data.length,
//							InetAddress.getByName("255.255.255.255"), 18888);
//					while (true) {
//						Thread.sleep(5000);
//						socket.send(packet);
//						Log.d("udp", "sending ....");
//					}
//					}catch(Exception e){
//						Log.d("udp","udp线程挂了");
//					}
//				};
//
//			}.start();
			return true;
		case R.id.logout:
			finish();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void sendMessage(View view) {
		final String msg = mMsg.getText().toString();
		if (msg == null || msg.trim().length() == 0) {
			Toast.makeText(this, "发送文字不能为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		final ChatMessage to = new ChatMessage(Type.OUTPUT, msg);
		to.setDate(new Date());
		to.setName("哥哥");
		lMsg.add(to);

		mAdapter.notifyDataSetChanged();
		mChatView.setSelection(lMsg.size() - 1);

		// 清空发送区的内容
		mMsg.setText("");

		// 关闭软键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			// 如果开启
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
			// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
		}

		// 使用udp socket 广播
		lock.acquire();
		DatagramSocket server;
		DatagramPacket packet;
		try {
			server = new DatagramSocket(18888,
					InetAddress.getByName("255.255.255.255"));
			server.setBroadcast(true);
			packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length);
			server.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		lock.release();
	}

	private void initEvents() {
		mDrawerLayout.setDrawerListener(new DrawerListener() {
			@Override
			public void onDrawerStateChanged(int newState) {
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				View mContent = mDrawerLayout.getChildAt(0);
				View mMenu = drawerView;
				float scale = 1 - slideOffset;
				float rightScale = 0.8f + scale * 0.2f;

				if (drawerView.getTag().equals("LEFT")) {

					float leftScale = 1 - 0.3f * scale;

					ViewHelper.setScaleX(mMenu, leftScale);
					ViewHelper.setScaleY(mMenu, leftScale);
					ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
					ViewHelper.setTranslationX(mContent,
							mMenu.getMeasuredWidth() * (1 - scale));
					ViewHelper.setPivotX(mContent, 0);
					ViewHelper.setPivotY(mContent,
							mContent.getMeasuredHeight() / 2);
					mContent.invalidate();
					ViewHelper.setScaleX(mContent, rightScale);
					ViewHelper.setScaleY(mContent, rightScale);
				} else {
					ViewHelper.setTranslationX(mContent,
							-mMenu.getMeasuredWidth() * slideOffset);
					ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
					ViewHelper.setPivotY(mContent,
							mContent.getMeasuredHeight() / 2);
					mContent.invalidate();
					ViewHelper.setScaleX(mContent, rightScale);
					ViewHelper.setScaleY(mContent, rightScale);
				}

			}

			@Override
			public void onDrawerOpened(View drawerView) {
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				mDrawerLayout.setDrawerLockMode(
						DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
			}
		});
	}

}
