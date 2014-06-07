package com.egeio.network;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.egeio.common.ConstValues;
import com.egeio.common.mo.DataTypes;
import com.egeio.common.mo.DataTypes.LoginResponse;
import com.egeio.common.mo.DataTypes.SortBy;
import com.egeio.common.mo.DataTypes.SortDirection;
import com.egeio.common.mo.Folder;
import com.egeio.common.mo.Message;
import com.egeio.utils.NetUtils;
import com.egeio.utils.StoreUtils;
import com.egeio.utils.Utils;
import com.google.gson.Gson;

import android.content.Context;

public class NetworkManager {
	
	private static NetworkManager mManager = null;
	
	private Context mContext;
	
	private String api_key = "37e359bba6b45e2cd91ac2ca2c7adb47";
	
	private String mAuth = null;
	
	private Gson mGson;
	
	public static NetworkManager getInstance (Context context) {
		if (mManager == null) {
			mManager = new NetworkManager(context);
			
//			mManager.mAuth = StoreUtils.get(context, ConstValues.auth_token);
		}
		
		return mManager;
	}
	
	public NetworkManager (Context context) {
		mContext = context;
		mGson = new Gson();
	}
	
	public boolean isLogined() {
		
//		mAuth = "dd52110375099626932cb890ee93b5a1";
//		mAuth = "3dfb2902bf120c6cc4a3bba81c407432";
		
		return mAuth==null || "".equals(mAuth) ? false : true;
	}
	
	public Boolean userLogin (String login, String password) throws Exception{
		BasicNameValuePair apiValue = new BasicNameValuePair(ConstValues.api_key, api_key);
		BasicNameValuePair loginValue = new BasicNameValuePair(ConstValues.login, login);
		BasicNameValuePair passwdValue = new BasicNameValuePair(ConstValues.password, password);
		String json = NetUtils.post(ConstValues.SERVER + ConstValues.USER_LOGIN, apiValue, loginValue, passwdValue);
		try {
			
			LoginResponse response = mGson.fromJson(json, LoginResponse.class);
			if (response != null) {
				mAuth = response.auth_token;
				StoreUtils.store(mContext, ConstValues.auth_token, mAuth);
				StoreUtils.storeContact(mContext, response.user);
			}
			
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject.has(ConstValues.auth_success)) {
				boolean flag = jsonObject.getBoolean(ConstValues.auth_success);
				if (flag) {
					if (jsonObject.has(ConstValues.auth_token)) {
						mAuth = jsonObject.getString(ConstValues.auth_token);
						StoreUtils.store(mContext, ConstValues.auth_token, mAuth);
						return true;
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public DataTypes.FolderItemBundle getFileListByKeywords (int limit, int offset) {
		return getFileListByKeywords("", limit, offset);
	}
	
	public DataTypes.FolderItemBundle getFileListByKeywords (String keywords, int limit, int offset) {
		return getFileListByKeywords(keywords, SortBy.date.name(), SortDirection.desc.name(), limit, offset);
	}
	
	public DataTypes.FolderItemBundle getFileListByKeywords (String keywords, String sort_by, String sort_direction, int limit, int offset) {
		Map<String, String> map = new HashMap<String, String>();
		// required
		if (mAuth != null) {
			map.put(ConstValues.auth_token, mAuth);
		} else {
			// get auth
		}
		map.put(ConstValues.keywords, keywords);
		
		if (sort_by != null && !"".equals(sort_by)) {
			map.put(ConstValues.sort_by, sort_by);
		}
		
		if (sort_direction != null && !"".equals(sort_by)) {
			map.put(ConstValues.sort_direction, sort_direction);
		}
		
		if (limit <= 0) {
			limit = ConstValues.DEFAULT_LIMIT;
		}
		map.put(ConstValues.limit, "" + limit);
		
		if (offset <= 0) {
			offset = ConstValues.DEFAULT_OFFSET;
		}
		map.put(ConstValues.offset, "" + offset);
		
		String json = NetUtils.getRequest(ConstValues.SERVER, map);
		
		DataTypes.FolderItemBundle itemsBundle = mGson.fromJson(json, DataTypes.FolderItemBundle.class);
		
		return itemsBundle;
	}
	
	public DataTypes.FolderItemBundle getFileList (long folderID) {
		return getFileList (folderID, SortBy.date.name(), SortDirection.desc.name());
	}
	
	public DataTypes.SearchResultBundle search (String keywords, String sort_by, String sort_direction, int limit, int offset, String fields) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConstValues.keywords, keywords);
		if (sort_by != null) {
			params.put(ConstValues.sort_by, sort_by);
		}
		if (sort_direction != null) {
			params.put(ConstValues.sort_direction, sort_direction);
		}
		if (fields != null) {
			params.put(ConstValues.fields, ""+fields);
		}
		params.put(ConstValues.limit, ""+limit);
		params.put(ConstValues.offset, ""+offset);
		return search(params);
	}
	
	public DataTypes.SearchResultBundle search (String keywords) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(ConstValues.keywords, keywords);
		return search(params);
	}
	
	public DataTypes.SearchResultBundle search (Map<String, String> params) {
		
		Map<String, String> map = params;
		if (mAuth != null) {
			map.put(ConstValues.auth_token, mAuth);
		} else {
			// get auth
		}
		
		String json = NetUtils.getRequest(ConstValues.SERVER + ConstValues.SEARCH, map);
		DataTypes.SearchResultBundle result = mGson.fromJson(json, DataTypes.SearchResultBundle.class);
		return result;
	}
	
	public DataTypes.FolderItemBundle getFileList (long folderID, String sort_by, String sort_direction) {
		String[] fields = {"id","type", "name", "size", "created_at", "modified_at"};
		
		return getFileList(folderID, fields, sort_by, sort_direction);
	}
	
	public DataTypes.FolderItemBundle getFileList (long folderID, String[] fields, String sort_by, String sort_direction) {
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<fields.length; i++) {
			String string = fields[i];
			builder.append(string);
			if (i<fields.length-1) {
				builder.append(",");
			}
		}
		
		Map<String, String> map = new HashMap<String, String>();
		
		// required
		map.put(ConstValues.auth_token, mAuth);
		
		map.put(ConstValues.fields, builder.toString());
		
		if (sort_by != null && !"".equals(sort_by)) {
			map.put(ConstValues.sort_by, sort_by);
		}
		
		if (sort_direction != null && !"".equals(sort_by)) {
			map.put(ConstValues.sort_direction, sort_direction);
		}
		
		String url = ConstValues.SERVER + ConstValues.GETFOLDERS + "/" + folderID + "?";
		String json = NetUtils.getRequest(url, map);
		
		DataTypes.FolderItemBundle itemsBundle = mGson.fromJson(json, DataTypes.FolderItemBundle.class);
		
		return itemsBundle;
	}
	
