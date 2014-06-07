package com.egeio.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

public abstract class BackgroundTask {

	protected Context mContext;
	private LoaderManager lm;
	protected int mBatch = -1;
	protected List<Integer> mIdsList = Collections.synchronizedList(new ArrayList<Integer>());
	protected List<Bundle> mBundleList = Collections.synchronizedList(new ArrayList<Bundle>());
	
	public BackgroundTask(BaseActivity activity){
		this(activity, 1);
	}
	
	public BackgroundTask(BaseFragment fragment){
		this(fragment, 1);
	}
	
	public BackgroundTask(MessageBox messagebox){
		this(messagebox, 1);
	}
	
	public BackgroundTask(BaseActivity activity, int batch) {
		mBatch = batch;
		mContext = activity.getApplicationContext();
		lm = activity.getSupportLoaderManager();
		for(int i=0; i<batch; i++){
			int id = activity.requestLoaderId();
			mIdsList.add(id);
			lm.initLoader(id, null, mCallback);
		}
	}

	public BackgroundTask(BaseFragment fragment, int batch) {
		mBatch = batch;
		mContext = fragment.getActivity().getApplicationContext();
		lm = fragment.getLoaderManager();
		for(int i=0; i<batch; i++){
			int id = fragment.requestLoaderId();
			mIdsList.add(id);
			lm.initLoader(id, null, mCallback);
		}
	}

	public BackgroundTask(MessageBox messagebox, int batch) {
		mBatch = batch;
		mContext = messagebox.getActivity().getApplicationContext();
		lm = messagebox.getLoaderManager();
		for(int i=0; i<batch; i++){
			int id = messagebox.requestLoaderId();
			mIdsList.add(id);
			lm.initLoader(id, null, mCallback);
		}
	}

	public void start(Bundle bundle) {
		if (mBatch == 1) {
			if (bundle == null) {
				bundle = new Bundle();
			}
			lm.restartLoader(mIdsList.get(0), bundle, mCallback);
		}
		else{
			if (bundle != null) {
				if(mBundleList.contains(bundle)){
					mBundleList.remove(bundle);
				}
			}
			else{
				bundle = new Bundle();
			}
			mBundleList.add(0, bundle);
			startBatch();
		}
	}
	
	private void startBatch(){
		if(mBundleList.size()==0){
			return;
		}
		
		for(int id : mIdsList){
			if(mBundleList.size() == 0){
				break;
			}
			Loader<Object> loader = lm.getLoader(id);
			if(loader!=null&&!loader.isStarted()){
				if(mBundleList.size() > 0){
					Bundle bundle = mBundleList.get(0);
					mBundleList.remove(0);
					lm.restartLoader(id, bundle, mCallback);
				}
			}
		}
	}
	
	private void startLoader(Loader<Object> loader){
		if(mBundleList.size() == 0){
			return;
		}
		
		if(loader!=null){
			loader.reset();
			int id = loader.getId();
			if (mBundleList.size() > 0) {
				Bundle bundle = mBundleList.get(0);
				mBundleList.remove(0);
				lm.restartLoader(id, bundle, mCallback);
			}
		}
	}

	protected abstract Object doInBackground(Bundle bundle);

	protected abstract void onPostExecute(Object result);
	
	static private class MyLoader extends AsyncTaskLoader<Object> {

		private Bundle mParam;
		private boolean mAllowStart = false;
		BackgroundTask mWork;

		public MyLoader(Context context, Bundle bundle, BackgroundTask work) {
			super(context);
			mParam = bundle;
			mWork = work;
			if (bundle != null) {
				mAllowStart = true;
			}
		}

		@Override
		public Object loadInBackground() {
			if (mAllowStart) {
				return mWork.doInBackground(mParam);
			}

			return null;
		}

		@Override
		protected void onStartLoading() {
			if (mAllowStart) {
				forceLoad();
			} else {
			    stopLoading();
			}
		}

		@Override
		protected void onStopLoading() {
			cancelLoad();
		}

		@Override
		protected void onReset() {
			super.onReset();

			onStopLoading();
		}

	}

	public void destroyLoader() {
		for(int id : mIdsList){
			lm.destroyLoader(id);
		}
	}

	private LoaderManager.LoaderCallbacks<Object> mCallback = new LoaderManager.LoaderCallbacks<Object>() {

		@Override
		public Loader<Object> onCreateLoader(int id, Bundle args) {
			return new MyLoader(mContext, args, BackgroundTask.this);
		}

		@Override
		public void onLoadFinished(final Loader<Object> loader, final Object result) {

			if (result != null) {
				new Handler().post(new Runnable() {
					@Override
					public void run() {
						onPostExecute(result);
						startLoader(loader);
					}
				});
			}
            
		}

		@Override
		public void onLoaderReset(Loader<Object> loader) {
		}

	};
}
