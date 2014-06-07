package com.egeio.common.holder;

import com.egeio.common.mo.Message;
import com.egeio.ui.R;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageInfoHolder extends BaseViewHolder{

	private ImageView mMsgimg;
	
	private TextView mMsgtag, mTitle, mMessage;
	
	public MessageInfoHolder(Context context) {
		super(context);
	}

	@Override
	public void initUi(View view) {
		
		mMsgimg = (ImageView)view.findViewById(R.id.msg_img);
		
		mMsgtag = (TextView)view.findViewById(R.id.msg_tag);
		
		mTitle = (TextView)view.findViewById(R.id.title);
		
		mMessage = (TextView)view.findViewById(R.id.message);
		
	}

	@Override
	public void setupView(Bundle bundle) {
		
	}
	
	public void update (Message message) {
		
		mMsgimg.setImageResource(R.drawable.todo);
		
		mMsgtag.setTag(2);
		
		mTitle.setText(message.content);
		
		mMessage.setText(message.created_at);
	}

}
