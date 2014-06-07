package com.egeio.common.holder;

import com.egeio.common.mo.Contact;
import com.egeio.common.view.NetworkedCacheableImageView;
import com.egeio.ui.R;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactItemHolder extends BaseViewHolder{

	public View mSock;
	
	public TextView mSort_key, name, group;
	
	public NetworkedCacheableImageView mImgContact;
	
	public CheckBox mRadioSelected;
	
	public Contact contact;
	
	public ContactItemHolder(Context context) {
		super(context);
	}

	@Override
	public void initUi(View view) {
		mSock = view.findViewById(R.id.sort_key);
		mImgContact = (NetworkedCacheableImageView)view.findViewById(R.id.contact_img);
		name = (TextView)view.findViewById(R.id.name);
		group = (TextView)view.findViewById(R.id.group);
		mRadioSelected = (CheckBox)view.findViewById(R.id.radioSelected);
	}

	@Override
	public void setupView(Bundle bundle) {
		
	}
	
	public void update (Contact contact) {
		
		this.contact = contact;
		
		if (name != null) {
			name.setText(contact.name);
		}
		
		if (group != null) {
			group.setText(contact.login);
		}
		
		if (mRadioSelected != null) {
			mRadioSelected.setChecked(false);
			mRadioSelected.setTag(contact);
		}
		
		if (mImgContact != null) {
			mImgContact.loadImage(contact.profile_pic_key, "" + contact.id, R.drawable.def_contacts_icon,false, null);
		}
	}
	
	public void setItemCheckedListener (OnCheckedChangeListener listener) {
		if (mRadioSelected != null) {
			mRadioSelected.setOnCheckedChangeListener(listener);
		}
	}
	
	public void setSelected (boolean selected) {
		if (mRadioSelected != null) {
			mRadioSelected.setChecked(selected);
		}
	}

}
