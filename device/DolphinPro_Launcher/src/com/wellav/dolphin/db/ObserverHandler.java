package com.wellav.dolphin.db;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public class ObserverHandler {
	private onDataChangeListener mListener;
	private int LISTENER_TYPE = 0;
	private Uri mUri;
	private Context mContext;
	private MyObserver mObserver;

	public interface onDataChangeListener {
		public void onChange(Uri uri);

		public Context getContext();
	}

	public ObserverHandler(Uri uri, onDataChangeListener listener) {
		mListener = listener;
		mUri = uri;
		mContext = mListener.getContext();
	}

	public void registerObserver() {
		mObserver = new MyObserver(handler);
		mContext.getContentResolver().registerContentObserver(mUri, true,
				mObserver);
	}

	public void unRegisterObserver() {
		mContext.getContentResolver().unregisterContentObserver(mObserver);
	}

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == LISTENER_TYPE) {
				mListener.onChange(mUri);
			}
		}
	};

	class MyObserver extends ContentObserver {

		public MyObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {

			handler.obtainMessage(LISTENER_TYPE).sendToTarget();
		}
	}
}
