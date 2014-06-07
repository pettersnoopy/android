package com.egeio.adapter;

import java.util.List;

import com.egeio.common.holder.MessageInfoHolder;
import com.egeio.common.mo.Message;
import com.egeio.ui.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MessageListAdapter extends BaseAdapter{
	
	private List<Message> mList =  null;
	
	private Context mContext;
	
	private LayoutInflater mInflater;
	
	public MessageListAdapter (Context context, List<Message> list) {
		mContext = context;
		mList = list;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mList.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Message message = mList.get(position);
		
		MessageInfoHolder holder = null;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.message_item, null);
		}
		
		holder = (MessageInfoHolder)convertView.getTag();
		
		if (holder == null) {
			holder = new MessageInfoHolder(mContext);
			holder.initUi(convertView);
		}
		
		holder.update(message);
		
		convertView.setTag(holder);
		return convertView;
	}

}
