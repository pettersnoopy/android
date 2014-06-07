package com.egeio.adapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.egeio.common.holder.ContactItemHolder;
import com.egeio.common.mo.Contact;
import com.egeio.ui.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ContatsListAdapter extends BaseAdapter implements SectionIndexer {
	
	private List<Contact> mContactsList;
	
	private Context mContext;
	
	private LayoutInflater mInflater;
	
	private Map<Long, Contact> selectedMap = new HashMap<Long, Contact>();
	
	private int mLayoutID;
	
	private OnCheckedChangeListener mSelectedListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Contact contact = (Contact) buttonView.getTag();
			if (isChecked) {
				selectedMap.put(contact.id, contact);
			} else {
				selectedMap.remove(contact.id);
			}
		}
	};
	
	public ContatsListAdapter(Context context, int LayoutID, List<Contact> contacts) {
		this(context, LayoutID, contacts, null);
	}
	
	public ContatsListAdapter(Context context, int LayoutID, List<Contact> contacts, Map<Long, Contact> selected) {
		mContactsList = contacts;
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mLayoutID = LayoutID;
		
		if (selected != null) {
			selectedMap = selected;
		}
	}
	
	public int getCount() {
		return mContactsList.size();
	}
	
	public Object getItem(int arg0) {
		return mContactsList.get(arg0);
	}
	
	public long getItemId(int arg0) {
		return 0;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		if (view == null) {
			view = mInflater.inflate(mLayoutID, null);
		}
		
		ContactItemHolder holder = null;
		if (view.getTag() == null) {
			holder = new ContactItemHolder(mContext);
			holder.initUi(view);
			holder.setupView(new Bundle());
			view.setTag(holder);
		}
		
		holder = (ContactItemHolder) view.getTag();
		
		LinearLayout header = (LinearLayout) view.findViewById(R.id.sort_key_layout);
		Contact contact = mContactsList.get(position);
		char firstChar = contact.login.toUpperCase().charAt(0);
		if (position == 0) {
			setSection(header, contact.login);
		} else {
			String preLabel = mContactsList.get(position - 1).login;
			char preFirstChar = preLabel.toUpperCase().charAt(0);
			if (firstChar != preFirstChar) {
				setSection(header, contact.login);
			} else {
				header.setVisibility(View.GONE);
			}
		}
		
		holder.update(contact);
		holder.setSelected(selectedMap.containsKey(contact.id));
		holder.setItemCheckedListener(mSelectedListener);
		
		return view;
	}
	
	private void setSection(LinearLayout header, String label) {
		TextView text = new TextView(mContext);
		header.setBackgroundColor(0xffaabbcc);
		text.setTextColor(Color.WHITE);
		text.setText(label.substring(0, 1).toUpperCase());
		text.setTextSize(20);
		text.setPadding(5, 0, 0, 0);
		text.setGravity(Gravity.CENTER_VERTICAL);
		header.addView(text);
	}
	
	
	public int getPositionForSection(int section) {
		if (section == 35) {
			return 0;
		}
		for (int i = 0; i < mContactsList.size(); i++) {
			Contact contact = mContactsList.get(i);
			String name = contact.name;
			char firstChar = name.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}
	
	public int getSectionForPosition(int arg0) {
		return 0;
	}
	
	public List<Contact> getSelectedList () {
		List<Contact> selectedList = new ArrayList<Contact>();
		selectedList.addAll(selectedMap.values());
		return selectedList;
	}
	
	
	public Object[] getSections() {
		return null;
	}
}