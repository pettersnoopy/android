package com.egeio.utils;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ThirdPartyRedirect {

	public static void sendSMS(Context context, String url, String msg) {
		Uri smsToUri = Uri.parse("smsto:");

		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

		intent.putExtra("sms_body", url);

		context.startActivity(intent);
	}

	public static void sendEmail(Context context, String url, String msg) {
		Intent data = new Intent(Intent.ACTION_SENDTO);
		data.setData(Uri.parse("mailto:455245521@qq.com"));
		data.putExtra(Intent.EXTRA_SUBJECT, "分享文件地址:" + url);
		data.putExtra(Intent.EXTRA_TEXT, url);
		context.startActivity(data);
	}

	public static void setClipBoard(Context context, String url) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("label", url);
			clipboard.setPrimaryClip(clip);
		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(url);
		}
	}

	public static String paste(Context context) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			return clipboard.getText().toString().trim();
		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			return clipboard.getText().toString().trim();
		}

	}

}
