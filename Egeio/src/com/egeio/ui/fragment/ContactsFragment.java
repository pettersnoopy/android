package com.egeio.ui.fragment;

import com.egeio.common.ConstValues;
import com.egeio.common.holder.ContactItemHolder;
import com.egeio.common.holder.ContactListHolder;
import com.egeio.common.mo.DataTypes;
import com.egeio.framework.BackgroundTask;
import com.egeio.framework.BaseFragment;
import com.egeio.network.NetworkManager;
import com.egeio.ui.R;
import com.egeio.ui.activity.ContactsActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactsFragment extends BaseFragment{
	
	private DataTypes.ContactsItemBundle mContactsBundle;
	
	private ContactsListLoader mLoader;
	
	private ContactListHolder mHolder;
	
	private OnItemClickListener mItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ContactItemHolder holder = (ContactItemHolder) view.getTag();
			if (mActivity!= null && mActivity instanceof ContactsActivity) {
				((ContactsActivity)mActivity).gotoDetail(holder.contact);
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mHolder = new ContactListHolder(getActivity());
		
		if (savedInstanceState != null) {
			mContactsBundle = (DataTypes.ContactsItemBundle) savedInstanceState.getSerializable(ConstValues.CONTACT_LIST);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.contactslist, null);
		
		mHolder.initUi(view);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (mContactsBundle == null || mContactsBundle.contacts == null || mContactsBundle.contacts.length <= 0) {
			
			if (mLoader == null) {
				mLoader = new ContactsListLoader();
			}
			
			mHolder.showLoading();
			
			mLoader.start(new Bundle());
		} else {
			mHolder.update(mContactsBundle, R.layout.contact_item);
		}

	}
	
	 @Override
	public void onSaveInstanceState(Bundle outState) {
		 
		 outState.putSerializable(ConstValues.CONTACT_LIST, mContactsBundle);
		 
		super.onSaveInstanceState(outState);
	} 

	protected class ContactsListLoader extends BackgroundTask {

		public ContactsListLoader() {
			super(ContactsFragment.this);
		}

		@Override
		protected Object doInBackground(Bundle bundle) {
			return NetworkManager.getInstance(getActivity()).getContacts();
		}

		@Override
		protected void onPostExecute(Object result) {
			mContactsBundle = (DataTypes.ContactsItemBundle) result;
			mHolder.update(mContactsBundle, R.layout.contact_item);
			mHolder.setOnListItemSelectedListener(mItemClick);
		}
		 
	 }

}
