package org.vudroid.core;

import java.io.File;

import org.vudroid.core.utils.PathFromUri;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class BaseViewer extends Fragment{
	
	public static String FIELD_NAME = "FILENAME";
	public static String FIELD_ID = "FILEID";
	
	public static String FILENAME = null;
	public static long FILEID = -1;
	
	public static Uri mFileUri = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getArguments();
		
		if (bundle != null) {
			initParameters (bundle);
		}
		
	}
	
	public void initParameters (Bundle bundle) {
		String url = bundle.getString(FIELD_NAME);
		FILEID = bundle.getLong(FIELD_ID);
		FILENAME = url.substring(url.lastIndexOf("/") + 1);
		
		File file = new File(url);
		mFileUri = Uri.fromFile(file);
	}

}
