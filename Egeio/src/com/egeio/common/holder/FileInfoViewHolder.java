package com.egeio.common.holder;

import com.egeio.common.mo.Item;
import com.egeio.ui.R;
import com.egeio.utils.FileUtils;
import com.egeio.utils.Utils;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class FileInfoViewHolder extends BaseViewHolder{
	
	public FileInfoViewHolder(Context context) {
		super(context);
	}

	private Context mContext;
	
	public ImageView album_arrow, album_thumb;
	
	public TextView album_name, album_date, album_time, album_size;
	
	public FileUtils.FileTypes Types;
	
	public CheckBox album_checkbox;
	
	public View leftLayout;
	
	public Item mItem;
	
	public void initUi (View view) {
		album_thumb = (ImageView)view.findViewById(R.id.album_thumb);
		album_arrow = (ImageView)view.findViewById(R.id.album_arrow);
		album_name = (TextView)view.findViewById(R.id.album_name);
		album_date = (TextView)view.findViewById(R.id.album_date);
		album_time = (TextView)view.findViewById(R.id.album_time);
		album_size = (TextView)view.findViewById(R.id.album_size);
		album_checkbox = (CheckBox) view.findViewById(R.id.album_checkbox);
		leftLayout = view.findViewById(R.id.leftLayout);
	}
	
	public void setSelected (boolean selected) {
		if (album_checkbox != null) {
			album_checkbox.setChecked(selected);
		}
	}
	
	@Override
	public void setupView(Bundle bundle) {
	}
	
	public void updateVaule(Item item) {
		if (item != null) {
			mItem = item;
			if (album_name != null) {
				album_name.setText(item.getName());
			}
			
			if (album_date != null) {
				album_date.setText(Utils.formatDate(item.getCreated_at()));
			}
			
			if (album_time != null) {
				album_time.setText(Utils.formatTime(item.getCreated_at()));
			}
			
			if (album_size != null) {
				album_size.setText(Utils.formatSize(mContext, item.getSize()));
			}
			
			if (album_thumb != null) {
				int resID = FileUtils.getFileTypeIcon(FileUtils.getFileTypes(item.getName()));
				album_thumb.setImageResource(resID);
			}
		}
	}
	
	public void setItemSelectedListener (OnCheckedChangeListener listener, boolean selected) {
		if (album_checkbox != null && leftLayout != null) {
			leftLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					album_checkbox.setChecked(!album_checkbox.isChecked());
				}
			});
			
			album_checkbox.setOnCheckedChangeListener(listener);
			album_checkbox.setChecked(selected);
			album_checkbox.setVisibility(View.VISIBLE);
		}
	}

}
