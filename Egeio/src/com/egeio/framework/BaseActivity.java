package com.egeio.framework;

import com.egeio.common.widget.TitleBar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public abstract class BaseActivity extends FragmentActivity{
	
    private int mLoaderCount = 0;
    private ConnectivityManager connectivityManager;
    
	protected TitleBar mTitleBar;
    
    protected void initTransportBundle (Bundle bundle) {
    	
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		
		if (bundle != null) {
			initTransportBundle (bundle);
		}
	}
	
	public abstract String getActivityTag();
	
	// must overwrite
	public Long getFolderID () {
		return 0L;
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, mFilter);
	}

    @Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(netWorkStateReceiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

	public int requestLoaderId() {
		return mLoaderCount++;
	}
	
	protected void netConnected(String netName) {
		
	}

	protected void netDisconnected() {
		
	}
	
	private BroadcastReceiver netWorkStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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
    
    public void onTaskDone(int code, Bundle resule) {
    	
    }
    
    public void showLoading() {
    	
    }
    
    public void hideLoading () {
    	
    }

}
