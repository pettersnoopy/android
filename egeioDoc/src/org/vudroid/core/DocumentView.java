package org.vudroid.core;

import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.Scroller;
import android.widget.Toast;

import org.vudroid.core.events.ZoomListener;
import org.vudroid.core.models.CurrentPageModel;
import org.vudroid.core.models.DecodingProgressModel;
import org.vudroid.core.models.ZoomModel;
import org.vudroid.core.multitouch.MultiTouchZoom;
import org.vudroid.core.multitouch.MultiTouchZoomImpl;
import org.vudroid.core.utils.PathFromUri;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DocumentView extends View implements ZoomListener {
	final ZoomModel zoomModel;
	private final CurrentPageModel currentPageModel;
	DecodeService decodeService;
	final HashMap<Integer, Page> pages = new HashMap<Integer, Page>();
	private boolean isInitialized = false;
	private int pageToGoTo;
	private float lastX;
	private float lastY;
	private VelocityTracker velocityTracker;
	private final Scroller scroller;
	DecodingProgressModel progressModel;
	private RectF viewRect;
	private boolean inZoom;
	private long lastDownEventTime;
	private static final int DOUBLE_TAP_TIME = 500;
	private MultiTouchZoom multiTouchZoom;
	private Context context;
	
	public static interface OnDocumentInitFinish {
		public void onDocumentinited () ;
	}

	public DocumentView(Context context, final ZoomModel zoomModel, DecodingProgressModel progressModel, CurrentPageModel currentPageModel) {
		super(context);
		this.context = context;
		this.zoomModel = zoomModel;
		this.progressModel = progressModel;
		this.currentPageModel = currentPageModel;
		setKeepScreenOn(true);
		scroller = new Scroller(getContext());
		setFocusable(true);
		setFocusableInTouchMode(true);
		initMultiTouchZoomIfAvailable(zoomModel);

	}

	private void initMultiTouchZoomIfAvailable(ZoomModel zoomModel) {
		try {
			multiTouchZoom = (MultiTouchZoom) Class.forName("org.vudroid.core.multitouch.MultiTouchZoomImpl").getConstructor(ZoomModel.class).newInstance(zoomModel);
		} catch (Exception e) {
			System.out.println("Multi touch zoom is not available: " + e);
		}
	}

	public void setDecodeService(DecodeService decodeService) {
		this.decodeService = decodeService;
	}

	private void init() {
		if (isInitialized) {
			return;
		}
		final int width = decodeService.getEffectivePagesWidth();
		final int height = decodeService.getEffectivePagesHeight();
		
		for (int i = 0; i < decodeService.getPageCount(); i++) {
			pages.put(i, new Page(this, i));
			pages.get(i).setAspectRatio(width, height);
		}
		isInitialized = true;
		invalidatePageSizes();
		goToPageImpl(pageToGoTo);

		BaseViewerFragment.handler.sendEmptyMessage(0);
	}

	private void goToPageImpl(final int toPage) {
		// scrollTo(0, pages.get(toPage).getTop()+1);
		scrollTo(pages.get(toPage).getLeft() + 1, 0);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		// bounds could be not updated
		post(new Runnable() {
			public void run() {
				currentPageModel.setCurrentPageIndex(getCurrentPage());
			}
		});
		if (inZoom) {
			return;
		}
		// on scrollChanged can be called from scrollTo just after new layout
		// applied so we should wait for relayout
		post(new Runnable() {
			public void run() {
				updatePageVisibility();
			}
		});
	}

	private void updatePageVisibility() {
		for (Page page : pages.values()) {
			page.updateVisibility();
		}
	}

	public void commitZoom() {
		for (Page page : pages.values()) {
			page.invalidate();
		}
		inZoom = false;
	}

	public void showDocument(final OnDocumentInitFinish callback) {
		// use post to ensure that document view has width and height before
		// decoding begin
		post(new Runnable() {
			public void run() {
				init();
				updatePageVisibility();
				callback.onDocumentinited();
			}
		});
	}
	
	public void showDocument() {
		// use post to ensure that document view has width and height before
		// decoding begin
		post(new Runnable() {
			public void run() {
				init();
				updatePageVisibility();
			}
		});
	}

	public void goToPage(int toPage) {
		if (isInitialized) {
			goToPageImpl(toPage);
		} else {
			pageToGoTo = toPage;
		}
	}

	public int getCurrentPage() {
		for (Map.Entry<Integer, Page> entry : pages.entrySet()) {
			if (entry.getValue().isVisible()) {
				return entry.getKey();
			}
		}
		return 0;
	}

	public void zoomChanged(float newZoom, float oldZoom) {
		inZoom = true;
		stopScroller();
		final float ratio = newZoom / oldZoom;
		invalidatePageSizes();
		scrollTo((int) ((getScrollX() + getWidth() / 2) * ratio - getWidth() / 2), (int) ((getScrollY() + getHeight() / 2) * ratio - getHeight() / 2));
		postInvalidate();

	}

	public boolean creatingMap = true;

	public void saveBitmapToSdcard(Bitmap bitmap, int pageindex) {
		float screenWidth = getResources().getDisplayMetrics().widthPixels;
		float bitmapScale = screenWidth / 5 / 2 / bitmap.getWidth();
		
		Bitmap outBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * bitmapScale), (int) (bitmap.getHeight() * bitmapScale), false);
		// bitmap.recycle();
		try {
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				String tempPath = PathFromUri.getFileDir(BaseViewer.mFileUri);
				File destDir = new File(tempPath);
				if (!destDir.exists()) {
					destDir.mkdirs();
				}
				File file = new File(tempPath, "page" + pageindex + ".png");
				if (!file.exists()) {
					FileOutputStream out = new FileOutputStream(file);
					outBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
					out.flush();
					out.close();
				}
			}
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	static public Bitmap convertPageToBitmap(Page page) {
		Bitmap bitmap = Bitmap.createBitmap((int) (page.bounds.width()), (int) (page.bounds.height()), Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.translate(-page.bounds.width() * page.index, 0);
		page.draw(canvas);
		return bitmap;
	}

	public void convertAndSave(Page page) {
		saveBitmapToSdcard(convertPageToBitmap(page), page.index + 1);
	}
	
	public void convertAndSave(int index) {
		Page page = pages.get(index);
		convertAndSave(page);
	}

	private void dealMyTouch(MotionEvent ev) {
		float touchScale = 800f / context.getResources().getDisplayMetrics().widthPixels;
		float x = ev.getX() * touchScale;
		float y = ev.getY() * touchScale;
		float zoom = zoomModel.getZoom();
		float evPageX = (getScrollX() + x) / zoom;
		float evPageY = (getScrollY() + y) / zoom;
		
		float realXInTouchPage = 0;
		int realTouchPage = 0;
		for (int i = 0; i < pages.size(); i++) {
			if (pages.get(i).getLeft() / zoom < evPageX) {
				if (i == pages.size() - 1) {
					realXInTouchPage = evPageX - pages.get(i).getLeft() / zoom;
					realTouchPage = i;
					break;
				}
				if (pages.get(i + 1).getLeft() / zoom > evPageX) {
					realXInTouchPage = evPageX - pages.get(i).getLeft() / zoom;
					realTouchPage = i;
					break;
				}
			}
		}
		if (realTouchPage != 0 && realTouchPage != pages.size()) {
			if (realXInTouchPage >= 40 && realXInTouchPage <= 80) {
				if (evPageY > 36 && evPageY < 158) {
					if (zoom != 1)
						zoomModel.setZoom(1);
					goToPageImpl(4 - 1);
					showDocument();
				} else if (evPageY > 158 && evPageY < 278) {
					if (zoom != 1)
						zoomModel.setZoom(1);
					goToPageImpl(12 - 1);
					showDocument();
				} else if (evPageY > 278 && evPageY < 392) {
					if (zoom != 1)
						zoomModel.setZoom(1);
					goToPageImpl(18 - 1);
					showDocument();
				} else if (evPageY > 392 && evPageY < 522) {
					if (zoom != 1)
						zoomModel.setZoom(1);
					goToPageImpl(21 - 1);
					showDocument();
				} else if (evPageY > 522 && evPageY < 643) {
					if (zoom != 1)
						zoomModel.setZoom(1);
					goToPageImpl(25 - 1);
					showDocument();
				} else if (evPageY > 643 && evPageY < 763) {
					if (zoom != 1)
						zoomModel.setZoom(1);
					goToPageImpl(32 - 1);
					showDocument();
				}
				return;
			}
		}
		
		BaseViewerFragment.notifyOnTouch();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		super.onTouchEvent(ev);

		if (multiTouchZoom != null) {
			if (multiTouchZoom.onTouchEvent(ev)) {
				return true;
			}

			if (multiTouchZoom.isResetLastPointAfterZoom()) {
				setLastPosition(ev);
				multiTouchZoom.setResetLastPointAfterZoom(false);
				isMove = true;
			}
		}

		if (velocityTracker == null) {
			velocityTracker = VelocityTracker.obtain();
		}
		velocityTracker.addMovement(ev);

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			stopScroller();
			setLastPosition(ev);

			isMove = false;

			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(lastX - ev.getX()) < 20 && Math.abs(lastY - ev.getY()) < 20) {
				return true;
			}
			isMove = true;
			scrollBy((int) (lastX - ev.getX()), (int) (lastY - ev.getY()));
			setLastPosition(ev);
			break;
		case MotionEvent.ACTION_UP:
			
			if (!isMove)
				dealMyTouch(ev);
			velocityTracker.computeCurrentVelocity(1000);

			scroller.fling(getScrollX(), getScrollY(), (int) -velocityTracker.getXVelocity(), (int) -velocityTracker.getYVelocity(), getLeftLimit(), getRightLimit(), getTopLimit(), getBottomLimit());
			
			int fx = scroller.getFinalX();
			int width = getWidth();
			if (fx % width != 0) {
				scroller.setFinalX(width * (fx/width));
			}
			
			velocityTracker.recycle();
			velocityTracker = null;

			break;
		}
		return true;
	}

	private boolean isMove = false;

	private void setLastPosition(MotionEvent ev) {
		lastX = ev.getX();
		lastY = ev.getY();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				lineByLineMoveTo(1);
				return true;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				lineByLineMoveTo(-1);
				return true;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				verticalDpadScroll(1);
				return true;
			case KeyEvent.KEYCODE_DPAD_UP:
				verticalDpadScroll(-1);
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	private void verticalDpadScroll(int direction) {
		scroller.startScroll(getScrollX(), getScrollY(), 0, direction * getHeight() / 2);
		invalidate();
	}

	private void lineByLineMoveTo(int direction) {
		if (direction == 1 ? getScrollX() == getRightLimit() : getScrollX() == getLeftLimit()) {
			scroller.startScroll(getScrollX(), getScrollY(), direction * (getLeftLimit() - getRightLimit()), (int) (direction * pages.get(getCurrentPage()).bounds.height() / 50));
		} else {
			scroller.startScroll(getScrollX(), getScrollY(), direction * getWidth() / 2, 0);
		}
		invalidate();
	}

	private int getTopLimit() {
		return 0;
	}

	private int getLeftLimit() {
		return 0;
	}

	private int getBottomLimit() {
		return (int) (getHeight() * zoomModel.getZoom()) - getHeight();
	}

	private int getRightLimit() {
		return (int) pages.get(pages.size() - 1).bounds.left - getWidth();
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(Math.min(Math.max(x, getLeftLimit()), getRightLimit()), Math.min(Math.max(y, getTopLimit()), getBottomLimit()));
		viewRect = null;
	}

	public RectF getViewRect() {
		if (viewRect == null) {
			viewRect = new RectF(getScrollX(), getScrollY(), getScrollX() + getWidth(), getScrollY() + getHeight());
		}
		return viewRect;
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (Page page : pages.values()) {
			page.draw(canvas);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		float scrollScaleRatio = getScrollScaleRatio();
		invalidatePageSizes();
		invalidateScroll(scrollScaleRatio);
		commitZoom();
	}

	public void invalidatePageSizes() {
		if (!isInitialized) {
			return;
		}

		int width = getWidth();
		int height = getHeight();
		float zoom = zoomModel.getZoom();
		for (int i = 0; i < pages.size(); i++) {
			Page page = pages.get(i);
			float pageHeight = page.getPageHeight(width, zoom);
			float topMargin = (height - pageHeight) / 2;
			topMargin = topMargin > 0 ? topMargin : 0;
			float leftMargin =  i * width * zoom;
			page.setBounds(new RectF(leftMargin, topMargin , leftMargin + width * zoom, topMargin + pageHeight));
		}
	}

	private void invalidateScroll(float ratio) {
		if (!isInitialized) {
			return;
		}
		stopScroller();
		final Page page = pages.get(0);
		if (page == null || page.bounds == null) {
			return;
		}
		scrollTo((int) (getScrollX() * ratio), (int) (getScrollY() * ratio));
	}

	private float getScrollScaleRatio() {
		final Page page = pages.get(0);
		if (page == null || page.bounds == null) {
			return 0;
		}
		final float v = zoomModel.getZoom();
		return getWidth() * v / page.bounds.width();
	}

	private void stopScroller() {
		if (!scroller.isFinished()) {
			scroller.abortAnimation();
		}
	}

}
