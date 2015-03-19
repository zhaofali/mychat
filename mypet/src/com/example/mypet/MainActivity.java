package com.example.mypet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.db.MySQLiteDao;
import com.example.my.ChatMessageAdapter;
import com.example.layout.RefreshableView;
import com.example.layout.RefreshableView.PullToRefreshListener;
import com.example.utils.C;
import com.example.utils.HttpUtils;
import com.nineoldandroids.view.ViewHelper;

public class MainActivity extends FragmentActivity {

	private ListView mChatView;

	private EditText mMsg;

	private List<ChatMessage> lMsg = new ArrayList<ChatMessage>();

	private ChatMessageAdapter mAdapter;

	private MySQLiteDao dbDao;

	private String dateStr;

	private RefreshableView refreshableView;

	private DrawerLayout mDrawerLayout;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressLint("SimpleDateFormat")
		public void handleMessage(Message msg) {
			int size = lMsg.size();
			if (msg.what == 0) {
				ChatMessage from = (ChatMessage) msg.obj;
				lMsg.add(from);

				dbDao.insert(from,C.robot);
				mAdapter.notifyDataSetChanged();
				mChatView.setSelection(lMsg.size() - 1);
			} else if (msg.what == 3) {
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
				List<ChatMessage> history = dbDao.query(dateStr,C.robot);
				Log.d("history", "history size" + history.size());
				if (history != null && history.size() > 0) {
					lMsg.addAll(0, history);
					mAdapter.notifyDataSetChanged();
					mChatView.setSelection(lMsg.size() - 1 - size);
				} else {

				}
			}

		};
	};

	/**
	 * fuction ��ȡlist����С��ʱ��
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
		dbDao = new MySQLiteDao(this);
		mAdapter = new ChatMessageAdapter(this, lMsg);
		mChatView.setAdapter(mAdapter);
	}

	protected void onStart() {
		super.onStart();
	}

	private void initView() {
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				// new Thread() {
				// public void run() {
				// List<ChatMessage> list = HttpUtils
				// .getMessageFromServer("���");
				// Message message = Message.obtain();
				// if (list != null && list.size() > 0) {
				// message.obj = list;
				// message.what = 2;
				// } else {
				// message.what = 1;
				// }
				// mHandler.sendMessage(message);
				// }
				// }.start();
				Message message = Message.obtain();
				message.what = 1;
				mHandler.sendMessage(message);
				refreshableView.finishRefreshing();
			}
		}, 0);
		mChatView = (ListView) findViewById(R.id.chat_listview);
		mMsg = (EditText) findViewById(R.id.id_chat_message);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
				Gravity.RIGHT);
		// ChatMessage message = new ChatMessage(Type.INPUT, "��ã����Ǳ���");
		// message.setDate(new Date());
		// message.setName("����");
		// lMsg.add(message);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.robot, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.history_message:
			Message message = Message.obtain();
			message.what = 1;
			mHandler.sendMessage(message);
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
			Toast.makeText(this, "发送内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		final ChatMessage to = new ChatMessage(Type.OUTPUT, msg);
		to.setDate(new Date());
		to.setName("哥哥");
		lMsg.add(to);

		dbDao.insert(to,C.robot);

		mAdapter.notifyDataSetChanged();
		mChatView.setSelection(lMsg.size() - 1);

		// ��շ����������
		mMsg.setText("");

		// �ر������
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			// �����
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
			// �ر�����̣�����������ͬ������������л�������ر�״̬��
		}

		new Thread() {
			public void run() {
				ChatMessage from = null;
				try {
					from = HttpUtils.sendMsg(msg);
				} catch (Exception e) {
					from = new ChatMessage(Type.INPUT, "服务器出错了...");
				}
				Message message = Message.obtain();
				message.what = 0;
				message.obj = from;
				mHandler.sendMessage(message);
			};
		}.start();
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
