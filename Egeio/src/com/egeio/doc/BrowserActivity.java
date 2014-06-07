package com.egeio.doc;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.vudroid.core.BaseViewer;
import org.vudroid.core.BaseViewerFragment;
import org.vudroid.core.utils.PathFromUri;
import org.vudroid.djvudroid.DjvuViewerFragment;
import org.vudroid.pdfdroid.PdfViewerFragment;

import com.egeio.common.ConstValues;
import com.egeio.common.holder.BaseButtonHolder;
import com.egeio.common.mo.Item;
import com.egeio.framework.ActionManager;
import com.egeio.framework.BaseActivity;
import com.egeio.ui.R;
import com.egeio.ui.fragment.CommenList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class BrowserActivity extends BaseActivity implements ActionManager.ActionListener{
	
	public static final int PAGE_DOCVIEW = 0;
	public static final int PAGE_COMMENT = 1;
	
	private Item mFileItem;
	
	private float lastX, lastY;
	
	private BaseButtonHolder mButtonHolder;
	private BaseViewerFragment mViewerFragment = null;
	private CommenList mCommenList;
	private ViewFlipper mFlipper;
	private LinearLayout content_comment;
	
	static public void inputstreamtofile(InputStream ins, File file) {
		try {
			OutputStream os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			ins.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void initTransportBundle(Bundle bundle) {
		mFileItem = (Item)bundle.getSerializable(ConstValues.ITEMINFO);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.viewercontainer);
		mFlipper = (ViewFlipper) findViewById(R.id.flipper);
		
		updateButtonHolder();
		
		if (mFileItem == null) {
			Toast.makeText(this, getString(R.string.novalidfile), Toast.LENGTH_LONG);
			return;
		}
		
		final File dir = new File(PathFromUri.getMyPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}

		final File f = new File(PathFromUri.getMyPath(),mFileItem.getName());

		if (!f.exists()) {
			final ProgressDialog mydialog = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.loadingfile), true);
			new Thread() {
				public void run() {
					try {
						File file = new File(PathFromUri.getMyPath(), "test.pdf");
						mydialog.dismiss();
						showDocument(file);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		} else {
			showDocument(f);
		}
		
		initCommentList();
		
		mFlipper.setDisplayedChild(PAGE_DOCVIEW);
	}

	private void showDocument(File file) {
		try {
			showDocument(Uri.fromFile(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initCommentList () {
		if (mCommenList == null) {
			mCommenList = new CommenList();
		}
		FragmentTransaction transaction =  getSupportFragmentManager().beginTransaction();
		Bundle bundle = new Bundle();
		bundle.putSerializable(ConstValues.ITEMINFO, mFileItem);
		mCommenList.setArguments(bundle);
		transaction.replace(R.id.conent_comment, mCommenList);
		transaction.commit();
	}

	protected void showDocument(Uri uri) throws Exception{
		// check and select fragment
		String uriString = uri.toString();
		String extension = uriString.substring(uriString.lastIndexOf('.') + 1);
		
		PathFromUri.Extension exten = PathFromUri.Extension.valueOf(extension);
		
		switch (exten) {
		case pdf:
			mViewerFragment = new PdfViewerFragment();
			break;
		case djvu:
		case djv:
			mViewerFragment = new DjvuViewerFragment();
			break;
		default:
			break;
		}
		
		FragmentTransaction transaction =  getSupportFragmentManager().beginTransaction();
		Bundle bundle = new Bundle();
		bundle.putString(BaseViewer.FIELD_NAME, uri.getPath());
		bundle.putLong(BaseViewer.FIELD_ID, mFileItem.getId());
		mViewerFragment.setArguments(bundle);
		transaction.replace(R.id.content, mViewerFragment);
		transaction.commit();
	}
	
	public void showPage (int page) {
		if (PAGE_DOCVIEW == page) {
			mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
			mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
			mFlipper.setDisplayedChild(PAGE_DOCVIEW);
		} else if (PAGE_COMMENT == page){
			mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
			mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
			mFlipper.setDisplayedChild(PAGE_COMMENT);
		}
	}
	
	private void updateButtonHolder() {
		mButtonHolder = new BaseButtonHolder(this, this);
		mButtonHolder.initUi(findViewById(R.id.bottom));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public String getActivityTag() {
		return BrowserActivity.class.toString();
	}

	@Override
	public void onStartAction(int code, Bundle bundle) {
		if (ActionManager.ACTION_MSG_HOS == code) {
			showPage(PAGE_COMMENT);
		} else if (ActionManager.ACTION_SHARED == code) {
			
		}
	}
}
