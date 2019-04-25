package com.wellav.dolphin.mmedia.commands;

import org.xmlpull.v1.XmlPullParser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.net.XMLRequest;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * 
 * @date2016年1月8日
 * @author Cheng
 */
public class Collect_CancleCollect {
	/**
	 * @param url
	 * @param msg
	 * @param v1触发请求的操作空间
	 * @param v2请求进度控件
	 * @return
	 */
	public static XMLRequest newXMLRequest(final String url, String msg,
			final ImageView v1, final View v2,final PhotoInfo pi) {
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
								String code=response.nextText();
								result = code.equals("0");
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
					if(result)
					{
						boolean flag=url.contains("Cancel");
						pi.setmIsFavor(!flag);
						Bitmap bitmap=BitmapFactory.decodeResource(HBaseApp
								.getInstance().getResources(),  flag? R.drawable.ic_like_off_32dp
										: R.drawable.ic_like_on_32dp);
						v1.setImageBitmap(bitmap);
					}
					
					v2.setVisibility(View.INVISIBLE);

				} catch (Exception e) {
					Log.e("", "e="+e.toString());
					v1.setEnabled(true);
					v2.setVisibility(View.INVISIBLE);
				}
			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("", "error="+error.toString());
				v1.setEnabled(true);
				v2.setVisibility(View.INVISIBLE);
			}
		});
	}
}
