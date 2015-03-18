package com.example.my;

import java.util.List;

import com.example.bean.ChatMessage;
import com.example.bean.ChatMessage.Type;
import com.example.mypet.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatMessageAdapter extends BaseAdapter {

	private List<ChatMessage> mList;

	private LayoutInflater mInflater;

	public ChatMessageAdapter(Context context, List<ChatMessage> mList) {
		this.mInflater = LayoutInflater.from(context);
		this.mList = mList;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		ChatMessage msg = mList.get(arg0);
		ViewHolder vHolder = null;
		Log.d("list", "type" + msg.getType());
		if (arg1 == null) {
			vHolder = new ViewHolder();
			if (msg.getType() == Type.INPUT) {
				arg1 = mInflater.inflate(R.layout.left_text_view, arg2, false);
				vHolder.createTime = (TextView) arg1
						.findViewById(R.id.left_text_view);
				vHolder.content = (TextView) arg1
						.findViewById(R.id.left_message);
				vHolder.name = (TextView) arg1.findViewById(R.id.left_name);
				arg1.setTag(vHolder);
				Log.d("list", "position" + arg0);
			} else {
				Log.d("list", "position" + arg0);
				arg1 = mInflater.inflate(R.layout.right_text_view, null);
				vHolder.createTime = (TextView) arg1
						.findViewById(R.id.right_text_view);
				vHolder.content = (TextView) arg1
						.findViewById(R.id.right_message);
				vHolder.name = (TextView) arg1.findViewById(R.id.right_name);
				arg1.setTag(vHolder);
			}
		} else {
			vHolder = (ViewHolder) arg1.getTag();
		}
		vHolder.content.setText(msg.getMsg());
		vHolder.name.setText(msg.getName());
		vHolder.createTime.setText(msg.getDateStr());
		return arg1;
	}

//	@Override
//	public View getView(int position, View convertView, ViewGroup parent)
//	{
//		ChatMessage chatMessage = mList.get(position);
//
//		ViewHolder viewHolder = null;
//
//		if (convertView == null)
//		{
//			viewHolder = new ViewHolder();
//			if (chatMessage.getType() == Type.INPUT)
//			{
//				convertView = mInflater.inflate(R.layout.left_text_view,
//						parent, false);
//				viewHolder.createTime = (TextView) convertView
//						.findViewById(R.id.left_text_view);
//				viewHolder.content = (TextView) convertView
//						.findViewById(R.id.left_message);
//				convertView.setTag(viewHolder);
//				Log.d("list", "position"+position);
//			} else
//			{
//				convertView = mInflater.inflate(R.layout.right_text_view,
//						null);
//				Log.d("list", "position"+position);
//				viewHolder.createTime = (TextView) convertView
//						.findViewById(R.id.right_text_view);
//				viewHolder.content = (TextView) convertView
//						.findViewById(R.id.right_message);
//				convertView.setTag(viewHolder);
//			}
//
//		} else
//		{
//			viewHolder = (ViewHolder) convertView.getTag();
//		}
//
//		viewHolder.content.setText(chatMessage.getMsg());
//		viewHolder.createTime.setText(chatMessage.getDateStr());
//
//		return convertView;
//	}
	
	class ViewHolder {
		private TextView createTime;
		private TextView name;
		private TextView content;
	}

	@Override
	public int getItemViewType(int position) {
		ChatMessage msg = mList.get(position);
		return msg.getType() == Type.INPUT ? 1 : 0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

}
