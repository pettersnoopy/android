package com.egeio.common.holder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.egeio.adapter.FileListAdapter;
import com.egeio.common.ConstValues;
import com.egeio.common.mo.Item;
import com.egeio.common.view.PullToRefreshListView;
import com.egeio.common.widget.SearchBox;
import com.egeio.doc.BrowserActivity;
import com.egeio.ui.R;
import com.egeio.ui.fragment.FileListFragment;
import com.egeio.utils.FileUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileListPageHolder extends BaseViewHolder{
	
	public SearchBox mSearchBox;
	
	public LinearLayout mFilecountinfo;
	
	public TextView mTVFilecountinfo;
	
	public PullToRefreshListView mFileList;
	
	public FileListAdapter mAdapter;
	
	public OnListSelectChangListener mListener;
	
	public interface OnListSelectChangListener {
		public void onSelectedChange(boolean hasSelected);
	}

	public FileListPageHolder(Context context) {
		super(context);
	}

	@Override
	public void initUi(View view) {
		mSearchBox = (SearchBox)view.findViewById(R.id.search);
		
		mFilecountinfo = (LinearLayout)view.findViewById(R.id.filecountinfo);
		
		mTVFilecountinfo = (TextView)view.findViewById(R.id.tv_filecountinfo);
		
		if (mTVFilecountinfo!=null) {
			mTVFilecountinfo.setVisibility(View.INVISIBLE);
		}
		
		mFileList = (PullToRefreshListView)view.findViewById(R.id.file_list);
	}

	@Override
	public void setupView(Bundle bundle) {
	}
	
	public void updateListener (final FileListFragment.OnRequestNewFragment request) {
		mFileList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!mAdapter.isSelected()) {
					FileInfoViewHolder holder = (FileInfoViewHolder)view.getTag();
					
					if (holder.mItem != null) {
						FileUtils.FileTypes type = FileUtils.getFileTypes(holder.mItem.getName());
						
						if (type == FileUtils.FileTypes.folder) {
							
							request.onNewFragment(holder.mItem.getId(), holder.mItem.getName());
							
						} else {
							// handle open file
							Intent intent = new Intent(mContext, BrowserActivity.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable(ConstValues.ITEMINFO, holder.mItem);
							intent.putExtras(bundle);
							mContext.startActivity(intent);
						}
					}
				} else {
					mAdapter.setSelect(view, position);
				}
			}
		});
	}
	
	public void setFileList(List<Item> items, OnListSelectChangListener selectedChangeListener) {
		
		mListener = selectedChangeListener;
		
		if (mFileList != null && mFilecountinfo != null) {
			if (items != null && items.size()>0) {
				if (mAdapter == null) {
					mAdapter = new FileListAdapter(mContext, selectedChangeListener);
					mFileList.setAdapter(mAdapter);
				}
				mAdapter.setFileList(items);
				mAdapter.notifyDataSetChanged();
				
			} else {
				mFilecountinfo.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	public void setFilecountinfo (int files, int directors) {
		if (mTVFilecountinfo != null) {
			mTVFilecountinfo.setVisibility(View.VISIBLE);
		}
		
		if (mTVFilecountinfo != null) {
			String fileinfo = String.format(mContext.getResources().getString(R.string.file_count_info), directors, files);
			mTVFilecountinfo.setText(fileinfo);
		}
	}
	
	public List<Item> getSelectedList() {
		List<Item> items = new ArrayList<Item>();
		if (mAdapter != null) {
			Map<Long, Item> map = mAdapter.getSelectedMap();
			items.addAll(map.values());
		} 
		return items;
	}

}
