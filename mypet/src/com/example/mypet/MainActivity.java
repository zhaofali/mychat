package com.example.mypet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bean.ChatMessage;
import com.example.bean.ChatMessage.Type;
import com.example.db.MySQLiteDao;
import com.example.my.ChatMessageAdapter;
import com.example.utils.HttpUtils;

public class MainActivity extends Activity {

	private ListView mChatView;

	private EditText mMsg;

	private List<ChatMessage> lMsg = new ArrayList<ChatMessage>();

	private ChatMessageAdapter mAdapter;

	private MySQLiteDao dbDao;

	private Button mButton;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressLint("SimpleDateFormat")
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				ChatMessage from = (ChatMessage) msg.obj;
				lMsg.add(from);

				dbDao.insert(from);
				mAdapter.notifyDataSetChanged();
				mChatView.setSelection(lMsg.size() - 1);
			}else{
				List<ChatMessage> history = dbDao.query(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				int size = lMsg.size();
				lMsg.addAll(history);
				mAdapter.notifyDataSetChanged();
				mChatView.setSelection(lMsg.size() - 1-size);
			}
			
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		initView();

		dbDao = new MySQLiteDao(this);
		mAdapter = new ChatMessageAdapter(this, lMsg);
		mChatView.setAdapter(mAdapter);
	}

	private void initView() {

		mButton = (Button) findViewById(R.id.history);

		mButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message message = Message.obtain();
				message.what = 1;
				mHandler.sendMessage(message);
			}
		});

		mChatView = (ListView) findViewById(R.id.chat_listview);
		mMsg = (EditText) findViewById(R.id.id_chat_message);
		ChatMessage message = new ChatMessage(Type.INPUT, "您好，我是宝宝");
		message.setDate(new Date());
		message.setName("宝宝");
		lMsg.add(message);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void sendMessage(View view) {
		final String msg = mMsg.getText().toString();
		if (msg == null || msg.trim().length() == 0) {
			Toast.makeText(this, "发送文字不能为空！", Toast.LENGTH_SHORT).show();
			return;
		}

		ChatMessage to = new ChatMessage(Type.OUTPUT, msg);
		to.setDate(new Date());
		to.setName("哥哥");
		lMsg.add(to);

		dbDao.insert(to);

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

		new Thread() {
			public void run() {
				ChatMessage from = null;
				try {
					from = HttpUtils.sendMsg(msg);
				} catch (Exception e) {
					from = new ChatMessage(Type.INPUT, "服务器挂了呢...");
				}
				Message message = Message.obtain();
				message.what = 0;
				message.obj = from;
				mHandler.sendMessage(message);
			};
		}.start();
	}

}
