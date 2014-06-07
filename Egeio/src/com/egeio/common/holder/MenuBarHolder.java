package com.egeio.common.holder;

import com.egeio.ui.R;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

public class MenuBarHolder {
	
	private Context mContext;
	
	private View mShare, mDelete, mMark, mRename, mView, mDown, mMore;
	
	private boolean mIsDirector = false;
	
	public MenuBarHolder(Context context) {
		mContext = context;
	}
	
	public void initView (View view, boolean isDirector) {
		
		mShare = view.findViewById(R.id.share);
		mDelete = view.findViewById(R.id.delete);
		mMark = view.findViewById(R.id.mark);
		mRename = view.findViewById(R.id.rename);
		mView = view.findViewById(R.id.view);
		mDown = view.findViewById(R.id.down);
		mMore = view.findViewById(R.id.more);
		setisDirector(isDirector);
	}
	
	public void setisDirector (boolean isDirector) {
		mIsDirector = isDirector;
		if (isDirector) {
			setViewShow(mShare, true);
			setViewShow(mDelete, true);
			setViewShow(mMark, true);
			setViewShow(mRename, true);
			setViewShow(mView, false);
			setViewShow(mDown, false);
			setViewShow(mMore, false);
		} else {
			setViewShow(mShare, true);
			setViewShow(mDelete, true);
			setViewShow(mMark, false);
			setViewShow(mRename, false);
			setViewShow(mView, true);
			setViewShow(mDown, false);
			setViewShow(mMore, true);
		}
	}
	
	public boolean isDirector () {
		return mIsDirector;
	}
	
	private void setViewShow (View view, boolean show) {
		if (view != null) {
			if (show) {
				view.setVisibility(View.VISIBLE);
			} else {
				view.setVisibility(View.GONE);
			}
		}
	}
	
	public void setOnItemClickListener (View.OnClickListener listener) {
		if (mShare != null) {
			mShare.setOnClickListener(listener);
		}
		
		if (mDelete != null) {
			mDelete.setOnClickListener(listener);
		}
		
		if (mMark != null) {
			mMark.setOnClickListener(listener);
		}
		
		if (mRename != null) {
			mRename.setOnClickListener(listener);
		}
		
		if (mView != null) {
			mView.setOnClickListener(listener);
		}
		
		if (mDown != null) {
			mDown.setOnClickListener(listener);
		}
		
		if (mMore != null) {
			mMore.setOnClickListener(listener);
		}
	}
	
	/**
	 * 
	 */
	public void setup() {
		
	}
	
}
