package com.egeio.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.egeio.common.ConstValues;
import com.egeio.common.mo.Contact;
import com.egeio.common.mo.Item;
import com.egeio.filebrowser.FileUtil;
import com.egeio.framework.ActionManager;
import com.egeio.framework.AppDebug;
import com.egeio.framework.BaseActivity;
import com.egeio.framework.BaseFragment;
import com.egeio.framework.ActionManager.OnDoingTaskListener;
import com.egeio.ui.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.widget.Toast;

public class Utils {
	
    public static final int COMPLEX_UNIT_PX = 0;
    public static final int COMPLEX_UNIT_DIP = 1;
    public static final int COMPLEX_UNIT_SP = 2;
    public static final int COMPLEX_UNIT_PT = 3;
    public static final int COMPLEX_UNIT_IN = 4;
    public static final int COMPLEX_UNIT_MM = 5;
    
    public static Uri Cache_path = null;

	public static String formatDate(String sDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
		Date date;
		
		try {
			date = sdf.parse(sDate);

			SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");

			return output.format(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String formatDate (Date date) {
		SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
		return output.format(date);
	}

	public static String formatTime(String sDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");
		Date date;
		try {
			date = format.parse(sDate);

			SimpleDateFormat output = new SimpleDateFormat("HH:mm:ss");

			return output.format(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String formatSize (Context context, String size) {
		return size != null && "".equals(size) == false ? formatSize(context, Long.parseLong(size)) : "0";
	}
	
	public static String formatSize (Context context, long size) {
		String strSize = Formatter.formatFileSize(context, size);
		
		if (strSize == null || "".equals(strSize)) {
			strSize = "0M";
		}
		return strSize;
	}
	
	public static String getFileName (String url) {
		String tmp = url.substring(url.lastIndexOf("/")+1, url.length());
		tmp = tmp.substring(0, tmp.lastIndexOf("?"));
		return tmp;
	}
	
	public static void showToast (Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	public static Item[] convertArray(List<Item> list) {
		int size = list.size();
		Item[] items = new Item[size];
		for (int i=0; i<size; i++) {
			items[i] = list.get(i);
		}
		return items;
	}
	
	public static Map<Long, Contact> convertMap(Contact[] contacts) {
		
		Map<Long, Contact> mapContacts = new HashMap<Long, Contact>();
		
		for (int i=0; i<contacts.length; i++) {
			mapContacts.put(contacts[i].id, contacts[i]);
		}
		
		return mapContacts;
	}
	
	public static String getFileCachePath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		} else {
			return "/myvudroid";
		}
		return sdDir.toString() + "/myvudroid";
	}
	
	public static String getMediaPath () {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		} else {
			return "/media";
		}
		return sdDir.toString() + "/media";
	}
	
	/**
	 * 启动系统相机
	 * 
	 * @param path 相机照相完成后保存的路径
	 * @author JinChao
	 */
	public static void camera(BaseActivity activity, int requestCode) {
		ContentValues values = new ContentValues();
		Cache_path = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Cache_path);
		activity.startActivityForResult(intentCamera, requestCode);
	}
	
	public static void camera(BaseFragment fragment, int requestCode) {
		ContentValues values = new ContentValues();
		Cache_path = fragment.getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Cache_path);
		fragment.startActivityForResult(intentCamera, requestCode);
	}
	
	public static void uploadFile (final BaseActivity context, String path, OnDoingTaskListener listener) {
		if (FileUtil.isUploadable(path)) {
			Bundle bundle = new Bundle();
			bundle.putString(ConstValues.PATH, path);
			bundle.putLong(ConstValues.FOLDER_ID, context.getFolderID());
			context.showLoading();
			ActionManager.getInstance().startAction(context, ActionManager.ACTION_UPLOAD, bundle, listener);
		} else {
			Utils.showToast(context, context.getString(R.string.invalidfile_cannotupload));
		}
	}
	
	public static void uploadUserPic (final BaseActivity context, String path, OnDoingTaskListener listener) {
		if (FileUtil.isUploadable(path)) {
			Bundle bundle = new Bundle();
			bundle.putString(ConstValues.PATH, path);
			bundle.putLong(ConstValues.FOLDER_ID, context.getFolderID());
			context.showLoading();
			ActionManager.getInstance().startAction(context, ActionManager.ACTION_UPLOAD_PIC, bundle, listener);
		} else {
			Utils.showToast(context, context.getString(R.string.invalidfile_cannotupload));
		}
	}
}
