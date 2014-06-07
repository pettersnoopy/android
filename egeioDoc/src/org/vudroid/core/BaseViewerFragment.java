package org.vudroid.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.vudroid.core.events.CurrentPageListener;
import org.vudroid.core.events.DecodingProgressListener;
import org.vudroid.core.models.CurrentPageModel;
import org.vudroid.core.models.DecodingProgressModel;
import org.vudroid.core.models.ZoomModel;
import org.vudroid.core.views.PageViewZoomControls;

import cn.com.cisco.pdf.R;

public abstract class BaseViewerFragment extends BaseViewer implements DecodingProgressListener, CurrentPageListener {
	private static final String DOCUMENT_VIEW_STATE_PREFERENCES = "DjvuDocumentViewState";

	private static final int CHILDEN_PAGE = 0;
	private static final int CHILDEN_GRID = 1;

	private DecodeService decodeService;
	private DocumentView documentView;
	private ViewerPreferences viewerPreferences;
	private Toast pageNumberToast;
	private CurrentPageModel currentPageModel;

	// public FrontView frontView;
	static Handler handler;

	private static int lastWhat = 1;

	private FrontViewFragment mFrontView = null;
	private LinearLayout mFrontContainer = null;
	private View mTitleContainer = null;
	private ViewFlipper mFlipper = null;
	private FrameLayout mGridContainer = null;

	private Context mContext;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initDecodeService();

		mContext = getActivity();
		// setContentView(frameLayout);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final ZoomModel zoomModel = new ZoomModel();
		final DecodingProgressModel progressModel = new DecodingProgressModel();
		progressModel.addEventListener(this);
		currentPageModel = new CurrentPageModel();
		currentPageModel.addEventListener(this);

		documentView = new DocumentView(mContext, zoomModel, progressModel, currentPageModel);
		zoomModel.addEventListener(documentView);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		documentView.setLayoutParams(params);
		decodeService.setContentResolver(mContext.getContentResolver());
		decodeService.setContainerView(documentView);
		documentView.setDecodeService(decodeService);

		decodeService.open(mFileUri);

		viewerPreferences = new ViewerPreferences(mContext);

