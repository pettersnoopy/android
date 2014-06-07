package com.egeio.common.holder;

import java.util.Arrays;
import java.util.List;

import com.egeio.adapter.ContatsListAdapter;
import com.egeio.common.mo.Contact;
import com.egeio.common.mo.DataTypes;
import com.egeio.common.view.SideBar;
import com.egeio.ui.R;
import com.egeio.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ContactListHolder extends BaseViewHolder{
	
	public ListView mList;

	public SideBar mIindexBar;
	
	public ContatsListAdapter mAdapter;
	
	public View mLoading;

	public ContactListHolder(Context context) {
		super(context);
	}

	@Override
	public void initUi(View view) {
		mList = (ListView) view.findViewById(R.id.listView);
		mIindexBar = (SideBar) view.findViewById(R.id.sideBar);
		
		mLoading = view.findViewById(R.id.loading);
	}
	
	public void update (DataTypes.ContactsItemBundle contactsBundle, int itemLayout) {
		
		if (contactsBundle != null && contactsBundle.contacts != null && contactsBundle.contacts.length>0) {
			
			mAdapter = new ContatsListAdapter(mContext, itemLayout, Arrays.asList(contactsBundle.contacts));
			
			mList.setAdapter(mAdapter);
			
			mIindexBar.setListView(mList);
			
		}
		
		mLoading.setVisibility(View.GONE);
	}
	
	public void update (DataTypes.ContactsItemBundle contactsBundle, DataTypes.ContactsItemBundle selectedContacts, int itemLayout) {
		
		if (contactsBundle != null && contactsBundle.contacts != null && contactsBundle.contacts.length>0) {
			
			if (selectedContacts != null && selectedContacts.contacts_count > 0 && selectedContacts.contacts != null) {
				
				mAdapter = new ContatsListAdapter(mContext, itemLayout, Arrays.asList(contactsBundle.contacts), Utils.convertMap(selectedContacts.contacts));
				
			} else {
				mAdapter = new ContatsListAdapter(mContext, itemLayout, Arrays.asList(contactsBundle.contacts));
			}
			
			mList.setAdapter(mAdapter);
			
			mIindexBar.setListView(mList);
		}
		
		mLoading.setVisibility(View.GONE);
	}

	@Override
	public void setupView(Bundle bundle) {
		
	}
	
	public DataTypes.ContactsItemBundle getSelectedList() {

		if (mAdapter != null) {
			List<Contact> selectedList = mAdapter.getSelectedList();
			
			DataTypes.ContactsItemBundle contactsBundle = new DataTypes.ContactsItemBundle();
			contactsBundle.contacts_count = selectedList.size();
			contactsBundle.contacts = new Contact[contactsBundle.contacts_count];
			
			for (int i=0; i<contactsBundle.contacts_count; i++) {
				contactsBundle.contacts[i] = selectedList.get(i);
			}
			return contactsBundle;
		}

		return null;
	}
	
	public void showLoading () {
		mLoading.setVisibility(View.VISIBLE);
	}
	
	public void setOnListItemSelectedListener (OnItemClickListener listener) {
		if (mList != null) {
			mList.setOnItemClickListener(listener);
		}
	}

}
