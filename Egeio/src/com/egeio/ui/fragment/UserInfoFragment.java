package com.egeio.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.VideoColumns;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.egeio.common.ConstValues;
import com.egeio.common.holder.BaseViewHolder;
import com.egeio.common.mo.Contact;
import com.egeio.common.mo.DataTypes;
import com.egeio.common.view.NetworkedCacheableImageView;
import com.egeio.framework.BaseActivity;
import com.egeio.framework.BaseFragment;
import com.egeio.framework.ActionManager.OnDoingTaskListener;
import com.egeio.ui.R;
import com.egeio.utils.Utils;

public class UserInfoFragment extends BaseFragment {

	class UserInfoHolder extends BaseViewHolder {

		private NetworkedCacheableImageView img_contact;

		private TextView name, phone, email;

		private View lin_name, lin_phone, lin_email;

		public UserInfoHolder(Context context) {
			super(context);
		}

		@Override
		public void initUi(View view) {
			img_contact = (NetworkedCacheableImageView) view.findViewById(R.id.img_contact);
			name = (TextView) view.findViewById(R.id.name);
			phone = (TextView) view.findViewById(R.id.phone);
			email = (TextView) view.findViewById(R.id.email);
			lin_name = view.findViewById(R.id.lin_name);
			lin_name.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mSettingDialog = new SettingInputDialog(getString(R.string.update_name), name.getText().toString());
					mSettingDialog.show(getChildFragmentManager(), getString(R.string.update_name));
				}
			});
			lin_phone = view.findViewById(R.id.lin_phone);
			lin_phone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mSettingDialog = new SettingInputDialog(getString(R.string.update_phone), phone.getText().toString());
					mSettingDialog.show(getChildFragmentManager(), getString(R.string.update_phone));
				}
			});
			lin_email = view.findViewById(R.id.lin_email);
			lin_email.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mSettingDialog = new SettingInputDialog(getString(R.string.update_email), email.getText().toString());
					mSettingDialog.show(getChildFragmentManager(), getString(R.string.update_email));
				}
			});

			img_contact.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					PopupWindow popWindow = initPopWindow(new String[] { getString(R.string.select_form_photo), getString(R.string.take_photo) }, new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							if (position == 0) {
								Intent intent = new Intent();
								intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								startActivityForResult(intent, ConstValues.Request_photo_select);
							} else {
								Utils.camera(UserInfoFragment.this, ConstValues.Request_Camera_Upload);
							}
						}
					});
					popWindow.showAsDropDown(img_contact);
				}
			});
		}

		@Override
		public void setupView(Bundle bundle) {
		}

		public void updateValue(Contact contact) {
			if (img_contact != null) {
				img_contact.loadImage(contact.profile_pic_key, String.valueOf(contact.id), R.drawable.def_contacts_icon, false, null);
			}
			if (name != null) {
				name.setText(contact.name);
			}
			if (phone != null) {
				phone.setText(contact.phone);
			}
			if (email != null) {
				email.setText(contact.login);
			}
		}
	}

	private UserInfoHolder mHolder;

	private Contact mContact;

	private SettingInputDialog mSettingDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle.containsKey(ConstValues.CONTACT)) {
			mContact = (Contact) bundle.getSerializable(ConstValues.CONTACT);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.userinfopage, null);
		mHolder = new UserInfoHolder(mActivity);
		mHolder.initUi(view);
		mHolder.updateValue(mContact);
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (ConstValues.Request_photo_select == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = data.getData();
				String[] proj = { MediaStore.Images.Media.DATA };

				Cursor cursor = mActivity.managedQuery(uri, proj, null, null, null);

				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

				cursor.moveToFirst();

				String path = cursor.getString(column_index);
				Utils.uploadUserPic(mActivity, path, new OnDoingTaskListener(mActivity) {
					@Override
					public boolean done(int code, Bundle result) {
						final String downloadurl = result.getString(ConstValues.DOWNLOAD_URL);
						if (mContact != null && downloadurl != null) {
							mActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// for test
									Utils.showToast(mActivity, "更新头像成功");

									mHolder.img_contact.loadImage(downloadurl, "" + mContact.id, R.drawable.def_contacts_icon, false, null);
								}
							});
						}
						return false;
					}
				});
			}
		} else if (ConstValues.Request_Camera_Upload == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				if (Utils.Cache_path != null) {
					ContentResolver cr = mActivity.getContentResolver();
					Cursor cursor = cr.query(Utils.Cache_path, null, null, null, null);
					cursor.moveToFirst();
					if (cursor != null) {
						String filepath = cursor.getString(1);
						Utils.uploadUserPic(mActivity, filepath, new OnDoingTaskListener(mActivity) {
							@Override
							public boolean done(int code, Bundle result) {
								final String downloadurl = result.getString(ConstValues.DOWNLOAD_URL);
								if (mContact != null && downloadurl != null) {
									mActivity.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											// for test
											Utils.showToast(mActivity, "更新头像成功");
											mHolder.img_contact.loadImage(downloadurl, "" + mContact.id, R.drawable.def_contacts_icon, false, null);
										}
									});
								}
								return false;
							}
						});

						cursor.close();
					}
				}
			}
		}
	}

	public static class SettingInputDialog extends DialogFragment {

		private EditText name;

		private String mTitle, mText;

		public SettingInputDialog(String Title, String text) {
			mTitle = Title;
			mText = text;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.setting_input, null);
			name = (EditText) view.findViewById(R.id.name);
			name.setText(mText);

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(view);
			builder.setTitle(mTitle);
			builder.setPositiveButton(getString(R.string.common_create), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DataTypes.FolderCreate creater = new DataTypes.FolderCreate();
					BaseActivity context = ((BaseActivity) getActivity());
					creater.name = name.getText().toString();
					dismiss();
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

	private PopupWindow initPopWindow(String[] arrays, OnItemClickListener listener) {
		BaseAdapter adapter = new ArrayAdapter<String>(mActivity, R.layout.simple_setting_items, arrays);
		return initPopWindow(adapter, listener);
	}

	private PopupWindow initPopWindow(BaseAdapter adapter, OnItemClickListener listener) {
		View contentView = LayoutInflater.from(mActivity).inflate(R.layout.list_internate_update, null);

		final PopupWindow popwindow = new PopupWindow(contentView);
		popwindow.setWidth((int) getResources().getDimension(R.dimen.picture_popwindow_width));
		popwindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		ListView listView = (ListView) contentView.findViewById(R.id.list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(listener);

		popwindow.setFocusable(true);

		contentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popwindow != null && popwindow.isShowing()) {
					popwindow.dismiss();
				}
				return false;
			}
		});
		return popwindow;
	}

}
