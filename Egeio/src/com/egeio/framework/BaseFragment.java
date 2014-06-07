package com.egeio.framework;

import com.egeio.common.ConstValues;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment implements ActionManager.ActionListener{
	
	private String TAG = "BaseFragment";
	
	private int mLoaderCount = 0;
	private ConnectivityManager connectivityManager;
	
	protected BaseActivity mActivity;
	protected int mLayout;
	protected BaseFrgmentLoader myTask;
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(ConstValues.LAYOUT_ID, mLayout);
		outState.putInt(ConstValues.LOADER_ID, mLoaderCount);
	}
	
	public void onLoadInstanceState(Bundle bundle) {
		if (bundle!=null) {
			mLayout = bundle.getInt(ConstValues.LAYOUT_ID, 0);
			mLoaderCount = bundle.getInt(ConstValues.LOADER_ID, 0);
		}
	}
	
	protected void initData() {
		if (myTask == null) {
			myTask = new BaseFrgmentLoader();
		}
		myTask.start(getArguments());
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (BaseActivity)activity;
		TAG = getClass().getName();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onLoadInstanceState(savedInstanceState);
	}

	public int requestLoaderId() {
		return mLoaderCount++;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mActivity.registerReceiver(netWorkStateReceiver, mFilter);
	}

    @Override
	public void onPause() {
		super.onPause();
		mActivity.unregisterReceiver(netWorkStateReceiver);
	}
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = inflater.inflate(mLayout, null);
    	return view;
	}
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		AppDebug.d(TAG, " Fragment created , class is " + TAG + "layout is " + mLayout);
		
		if (savedInstanceState!=null) {
			
		}
	}
	
	/**
	 * load initial data in background
	 * @param bundle
	 */
	protected Object loadNextPage(Bundle bundle) {
		return null;
	}
	
	/**
	 * update values in ui thread
	 * @param result
	 */
	protected void displayNextPage(Object result) {
		
	}
	
    protected void netConnected(String netName) {
    	
    	AppDebug.d(TAG, " network connected.");
    	
    }
    
    protected void netDisconnected() {
    	AppDebug.d(TAG, " network disconnect.");
    }

	private BroadcastReceiver netWorkStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager)mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if(info != null && info.isAvailable()) {
                    String name = info.getTypeName();
                    netConnected(name);
                } else {
                    netDisconnected();
                }
            }
        }
    };
    
    protected class BaseFrgmentLoader extends BackgroundTask {

		public BaseFrgmentLoader() {
			super(BaseFragment.this, 1);
		}

		@Override
		protected Object doInBackground(Bundle bundle) {
			return loadNextPage(bundle);
		}

		@Override
		protected void onPostExecute(Object result) {
			displayNextPage(result);
		}
    	
    }

	@Override
	public void onStartAction(int code, Bundle bundle) {
		if (bundle == null) {
			bundle = new Bundle();
		}
	}

}
