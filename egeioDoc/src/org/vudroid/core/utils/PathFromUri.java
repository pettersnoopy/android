package org.vudroid.core.utils;

import java.io.File;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

public class PathFromUri
{
    public static String retrieve(ContentResolver resolver, Uri uri)
    {
        if (uri.getScheme().equals("file"))
        {
            return uri.getPath();
        }
        final Cursor cursor = resolver.query(uri, new String[]{"_data"}, null, null, null);
        if (cursor.moveToFirst())
        {
            return cursor.getString(0);
        }
        throw new RuntimeException("Can't retrieve path from uri: " + uri.toString());
    }
    
    public static String getMyPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		} else {
			// sdDir = Environment.getDataDirectory();
			return "/myvudroid";
		}
		return sdDir.toString() + "/myvudroid";
	}
    
    public static String getFileDir (Uri uri) {
    	String path = uri.getPath();
    	return path.substring(0, path.lastIndexOf("/"));
    }
    
    public static float getScale (Context context) {
    	return context.getResources().getDisplayMetrics().density;
    }
    
    // 支持的所有扩展格式
    public static enum Extension {pdf, djvu, djv};
}
