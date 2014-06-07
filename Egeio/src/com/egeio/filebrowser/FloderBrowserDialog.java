package com.egeio.filebrowser;

import java.util.ArrayList;
import java.util.List;

import com.egeio.framework.BaseActivity;
import com.egeio.ui.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class FloderBrowserDialog extends DialogFragment{
	
	private ListView mListview;
	
	private FileAdapter mAdapter;
	
	private List<FileInfo> mList = new ArrayList<FileInfo>();
	
	private String mCurrentPath = FileUtil.getSDPath();
	
	private TextView mFilepath;

	private OnItemSelectedListener mOnItemSelectedListener = null;
	
	public static interface OnItemSelectedListener {
		public void onSelected (String path);
	}
	
	public void setOnItemSelectedListener (OnItemSelectedListener listener) {
		mOnItemSelectedListener = listener;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_fileselect, null);
		mListview = (ListView) view.findViewById(R.id.listView);
		mListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FileInfo f = mList.get(position);
				if (f.IsDirectory) {
					viewFiles(f.Path);
				} else {
					// SELECTED
					if (mOnItemSelectedListener != null) {
						mOnItemSelectedListener.onSelected(f.Path);
					}
				}
			}
		});
		
		mFilepath = (TextView) view.findViewById(R.id.filepath);
		BaseActivity context = (BaseActivity)getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		mAdapter = new FileAdapter(context, mList);
		mListview.setAdapter(mAdapter);
		builder.setView(view);
		builder.setTitle(getString(R.string.filelist));
		builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		});
		viewFiles (mCurrentPath);
		return builder.create();
	}
	
	private void viewFiles(String filePath) {
		ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(getActivity(), filePath);
		if (tmp != null) {
			mList.clear();
			mList.addAll(tmp);
			tmp.clear();

			mCurrentPath = filePath;
			mFilepath.setText(filePath);

			mAdapter.notifyDataSetChanged();
		}
	}
}
