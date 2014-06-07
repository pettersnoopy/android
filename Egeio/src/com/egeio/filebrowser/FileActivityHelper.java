package com.egeio.filebrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.egeio.framework.BaseActivity;
import com.egeio.ui.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FileActivityHelper {

	public static ArrayList<FileInfo> getFiles(Context activity, String path) {
		File f = new File(path);
		File[] files = f.listFiles();
		if (files == null) {
			Toast.makeText(activity, String.format(activity.getString(R.string.file_cannotopen), path), Toast.LENGTH_SHORT).show();
			return null;
		}

		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			FileInfo fileInfo = new FileInfo();
			fileInfo.Name = file.getName();
			fileInfo.IsDirectory = file.isDirectory();
			fileInfo.Path = file.getPath();
			fileInfo.Size = file.length();
			fileList.add(fileInfo);
		}

		Collections.sort(fileList, new FileComparator());

		return fileList;
	}

	public static void createDir(final BaseActivity activity, final String path, final Handler hander) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		View layout = LayoutInflater.from(activity).inflate(R.layout.createnewfolder, null);
		final EditText text = (EditText) layout.findViewById(R.id.foldername);
		builder.setView(layout);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				String newName = text.getText().toString().trim();
				if (newName.length() == 0) {
					Toast.makeText(activity, R.string.file_namecannotempty, Toast.LENGTH_SHORT).show();
					return;
				}
				String fullFileName = FileUtil.combinPath(path, newName);
				File newFile = new File(fullFileName);
				if (newFile.exists()) {
					Toast.makeText(activity, R.string.file_exists, Toast.LENGTH_SHORT).show();
				} else {
					try {
						if (newFile.mkdir()) {
							hander.sendEmptyMessage(0);
						} else {
							Toast.makeText(activity, R.string.file_create_fail, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception ex) {
						Toast.makeText(activity, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
					}
				}
			}
		}).setNegativeButton(R.string.cancel, null);
		AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.mainmenu_createdir);
		alertDialog.show();
	}

	public static void renameFile(final BaseActivity activity, final File f, final Handler hander) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		View layout = LayoutInflater.from(activity).inflate(R.layout.file_rename, null);
		final EditText text = (EditText) layout.findViewById(R.id.file_name);
		text.setText(f.getName());
		builder.setView(layout);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				String path = f.getParentFile().getPath();
				String newName = text.getText().toString().trim();
				if (newName.equalsIgnoreCase(f.getName())) {
					return;
				}
				if (newName.length() == 0) {
					Toast.makeText(activity, R.string.file_namecannotempty, Toast.LENGTH_SHORT).show();
					return;
				}
				String fullFileName = FileUtil.combinPath(path, newName);

				File newFile = new File(fullFileName);
				if (newFile.exists()) {
					Toast.makeText(activity, R.string.file_exists, Toast.LENGTH_SHORT).show();
				} else {
					try {
						if (f.renameTo(newFile)) {
							hander.sendEmptyMessage(0);
						} else {
							Toast.makeText(activity, R.string.file_rename_fail, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception ex) {
						Toast.makeText(activity, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
					}
				}
			}
		}).setNegativeButton(R.string.cancel, null);
		AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.file_rename);
		alertDialog.show();
	}

	public static void viewFileInfo(BaseActivity activity, File f) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		View layout = LayoutInflater.from(activity).inflate(R.layout.file_info, null);
		FileInfo info = FileUtil.getFileInfo(f);

		((TextView) layout.findViewById(R.id.file_name)).setText(f.getName());
		((TextView) layout.findViewById(R.id.file_lastmodified)).setText(new Date(f.lastModified()).toLocaleString());
		((TextView) layout.findViewById(R.id.file_size)).setText(FileUtil.formetFileSize(info.Size));
		if (f.isDirectory()) {
			((TextView) layout.findViewById(R.id.file_contents)).setText("Folder " + info.FolderCount + ", File " + info.FileCount);
		}

		builder.setView(layout);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {
				dialoginterface.cancel();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(R.string.file_info);
		alertDialog.show();
	}
}