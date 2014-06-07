package com.egeio.framework;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.egeio.ui.R;

public class MessageBox extends DialogFragment implements DialogInterface.OnKeyListener {

	public final static String DIALOG_DATA = "DIALOG_DATA";
	protected DialogData mData = new DialogData();

	private int mLoaderCount = 0;

	public int requestLoaderId() {
		return mLoaderCount++;
	}

	protected static class DialogData implements Serializable {
		private static final long serialVersionUID = 3727171124767603465L;
		private int mTitleId;
		private int mBoldId;
		private int mPlainIds[];
		private int mOkId;
		private int mCancelId;
		private boolean mOkFirst = true;
		private String mTitleText;
		private String mBoldText;
		private String mPlainTexts[];
		private String mOkText;
		private String mCancelText;
	}

	public void setTitleId(int titleId) {
		mData.mTitleId = titleId;
	}

	public void setBoldId(int boldId) {
		mData.mBoldId = boldId;
	}

	public void setPlainIds(int... plainIds) {
		mData.mPlainIds = plainIds;
	}

	public void setOkId(int okId) {
		mData.mOkId = okId;
	}

	public void setCancelId(int cancelId) {
		mData.mCancelId = cancelId;
	}

	public void setOkFirst(boolean okFirst) {
		mData.mOkFirst = okFirst;
	}

	public void setError(Exception error) {
		int okTextId = R.string.common_try_again;
		int cancelTextId = R.string.common_cancel;
		int errorMessageId = 0;
		int errorTextId = 0;

		if ((error instanceof UnknownHostException) || (error instanceof SocketException) || (error instanceof SocketTimeoutException)
				|| (error instanceof org.apache.http.conn.HttpHostConnectException)) {
			errorMessageId = R.string.store_no_internet;
			errorTextId = R.string.request_timed_out;
		} else if (error instanceof RemoteException) {
			errorMessageId = R.string.store_server_error;
			errorTextId = R.string.general_no_internet_txt2;
		} else if (error instanceof IOException) {
			errorMessageId = R.string.store_server_error;
			errorTextId = R.string.store_server_error_retry;
		} else {
			errorMessageId = R.string.err_system_error;
			errorTextId = R.string.err_system_error;
		}

		mData.mOkId = okTextId;
		mData.mCancelId = cancelTextId;
		mData.mOkFirst = true;

		setPlainIds(errorTextId);
		setBoldId(errorMessageId);
	}

	public void setTitleText(String titleText) {
		mData.mTitleText = titleText;
	}

	public void setBoldText(String boldText) {
		mData.mBoldText = boldText;
	}

	public void setPlainTexts(String... plainTexts) {
		mData.mPlainTexts = plainTexts;
	}

	public void setOkText(String okText) {
		mData.mOkText = okText;
	}

	public void setCancelText(String cancelText) {
		mData.mCancelText = cancelText;
	}

	public void show(FragmentManager fm) {
		if (fm != null) {

			Bundle bundle = getArguments();
			if (bundle == null)
				bundle = new Bundle();

			bundle.putSerializable(DIALOG_DATA, mData);
			mData = null;
			setArguments(bundle);
			show(fm, "MessageBox");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(DIALOG_DATA, mData);
		super.onSaveInstanceState(outState);
	}

	public void showDialogInstanceState(FragmentManager fManager) {
		show(fManager, "MessageBox");
	}

	public void setMsgId(int titleTextId, int okBtnTextId, int cancelBtnTextId, boolean okFirst) {
		mData.mTitleId = titleTextId;
		mData.mOkId = okBtnTextId;
		mData.mCancelId = cancelBtnTextId;
		mData.mOkFirst = okFirst;
	}

	public DialogData getDialogData() {
		return (DialogData) (getArguments().getSerializable("DIALOG_DATA"));
	}

	@Override
	public void onAttach(Activity activity) {
		Log.i("MessageBox", "------------------------onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("MessageBox", getFragmentManager() + "------------------------onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Log.i("MessageBox", ":" + this + "------------------------onCreateDialog");

		Dialog dlg = new Dialog(getActivity(), R.style.dialog_style);
		dlg.setContentView(R.layout.messagebox2);
		DialogData data = getDialogData();
		if (data != null) {
			if (data.mOkFirst) {
				dlg.setContentView(R.layout.messagebox);
			}

			setDialogContentMsg(data, dlg);
		}
		return dlg;
	}

	protected void setDialogContentMsg(DialogData data, Dialog dlg) {
		if (data != null) {
			TextView title = (TextView) dlg.findViewById(R.id.title_name);
			if (title != null) {
				if (data.mTitleId != 0) {
					title.setText(getString(data.mTitleId));
				} else {
					title.setText(data.mTitleText);
				}
			}

			TextView boldTextView = (TextView) dlg.findViewById(R.id.boldText);
			if (boldTextView != null) {
				if (data.mBoldId != 0) {
					boldTextView.setText(getString(data.mBoldId));
				} else {
					boldTextView.setText(data.mBoldText);
				}
			}
			TextView plainTextView = (TextView) dlg.findViewById(R.id.plainText);
			if (plainTextView != null) {
				StringBuilder strBuf = new StringBuilder();
				if (data.mPlainIds != null && data.mPlainIds.length != 0) {
					for (int plainTextId : data.mPlainIds) {
						strBuf.append(getString(plainTextId)).append('\n');
					}
				} else if (data.mPlainTexts != null && data.mPlainTexts.length != 0) {
					for (String plainText : data.mPlainTexts) {
						strBuf.append(plainText).append('\n');
					}
				}
				if (!"".equals(strBuf.toString())) {
					plainTextView.setText(strBuf.toString());
				}
			}
		}
		Button buttonPositive = (Button) dlg.findViewById(R.id.ok_button);
		Button buttonNegative = (Button) dlg.findViewById(R.id.cancel_button);
		if (buttonPositive != null) {
			buttonPositive.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onPositiveClicked();

				}
			});
		}
		if (buttonNegative != null) {
			buttonNegative.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onNegativeClicked();
				}
			});
		}
		if (data != null) {
			if (data.mOkId != 0) {
				buttonPositive.setText(getString(data.mOkId));
				buttonPositive.setVisibility(View.VISIBLE);
			} else if (data.mOkText != null) {
				buttonPositive.setText(data.mOkText);
				buttonPositive.setVisibility(View.VISIBLE);
			} else {
				buttonPositive.setVisibility(View.GONE);
			}

			if (data.mCancelId != 0) {
				buttonNegative.setText(getString(data.mCancelId));
				buttonNegative.setVisibility(View.VISIBLE);
			} else if (data.mCancelText != null) {
				buttonNegative.setText(data.mCancelId);
				buttonNegative.setVisibility(View.VISIBLE);
			} else {
				buttonNegative.setVisibility(View.GONE);
			}
		}
		dlg.setOnKeyListener(this);
		dlg.setCancelable(true);
	}

	@Override
	public void onPause() {
		Log.i("MessageBox", "------------------------onPause");
		super.onPause();
	}

	protected void onPositiveClicked() {
	}

	protected void onNegativeClicked() {
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}