package com.egeio.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.egeio.common.ConstValues;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetUtils {
	
	private static final int REQUEST_TIMEOUT = 10*1000;
	private static final int SO_TIMEOUT = 10*1000;

	private static final String CHARSET = HTTP.UTF_8;
	private static HttpClient customerHttpClient;
	private static final String TAG = "CustomerHttpClient";

	private NetUtils() {

	}

	/**
	 * 
	 * verfy wify connect
	 * 
	 * @param context
	 * @return
	 */
	public boolean isWifiActive(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] infos = connectivity.getAllNetworkInfo();
			if (infos != null) {
				for (int i = 0; i < infos.length; i++) {
					if (infos[i].getTypeName().equals("WIFI") && infos[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String downloadFile(String url, String localPath, String fileName) {

		OutputStream outStream = null;
		InputStream inputStream = null;
		try {
			File fileDir = new File(localPath);
			if (!fileDir.exists()) {
				fileDir.mkdir();
			}
			String newFileName = localPath + "/" + fileName;
			File file = new File(newFileName);
			if (file.exists()) {
				file.delete();
			}

			URL downloadUrl = new URL(url);
			URLConnection connection = downloadUrl.openConnection();
			inputStream = connection.getInputStream();

			byte[] buffer = new byte[1024];
			
			outStream = new FileOutputStream(newFileName);
			int length;  
            while((length=(inputStream.read(buffer))) >0){  
            	outStream.write(buffer,0,length);  
             }
            outStream.flush();
			return newFileName;
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outStream.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String post(String url, String msg, NameValuePair... params) throws Exception{
		try {
			// request params
			
			if (!url.endsWith("?")) {
				url = url + "?";
			}
			
			for (NameValuePair p : params) {
				if (ConstValues.api_key.equals(p.getName())) {
					url = url + ConstValues.api_key + "=" + p.getValue();
				} else if (ConstValues.auth_token.equals(p.getName())) {
					url = url + ConstValues.auth_token + "=" + p.getValue();
				}
			}
			
			StringEntity entity = new StringEntity(msg);
			// create POST request
			HttpPost request = new HttpPost(url);
			request.setEntity(entity);
			
			// send request
			HttpClient client = getHttpClient();
			HttpResponse response = client.execute(request);
			
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new RuntimeException("request exception");
			}
			HttpEntity resEntity = response.getEntity();
			return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
		} catch (UnsupportedEncodingException e) {
			Log.w(TAG, e.getMessage());
			return null;
		} catch (ClientProtocolException e) {
			Log.w(TAG, e.getMessage());
			return null;
		} catch (IOException e) {
			throw new RuntimeException("connect error", e);
		}
	}
	
	public static String post(String url, NameValuePair... params) throws Exception{
		try {
			// request params
			JSONObject jsonParams = new JSONObject();
			
			if (!url.endsWith("?")) {
				url = url + "?";
			}
			
			for (NameValuePair p : params) {
				if (ConstValues.api_key.equals(p.getName())) {
					url = url + ConstValues.api_key + "=" + p.getValue();
				} else if (ConstValues.auth_token.equals(p.getName())) {
					url = url + ConstValues.auth_token + "=" + p.getValue();
				} else {
					jsonParams.put(p.getName(), p.getValue());
				}
			}
			
			StringEntity entity = new StringEntity(jsonParams.toString());
			// create POST request
			HttpPost request = new HttpPost(url);
			request.setEntity(entity);
			
			// send request
			HttpClient client = getHttpClient();
			HttpResponse response = client.execute(request);
			
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new RuntimeException("request exception");
			}
			HttpEntity resEntity = response.getEntity();
			return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
		} catch (UnsupportedEncodingException e) {
			Log.w(TAG, e.getMessage());
			return null;
		} catch (ClientProtocolException e) {
			Log.w(TAG, e.getMessage());
			return null;
		} catch (IOException e) {
			throw new RuntimeException("connect error", e);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static String postTextComment(String urlString, String mToken, long itemID, String content) throws Exception{
		URI url = null;
		try {
			// request params
			
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append( urlString);
			if (! urlString.endsWith("?")) {
				urlBuilder.append("?");
			}
			urlBuilder.append(ConstValues.auth_token).append("=").append(mToken);
			url = new URI(urlBuilder.toString());
			
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			entity.addPart("item_id", new StringBody("" + itemID));
			entity.addPart("item_type", new StringBody("file"));
			entity.addPart("content", new StringBody(content));
			entity.addPart("is_voice", new StringBody("0"));
			// create POST request
			HttpPost request = new HttpPost(url);
			request.setEntity(entity);
			
			// send request
			HttpClient client = getHttpClient();
			HttpResponse response = client.execute(request);
			
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new RuntimeException("request exception");
			}
			HttpEntity resEntity = response.getEntity();
			return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
		} catch (UnsupportedEncodingException e) {
			Log.w(TAG, e.getMessage());
			return null;
		} catch (ClientProtocolException e) {
			Log.w(TAG, e.getMessage());
			return null;
		} catch (IOException e) {
			throw new RuntimeException("connect error", e);
		}

	}
	
	@SuppressWarnings("deprecation")
	public static String postVoiceComment(String urlString, String mToken, Long itemID, String voice_date) throws Exception{
		URI url = null;
		try {
			// request params
			
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append( urlString);
			if (! urlString.endsWith("?")) {
				urlBuilder.append("?");
			}
			urlBuilder.append(ConstValues.auth_token).append("=").append(mToken);
			url = new URI(urlBuilder.toString());
			
			File file = new File(voice_date);
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			entity.addPart("content", new StringBody(""));
			entity.addPart("item_id", new StringBody("" + itemID));
			entity.addPart("item_type", new StringBody("file"));
			entity.addPart("is_voice", new StringBody("1"));
			entity.addPart("voice_length", new StringBody(""+30));
			entity.addPart("voice_data", new FileBody(file));
			// create POST request
			HttpPost request = new HttpPost(url);
			request.setEntity(entity);
			
			// send request
			HttpClient client = getHttpClient();
			HttpResponse response = client.execute(request);
			
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new RuntimeException("request exception");
			}
			HttpEntity resEntity = response.getEntity();
			return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
		} catch (UnsupportedEncodingException e) {
			Log.w(TAG, e.getMessage());
			return null;
		} catch (ClientProtocolException e) {
			Log.w(TAG, e.getMessage());
			return null;
		} catch (IOException e) {
			throw new RuntimeException("connect error", e);
		}

	}

	public static String getRequest(String urlString, Map<String, String> params) {

		try {
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append(urlString);

			if (null != params) {
				if (!urlString.endsWith("?")) {
					urlBuilder.append("?");
				}

				Iterator<Entry<String, String>> iterator = params.entrySet().iterator();

				while (iterator.hasNext()) {
					Entry<String, String> param = iterator.next();
					urlBuilder.append(URLEncoder.encode(param.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(param.getValue(), "UTF-8"));
					if (iterator.hasNext()) {
						urlBuilder.append('&');
					}
				}
			}
			// Create HttpClient
			HttpClient client = getHttpClient();
			// send HttpGet
			HttpGet getMethod = new HttpGet(urlBuilder.toString());
			HttpResponse response = client.execute(getMethod);
			// get status
			int res = response.getStatusLine().getStatusCode();
			if (res == 200) {

				StringBuilder builder = new StringBuilder();
				// get response
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					builder.append(s);
				}
				return builder.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static String uploadFile(String urlString, String mToken, String filedir) {
		try {
				HttpClient httpClient = new DefaultHttpClient();  
			    HttpContext localContext = new BasicHttpContext();
			    URI url;
				try {
					StringBuilder urlBuilder = new StringBuilder();
					urlBuilder.append(urlString);
					if (!urlString.endsWith("&")) {
						urlBuilder.append("&");
					}
					urlBuilder.append(ConstValues.auth_token).append("=").append(mToken);
					url = new URI(urlBuilder.toString());
					
					HttpPost httpPost = new HttpPost(url);
			        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			        entity.addPart("file", new FileBody(new File(filedir)));
			        httpPost.setEntity(entity);
			        HttpResponse response = httpClient.execute(httpPost, localContext);
			        if (response.getStatusLine().getStatusCode()==200) {
			        	StringBuilder builder = new StringBuilder();
						// get response
						BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			        	for (String s = reader.readLine(); s != null; s = reader.readLine()) {
							builder.append(s);
						}
						return builder.toString();
			        } else {
			        	response.getEntity();
			        	return null;
			        }
				} catch (Exception e) {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}
	
    @SuppressWarnings("deprecation")
	public static String updatePhoto (String urlString, String filedir) {
		try {
			HttpClient httpClient = getHttpClient();  
			try {
				HttpPost httpPost = new HttpPost(urlString);

				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//		        FormBodyPart fbp = new FormBodyPart("profile_pic", new FileBody(new File(filedir), "image/*"));
//	            fbp.addField("Content-Disposition", "form-data");
//	            fbp.addField("name", "\"file\"");
//	            fbp.addField("Content-Type", "multipart/form-data;boundary=*****");
//	            fbp.addField("Content-Transfer-Encoding", "binary");
//	            fbp.addField("filename", filedir.substring(filedir.lastIndexOf("\\")+1));
	            entity.addPart("profile_pic", new FileBody(new File(filedir)));
	            entity.addPart("Content-Type", new StringBody("multipart/form-data;boundary=*****"));
	            entity.addPart("filename", new StringBody(filedir.substring(filedir.lastIndexOf("\\")+1)));
		        httpPost.setEntity(entity);
		        
		        HttpResponse response = httpClient.execute(httpPost);
		        if (response.getStatusLine().getStatusCode()==200) {
		        	StringBuilder builder = new StringBuilder();
					// get response
					BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		        	for (String s = reader.readLine(); s != null; s = reader.readLine()) {
						builder.append(s);
					}
					return builder.toString();
		        } else {
		        	response.getEntity();
		        	return null;
		        }
			} catch (Exception e) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * Verfy network connect 
	 * 
	 * @param context
	 * @return true activity ,or false 
	 */
	public static boolean isOpenNetwork(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}

		return false;
	}

	public static synchronized HttpClient getHttpClient() {
		if (null == customerHttpClient) {
			HttpParams params = new BasicHttpParams();
			// params
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams.setUserAgent(params, "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) " + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");

			ConnManagerParams.setTimeout(params, 5000);

			HttpConnectionParams.setConnectionTimeout(params, 5000);

			HttpConnectionParams.setSoTimeout(params, 5000);

			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
			customerHttpClient = new DefaultHttpClient(conMgr, params);
		}
		return customerHttpClient;
	}

	public interface WifiInterface {
		void loginValidate(String... args);
	}
}
