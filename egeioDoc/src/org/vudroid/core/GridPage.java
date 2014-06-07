package org.vudroid.core;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GridPage extends LinearLayout {
	
	private DocumentView mDocumentView;
	
	private List<Page> mValidPages = null;
	
	public enum MarkType {all, maeked, edited};
	
	private Context mContext;
	
	private GridPageAdapter mAdapter = null;
	
	private float scale;
	
	private View.OnClickListener mListener;

	public GridPage(Context context, DocumentView document, View.OnClickListener listener) {
		super(context);
		mDocumentView = document;
		mContext = context;
		mValidPages = flitterPages(MarkType.all);
		scale = mContext.getResources().getDisplayMetrics().density;
		mListener = listener;
		initView(context);
	}
	
	private void initView (Context context) {
		GridView gridView = new GridView(context);
		gridView.setFocusable(true);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		params.topMargin = (int) (60 * scale);
		params.leftMargin = (int) (10 * scale);
		mAdapter = new GridPageAdapter();
		gridView.setAdapter(mAdapter);
		gridView.setNumColumns(4);
		gridView.setVerticalSpacing((int) (10 * scale));
		addView(gridView, params);
	}
	
	private List<Page> flitterPages(MarkType type) {
		List<Page> pages = new ArrayList<Page>();
		pages.addAll(mDocumentView.pages.values());
		return pages;
	}
	
	class GridPageAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mValidPages.size();
		}

		@Override
		public Object getItem(int position) {
			return mValidPages.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			FrameLayout layout = new FrameLayout(mContext);
			AbsListView.LayoutParams params = new AbsListView.LayoutParams((int) scale * 64, (int) scale * 96);
			layout.setLayoutParams(params);
			
			final ImageView img = new ImageView(mContext);
			FrameLayout.LayoutParams imgParams = new FrameLayout.LayoutParams((int) scale * 64, (int) scale * 96);
			layout.addView(img, imgParams);
			
			LinearLayout textLayout = new LinearLayout(mContext);
			textLayout.setBackgroundColor(Color.GRAY);
			FrameLayout.LayoutParams textLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) scale * 25);
			textLayoutParams.gravity = Gravity.BOTTOM;
			layout.addView(textLayout, textLayoutParams);
			
			TextView pageNum = new TextView(mContext);
			LinearLayout.LayoutParams pageNumParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			pageNumParam.gravity = Gravity.CENTER;
			pageNum.setTextSize(2, 10);
			pageNum.setTextColor(Color.WHITE);
			pageNum.setGravity(Gravity.CENTER);
			textLayout.addView(pageNum, pageNumParam);
			
			pageNum.setText("-- " + (position+1) + " --");
			
			new BitmapLoadingTask(mContext, mDocumentView, position+1) {
				@Override
				protected void onUpdateSession(float scale, final SessionLoad load) {
					((Activity)mContext).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							img.setImageBitmap(load.bitmap);
						}
					});
				}
			}.start();
			
			layout.setTag(position);
			layout.setClickable(true);
			layout.setOnClickListener(mListener);
			
			return layout;
		}
		
	}

}
