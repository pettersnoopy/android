package com.egeio.common.view;

import java.util.ArrayList;
import java.util.List;
import com.egeio.common.mo.Contact;
import com.egeio.common.mo.DataTypes.ContactsItemBundle;
import com.egeio.framework.BaseActivity;
import com.egeio.ui.R;
import com.egeio.ui.R.color;
import com.egeio.ui.dialog.ContactsSelectedDialog;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RequestReview extends LinearLayout implements ContactsSelectedDialog.OnSelectedDoneListener{
	
	private static final int ADDMEMBER_ID = -1;
	private static final int REMOVEMEMBER_ID = -2;
	
	private Context mContext;
	
	private GridView mReviewMemberLayout;
	
	private SampleMemberAdapter mAdapter;
	
	private List<Contact> mReviewMember = new ArrayList<Contact>();
	
	private ContactsSelectedDialog mSelectedContactDialog = null;
	
	private FragmentManager fm  = null;

	public RequestReview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init(context);
	}

	public RequestReview(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}

	public RequestReview(Context context) {
		super(context);
		
		init(context);
	}
	
	private void init (Context context) {
		mContext = context;
		
		fm  = ((BaseActivity)mContext).getSupportFragmentManager();
		
		setOrientation(LinearLayout.VERTICAL);
		
		Resources res = mContext.getResources();
		
		LinearLayout title = new LinearLayout(mContext);
		title.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams titleparamsLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		titleparamsLayout.gravity = Gravity.CENTER_VERTICAL;
		titleparamsLayout.leftMargin = (int) res.getDimension(R.dimen.common_10dp);
		titleparamsLayout.rightMargin = (int) res.getDimension(R.dimen.common_10dp);
		
		addView(title, titleparamsLayout);
		
		LinearLayout.LayoutParams titleparamsIcon = new LinearLayout.LayoutParams((int) res.getDimension(R.dimen.titlebar_icon_width), (int) res.getDimension(R.dimen.titlebar_icon_height));
		titleparamsIcon.gravity = Gravity.CENTER_VERTICAL;
		titleparamsIcon.leftMargin = (int) res.getDimension(R.dimen.common_10dp);
		ImageView titleIcon = new ImageView(mContext);
		titleIcon.setImageResource(R.drawable.review_title_icon);
		title.addView(titleIcon, titleparamsIcon);
		
		LinearLayout.LayoutParams titleparamstv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		titleparamstv.gravity = Gravity.CENTER_VERTICAL;
		titleparamstv.leftMargin = (int) res.getDimension(R.dimen.common_10dp);
		TextView titletv = new TextView(mContext);
		titletv.setTextAppearance(mContext, R.style.Text_Body_font_large);
		titletv.setText(res.getString(R.string.review_member));
		title.addView(titletv, titleparamstv);
		
		
		LinearLayout.LayoutParams titleparamsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		titleparamsText.gravity = Gravity.CENTER_VERTICAL;
		titleparamsText.leftMargin = (int) res.getDimension(R.dimen.common_10dp);
		titleparamsText.rightMargin = (int) res.getDimension(R.dimen.common_10dp);
		TextView requestName = new TextView(mContext);
		requestName.setTextAppearance(mContext, R.style.Text_Body_font_large);
		
		addView(requestName, titleparamsText);
		
		mReviewMemberLayout = new GridView(mContext);
		mReviewMemberLayout.setBackgroundResource(R.drawable.frame_bg_blue);
		mReviewMemberLayout.setNumColumns(4);
		mReviewMemberLayout.setVerticalSpacing((int)mContext.getResources().getDimension(R.dimen.review_gridview_vir_span));
		mReviewMemberLayout.setHorizontalSpacing((int)mContext.getResources().getDimension(R.dimen.review_gridview_hor_span));
		
		LinearLayout.LayoutParams ReviewMemberparamsLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		ReviewMemberparamsLayout.gravity = Gravity.CENTER_VERTICAL;
		ReviewMemberparamsLayout.leftMargin = (int) res.getDimension(R.dimen.common_10dp);
		ReviewMemberparamsLayout.rightMargin = (int) res.getDimension(R.dimen.common_10dp);
		
		addView(mReviewMemberLayout, ReviewMemberparamsLayout);
		
		mReviewMember.add(new Contact(ADDMEMBER_ID, "add member"));
		mReviewMember.add(new Contact(REMOVEMEMBER_ID, "remove member"));
		
		mReviewMemberLayout.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (ADDMEMBER_ID == id) {
					// addMember
					showContactDialog();
				} else if (REMOVEMEMBER_ID == id) {
					// removeMember
					showContactDialog();
				} else {
					// member info
				}
			}
		});
		
		mAdapter = new SampleMemberAdapter();
		mReviewMemberLayout.setAdapter(mAdapter);
	}
	
	@SuppressWarnings("unused")
	private class SampleMemberAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mReviewMember.size();
		}

		@Override
		public Object getItem(int position) {
			return mReviewMember.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mReviewMember.get(position).id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Contact contact = mReviewMember.get(position);
			
			int item_width = (int) mContext.getResources().getDimension(R.dimen.review_addmember_icon_width);
			int item_height = (int) mContext.getResources().getDimension(R.dimen.review_addmember_icon_height);
			
			FrameLayout layout = new FrameLayout(mContext);
			AbsListView.LayoutParams layoutparams = new AbsListView.LayoutParams(item_width, item_height);
			layout.setBackgroundColor(color.blue);
			layout.setLayoutParams(layoutparams);
			
			if (ADDMEMBER_ID == contact.id) {
				ImageView item = new ImageView(mContext);
				item.setImageResource(R.drawable.edit_add);
				FrameLayout.LayoutParams itemParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
				layout.addView(item, itemParams);
			} else if (REMOVEMEMBER_ID == contact.id) {
				ImageView item = new ImageView(mContext);
				item.setImageResource(R.drawable.symbol_remove);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
				layout.addView(item, params);
			} else {
				
				ImageView memphoto = new ImageView(mContext);
				memphoto.setImageResource(R.drawable.def_contacts_icon);
				FrameLayout.LayoutParams memPhotoParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
				memPhotoParams.gravity = Gravity.CENTER_HORIZONTAL;
				layout.addView(memphoto, memPhotoParams);
				TextView memName = new TextView(mContext);
				memName.setBackgroundColor(color.grey);
				FrameLayout.LayoutParams memTNameParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				memTNameParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
				memName.setText(contact.name);
				layout.addView(memName, memTNameParams);
				memName.setGravity(Gravity.CENTER_HORIZONTAL);
			}
			
			convertView = layout;
			return convertView;
		}
	}
	
	private void showContactDialog () {
		if (mSelectedContactDialog == null) {
			mSelectedContactDialog = new ContactsSelectedDialog(mContext);
			mSelectedContactDialog.setTitleText(mContext.getResources().getString(R.string.title_select_contacts));
			mSelectedContactDialog.setCancelable(false);
			mSelectedContactDialog.setSelectedDoneListener(this);
		}
		
		mSelectedContactDialog.show(fm);
	}

	@Override
	public void onSelectedDone(ContactsItemBundle selected) {
		mReviewMember.clear(); 
		if (selected != null && selected.contacts_count > 0) {
			for (int i=0; i<selected.contacts_count; i++) {
				mReviewMember.add(selected.contacts[i]);
			}
		}
		
		mReviewMember.add(new Contact(ADDMEMBER_ID, "add member"));
		mReviewMember.add(new Contact(REMOVEMEMBER_ID, "remove member"));
		
		mAdapter.notifyDataSetChanged();
		if (mSelectedContactDialog != null) {
			mSelectedContactDialog.dismiss();
		}
	}

}
