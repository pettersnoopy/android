package com.egeio;

import java.io.File;

import uk.co.senab.bitmapcache.BitmapLruCache;
import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

public class EgeioApplication extends Application {
	
	public static final String TAG = "EgeioApplication";

	private BitmapLruCache mCache;

	@Override
	public void onCreate() {
		super.onCreate();

		File cacheLocation;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			cacheLocation = new File(Environment.getExternalStorageDirectory() + "/Egeio-BitmapCache");
		} else {
			cacheLocation = new File(getFilesDir() + "/Egeio-BitmapCache");
		}
		cacheLocation.mkdirs();

		BitmapLruCache.Builder builder = new BitmapLruCache.Builder(this);
		builder.setMemoryCacheEnabled(true).setMemoryCacheMaxSizeUsingHeapSize();
		builder.setDiskCacheEnabled(true).setDiskCacheLocation(cacheLocation);

		mCache = builder.build();

		Log.d(TAG, " ================ device ID " + getLocalMacAddress(this));
	}

	public BitmapLruCache getBitmapCache() {
		return mCache;
	}

	public static EgeioApplication getApplication(Context context) {
		return (EgeioApplication) context.getApplicationContext();
	}

	/**
	 * 获取mac地址
	 * 
	 * @param context
	 * @return
	 */
	public String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

}
