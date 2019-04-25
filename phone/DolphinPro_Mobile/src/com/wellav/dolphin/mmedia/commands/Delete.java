package com.wellav.dolphin.mmedia.commands;

import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.net.XMLRequest;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * 
 * @date2016年1月11日
 * @author Cheng
 */
public class Delete {
	public interface OnDelSuccessLis{
		void onDelSuccess(List<PhotoInfo> delSrc);
	}
	
	
	/**
	 * @param url
	 * @param msg
	 * @param v1触发请求的操作空间
	 * @param v2请求进度控件
	 * @return
	 */

	public static  XMLRequest newXMLRequest(final String url, String msg,
			final View v1, final View v2,final List<PhotoInfo> delSrc,final OnDelSuccessLis lis) {
		v1.setEnabled(false);
		v2.setVisibility(View.VISIBLE);
		return new XMLRequest(url, msg, new Response.Listener<XmlPullParser>() {
			@Override
			public void onResponse(XmlPullParser response) {
				// TODO Auto-generated method stub
				try {
					int eventType = response.getEventType();
					boolean result = false;
					while (eventType != XmlPullParser.END_DOCUMENT) {

						switch (eventType) {
						case XmlPullParser.START_TAG:
							String nodeName = response.getName();
							if (nodeName.equals("n:Code")) {
								result = response.nextText().equals("0");
								Log.i("DelPhoto", "code=" + result);
							}
							break;
						case XmlPullParser.END_TAG:
							if (response.getName().equalsIgnoreCase("n:Code")) {

							}
							break;
						}
						if (result)
							break;
						eventType = response.next();
					}
					v1.setEnabled(true);
					v2.setVisibility(View.INVISIBLE);
					if(result)
					{
						
						lis.onDelSuccess(delSrc);
					}
					

				} catch (Exception e) {
					v1.setEnabled(true);
					v2.setVisibility(View.INVISIBLE);
				}
			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.i("DelPhoto", "error=" + error.getMessage());
				v1.setEnabled(true);
				v2.setVisibility(View.INVISIBLE);
			}
		});
	}

}
