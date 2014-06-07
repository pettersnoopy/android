package com.egeio.ui.activity;

import com.egeio.common.ConstValues;
import com.egeio.common.Queue;
import com.egeio.common.widget.TitleBar;
import com.egeio.common.widget.TitleBar.Operator;
import com.egeio.framework.ActionManager;
import com.egeio.framework.BaseActivity;
import com.egeio.framework.BaseElement;
import com.egeio.ui.R;
import com.egeio.ui.fragment.FileListFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;

public class FileBrowserActivity extends BaseActivity implements FileListFragment.OnRequestNewFragment, TitleBar.IOnButtonOperatorListener{
	
	private Queue<BaseElement> mQueue = null;
	
	private FileListFragment mFileListFragment;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_content);
		
		if (savedInstanceState != null && savedInstanceState.containsKey(ConstValues.QUEUE)) {
			mQueue = (Queue<BaseElement>)savedInstanceState.getSerializable(ConstValues.QUEUE);
		} else {
			mQueue = new Queue<BaseElement>();
		}
		
		// root
		onNewFragment(0, getString(R.string.File));
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(ConstValues.QUEUE, mQueue);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onNewFragment(long itemID, String name) {
		FragmentTransaction tranSaction = getSupportFragmentManager().beginTransaction();
		// init content
		mFileListFragment = FileListFragment.newInstance(this, this);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ConstValues.FOLDER_ID, itemID);
		mFileListFragment.setArguments(bundle);
		tranSaction.replace(R.id.content, mFileListFragment);
		
		if (name == null || "".equals(name)) {
			name = getString(R.string.File);
		}
		
		if (itemID != 0) {
			mTitleBar = TitleBar.creater(this, name,  Operator.Back, null, null, Operator.CreateNewFile);
		} else {
			mTitleBar = TitleBar.creater(this, name, null, null, null, Operator.CreateNewFile);
		}
		
		mTitleBar.setOnButtonOperatorListener(this);
		
		tranSaction.replace(R.id.lay_titlebar, mTitleBar);
		tranSaction.commit();
		
		// update Queue
		BaseElement element = new BaseElement(itemID, name);
		if (!mQueue.hasElement(element)) {
			mQueue.put(element);
		}
	}

	@Override
	public boolean onClick(Operator operator) {
		if (operator == Operator.Back) {
			BaseElement element = mQueue.get();
			onNewFragment(element.Id, element.Name);
			return true;
		}
		return false;
	}

	@Override
	public String getActivityTag() {
		return FileBrowserActivity.class.getName();
	}
	
	@Override
	public Long getFolderID() {
		return mFileListFragment != null ? mFileListFragment.getFolderID() : -1;
	}
	
	@Override
	public void onTaskDone(int code, Bundle resule) {
		if (ActionManager.ACTION_CREATEFOLDER == code && mFileListFragment != null) {
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mFileListFragment.doUpdate();
				}
			});
			
		}
	}
	
	@Override
	public void showLoading() {
		View loading = findViewById(R.id.loading);
		if (loading != null) {
			loading.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void hideLoading() {
		View loading = findViewById(R.id.loading);
		if (loading != null) {
			loading.setVisibility(View.GONE);
		}
	}

}
