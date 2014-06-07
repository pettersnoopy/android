package com.egeio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.egeio.common.holder.FileInfoViewHolder;
import com.egeio.common.mo.DataTypes;
import com.egeio.common.mo.Item;
import com.egeio.network.NetworkManager;
import com.egeio.ui.R;

public class FileListFilterAdapter extends BaseAdapter implements Filterable {

	private Item[] mList = null;

	private FileFilter mFilter;

	private LayoutInflater mInflater;
	private Context mContext;

	private final Object mLock = new Object();

	public FileListFilterAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.length;
	}

	@Override
	public Object getItem(int position) {
		return mList[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Item item = mList[position];
		FileInfoViewHolder mInfoHolder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_group_filefilter, null);
		}

		mInfoHolder = (FileInfoViewHolder) convertView.getTag();

		if (mInfoHolder == null) {
			mInfoHolder = new FileInfoViewHolder(mContext);
			mInfoHolder.initUi(convertView);
		}

		// parrams
		mInfoHolder.updateVaule(item);

		convertView.setTag(mInfoHolder);

		return convertView;
	}

	@Override
	public Filter getFilter() {

		if (mFilter == null) {
			mFilter = new FileFilter();
		}

		return mFilter;
	}

	private class FileFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (prefix != null || prefix.length() != 0) {
				String prefixString = prefix.toString().toLowerCase();

				DataTypes.SearchResultBundle result = NetworkManager.getInstance(mContext).search(prefixString);
				results.values = result.items;
				results.count = result.num_found;
				
			}
			
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			 mList = (Item[])results.values;

	         if (results.count > 0) {
	            notifyDataSetChanged();
	         } else {
	            notifyDataSetInvalidated();
	         }
		}

	}

}
