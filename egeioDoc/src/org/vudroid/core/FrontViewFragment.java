package org.vudroid.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class FrontViewFragment extends Fragment {

	static final int myColor = Color.argb(255, 255, 153, 0);

	private Context mContext;

	public FrontView mFrontView;

	public DocumentView mDocumentView;
	
	private BitmapLoadingTask mBitmapTask;

	public static FrontViewFragment create(Context context, DocumentView documentView) {
		FrontViewFragment fragment = new FrontViewFragment();
		fragment.mDocumentView = documentView;
		return fragment;
	}

	private View.OnClickListener mClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v instanceof ImageButton) {
				ImageButton ib = (ImageButton) v;
				mDocumentView.zoomModel.setZoom(1);
				mDocumentView.goToPage((Integer) ib.getTag());
				mDocumentView.showDocument();
				mFrontView.clearAllButtonBackground();
				ib.setBackgroundColor(myColor);
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (mBitmapTask == null) {
			mBitmapTask = new BitmapLoadingTask(mContext, mDocumentView) {
				@Override
				protected void onUpdateSession(final float scale, final SessionLoad load) {
					mDocumentView.post(new Runnable() {
						@Override
						public void run() {
							ImageButton ib = new ImageButton(mContext);
							ib.setImageBitmap(load.bitmap);
							ib.setOnClickListener(mClickListener);
							ib.setTag(load.index);
							ib.setBackgroundColor(Color.TRANSPARENT);
							ib.setPadding((int) (3 * scale), (int) (5 * scale), (int) (3 * scale), (int) (5 * scale));
							if (load.index == 1) {
								ib.setBackgroundColor(myColor);
							}
							mFrontView.addView(ib);
						}
					});
				}
			};
		}
		mBitmapTask.start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mFrontView = new FrontView(mContext, mDocumentView);

		return mFrontView;
	}
	
	public void changeToButton (int latestPage, int pageIndex) {
		mFrontView.changeToButton(latestPage, pageIndex);
	}
}
