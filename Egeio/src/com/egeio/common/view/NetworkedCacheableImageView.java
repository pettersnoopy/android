/*******************************************************************************
 * Copyright (c) 2013 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.egeio.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.RejectedExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.egeio.EgeioApplication;
import com.egeio.common.ConstValues;
import com.egeio.network.NetworkManager;
import com.egeio.utils.NetUtils;

import uk.co.senab.bitmapcache.BitmapLruCache;
import uk.co.senab.bitmapcache.CacheableBitmapDrawable;
import uk.co.senab.bitmapcache.CacheableImageView;

/**
 * Simple extension of CacheableImageView which allows downloading of Images of
 * the Internet.
 * 
 * This code isn't production quality, but works well enough for this sample.s
 * 
 * @author Chris Banes
 */
public class NetworkedCacheableImageView extends CacheableImageView {

	public interface OnImageLoadedListener {
		void onImageLoaded(CacheableBitmapDrawable result);
	}
	
	public static String key2url(String key, String userID) {
		String url;
		url = ConstValues.SERVER + ConstValues.GETPICS + "/" + userID + "?auth_token=" + NetworkManager.getToken() + "&profile_pic_key=" + key;
		return url;
	}

	/**
	 * This task simply fetches an Bitmap from the specified URL and wraps it in
	 * a wrapper. This implementation is NOT 'best practice' or production ready
	 * code.
	 */
	private static class ImageUrlAsyncTask extends AsyncTask<String, Void, CacheableBitmapDrawable> {

		private final BitmapLruCache mCache;

		private final WeakReference<ImageView> mImageViewRef;
		private final OnImageLoadedListener mListener;

		private final BitmapFactory.Options mDecodeOpts;

		ImageUrlAsyncTask(ImageView imageView, BitmapLruCache cache, BitmapFactory.Options decodeOpts, OnImageLoadedListener listener) {
			mCache = cache;
			mImageViewRef = new WeakReference<ImageView>(imageView);
			mListener = listener;
			mDecodeOpts = decodeOpts;
		}

		@Override
		protected CacheableBitmapDrawable doInBackground(String... params) {
			try {
				// Return early if the ImageView has disappeared.
				if (null == mImageViewRef.get()) {
					return null;
				}

				final String url = params[0];

				// Now we're not on the main thread we can check all caches
				CacheableBitmapDrawable result = mCache.get(url, mDecodeOpts);

				if (null == result) {
					Log.d("ImageUrlAsyncTask", "Downloading: " + url);

					// The bitmap isn't cached so download from the web
					String url_2 = key2url(params[0], params[1]);
					//String url_2 = ConstValues.SERVER + ConstValues.GETPICS + "/" + userID + "?auth_token=" + NetworkManager.getToken() + "&profile_pic_key=" + url;
					HttpClient client = NetUtils.getHttpClient();
					HttpGet getMethod = new HttpGet(url_2);
					HttpResponse response = client.execute(getMethod);
					Bitmap bitmap = null;
					int res = response.getStatusLine().getStatusCode();
					if (res == 200) {
						InputStream is = response.getEntity().getContent();
						bitmap = BitmapFactory.decodeStream(is);
					}

					// Add to cache
					result = mCache.put(url_2, bitmap);
				} else {
					Log.d("ImageUrlAsyncTask", "Got from Cache: " + url);
				}

				return result;

			} catch (IOException e) {
				Log.e("ImageUrlAsyncTask", e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(CacheableBitmapDrawable result) {
			super.onPostExecute(result);

			ImageView iv = mImageViewRef.get();
			if (null != iv) {
				iv.setImageDrawable(result);
			}

			if (null != mListener) {
				mListener.onImageLoaded(result);
			}
		}
	}

	private final BitmapLruCache mCache;

	private ImageUrlAsyncTask mCurrentTask;

	public NetworkedCacheableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mCache = EgeioApplication.getApplication(context).getBitmapCache();
	}
	
	public NetworkedCacheableImageView(Context context) {
		super(context);
		mCache = EgeioApplication.getApplication(context).getBitmapCache();
	}

	/**
	 * Loads the Bitmap.
	 * 
	 * @param url
	 *            - URL of image
	 * @param fullSize
	 *            - Whether the image should be kept at the original size
	 * @return true if the bitmap was found in the cache
	 */
	public boolean loadImage(String key, String userID, int res, final boolean fullSize, OnImageLoadedListener listener) {
		// First check whether there's already a task running, if so cancel it
		if (null != mCurrentTask) {
			mCurrentTask.cancel(true);
		}
		
		String url = key2url(key, userID);

		// Check to see if the memory cache already has the bitmap. We can
		// safely do
		// this on the main thread.
		BitmapDrawable wrapper = mCache.getFromMemoryCache(url);
		
		if (null != wrapper) {
			// The cache has it, so just display it
			setImageDrawable(wrapper);
			return true;
		} else {
			// Memory Cache doesn't have the URL, do threaded request...
//			setImageDrawable(null);
			setImageResource(res);

			BitmapFactory.Options decodeOpts = null;

			if (!fullSize) {
				// decodeOpts = new BitmapFactory.Options();
				// decodeOpts.inSampleSize = 2;
			}

			mCurrentTask = new ImageUrlAsyncTask(this, mCache, decodeOpts, listener);

			try {
				// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// SDK11.executeOnThreadPool(mCurrentTask, url);
				// } else {
				// mCurrentTask.execute(url);
				// }
				mCurrentTask.execute(key, userID);
			} catch (RejectedExecutionException e) {
				// This shouldn't happen, but might.
			}

			return false;
		}
	}

}
