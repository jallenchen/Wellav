package com.wellav.dolphin.mmedia.commands;

import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.mmedia.entity.PhotoInfo_grpByDay;
import com.wellav.dolphin.mmedia.net.XMLRequest;
import com.wellav.dolphin.mmedia.utils.DateUtils;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * @date2016年3月15日
 * @author Cheng
 */
public class GetSecurityPhotoGroup {
	private static final String TAG="GetSecurityPhotoGroup";
	public interface OnGetNewestSecurityPhotoGroupListener{
		public void onGetNewestSecurityPhotoGroup(List<PhotoInfo_grpByDay> getSrc);
	}
	/**
	 * @param url
	 * @param msg
	 * @param lis
	 * @return
	 */
	public static XMLRequest newXMLRequest(final String url, String msg,
			final OnGetNewestSecurityPhotoGroupListener lis) {
		return new XMLRequest(url, msg, new Response.Listener<XmlPullParser>() {
			@Override
			public void onResponse(final XmlPullParser response) {
				// TODO Auto-generated method stub
				new AsyncTask<Void, Void, Void>() {
					List<PhotoInfo_grpByDay> mGridList = new LinkedList<PhotoInfo_grpByDay>();
					
					
					@Override
					protected Void doInBackground(Void... params) {
						
						try {

							int eventType = response.getEventType();

							PhotoInfo_grpByDay photoInfo = null;
							while (eventType != XmlPullParser.END_DOCUMENT) {
								switch (eventType) {

								case XmlPullParser.START_TAG:
									String nodeName = response.getName();

									if (nodeName.equals("n:SecurityPhotoGroups")) {
										photoInfo = new PhotoInfo_grpByDay();
									} else if (nodeName.equals("n:UploadTime")) {
										String tim = DateUtils.toYYYYMMDD(response
												.nextText());
										photoInfo.setDate(tim);
									} else if (nodeName.equals("n:ThumbnailUrl")) {
										photoInfo.setThumbnial(response
												.nextText());
									} 
									break;
								case XmlPullParser.END_TAG:
									if (response.getName().equalsIgnoreCase(
											"n:SecurityPhotoGroups")
											&& photoInfo != null) {
										mGridList.add(photoInfo);
										photoInfo = null;
									}
									break;
								}

								eventType = response.next();
							}

						} catch (Exception e) {
							Log.e(TAG, "err=" + e.getMessage());
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						lis.onGetNewestSecurityPhotoGroup(mGridList);;
					}
				}.execute(new Void[] {});

			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "error=" + error.toString());
				lis.onGetNewestSecurityPhotoGroup(null);

			}
		});
	}
}
