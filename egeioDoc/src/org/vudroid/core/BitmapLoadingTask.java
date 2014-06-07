package org.vudroid.core;

import java.io.File;
import java.io.FileInputStream;

import org.vudroid.core.utils.PathFromUri;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class BitmapLoadingTask extends Thread  {

	private DocumentView mDocumentView;
	private float scale;
	private Context mContext;
	private int mIndex = -1;

	public BitmapLoadingTask(Context context, DocumentView documentView) {
		mDocumentView = documentView;
		mContext = context;
		scale = mContext.getResources().getDisplayMetrics().density;
	}
	
	public BitmapLoadingTask(Context context, DocumentView documentView, int index) {
		mDocumentView = documentView;
		mContext = context;
		scale = mContext.getResources().getDisplayMetrics().density;
		mIndex = index;
	}

	protected abstract void onUpdateSession(float scale, SessionLoad load);
	
	@Override
	public void run() {
		int size = 0;
		if (mIndex < 0) {
			size = mDocumentView.pages.size();
			for (int i = 1; i <= size; i++) {
				checkImageFlie(i);
			}
		} else {
			checkImageFlie(mIndex);
		}
		mDocumentView.post(new Runnable() {
			public void run() {
				mDocumentView.goToPage(0);
			}
		});
	}


	private void checkImageFlie(final int i) {
		File file = new File(PathFromUri.getFileDir(BaseViewer.mFileUri), "page" + i + ".png");
		if (!file.exists()) {
			mDocumentView.post(new Runnable() {
				public void run() {
					mDocumentView.goToPage(i - 1);
				}
			});
			
			while(true){
				if(file.exists()){
					break;
				}
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
			float bitmapScale = 50 * scale / bitmap.getHeight();
			Bitmap b = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * bitmapScale), (int) (bitmap.getHeight() * bitmapScale), false);
			SessionLoad load = new SessionLoad(i, b);
			onUpdateSession(scale ,load);

			bitmap.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class SessionLoad {

		private SessionLoad(int i, Bitmap bmap) {
			index = i;
			bitmap = bmap;
		}

		public int index;
		public Bitmap bitmap;
	}
}
