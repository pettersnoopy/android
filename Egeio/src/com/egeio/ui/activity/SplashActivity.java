package com.egeio.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.egeio.framework.BaseActivity;
import com.egeio.network.NetworkManager;
import com.egeio.ui.R;
import com.egeio.ui.fragment.LoginFragment;

public class SplashActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		setContentView(R.layout.mainpage);
		
		if (NetworkManager.getInstance(this).isLogined()) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		} else {
			LoginFragment login = new LoginFragment();
			FragmentTransaction tranSaction = getSupportFragmentManager().beginTransaction();
			tranSaction.replace(R.id.content, login);
			tranSaction.commit();
		}
	}

	@Override
	public String getActivityTag() {
		return SplashActivity.class.toString();
	}

}
