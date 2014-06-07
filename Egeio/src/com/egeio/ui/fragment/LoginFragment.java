package com.egeio.ui.fragment;

import com.egeio.common.ConstValues;
import com.egeio.common.mo.Contact;
import com.egeio.framework.BackgroundTask;
import com.egeio.framework.BaseFragment;
import com.egeio.network.NetworkManager;
import com.egeio.ui.R;
import com.egeio.ui.activity.MainActivity;
import com.egeio.utils.StoreUtils;
import com.egeio.utils.NetUtils.WifiInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * login page
 * 
 * @author leixin525
 * 
 */
public class LoginFragment extends BaseFragment implements WifiInterface {
	private final String TAG = "LoginActivity";

	protected class LoginViewHolder {
		private EditText userNameText = null;
		private EditText passwordText = null;
		private Button exit, login = null;
		private TextView errorMsg = null;

		public void initUi(View view) {
			userNameText = (EditText) view.findViewById(R.id.username);
			passwordText = (EditText) view.findViewById(R.id.password);
			errorMsg = (TextView) view.findViewById(R.id.errorMsg);
			
			userNameText.setText(StoreUtils.get(mActivity, ConstValues.login));
			passwordText.setText(StoreUtils.get(mActivity, ConstValues.password));

			login = (Button) view.findViewById(R.id.login_button);
			exit = (Button) view.findViewById(R.id.exit_button);
			login.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// loginValidate();
					errorMsg.setVisibility(View.GONE);
					errorMsgShow.setVisibility(View.GONE);
					InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					manager.hideSoftInputFromWindow(userNameText.getWindowToken(), 0);
					manager.hideSoftInputFromWindow(passwordText.getWindowToken(), 0);
					String username = userNameText.getText().toString().trim();
					String password = passwordText.getText().toString().trim();

					new LoginSyncTask().start(wrapUserInfo(username, password));
				}
			});

			if (exit != null) {
				exit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {

							System.exit(0);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}

		}

		public void showErrorMsg(String msg, int gone) {
			if (msg != null) {
				errorMsg.setText(msg);
			}
			errorMsg.setVisibility(gone);
		}
	}

	private Bundle wrapUserInfo(String username, String password) {
		Bundle bundle = new Bundle();
		bundle.putString(ConstValues.login, username);
		bundle.putString(ConstValues.password, password);
		StoreUtils.store(mActivity, ConstValues.login, username);
		StoreUtils.store(mActivity, ConstValues.password, password);
		return bundle;
	}

	protected LoginViewHolder mHolder;
	protected TextView errorMsgShow = null;

	class LoginSyncTask extends BackgroundTask {

		public LoginSyncTask() {
			super(LoginFragment.this, 1);
		}

		@Override
		protected Object doInBackground(Bundle bundle) {
			// do Login
			String user = bundle.getString(ConstValues.login);
			String psd = bundle.getString(ConstValues.password);
			
			Boolean flag = false;
			try {
				flag = NetworkManager.getInstance(getActivity()).userLogin(user, psd);
			} catch (Exception e) {
				// error handler
			}
			return flag;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (result == null) {
				// login exception
				return;
			}
			if (result instanceof Boolean) {
				boolean flag = (Boolean)result;
				
				if (flag) {
					Context context = getActivity();
					Intent intent = new Intent(context, MainActivity.class);
					context.startActivity(intent);
				}
			}
		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// init
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.userlogin, null);
		errorMsgShow = (TextView) view.findViewById(R.id.errorMsgShow);
		mHolder = new LoginViewHolder();
		mHolder.initUi(view);
		return view;
	}

	protected void onLoginSuccess() {

	}

	@Override
	public void loginValidate(String... args) {

	}

	@Override
	public void onResume() {
		Log.d(TAG, "~~~onResume~~~");
		super.onResume();
	}
}