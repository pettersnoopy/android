package com.egeio.ui.activity;

import java.util.Arrays;

import com.egeio.adapter.MessageListAdapter;
import com.egeio.common.ConstValues;
import com.egeio.common.mo.DataTypes;
import com.egeio.common.mo.Message;
import com.egeio.common.widget.TitleBar;
import com.egeio.common.widget.TitleBar.Operator;
import com.egeio.framework.BackgroundTask;
import com.egeio.framework.BaseActivity;
import com.egeio.ui.R;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.ListView;

public class MessageActivity extends BaseActivity{

	private ListView mList;
	
	private DataTypes.CommentItemBundle mCommentBundle;
	
	private MessageListLoader mLoader;
	
	private MessageListAdapter mAdapter;
	
	private TitleBar mTitleBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			mCommentBundle = (DataTypes.CommentItemBundle) savedInstanceState.getSerializable(ConstValues.CONTACT_LIST);
		}
		
		setContentView(R.layout.comments_main);
		
		mList = (ListView) findViewById(R.id.listView);
		
		
		if (mCommentBundle == null || mCommentBundle.comments == null || mCommentBundle.comments.length <= 0) {
			
			if (mLoader == null) {
				mLoader = new MessageListLoader(this);
			}
			
			mLoader.start(new Bundle());
		} else {
			initValues(mCommentBundle);
		}
	}
	
	private void initValues (DataTypes.CommentItemBundle commentsBundle) {
		
		mTitleBar = TitleBar.creater(this, getString(R.string.Message), null, null, null, Operator.MenuFilter);
		
		FragmentTransaction tranSaction = getSupportFragmentManager().beginTransaction();
		tranSaction.replace(R.id.lay_titlebar, mTitleBar);
		tranSaction.commit();
		
		if (commentsBundle != null && commentsBundle.comments != null && commentsBundle.comments.length>0) {
			
			mAdapter = new MessageListAdapter(this, Arrays.asList(commentsBundle.comments));
			
			mList.setAdapter(mAdapter);
			
		}
	}
	
	 @Override
	protected void onSaveInstanceState(Bundle outState) {
		 
		 outState.putSerializable(ConstValues.CONTACT_LIST, mCommentBundle);
		 
		super.onSaveInstanceState(outState);
	}



	protected class MessageListLoader extends BackgroundTask {

		public MessageListLoader(BaseActivity activity) {
			super(activity);
		}

		@Override
		protected Object doInBackground(Bundle bundle) {
//			return NetworkManager.getInstance(MessageActivity.this).getMessage(0);
			DataTypes.CommentItemBundle commentItemBundle = new DataTypes.CommentItemBundle();
			commentItemBundle.comment_count = 10;
			commentItemBundle.comments = new Message[10];
			for (int i=0; i<commentItemBundle.comment_count; i++) {
				commentItemBundle.comments[i] = new Message();
				commentItemBundle.comments[i].content = "王某某给你发来贺电";
				commentItemBundle.comments[i].created_at = "2012-10-08T6:41:43-08:00";
				commentItemBundle.comments[i].id = i;
			}
			return commentItemBundle;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (result != null && result instanceof DataTypes.CommentItemBundle) {
				mCommentBundle = (DataTypes.CommentItemBundle) result;
				
				initValues (mCommentBundle);
			}
		}
		 
	 }



	@Override
	public String getActivityTag() {
		return MessageActivity.class.toString();
	}

}
