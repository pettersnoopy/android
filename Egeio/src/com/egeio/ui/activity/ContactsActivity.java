package com.egeio.ui.activity;

import com.egeio.common.ConstValues;
import com.egeio.common.mo.Contact;
import com.egeio.framework.BaseActivity;
import com.egeio.ui.R;
import com.egeio.ui.fragment.ContactsDetailFragment;
import com.egeio.ui.fragment.ContactsFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class ContactsActivity extends BaseActivity{
	
	private static final int LIST = 0;
	
	private static final int DETAIL = 1;
	
	private ContactsFragment mContacts;
	
	private ContactsDetailFragment mDetail;
	
	private ViewFlipper mFlipper;
	
	private TextView detail_name, back_contact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.maincontact);
		
		mFlipper = (ViewFlipper) findViewById(R.id.flipper);
		detail_name = (TextView) findViewById(R.id.detail_name);
		back_contact = (TextView) findViewById(R.id.back_contact);
		
		initValues();
	}
	
	private void initValues () {
		
		FragmentTransaction tranSaction = getSupportFragmentManager().beginTransaction();
		
		mContacts = new ContactsFragment();
		tranSaction.replace(R.id.contentList, mContacts);
		
		tranSaction.commit();
	}

	@Override
	public String getActivityTag() {
		return ContactsActivity.class.toString();
	}

	public void gotoDetail(Contact contact) {
		mDetail = new ContactsDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(ConstValues.CONTACT, contact);
		mDetail.setArguments(bundle);
		FragmentTransaction tranSaction = getSupportFragmentManager().beginTransaction();
		tranSaction.replace(R.id.contentDetail, mDetail);
		tranSaction.commit();
		
		detail_name.setText(contact.name);
		
		mFlipper.postDelayed(new Runnable() {
			@Override
			public void run() {
				mFlipper.setInAnimation(AnimationUtils.loadAnimation(ContactsActivity.this, R.anim.push_left_in));
				mFlipper.setOutAnimation(AnimationUtils.loadAnimation(ContactsActivity.this, R.anim.push_left_out));
				mFlipper.setDisplayedChild(DETAIL);
			}
		}, 100);
		
		back_contact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFlipper.setInAnimation(AnimationUtils.loadAnimation(ContactsActivity.this, R.anim.push_right_in));
				mFlipper.setOutAnimation(AnimationUtils.loadAnimation(ContactsActivity.this, R.anim.push_right_out));
				mFlipper.setDisplayedChild(LIST);
			}
		});
	}

}
