package com.egeio.ui.activity;

import com.egeio.ui.R;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	private final String TAG = "MainTab";
	private Dialog sureQuite;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setTabs();

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		int index = 0;
		if (bundle != null) {
			index = bundle.getInt("tab_index", 0);
		}

		getTabHost().setCurrentTab(index);
	}

	private void setTabs() {
		addTab(getString(R.string.File), R.drawable.icon_home_file, FileBrowserActivity.class);
		addTab(getString(R.string.Message), R.drawable.icon_home_message, MessageActivity.class);
		addTab(getString(R.string.Contacts), R.drawable.icon_home_contacts, ContactsActivity.class);
		addTab(getString(R.string.Setting), R.drawable.icon_home_setting, SettingActivity.class);
	}

	private void addTab(String labelId, int drawableId, Class<?> c) {
		TabHost tabHost = getTabHost();
		Intent intent = new Intent(this, c);
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);

		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);

		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		tabHost.addTab(spec);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			onBackPressed();
			return false;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onBackPressed() {
		if (sureQuite == null) {
			sureQuite = new AlertDialog.Builder(this).setMessage(R.string.notify_exit_application).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
					sureQuite.dismiss();
				}
			}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			}).show();
		} else {
			sureQuite.show();
		}
	}
}
