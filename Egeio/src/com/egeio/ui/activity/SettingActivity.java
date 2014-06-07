package com.egeio.ui.activity;

import com.egeio.common.ConstValues;
import com.egeio.common.holder.BaseViewHolder;
import com.egeio.common.mo.Contact;
import com.egeio.common.view.NetworkedCacheableImageView;
import com.egeio.framework.BaseActivity;
import com.egeio.ui.R;
import com.egeio.ui.fragment.UserInfoFragment;
import com.egeio.utils.StoreUtils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class SettingActivity extends BaseActivity{
	
	public static enum SettingPage { main, userinfo, transport }
	
	class SettingHolder extends BaseViewHolder {
		
		public SettingHolder(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		private NetworkedCacheableImageView img_contact;
		private TextView name, phone, tran_back_setting, info_back_setting;
		private View lin_transport, lin_setting, lin_userinfo;
		
		@Override
		public void initUi(View view) {

			img_contact = (NetworkedCacheableImageView) view.findViewById(R.id.img_contact);
			name = (TextView) view.findViewById(R.id.name);
			phone = (TextView) view.findViewById(R.id.phone);
			
			tran_back_setting = (TextView) view.findViewById(R.id.tran_back_setting);
			tran_back_setting.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					gotoPage(SettingPage.main);
				}
			});
			
			info_back_setting = (TextView) view.findViewById(R.id.info_back_setting);
			info_back_setting.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					gotoPage(SettingPage.main);
				}
			});
			
			lin_transport = view.findViewById(R.id.lin_transport);
			lin_setting = view.findViewById(R.id.lin_setting);
			lin_userinfo = view.findViewById(R.id.lin_userinfo);
			
			lin_userinfo.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick(View v) {
					gotoPage(SettingPage.userinfo);
				}
			});
		}
		
		@Override
		public void setupView(Bundle bundle) {}
		
		public void updateValue (Contact contact) {
			if (img_contact != null) {
				mHolder.img_contact.loadImage(contact.profile_pic_key, "" + contact.id, R.drawable.def_contacts_icon, false, null);
			}
			if (name != null) {
				name.setText(contact.name);
			}
			if (phone != null) {
				phone.setText(contact.phone);
			}
			
		}
	}
	
	private Contact mContact;
	
	private SettingHolder mHolder;
	
	private UserInfoFragment mUserInfo;
	
	private ViewFlipper mFlipper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContact = StoreUtils.getContact(this);
		if (mContact != null) {
			initUI();
			if (mUserInfo == null) {
				mUserInfo = new UserInfoFragment();
				Bundle bundle = new Bundle();
				bundle.putSerializable(ConstValues.CONTACT, mContact);
				mUserInfo.setArguments(bundle);
			}
			FragmentTransaction  transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.userInfo, mUserInfo);
			transaction.commit();
			mFlipper.setDisplayedChild(SettingPage.main.ordinal());
		} else {
			// need to login
		}
	}
	
	public void initUI () {
		View view = LayoutInflater.from(this).inflate(R.layout.mainsetting, null);
		setContentView(view);
		mFlipper = (ViewFlipper) view.findViewById(R.id.flipper);
		mHolder = new SettingHolder(this);
		mHolder.initUi(view);
		mHolder.updateValue(mContact);
	}
	
	public void gotoPage (SettingPage page) {
		switch (page) {
		case main:
			mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
			mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
			mFlipper.setDisplayedChild(page.ordinal());
			break;
			
		case userinfo:
			mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
			mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
			mFlipper.setDisplayedChild(page.ordinal());
			break;
			
		case transport:
			break;
		}
	}

	@Override
	public String getActivityTag() {
		return SettingActivity.class.toString();
	}
}
