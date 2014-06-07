package org.vudroid.core;

import cn.com.cisco.pdf.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class FrontView extends HorizontalScrollView{
//	DocumentView documentView;
	Context mContext;
	float scale;
	LinearLayout ll;
	static final int myColor = Color.argb(255, 255, 153, 0);

	public FrontView(final Context context, final DocumentView documentView) {
		super(context);
		mContext = context;
		this.setBackgroundResource(R.drawable.frontviewback);
		ll = new LinearLayout(context);
		setHorizontalScrollBarEnabled(false);
		// ll.setBackgroundColor(Color.RED);
		ll.setGravity(Gravity.CENTER);
		scale = getResources().getDisplayMetrics().density;
		addView(ll, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, Gravity.CENTER));
		ll.setPadding((int) (10 * scale), 0, (int) (10 * scale), 0);
	}

	public void clearAllButtonBackground() {
		LinearLayout ll = (LinearLayout) getChildAt(0);
		for (int i = 0; i < 34; i++) {
			ll.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
		}
	}

	public void changeToButton(int lastPage, int page) {
		LinearLayout ll = (LinearLayout) getChildAt(0);
		for (int i = 0; i < 34; i++) {
			ll.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
			if (i == page) {
				ll.getChildAt(i).setBackgroundColor(myColor);
				scrollToButton(page);
			}
		}
	}

	public void scrollToButton(int page) {
		if (page < 3)
			return;
		int scrollFrom = getScrollX();
		int scrollTo = getChildAt(0).getWidth() / 34 * (page - 3);
		scrollXAnim(scrollFrom, scrollTo);
	}

	long animTime = 300;

	public void scrollXAnim(final int scrollFrom, final int scrollTo) {
		final long timeRec = System.currentTimeMillis();
		new Thread() {
			public void run() {
				while (true) {
					long timeUse = System.currentTimeMillis() - timeRec;
					if (timeUse > animTime)
						break;
					handler.obtainMessage((int) (scrollFrom + (scrollTo - scrollFrom) * timeUse / animTime)).sendToTarget();
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	public float getScale () {
		return scale;
	}

	Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			scrollTo(msg.what, 0);
		}
	};
	
	public void addView (View view) {
		ll.addView(view);
	}
}
