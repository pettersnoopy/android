package com.egeio.common.holder;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public abstract class BaseViewHolder {
	
	protected Context mContext;
	
	public BaseViewHolder (Context context) {
		mContext = context;
	}
	
	/**
	 *  init view 
	 * @param view
	 */
	public abstract void initUi (View view) ;
	
	/**
	 * init view data
	 * @param bundle 
	 */
	public abstract void setupView (Bundle bundle) ;
}
