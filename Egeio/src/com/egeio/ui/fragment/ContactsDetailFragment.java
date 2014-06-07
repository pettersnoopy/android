package com.egeio.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.egeio.common.ConstValues;
import com.egeio.common.holder.BaseViewHolder;
import com.egeio.common.mo.Contact;
import com.egeio.common.view.NetworkedCacheableImageView;
import com.egeio.framework.BaseFragment;
import com.egeio.ui.R;

public class ContactsDetailFragment extends BaseFragment{
	
	
	class ContactDetailHolder extends BaseViewHolder{
		
		private NetworkedCacheableImageView img_contact;
		
		private TextView name, group, tel_number, email_addr;

		public ContactDetailHolder(Context context) {
			super(context);
		}

		@Override
		public void initUi(View view) {
			img_contact = (NetworkedCacheableImageView) view.findViewById(R.id.img_contact);
			name = (TextView) view.findViewById(R.id.name);
			group = (TextView) view.findViewById(R.id.group);
			tel_number = (TextView) view.findViewById(R.id.tel_number);
			email_addr = (TextView) view.findViewById(R.id.email_addr);
		}
		
		public void updateValue (Contact contact) {
			if (name != null) {
				name.setText(contact.name);
			}
			if (group != null) {
				// for test
				group.setText("市场部");
			}
			if (tel_number != null) {
				tel_number.setText(contact.phone);
			}
			if (email_addr != null) {
				email_addr.setText(contact.login);
			}
			if (img_contact != null) {
				img_contact.loadImage(contact.profile_pic_url, R.drawable.def_contacts_icon,false, null);
			}
		}

		@Override
		public void setupView(Bundle bundle) { }
		
	}
	
	private ContactDetailHolder mHolder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHolder = new ContactDetailHolder(mActivity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.contacts_detail, null);
		mHolder.initUi(view);
		Bundle bundle = getArguments();
		if (bundle != null && bundle.containsKey(ConstValues.CONTACT)) {
			Contact contact = (Contact)bundle.getSerializable(ConstValues.CONTACT);
			mHolder.updateValue(contact);
		}
		return view;
	}
}
