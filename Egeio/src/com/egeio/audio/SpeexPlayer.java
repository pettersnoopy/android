/**
 * 
 */
package com.egeio.audio;

import java.io.File;

import com.gauss.speex.encode.SpeexDecoder;

/**
 * @author Gauss
 * 
 */
public class SpeexPlayer {
	private String fileName = null;
	private SpeexDecoder speexdec = null;

	public static interface OnPlayListener {
		public void onStart(String fileName);
		public void onStop(String fileName);
	}
	
	private OnPlayListener mListener;
	
	public void setOnPlayListener (OnPlayListener listener) {
		mListener = listener;
	}

	public SpeexPlayer(String fileName) {

		this.fileName = fileName;
		System.out.println(this.fileName);
		try {
			speexdec = new SpeexDecoder(new File(this.fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startPlay() {
		RecordPlayThread rpt = new RecordPlayThread();

		Thread th = new Thread(rpt);
		th.start();
	}

	boolean isPlay = true;

	class RecordPlayThread extends Thread {

		public void run() {
			try {
				if (mListener != null) {
					mListener.onStart(fileName);
				}
				
				if (speexdec != null)
					speexdec.decode();

				if (mListener != null) {
					mListener.onStop(fileName);
				}
			} catch (Exception t) {
				t.printStackTrace();
			}
		}
	};
}
