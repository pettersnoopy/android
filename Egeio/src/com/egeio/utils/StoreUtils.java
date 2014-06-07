package com.egeio.utils;

import com.egeio.common.ConstValues;
import com.egeio.common.mo.Contact;
import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class StoreUtils {
	
	public static final String STORE_NAME = "egeio_store";
	
	public static void store(Context context, String key, String value) {
		 SharedPreferences prefer = context.getSharedPreferences(STORE_NAME, 0);
		 Editor editer = prefer.edit();
		 editer.putString(key, value);
		 editer.commit();
	}
	
	public static String get(Context context, String key) {
		SharedPreferences prefer = context.getSharedPreferences(STORE_NAME, 0);
		return prefer.getString(key, null);
	}
	
	public static void storeContact (Context context, Contact contact) {
		SharedPreferences prefer = context.getSharedPreferences(STORE_NAME, 0);
		Editor editer = prefer.edit();
		Gson gson = new Gson();
		editer.putString(ConstValues.CONTACT, gson.toJson(contact));
		editer.commit();
	}
	
	public static Contact getContact (Context context) {
		SharedPreferences prefer = context.getSharedPreferences(STORE_NAME, 0);
		String contactJson = prefer.getString(ConstValues.CONTACT, null);
		Contact contact = null;
		if (contactJson != null) {
			Gson gson = new Gson();
			contact = gson.fromJson(contactJson, Contact.class);
		}
		return contact;
	}

}
