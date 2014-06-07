package com.egeio.common.holder;

import com.egeio.framework.ActionManager;
import com.egeio.ui.R;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class BaseButtonHolder extends BaseViewHolder{
	
	protected View AddFolder, SendReview, Viewer, Delete, Mark, Share, Rename, Download, Upload, More, Copy, msgHos, Comment;
	
	protected ActionManager.ActionListener mActionListener; 

	public BaseButtonHolder(Context context, ActionManager.ActionListener listener) {
		super(context);
		mActionListener = listener;
	}

	@Override
	public void initUi(View view) {
		
		AddFolder = view.findViewById(R.id.AddFolder);
		
		SendReview = view.findViewById(R.id.SendReview);
		
		Viewer = view.findViewById(R.id.Viewer);
		
		Delete = view.findViewById(R.id.Delete);
		
		Mark = view.findViewById(R.id.Mark);
		
		Share = view.findViewById(R.id.Share);
		
		Rename = view.findViewById(R.id.Rename);
		
		Download = view.findViewById(R.id.Download);
		
		Upload = view.findViewById(R.id.Upload);
		
		More = view.findViewById(R.id.more);
		
		Copy = view.findViewById(R.id.Copy);
		
		msgHos = view.findViewById(R.id.msgHos);
		
		Comment = view.findViewById(R.id.Comment);
		
		updateListener();
	}

	@Override
	public void setupView(Bundle bundle) {
		
	}
	
	protected void updateListener () {
		
		if (AddFolder != null) {
			AddFolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionListener.onStartAction(ActionManager.ACTION_ADDFOLDER, null);
				}
			});
		}
		
		if (SendReview != null) { 
			SendReview.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionListener.onStartAction(ActionManager.ACTION_SENDREVIEW, null);
				}
			});
		}
		
		if (Viewer != null) {
			Viewer.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionListener.onStartAction(ActionManager.ACTION_VIEWER, null);
				}
			});
		}
		
		if (Delete != null) {
			Delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionListener.onStartAction(ActionManager.ACTION_DELETE, null);
				}
			});
		}
		
		if (Mark != null) {
			Mark.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionListener.onStartAction(ActionManager.ACTION_MARK, null);
				}
			});
		}
		
		if (Share != null) {
			Share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionListener.onStartAction(ActionManager.ACTION_SHARED, null);
				}
			});
		}
		
		if (Rename != null) {
			Rename.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionListener.onStartAction(ActionManager.ACTION_RENAME, null);
				}
			});
		}
		
		if (Download != null) {
			Download.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionListener.onStartAction(ActionManager.ACTION_DOWNLOAD, null);
				}
			});
		}
		
		if (Upload != null) {
			Upload.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionListener.onStartAction(ActionManager.ACTION_UPLOAD, null);
				}
			});
		}
		
		if (More != null) {
			More.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionListener.onStartAction(ActionManager.ACTION_MORE, null);
				}
			});
		}
		
		if (Copy != null) {
			Copy.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionListener.onStartAction(ActionManager.ACTION_COPY, null);
				}
			});
		}
		
		if (msgHos != null) {
			msgHos.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionListener.onStartAction(ActionManager.ACTION_MSG_HOS, null);
				}
			});
		}
		
		if (Comment != null) {
			Comment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActionListener.onStartAction(ActionManager.ACTION_MSG_HOS, null);
				}
			});
		}
	}

}
