package com.egeio.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.egeio.common.holder.FileInfoViewHolder;
import com.egeio.common.holder.FileListPageHolder;
import com.egeio.common.mo.Item;
import com.egeio.ui.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FileListAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private Context mContext;
	
	private List<Item> mFileLists = new ArrayList<Item>();
	
	private Map<Long, Item> mSelectedMap = new HashMap<Long, Item>();
	
	private FileListPageHolder.OnListSelectChangListener mListener;
	
	public FileListAdapter(Context context, FileListPageHolder.OnListSelectChangListener listener) {
		mInflater = LayoutInflater.from(context);
		mListener = listener;
	}
	
	public void setFileList (List<Item> items) {
		mFileLists = items;
	}
	
	public Map<Long, Item> getSelectedMap() {
		return mSelectedMap;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFileLists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mFileLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mFileLists.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Item item = mFileLists.get(position);
		FileInfoViewHolder mInfoHolder;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_group_filelist, null);
		}
		
		mInfoHolder = (FileInfoViewHolder)convertView.getTag();
		
		if (mInfoHolder == null) {
			mInfoHolder = new FileInfoViewHolder(mContext);
			mInfoHolder.initUi(convertView);
		}
		
		// parrams
		mInfoHolder.updateVaule(item);
		mInfoHolder.setItemSelectedListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (mSelectedMap.isEmpty()){
						mListener.onSelectedChange(true);
					}
					mSelectedMap.put(item.getId(), item);
				} else {
					mSelectedMap.remove(item.getId());
					if (mSelectedMap.isEmpty()){
						mListener.onSelectedChange(false);
					}
				}
			}
		}, mSelectedMap.containsKey(item.getId()));
		convertView.setTag(mInfoHolder);
		
		return convertView;
	}
	
	public boolean isSelected () {
		return mSelectedMap.size() > 0 ? true : false;
	}
	
	public void setSelect (View view, int position) {
		FileInfoViewHolder holder = (FileInfoViewHolder) view.getTag();
		if (holder != null) {
			holder.setSelected(!mSelectedMap.containsKey(holder.mItem.getId()));
		}
	}

}
