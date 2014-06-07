package com.egeio.framework;

import android.util.Log;

public class AppDebug {
	
	private static boolean isDebug = true;
	
	public static void d(String tag, String message) {
		if (isDebug) {
			Log.d(tag, message);
		}
	}

}
