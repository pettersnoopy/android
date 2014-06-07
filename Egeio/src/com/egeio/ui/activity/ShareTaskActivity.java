package com.egeio.ui.activity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.egeio.framework.ActionManager;
import com.egeio.common.ConstValues;
import com.egeio.common.holder.BaseViewHolder;
import com.egeio.common.mo.DataTypes;
import com.egeio.framework.BaseActivity;
import com.egeio.ui.R;
import com.egeio.utils.ThirdPartyRedirect;
import com.egeio.utils.Utils;

public class ShareTaskActivity extends BaseActivity {

	class ShareTaskHolder extends BaseViewHolder {

		private ViewFlipper mFlipper;
		private GridView gridView;
		private RadioGroup radiogroup_share;
		private CheckBox downloadable;
		private TextView died_line, setpassword;
		private Button back, Share;

		public ShareTaskHolder(Context context) {
			super(context);
		}

		@Override
		public void initUi(View view) {
			mFlipper = (ViewFlipper) view.findViewById(R.id.flipper);

			gridView = (GridView) view.findViewById(R.id.grid_share_file);
			radiogroup_share = (RadioGroup) view.findViewById(R.id.radiogroup_share);
			downloadable = (CheckBox) view.findViewById(R.id.downloadable);
			died_line = (TextView) view.findViewById(R.id.died_line);
			setpassword = (TextView) view.findViewById(R.id.setpassword);
			back = (Button) view.findViewById(R.id.back);
			Share = (Button) view.findViewById(R.id.Share);

			setpassword.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					PasswordSetDialog mPasswordSet = new PasswordSetDialog();
					mPasswordSet.show(getSupportFragmentManager(), getString(R.string.setpassword));
				}
			});

			died_line.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDatePickerDialog(v);
				}
			});

			back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});

			Share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int accessID = radiogroup_share.getCheckedRadioButtonId();
					DataTypes.ShareAction shareAction = new DataTypes.ShareAction();
					switch (accessID) {
					case R.id.open_all:
						shareAction.access = DataTypes.Access.open;
						break;
					case R.id.open_group:
						shareAction.access = DataTypes.Access.company;
						break;
					case R.id.open_term:
						shareAction.access = DataTypes.Access.collaborators;
						break;
					}
//					shareAction.password = setpassword.getText().toString();
					shareAction.disable_download = downloadable.isChecked() ? 1 : 0;
					
					String due_time = died_line.getText().toString();
					if (due_time != null && !getString(R.string.never).equals(due_time)) {
						shareAction.due_time = died_line.getText().toString();
					}
					
					String password = setpassword.getText().toString();
					if (password != null && !getString(R.string.notset).equals(password)) {
						shareAction.password = password;
					}
					
					Bundle bundle = new Bundle();
					bundle.putSerializable(ConstValues.SHAREACTION, shareAction);
					bundle.putSerializable(ConstValues.ITEM_LIST, mShareFolders);
					
					ActionManager.getInstance().startAction(ShareTaskActivity.this, ActionManager.ACTION_SENDSHARE, bundle, new ActionManager.OnDoingTaskListener(ShareTaskActivity.this) {
						
						@Override
						public boolean done(int code, Bundle args) {
							
							DataTypes.ShareResopnse response = (DataTypes.ShareResopnse)args.getSerializable(ConstValues.SHARERESPONSE);
							
							if (response == null) {
								Utils.showToast(mContext, getString(R.string.share_error));
								return false;
							}
							
							if (getString(R.string.qq_msg).equals(mSenderTo)) {
								
							} else if (getString(R.string.sms).equals(mSenderTo)) {
								
								ThirdPartyRedirect.sendSMS(mContext, response.getShareUrl(), "");
								
							} else if (getString(R.string.email).equals(mSenderTo)) {
								
								ThirdPartyRedirect.sendEmail(mContext, response.getShareUrl(), "");
								
							} else if (getString(R.string.copy).equals(mSenderTo)) {
								ThirdPartyRedirect.setClipBoard(mContext, response.getShareUrl());
							}
							return false;
						}
					});
				}
			});

			ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();

			// 同事
			HashMap<String, Object> colleague = new HashMap<String, Object>();
			colleague.put("ItemImage", R.drawable.colleague);
			colleague.put("ItemText", getString(R.string.colleague));
			lstImageItem.add(colleague);

			// 微信
			HashMap<String, Object> qq_msg = new HashMap<String, Object>();
			qq_msg.put("ItemImage", R.drawable.qq_msg);
			qq_msg.put("ItemText", getString(R.string.qq_msg));
			lstImageItem.add(qq_msg);

			// 短信
			HashMap<String, Object> sms = new HashMap<String, Object>();
			sms.put("ItemImage", R.drawable.sms);
			sms.put("ItemText", getString(R.string.sms));
			lstImageItem.add(sms);

			// 短信
			HashMap<String, Object> email = new HashMap<String, Object>();
			email.put("ItemImage", R.drawable.email);
			email.put("ItemText", getString(R.string.email));
			lstImageItem.add(email);

			// 短信
			HashMap<String, Object> copy = new HashMap<String, Object>();
			copy.put("ItemImage", R.drawable.copy);
			copy.put("ItemText", getString(R.string.copy));
			lstImageItem.add(copy);

			SimpleAdapter shareAdapter = new SimpleAdapter(mContext, lstImageItem, R.layout.gridview_item, new String[] { "ItemImage", "ItemText" }, new int[] { R.id.share_image, R.id.shareText });
			gridView.setAdapter(shareAdapter);

			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					mFlipper.showNext();
					TextView tv = (TextView)view.findViewById(R.id.shareText);
					if (tv != null) {
						mSenderTo = tv.getText().toString();
					}
				}
			});
		}

		@Override
		public void setupView(Bundle bundle) {
		}

		public void setDate(int year, int month, int day) {
			died_line.setText(Utils.formatDate(new Date(year, month, day)));
		}
		
		public void setPassword (String password) {
			if (null != password && !"".equals(password)) {
				setpassword.setText(password);
			}
		}
	}

	private ShareTaskHolder mHolder;

	private DataTypes.BaseItems mShareFolders;
	
	private String mSenderTo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey(ConstValues.ITEM_LIST)) {
			mShareFolders = (DataTypes.BaseItems) bundle.getSerializable(ConstValues.ITEM_LIST);
		}

		View view = LayoutInflater.from(this).inflate(R.layout.layout_share, null);
		setContentView(view);
		mHolder = new ShareTaskHolder(this);
		mHolder.initUi(view);
	}

	@Override
	public String getActivityTag() {
		return ShareTaskActivity.class.toString();
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), getString(R.string.diedline));
	}

	public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			mHolder.setDate(year, month, day);
		}
	}

	public class PasswordSetDialog extends DialogFragment {

		private EditText password, repeat;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.setpassword, null);
			password = (EditText) view.findViewById(R.id.password);
			repeat = (EditText) view.findViewById(R.id.repeat);

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(getString(R.string.setpassword));
			builder.setView(view);
			builder.setPositiveButton(getString(R.string.common_sure), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					password.getText().toString();
					repeat.getText().toString();
				}
			});
			builder.setNegativeButton(getString(R.string.common_cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismiss();
				}
			});
			return builder.create();
		}
	}
}
