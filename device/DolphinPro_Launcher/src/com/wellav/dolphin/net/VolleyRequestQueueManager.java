package com.wellav.dolphin.net;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.wellav.dolphin.application.LauncherApp;

public class VolleyRequestQueueManager {
	public static final String TAG = "volley";
	private static RequestQueue mRequestQueue = null;

	public static RequestQueue newInstance() {
		if (mRequestQueue == null) {
			mRequestQueue = MyVolley.newRequestQueue(LauncherApp.getInstance());
			mRequestQueue.start();
		}
		return mRequestQueue;
	}

	public static void addRequest(Request<?> request, Object tag) {

		request.setTag(tag == null ? TAG : tag);

		newInstance().add(request);
	}

	public static void cancleRequest(Object tag) {
		newInstance().cancelAll(tag == null ? TAG : tag);
	}

	public static void release(Object tag) {
		cancleRequest(tag);
		newInstance().stop();
		mRequestQueue = null;
	}
}
