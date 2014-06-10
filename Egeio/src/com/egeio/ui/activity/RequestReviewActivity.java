package com.egeio.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.egeio.common.ConstValues;
import com.egeio.common.holder.BaseViewHolder;
import com.egeio.common.mo.DataTypes;
import com.egeio.common.view.RequestReview;
import com.egeio.framework.BaseActivity;
import com.egeio.ui.R;

public class RequestReviewActivity extends BaseActivity{
	
	private RequestReviewHolder mHolder;
	
	private DataTypes.BaseItems items = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.request_review);
		
		if (savedInstanceState != null) {
			items = (DataTypes.BaseItems) savedInstanceState.getSerializable(ConstValues.ITEM_LIST);
		}
		
		Bundle bundle = getIntent().getExtras();
		
		if (items == null && bundle != null) {
			items = (DataTypes.BaseItems) bundle.getSerializable(ConstValues.ITEM_LIST);
		}
		
		mHolder = new RequestReviewHolder(this);
		
		mHolder.initUi(null);
		
		if (items != null) {
			mHolder.update(items);
		}
	}
	
	class RequestReviewHolder extends BaseViewHolder{
		
		public TextView filelists, endpoint;
		
		public EditText RequestRename, comment;
		
		public RequestReview RequestMember;

		public RequestReviewHolder(Context context) {
			super(context);
		}

		@Override
		public void initUi(View view) {
			
			filelists = (TextView) findViewById(R.id.file_list);
			
			endpoint = (TextView) findViewById(R.id.endpoint);
			
			RequestRename = (EditText) findViewById(R.id.request_rename);
			
			comment = (EditText) findViewById(R.id.comment);
		}

		@Override
		public void setupView(Bundle bundle) {
		}
		
		public void update (DataTypes.BaseItems items) {
			StringBuilder builder = new StringBuilder();
			
			for (int i=0; i<items.children_count; i++) {
				builder.append(items.items[i].getName());
				if (i != items.children_count) {
					builder.append("\n");
				}
			}
			filelists.setText(builder.toString());
		}
	}

	@Override
	public String getActivityTag() {
		return RequestReviewActivity.class.toString();
	}

}
