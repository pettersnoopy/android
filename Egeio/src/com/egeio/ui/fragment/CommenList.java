package com.egeio.ui.fragment;

import java.util.Calendar;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egeio.audio.SpeexPlayer;
import com.egeio.audio.SpeexPlayer.OnPlayListener;
import com.egeio.audio.SpeexRecorder;
import com.egeio.common.ConstValues;
import com.egeio.common.holder.BaseViewHolder;
import com.egeio.common.mo.DataTypes;
import com.egeio.common.mo.Item;
import com.egeio.common.mo.Message;
import com.egeio.common.view.NetworkedCacheableImageView;
import com.egeio.common.view.PullToRefreshListView;
import com.egeio.framework.ActionManager;
import com.egeio.framework.BackgroundTask;
import com.egeio.framework.BaseFragment;
import com.egeio.network.NetworkManager;
import com.egeio.ui.R;
import com.egeio.utils.Utils;

public class CommenList extends BaseFragment {

	private Handler mHandler = new Handler();

	private PullToRefreshListView list;
	private ImageView audio_send;
	private EditText et_sendmessage;
	private Button btn_send;
	private TextView btn_rcd;
	private RelativeLayout mBottom;

	private CommentListAdapter mAdapter;
	private DataTypes.CommentItemBundle mComments;

	private boolean btn_vocie = false;
	private ImageView volume;
	
	private SpeexRecorder recorderInstance = null;
	private SpeexPlayer splayer = null;
	
	private LinearLayout rcChat_popup;
	
