package com.egeio.ui.fragment;

import java.util.Arrays;
import java.util.List;

import com.egeio.common.ConstValues;
import com.egeio.common.holder.BaseButtonHolder;
import com.egeio.common.holder.FileListPageHolder;
import com.egeio.common.mo.DataTypes;
import com.egeio.common.mo.Item;
import com.egeio.common.view.PullToRefreshListView.OnRefreshListener;
import com.egeio.framework.ActionManager;
import com.egeio.framework.BackgroundTask;
import com.egeio.framework.BaseActivity;
import com.egeio.framework.BaseFragment;
import com.egeio.network.NetworkManager;
import com.egeio.ui.R;
import com.egeio.ui.activity.RequestReviewActivvity;
import com.egeio.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;


public class FileListFragment extends BaseFragment implements FileListPageHolder.OnListSelectChangListener{

	public static final String TAG = "FileListFragment";

	private FileListPageHolder mPageHolder;

	private int mLimit = 0, mOffset = 0;
	
	private long ItemID = -1;
	
	private DataTypes.FolderItemBundle mItemBundle;
	
	private OnRequestNewFragment mRequest;
	
	private View loading;
	
	private PopupWindow mSelectedMenuPop;
	
	private LayoutInflater mInflater;
	
	private BaseButtonHolder mMeuBarHolder;
	
	private View mParent;
	
	private ActionManager.OnDoingTaskListener mTaskDoingListener = new ActionManager.OnDoingTaskListener((BaseActivity)getActivity()) {
		@Override
		public boolean done(int code, final Bundle args) {
			
			if (ActionManager.ACTION_REFERSH_FILE_LIST == code) {
				mParent.post(new Runnable() {
					@Override
					public void run() {
						DataTypes.FolderItemBundle mItemBundle = (DataTypes.FolderItemBundle) args.getSerializable(ConstValues.ITEM_LIST);
						if (mItemBundle != null && mItemBundle.items != null) {
							mPageHolder.setFileList(Arrays.asList(mItemBundle.items), FileListFragment.this);
							mPageHolder.setFilecountinfo(20, 100);
						}
						loading.setVisibility(View.GONE);
					}
				});
			} else if (ActionManager.ACTION_DELETE == code) {
				mParent.post(new Runnable() {
					@Override
					public void run() {
						Utils.showToast(getActivity(), " 删除成功");
						doUpdate();
					}
				});
			}
			
			return true;
		}
	};
	
	public static interface OnRequestNewFragment {
		public void onNewFragment(long itemID, String name);
	}
	
	public static FileListFragment newInstance (Context context, OnRequestNewFragment request) {
		FileListFragment fragment = new FileListFragment();
		fragment.mRequest = request;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getArguments();
		if (bundle != null) {
			ItemID = bundle.getLong(ConstValues.FOLDER_ID, 0);
		}
		
		mMeuBarHolder = new BaseButtonHolder(getActivity(), this);
		
		mInflater = LayoutInflater.from(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mParent = inflater.inflate(R.layout.fragment_filelist, null);
		loading = mParent.findViewById(R.id.loading); 
		
		mPageHolder = new FileListPageHolder(getActivity());
		mPageHolder.initUi(mParent);
		mPageHolder.updateListener(mRequest);
		mPageHolder.mFileList.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				doUpdate();
				loading.setVisibility(View.VISIBLE);
			}
		});
		mParent.setTag(mPageHolder);
		
		return mParent;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		initData();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(ConstValues.limit, mLimit);
		outState.putInt(ConstValues.offset, mOffset);
		outState.putSerializable(ConstValues.ITEM_LIST, mItemBundle);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onLoadInstanceState(Bundle bundle) {
		super.onLoadInstanceState(bundle);
		if (bundle != null) {
			mLimit = bundle.getInt(ConstValues.limit);
			mOffset = bundle.getInt(ConstValues.offset);
			mItemBundle = (DataTypes.FolderItemBundle)bundle.getSerializable(ConstValues.ITEM_LIST);
		}
	}
	
	@Override
	protected void initData() {
		if (mItemBundle == null || mItemBundle.items == null || mItemBundle.items.length<= 0) {
			doUpdate();
			loading.setVisibility(View.VISIBLE);
		} else {
			mPageHolder.setFileList(Arrays.asList(mItemBundle.items), this);
			mPageHolder.setFilecountinfo(20, 100);
		}
		loading.setVisibility(View.VISIBLE);
	}
	
	public void doUpdate () {
		Bundle bundle = new Bundle();
		bundle.putLong(ConstValues.FOLDER_ID, ItemID > 0 ? ItemID : 0);
		ActionManager.getInstance().startAction(mActivity, ActionManager.ACTION_REFERSH_FILE_LIST, bundle, mTaskDoingListener);
	}
	
	public void openSelectedMenu() {
		if (mSelectedMenuPop != null && mSelectedMenuPop.isShowing())
			return;
		
		View popView = mInflater.inflate(R.layout.menu_bottom, null);
		mMeuBarHolder.initUi(popView);
		
		mSelectedMenuPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		mSelectedMenuPop.setFocusable(false);
		
		mSelectedMenuPop.showAtLocation(mParent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	private String[] getSelectedIds () {
		if (mPageHolder != null) {
			List<Item> items = mPageHolder.getSelectedList();
			String[] ids = new String[items.size()];
			for (int i=0; i<items.size(); i++) {
				Item item = items.get(i);
				ids[i] = item.getType() + "_" + item.getId();
			}
			return ids;
		}
		return null;
	}
	
	@Override
	public void onStartAction(int code, Bundle bundle) {
		
		if (bundle == null) {
			bundle = new Bundle();
		}
		
		if (ActionManager.ACTION_DELETE == code) {
			bundle.putStringArray(ConstValues.ITEM_IDS, getSelectedIds());
		} else if (ActionManager.ACTION_SENDREVIEW == code) {
			List<Item> selecteds = mPageHolder.getSelectedList();
			DataTypes.BaseItems items = new DataTypes.BaseItems();
			items.children_count = selecteds.size();
			items.items = Utils.convertArray(selecteds);
			bundle.putSerializable(ConstValues.ITEM_LIST, items);
		} else if (ActionManager.ACTION_MORE == code) {
			// 展开菜单，返回
			return;
		} else if (ActionManager.ACTION_SHARED == code) {
			bundle.putSerializable(ConstValues.ITEM_LIST, DataTypes.BaseItems.wrapList(mPageHolder.getSelectedList()));
		}
		ActionManager.getInstance().startAction(mActivity, code, bundle, mTaskDoingListener);
	}

	@Override
	public void onSelectedChange(boolean hasSelected) {
		if (hasSelected) {
			openSelectedMenu();
		} else {
			if (mSelectedMenuPop != null) {
				mSelectedMenuPop.dismiss();
			}
		}
	}
	
	public Long getFolderID () {
		return ItemID;
	}
	
}