	public DataTypes.ContactsItemBundle getContacts () {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// required
		map.put(ConstValues.auth_token, mAuth);
		
		String url = ConstValues.SERVER + ConstValues.GETCONTACTS;
		String json = NetUtils.getRequest(url, map);
		
		DataTypes.ContactsItemBundle itemsBundle = mGson.fromJson(json, DataTypes.ContactsItemBundle.class);
		
		if (itemsBundle == null) {
			itemsBundle = new DataTypes.ContactsItemBundle();
		}
		return itemsBundle;
	}
	
	public DataTypes.CommentItemBundle getMessage (long folderID) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		// required
		map.put(ConstValues.auth_token, mAuth);
		
		String url = ConstValues.SERVER + ConstValues.GETCOMMENTS + "/" + folderID + "?";
		String json = NetUtils.getRequest(url, map);
		
		DataTypes.CommentItemBundle itemsBundle = mGson.fromJson(json, DataTypes.CommentItemBundle.class);
		
		return itemsBundle;
	}
	
	public boolean deleteFile (String... fileID) {
		
		StringBuilder builder = new StringBuilder("{" + "\"items\"" + ": [");
		for (int i=0; i<fileID.length; i++) {
			builder.append("\"");
			builder.append(fileID[i]);
			builder.append("\"");
			
			if (i != fileID.length-1) {
				builder.append(",");
			}
		}
		builder.append("]" + "}");
		
		BasicNameValuePair auth = new BasicNameValuePair(ConstValues.auth_token, mAuth);
		
		try {
			String json = NetUtils.post(ConstValues.SERVER + ConstValues.DELETE, builder.toString(), auth);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public DataTypes.ShareResopnse sendShare(long ForderID, DataTypes.ShareAction action) {
		String msg = mGson.toJson(action);
		BasicNameValuePair auth = new BasicNameValuePair(ConstValues.auth_token, mAuth);
		try {
			String json = NetUtils.post(ConstValues.SERVER + ConstValues.SHARE + "/" + ForderID, msg, auth);
			DataTypes.ShareResopnse response = mGson.fromJson(json, DataTypes.ShareResopnse.class);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Folder ceateFolder (long id, String folderName, String desc) {
		DataTypes.FolderCreate foldercreate = new DataTypes.FolderCreate();
		foldercreate.parent_id = id;
		foldercreate.name = folderName;
		foldercreate.description = desc;
		
		return createFolder (foldercreate);
	}
	
	public Folder createFolder (DataTypes.FolderCreate folderCreate) {
		
		String msg = mGson.toJson(folderCreate);
		
		BasicNameValuePair auth = new BasicNameValuePair(ConstValues.auth_token, mAuth);
		
		try {
			String json = NetUtils.post(ConstValues.SERVER + ConstValues.FOLDERCREATE , msg, auth);
			
			Folder folder = mGson.fromJson(json, Folder.class);
			
			return folder;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String uploadFile (long folderID, String path) {
		
		try {
			String json = NetUtils.uploadFile(ConstValues.SERVER + ConstValues.UPLOAD + "?folder_id=" + folderID, mAuth, path);
			
			if (json != null) {
			JSONObject jsonObj = new JSONObject(json);
				if (jsonObj.has(ConstValues.upload_key)) {
					return jsonObj.getString(ConstValues.upload_key);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Message createTextComment (Long itemID, String content) {
		try {
			String json = NetUtils.postTextComment(ConstValues.SERVER + ConstValues.CREATE_COMMENT, mAuth, itemID, content);
			Message message = mGson.fromJson(json, Message.class);
			return message;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Message createVoiceComment (Long itemID, String voice_data) {
		try {
			String json = NetUtils.postVoiceComment(ConstValues.SERVER + ConstValues.CREATE_COMMENT, mAuth, itemID, voice_data);
			Message message = mGson.fromJson(json, Message.class);
			return message;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String downloadAudio(String url) {
		String localDir = Utils.getFileCachePath();
		String fileName = Utils.getFileName(url);
		NetUtils.downloadFile(url, localDir, fileName);
		return localDir + "/" + fileName;
	}
	
	public String uploadPic (String path) {
		
		try {
			String json = NetUtils.updatePhoto(ConstValues.SERVER + ConstValues.UPLOAD_USER_PIC + "?" + ConstValues.auth_token + "="+mAuth, path);
			if (json != null) {
			JSONObject jsonObj = new JSONObject(json);
				if (jsonObj.has(ConstValues.DOWNLOAD_URL)) {
					return jsonObj.getString(ConstValues.DOWNLOAD_URL);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
 
}