		View view = inflater.inflate(R.layout.baseviewer, null);
		FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.mainContainer);
		mFrontContainer = (LinearLayout) view.findViewById(R.id.frontContainer);
		mTitleContainer = view.findViewById(R.id.titleContainer);
		
		mFlipper = (ViewFlipper) view.findViewById(R.id.flipper);
		
		mGridContainer = (FrameLayout) view.findViewById(R.id.gridContainer);

		frameLayout.addView(documentView);

		handler = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				msg.what = lastWhat * -1;
				switch (msg.what) {
				case -1:
					lastWhat = -1;
					// bottom
					TranslateAnimation bottomHideAnim = new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
					bottomHideAnim.setDuration(300);
					bottomHideAnim.setFillAfter(true);
					mFrontContainer.startAnimation(bottomHideAnim);

					// top
					TranslateAnimation titleShowAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
							Animation.RELATIVE_TO_SELF, 0.0f);
					titleShowAnim.setDuration(300);
					titleShowAnim.setFillAfter(true);
					mTitleContainer.startAnimation(titleShowAnim);

					break;
				case 1:
					lastWhat = 1;
					// bottom
					TranslateAnimation bottomShowAnim = new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
					bottomShowAnim.setDuration(300);
					bottomShowAnim.setFillAfter(true);
					mFrontContainer.startAnimation(bottomShowAnim);

					// top
					TranslateAnimation titleHideAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
							Animation.RELATIVE_TO_SELF, -1.0f);
					titleHideAnim.setDuration(300);
					titleHideAnim.setFillAfter(true);
					mTitleContainer.startAnimation(titleHideAnim);

					break;
				}
			}
		};

		frameLayout.addView(createZoomControls(zoomModel));

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final SharedPreferences sharedPreferences = mContext.getSharedPreferences(DOCUMENT_VIEW_STATE_PREFERENCES, 0);
		documentView.goToPage(0);

		documentView.showDocument(new DocumentView.OnDocumentInitFinish() {
			@Override
			public void onDocumentinited() {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						initFrontView();
					}
				});
			}
		});

		mFlipper.setDisplayedChild(CHILDEN_PAGE);

		viewerPreferences.addRecent(mFileUri);
	}

	public void initFrontView() {

		TextView titleName = (TextView) mTitleContainer.findViewById(R.id.docname);
		titleName.setText(FILENAME);
		View back = mTitleContainer.findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});

		View grid = mTitleContainer.findViewById(R.id.grid);
		grid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int display = mFlipper.getDisplayedChild();
				switch (display) {
				case CHILDEN_PAGE:
					showMainPage ();
					break;
				case CHILDEN_GRID:
					showGridPage ();
					break;
				}
			}
		});
		
		GridPage gridPage = new GridPage(mContext, documentView, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final int position = (Integer)v.getTag();
				mFlipper.showPrevious();
				
				documentView.post(new Runnable() {
					@Override
					public void run() {
						documentView.goToPage(position);
					}
				});
				
			}
		});
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		mGridContainer.addView(gridPage, params);
		
		// fragment
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		if (mFrontView == null) {
			mFrontView = FrontViewFragment.create(mContext, documentView);
		}
		transaction.replace(R.id.frontContainer, mFrontView);
		transaction.commit();
	}
	
	private void showMainPage () {
		Animation lInAnim = AnimationUtils.loadAnimation(mContext, R.anim.push_left_in);
		Animation lOutAnim = AnimationUtils.loadAnimation(mContext, R.anim.push_left_out);
		mFlipper.setInAnimation(lInAnim);
		mFlipper.setOutAnimation(lOutAnim);
		mFlipper.showNext();
	}
	
	private void showGridPage () {
		Animation rInAnim = AnimationUtils.loadAnimation(mContext, R.anim.push_right_in);
		Animation rOutAnim = AnimationUtils.loadAnimation(mContext, R.anim.push_right_out);
		mFlipper.setInAnimation(rInAnim);
		mFlipper.setOutAnimation(rOutAnim);
		mFlipper.showPrevious();
	}

	public void decodingProgressChanged(final int currentlyDecoding) {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				getActivity().getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS, currentlyDecoding == 0 ? 10000 : currentlyDecoding);
			}
		});
	}

	private int lastPage = 0;

	public void currentPageChanged(int pageIndex) {
		final String pageText = (pageIndex + 1) + "/" + decodeService.getPageCount();
		if (pageNumberToast != null) {
			pageNumberToast.setText(pageText);
		} else {
			pageNumberToast = Toast.makeText(mContext, pageText, 300);
		}
		pageNumberToast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
		pageNumberToast.show();
		saveCurrentPage();

		if (!documentView.creatingMap) {
			mFrontView.changeToButton(lastPage, pageIndex);
			lastPage = pageIndex;
		}
	}

	private PageViewZoomControls createZoomControls(ZoomModel zoomModel) {
		final PageViewZoomControls controls = new PageViewZoomControls(mContext, zoomModel);
		controls.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
		zoomModel.addEventListener(controls);
		controls.setVisibility(View.INVISIBLE);
		return controls;
	}

	private FrontView creatIndexLeftView() {
		FrontView indexLeftView = new FrontView(mContext, documentView);

		return indexLeftView;
	}

	private void initDecodeService() {
		if (decodeService == null) {
			decodeService = createDecodeService();
		}
	}

	protected abstract DecodeService createDecodeService();

	@Override
	public void onDestroy() {
		decodeService.recycle();
		decodeService = null;
		super.onDestroy();
	}

	private void saveCurrentPage() {
		final SharedPreferences sharedPreferences = mContext.getSharedPreferences(DOCUMENT_VIEW_STATE_PREFERENCES, 0);
		final SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(mFileUri.toString(), documentView.getCurrentPage());
		editor.commit();
	}

	public static void notifyOnTouch() {
		if (handler != null) {
			Message msg = handler.obtainMessage();
			msg.what = lastWhat;
			msg.sendToTarget();
		}
	}
}
