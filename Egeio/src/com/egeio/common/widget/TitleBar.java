package com.egeio.common.widget;

import java.util.Vector;

import com.egeio.common.ConstValues;
import com.egeio.common.mo.DataTypes;
import com.egeio.filebrowser.FileUtil;
import com.egeio.filebrowser.FloderBrowserDialog;
import com.egeio.framework.ActionManager;
import com.egeio.framework.ActionManager.OnDoingTaskListener;
import com.egeio.framework.BaseActivity;
import com.egeio.ui.R;
import com.egeio.utils.Utils;
import com.jakewharton.disklrucache.Util;

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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.MotionEvent;

/**
 * TitleBar create
 * 
 * @author leixin525
 * 
 */
public class TitleBar extends Fragment {

	private BaseActivity mContext;
	private Button mTitlebarName;
	private String mName;

	private IOnButtonOperatorListener mListener;

	private Operator mLeft1, mLeft2, mRight1, mRight2;

	private Vector<String> mSettingItems = new Vector<String>();

	private LayoutInflater mInflater;

	private PopupWindow mSettingPop;

	private CreateFilderDialog mDlgCreateNewFolder;

	private FloderBrowserDialog mFolderBrowserDialog;

	private SettingsAdapter mSettingAdapter;

	private View mParent;

	public enum Operator {
		Select, CreateNewFile, Back, MenuFilter, Title /* title */ , Back_Contact
	};

	private int[] imgRes = { R.drawable.operator_select, R.drawable.addnew, R.drawable.btn_backward, R.drawable.message_filter, R.drawable.group_contact};

	private ImageView icon_left_1, icon_left_2, icon_right_1, icon_right_2;

	private View.OnClickListener mMenuClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			Operator opt = (Operator) v.getTag();

			if (mListener != null) {
				boolean flag = mListener.onClick(opt);
				if (flag) {
					return;
				}
			}