	private String mFileName = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.commentlist, null);
		list = (PullToRefreshListView) view.findViewById(R.id.list);
		audio_send = (ImageView) view.findViewById(R.id.audio_send);
		btn_send = (Button) view.findViewById(R.id.btn_send);
		et_sendmessage = (EditText) view.findViewById(R.id.et_sendmessage);
		mBottom = (RelativeLayout) view.findViewById(R.id.btn_bottom);
		btn_rcd = (TextView) view.findViewById(R.id.btn_rcd);
		volume = (ImageView) view.findViewById(R.id.volume);
		rcChat_popup = (LinearLayout) view.findViewById(R.id.rcChat_popup);

		audio_send.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (btn_vocie) {
					btn_rcd.setVisibility(View.GONE);
					mBottom.setVisibility(View.VISIBLE);
					btn_vocie = false;
					audio_send.setImageResource(R.drawable.chatting_setmode_msg_btn);

				} else {
					btn_rcd.setVisibility(View.VISIBLE);
					mBottom.setVisibility(View.GONE);
					audio_send.setImageResource(R.drawable.chatting_setmode_voice_btn);
					btn_vocie = true;
				}
			}
		});
		
		btn_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle(getArguments());
				bundle.putString(ConstValues.content, et_sendmessage.getText().toString());
				ActionManager.getInstance().startAction(mActivity, ActionManager.ACTION_CREATE_COMMENT, bundle, new ActionManager.OnDoingTaskListener(mActivity){
					@Override
					public boolean done(int code, Bundle result) {
						initData();
						return false;
					}
					
				});
			}
		});
		
		btn_rcd.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				AnimationDrawable volumeDrawable = (AnimationDrawable) volume.getDrawable();
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//按下
					btn_rcd.setPressed(true);
					rcChat_popup.setVisibility(View.VISIBLE);
					mFileName = Utils.getFileCachePath() + "/" + Calendar.getInstance().getTimeInMillis() + ".spx";
					if (volumeDrawable != null) {
						volumeDrawable.start();
					}
					start(mFileName);
					break;
				case MotionEvent.ACTION_MOVE:
					//移动
					break;
				case MotionEvent.ACTION_UP:
					//抬起
					btn_rcd.setPressed(false);
					//send file
					final Bundle bundle = new Bundle(getArguments());
					bundle.putString(ConstValues.voice_data, mFileName);
					bundle.remove(ConstValues.content);
					recorderInstance.setRecording(false);
					rcChat_popup.setVisibility(View.GONE);
					if (volumeDrawable != null) {
						volumeDrawable.stop();
					}
					recorderInstance = null;
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							ActionManager.getInstance().startAction(mActivity, ActionManager.ACTION_CREATE_COMMENT, bundle, new ActionManager.OnDoingTaskListener(mActivity){
								@Override
								public boolean done(int code, Bundle result) {
									initData();
									return false;
								}
							});
						}
					}, 300);
					break;
				}
				return true;
			}
		});

		mAdapter = new CommentListAdapter();
		list.setAdapter(mAdapter);
		return view;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		stop();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle bundle = getArguments();
		if (!bundle.containsKey(ConstValues.ITEMINFO)) {
			return;
		}

		super.initData();
	}

	@Override
	protected Object loadNextPage(Bundle bundle) {
		Item item = (Item) bundle.getSerializable(ConstValues.ITEMINFO);
		return NetworkManager.getInstance(mActivity).getMessage(item.getId());
	}

	@Override
	protected void displayNextPage(Object result) {
		if (result != null && result instanceof DataTypes.CommentItemBundle) {
			mComments = (DataTypes.CommentItemBundle) result;
		}
		mAdapter.notifyDataSetChanged();
	}

	class CommentListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mComments == null ? 0 : mComments.comment_count;
		}

		@Override
		public Object getItem(int position) {
			return mComments.comments[position];
		}

		@Override
		public long getItemId(int position) {
			return mComments.comments[position].id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Message message = mComments.comments[position];
			AdapterViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_comment, null);
				holder = new AdapterViewHolder(mActivity);
			}
			if (convertView.getTag() != null) {
				holder = (AdapterViewHolder) convertView.getTag();
			}
			holder.initUi(convertView);
			holder.updateValue(message);
			convertView.setTag(holder);
			return convertView;
		}
	}

	class AdapterViewHolder extends BaseViewHolder {

		private NetworkedCacheableImageView icon;
		private TextView name, time;
		private LinearLayout content;

		public AdapterViewHolder(Context context) {
			super(context);
		}

		@Override
		public void initUi(View view) {
			icon = (NetworkedCacheableImageView) view.findViewById(R.id.icon);
			name = (TextView) view.findViewById(R.id.name);
			time = (TextView) view.findViewById(R.id.time);
			content = (LinearLayout) view.findViewById(R.id.content);
		}

		@Override
		public void setupView(Bundle bundle) {
		}

		public void updateValue(Message message) {
			if (icon != null) {
//				icon.loadImage(message.user.profile_pic_url, R.drawable.def_contacts_icon, false, null);
			}
			if (name != null) {
				name.setText(message.user.name);
			}
			if (time != null) {
				time.setText(message.created_at);
			}

			if (content != null) {
				content.removeAllViews();
				if (!DataTypes.CommentItemBundle.isVoice(message)) {
					TextView tv = new TextView(mContext);
					tv.setText(message.content);
					tv.setTextAppearance(mActivity, R.style.Text_Body_font);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					content.addView(tv, params);
				} else {
					LinearLayout layout = new LinearLayout(mContext);
					layout.setBackgroundResource(R.drawable.chatfrom_bg);
					layout.setOrientation(LinearLayout.HORIZONTAL);
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					
					TextView fileName = new TextView(mContext);
					fileName.setText(Utils.getFileName(message.voice_storage_path_ogg));
					fileName.setTextAppearance(mActivity, R.style.Text_Body_font);
					LinearLayout.LayoutParams fileParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					fileParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
					layout.addView(fileName, fileParams);
					
					ImageView voiceImg = new ImageView(mContext);
					voiceImg.setImageResource(R.drawable.bg_comment_voiceplay);
					LinearLayout.LayoutParams voiceParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					voiceParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
					voiceParams.leftMargin = (int)getResources().getDimension(R.dimen.common_10dp);
					layout.addView(voiceImg, voiceParams);
					
					layout.setOnClickListener(new PlayVoiceListener(voiceImg, message));
					
					content.addView(layout, layoutParams);
				}
			}
		}
	}
	
	class PlayVoiceListener implements View.OnClickListener {
		
		private ImageView mListenerImg = null;
		
		private AnimationDrawable animationDrawable = null;
		
		private boolean isPlaying = false;
		
		private BackgroundTask mPlayTask;
		
		private Message mMessage;
		
		private SpeexRecorder recorderInstance = null;
		
		public PlayVoiceListener (ImageView img, Message message) {
			mListenerImg = img;
			mMessage = message;
		}
		
		@Override
		public void onClick(View v) {
			animationDrawable = (AnimationDrawable) mListenerImg.getDrawable();
			if (mPlayTask != null) {
				mPlayTask.destroyLoader();
				mPlayTask = null;
			}
			
			mPlayTask = new BackgroundTask(mActivity) {
				
				@Override
				protected void onPostExecute(Object result) {
					final String path = (String)result;
					if (path != null && !"".equals(path)) {
//						recorderInstance = new SpeexRecorder(path);
//						recorderInstance.setRecording(false);
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								splayer = new SpeexPlayer(path);
								splayer.setOnPlayListener(new OnPlayListener() {
									
									@Override
									public void onStop(String fileName) {
										if (animationDrawable != null) {
											mHandler.postDelayed(new Runnable() {
												
												@Override
												public void run() {
													animationDrawable.stop();
												}
											}, 100);
										}
									}
									
									@Override
									public void onStart(String fileName) {
										if (animationDrawable != null) {
											mHandler.postDelayed(new Runnable() {
												
												@Override
												public void run() {
													animationDrawable.start();
												}
											}, 100);
										}
									}
								});
								splayer.startPlay();
							
							}
						}, 100);
					}
				}
				
				@Override
				protected Object doInBackground(Bundle bundle) {
					isPlaying = true;
					return NetworkManager.getInstance(mContext).downloadAudio(mMessage.voice_storage_path_ogg);
				}
			};
			mPlayTask.start(new Bundle());
		}
	}

	private static final int POLL_INTERVAL = 300;

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};
	private Runnable mPollTask = new Runnable() {
		public void run() {
//			double amp = mSensor.getAmplitude();
//			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};

	private void start(String name) {
		if (recorderInstance == null) {
			// recorderInstance = new PcmRecorder();
			recorderInstance = new SpeexRecorder(name);
			Thread th = new Thread(recorderInstance);
			th.start();
		}
		recorderInstance.setRecording(true);
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		recorderInstance.setRecording(false);
		volume.setImageResource(R.drawable.amp1);
	}
}
