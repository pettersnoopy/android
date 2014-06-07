package com.egeio.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.egeio.common.ConstValues;
import com.egeio.common.holder.ContactListHolder;
import com.egeio.common.mo.DataTypes;
import com.egeio.framework.BackgroundTask;
import com.egeio.framework.MessageBox;
import com.egeio.network.NetworkManager;
import com.egeio.ui.R;

public class ContactsSelectedDialog extends MessageBox{

	private Context mContext;
	
	private LayoutInflater inflater;
	
	private DataTypes.ContactsItemBundle mContactsBundle;
	
	private DataTypes.ContactsItemBundle mSelectedContacts;
	
	private ContactListHolder mHolder;
	
	private ContactsListLoader mLoader;
	
	private Button mCommit;
	
	private OnSelectedDoneListener mSelectedDone;
	
	public void setSelectedDoneListener (OnSelectedDoneListener selectedDone) {
		mSelectedDone = selectedDone;
	}
	
	public static interface OnSelectedDoneListener {
		public void onSelectedDone (DataTypes.ContactsItemBundle selected);
	}
	
	public ContactsSelectedDialog (Context context) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		outState.putSerializable(ConstValues.CONTACT_LIST, mContactsBundle);
		if (mHolder != null) {
			mSelectedContacts = mHolder.getSelectedList();
		}
		outState.putSerializable(ConstValues.SELECTED_CONTACT_LIST, mSelectedContacts);
		
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dlg = new Dialog(getActivity(), R.style.default_dialog_style);
		
		if (savedInstanceState != null) {
			mContactsBundle = (DataTypes.ContactsItemBundle) savedInstanceState.getSerializable(ConstValues.CONTACT_LIST);
			mSelectedContacts = (DataTypes.ContactsItemBundle)savedInstanceState.getSerializable(ConstValues.SELECTED_CONTACT_LIST);
		}
		
		View view = inflater.inflate(R.layout.dialog_contacts_main, null);
		dlg.setContentView(view);
		
		mCommit = (Button) view.findViewById(R.id.commit);
		
		mCommit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectedDone != null) {
					mSelectedDone.onSelectedDone(mHolder.getSelectedList());
				}
			}
		});
				
		mHolder = new ContactListHolder(mContext);
		mHolder.initUi(view);
		
		return dlg;
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		
		if (mContactsBundle == null || mContactsBundle.contacts == null || mContactsBundle.contacts.length <= 0) {
			
			if (mLoader == null) {
				mLoader = new ContactsListLoader();
			}
			
			mHolder.showLoading();
			
			mLoader.start(new Bundle());
		} else {
			mHolder.update(mContactsBundle, R.layout.contacts_item_checkable);
		}
	}
	
	private class ContactsListLoader extends BackgroundTask {
		public ContactsListLoader() {
			super(ContactsSelectedDialog.this);
		}

		@Override
		protected Object doInBackground(Bundle bundle) {
			return NetworkManager.getInstance(getActivity()).getContacts();
		}

		@Override
		protected void onPostExecute(Object result) {
			if (result != null && result instanceof DataTypes.ContactsItemBundle) {
				mContactsBundle = (DataTypes.ContactsItemBundle) result;
				
				mHolder.update(mContactsBundle, R.layout.contacts_item_checkable);
			}
		}
	 }

}