			switch (opt) {
			case Select:
				break;
			case Title:
			case CreateNewFile:
				openSettingsPop(mParent, opt);
				break;
			case MenuFilter:
				openMenuMsgPop(v, opt);
				break;
			case Back:
				break;
			default:
				break;
			}
		}
	};

	private AdapterView.OnItemClickListener mItemSelectedListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
			ViewHolderSimple holder = (ViewHolderSimple) view.getTag();
			if (getString(R.string.menu_create).equals(holder.text.getText())) { // 新建文件

				if (mDlgCreateNewFolder == null) {
					mDlgCreateNewFolder = new CreateFilderDialog();
				}
				mDlgCreateNewFolder.show(getFragmentManager(), getString(R.string.common_create));
			} else if (getString(R.string.menu_photo_select).equals(holder.text.getText())) {
				if (mFolderBrowserDialog == null) {
					mFolderBrowserDialog = new FloderBrowserDialog();
					mFolderBrowserDialog.setOnItemSelectedListener(new FloderBrowserDialog.OnItemSelectedListener() {
						@Override
						public void onSelected(String path) {
							uploadFile(path);
							mFolderBrowserDialog.getDialog().hide();
						}
					});
				}
				mFolderBrowserDialog.show(getFragmentManager(), holder.text.getText().toString());
			} else if (getString(R.string.menu_photo).equals(holder.text.getText())) {
				Utils.camera(mContext, ConstValues.Request_Camera_Upload);
			}
			mSettingPop.dismiss();
		}
	};

	/**
	 * handle button click listener
	 * 
	 * @author leixin525
	 * 
	 */
	public interface IOnButtonOperatorListener {
		public boolean onClick(Operator operator);
	}

	public void setOnButtonOperatorListener(IOnButtonOperatorListener listener) {
		mListener = listener;
	}

	public static TitleBar creater(Context context, String name) {
		return creater(context, name, null, null, null, null);
	}

	public static TitleBar creater(Context context, String name, Operator left1, Operator left2, Operator right1, Operator right2) {
		TitleBar titlebar = new TitleBar();
		titlebar.mName = name;
		titlebar.mLeft1 = left1;
		titlebar.mLeft2 = left2;
		titlebar.mRight1 = right1;
		titlebar.mRight2 = right2;
		return titlebar;
	}

	public TitleBar() {
	}

	@Override
	public void onAttach(Activity activity) {
		mContext = (BaseActivity) activity;
		mInflater = LayoutInflater.from(mContext);
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mParent = inflater.inflate(R.layout.titlebar, null);

		mTitlebarName = (Button) mParent.findViewById(R.id.titlebar_opt_name);

		icon_left_1 = (ImageView) mParent.findViewById(R.id.icon_left_1);

		icon_left_2 = (ImageView) mParent.findViewById(R.id.icon_left_2);

		icon_right_1 = (ImageView) mParent.findViewById(R.id.icon_right_1);

		icon_right_2 = (ImageView) mParent.findViewById(R.id.icon_right_2);

		reset(mLeft1, mLeft2, mRight1, mRight2);

		setTitleName(mName);

		setPopData();

		return mParent;
	}

	@Override
	public void onStop() {
		super.onStop();

		if (mDlgCreateNewFolder != null && mDlgCreateNewFolder.isVisible()) {
			mDlgCreateNewFolder.dismiss();
		}

		if (mSettingPop != null && mSettingPop.isShowing()) {
			mSettingPop.dismiss();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (ConstValues.Request_Camera_Upload == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				if (Utils.Cache_path != null) {
					ContentResolver cr = mContext.getContentResolver();
					Cursor cursor = cr.query(Utils.Cache_path, null, null, null, null);
					cursor.moveToFirst();
					if (cursor != null) {
						String filepath = cursor.getString(1);
						uploadFile(filepath);
						cursor.close();
					}
				}
			}
		} else {
			
		}
	}
	
	private void uploadFile (String path) {
		Utils.uploadFile(mContext, path, new OnDoingTaskListener(mContext) {
				@Override
				public boolean done(int code, Bundle result) {
					String downloadurl = result.getString(ConstValues.DOWNLOAD_URL);
					if (downloadurl != null) {
						mContext.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mFolderBrowserDialog.dismiss();
								mContext.hideLoading();
							}
						});
					}
					return false;
				}
			});
	}

	public void reset(Operator left1, Operator left2, Operator right1, Operator right2) {

		icon_left_1.setOnClickListener(mMenuClickListener);
		replaceLeft(1, left1);

		icon_left_2.setOnClickListener(mMenuClickListener);
		replaceLeft(2, left2);

		icon_right_1.setOnClickListener(mMenuClickListener);
		replaceRight(1, right1);

		icon_right_2.setOnClickListener(mMenuClickListener);
		replaceRight(2, right2);
	}

	private Vector<String> setPopData() {

		if (mSettingItems == null) {
			mSettingItems = new Vector<String>();
		}

		mSettingItems.clear();

		mSettingItems.add(getResources().getString(R.string.menu_all_filder));

		mSettingItems.add(getResources().getString(R.string.menu_mark));

		mSettingItems.add(getResources().getString(R.string.menu_usually));

		mSettingItems.add(getResources().getString(R.string.menu_create));

		mSettingItems.add(getResources().getString(R.string.menu_photo));

		mSettingItems.add(getResources().getString(R.string.menu_photo_select));

		return mSettingItems;
	}

	private Vector<String> setMsgFilterData() {

		if (mSettingItems == null) {
			mSettingItems = new Vector<String>();
		}

		mSettingItems.clear();

		mSettingItems.add(getResources().getString(R.string.menu_all_message));

		mSettingItems.add(getResources().getString(R.string.menu_view));

		mSettingItems.add(getResources().getString(R.string.menu_news));

		mSettingItems.add(getResources().getString(R.string.menu_mis));

		mSettingItems.add(getResources().getString(R.string.menu_toast));

		mSettingItems.add(getResources().getString(R.string.menu_href));

		mSettingItems.add(getResources().getString(R.string.menu_share));

		return mSettingItems;
	}

	/**
	 * set Titlebar name
	 * 
	 * @param name
	 */
	public void setTitleName(String name) {
		if (mTitlebarName != null) {
			mTitlebarName.setVisibility(View.VISIBLE);
			mTitlebarName.setText(name);
			mTitlebarName.setOnClickListener(mMenuClickListener);
			mTitlebarName.setTag(Operator.Title);
		}
	}

	/**
	 * Replace a image button
	 * 
	 * @param orgin
	 * @param dist
	 */
	public void replaceLeft(int index, Operator dist) {

		ImageView view = null;
		if (index == 1) {
			view = icon_left_1;
		} else {
			view = icon_left_2;
		}

		if (dist == null) {
			view.setVisibility(View.INVISIBLE);
			return;
		}

		view.setImageResource(imgRes[dist.ordinal()]);
		view.setVisibility(View.VISIBLE);
		view.setTag(dist);
	}

	/**
	 * Replace a image button
	 * 
	 * @param orgin
	 * @param dist
	 */
	public void replaceRight(int index, Operator dist) {

		ImageView view = null;
		if (index == 1) {
			view = icon_right_1;
		} else {
			view = icon_right_2;
		}

		if (dist == null) {
			view.setVisibility(View.INVISIBLE);
			return;
		}

		view.setImageResource(imgRes[dist.ordinal()]);
		view.setVisibility(View.VISIBLE);
		view.setTag(dist);
	}

	public void openMenuMsgPop(View view, Operator operator) {
		if (!isVisible())
			return;
		mSettingItems = setMsgFilterData();

		View popView = mInflater.inflate(R.layout.setting, null);

		mSettingPop = new PopupWindow(popView, (int) getResources().getDimension(R.dimen.menu_msg_fliter_width), ViewGroup.LayoutParams.WRAP_CONTENT, true);
		mSettingPop.setFocusable(true);
		// mSettingPop.setAnimationStyle(R.style.TitleBarAnimationFade);

		popView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (mSettingPop != null && mSettingPop.isShowing()) {
					mSettingPop.dismiss();
					mSettingPop = null;
				}
				return false;
			}
		});

		mSettingPop.showAsDropDown(view);
		ListView listview = (ListView) popView.findViewById(R.id.settinglist);
		listview.setDivider(null);

		if (mSettingAdapter == null) {
			mSettingAdapter = new SettingsAdapter(operator);
		}
		listview.setAdapter(mSettingAdapter);
		mSettingAdapter.notifyDataSetChanged();
	}

	public void openSettingsPop(View view, Operator operator) {
		if (!isVisible())
			return;
		mSettingItems = setPopData();

		View popView = mInflater.inflate(R.layout.setting, null);

		switch (operator) {
		case CreateNewFile:
			mSettingItems.remove(getResources().getString(R.string.menu_all_filder));
			mSettingItems.remove(getResources().getString(R.string.menu_mark));
			mSettingItems.remove(getResources().getString(R.string.menu_usually));
			break;
		case Title:
			mSettingItems.remove(getResources().getString(R.string.menu_create));
			mSettingItems.remove(getResources().getString(R.string.menu_photo));
			mSettingItems.remove(getResources().getString(R.string.menu_photo_select));
			break;
		default:
			break;
		}

		mSettingPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		mSettingPop.setFocusable(true);
		// mSettingPop.setAnimationStyle(R.style.TitleBarAnimationFade);

		popView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (mSettingPop != null && mSettingPop.isShowing()) {
					mSettingPop.dismiss();
					mSettingPop = null;
				}
				return false;
			}
		});
		mSettingPop.showAsDropDown(view);
		ListView listview = (ListView) popView.findViewById(R.id.settinglist);
		listview.setDivider(null);

		listview.setOnItemClickListener(mItemSelectedListener);
		if (mSettingAdapter == null) {
			mSettingAdapter = new SettingsAdapter(operator);
		}
		listview.setAdapter(mSettingAdapter);
		mSettingAdapter.notifyDataSetChanged();
	}

	class SettingsAdapter extends BaseAdapter {

		private Operator mOperator;

		public SettingsAdapter(Operator operator) {
			mOperator = operator;
		}

		@Override
		public int getCount() {
			return mSettingItems.size();
		}

		public int getItemViewType(int position) {
			return mOperator.ordinal();
		}

		@Override
		public Object getItem(int position) {
			return mSettingItems.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolderSimple holder = new ViewHolderSimple();
			convertView = mInflater.inflate(R.layout.profileitem, parent, false);
			holder.init(convertView);
			holder.text.setText(mSettingItems.get(position));
			convertView.setTag(holder);
			return convertView;
		}
	}

	public class ViewHolderSimple {
		TextView text;
		ImageView image;

		public void init(View view) {
			text = (TextView) view.findViewById(R.id.settingtext);
		}
	}

	public static class CreateFilderDialog extends DialogFragment {

		private EditText foldername, desc;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.createnewfolder, null);
			foldername = (EditText) view.findViewById(R.id.foldername);
			desc = (EditText) view.findViewById(R.id.desc);

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(getString(R.string.menu_create));
			builder.setView(view);
			builder.setPositiveButton(getString(R.string.common_create), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DataTypes.FolderCreate creater = new DataTypes.FolderCreate();
					BaseActivity context = ((BaseActivity) getActivity());
					creater.name = foldername.getText().toString();
					creater.description = desc.getText().toString();
					creater.parent_id = context.getFolderID();
					Bundle bundle = new Bundle();
					bundle.putSerializable(ConstValues.FOLDERACTION, creater);
					ActionManager.getInstance().startAction(context, ActionManager.ACTION_CREATEFOLDER, bundle);
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

	public void cleanBtns() {
		icon_left_1.setVisibility(View.GONE);
		icon_left_2.setVisibility(View.GONE);
		icon_right_1.setVisibility(View.GONE);
		icon_right_2.setVisibility(View.GONE);
	}

}
