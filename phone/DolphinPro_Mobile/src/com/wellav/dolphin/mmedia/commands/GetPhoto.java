package com.wellav.dolphin.mmedia.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.os.AsyncTask;
import android.util.Log;

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
public class GetPhoto {
	public interface OnGetSuccessLis {
		void onGetSuccess(List<PhotoInfo> getSrc);
	}

	/**
	 * @param url
	 * @param msg
	 * @param lis
	 * @return
	 */
	public static XMLRequest newXMLRequest(final String url, String msg,
			final OnGetSuccessLis lis, final Map<String, Integer> sectionMap) {
		return new XMLRequest(url, msg, new Response.Listener<XmlPullParser>() {
			@Override
			public void onResponse(final XmlPullParser response) {
				// TODO Auto-generated method stub
				new AsyncTask<Void, Void, Void>() {
					List<PhotoInfo> mGridList = new ArrayList<PhotoInfo>();

					@Override
					protected Void doInBackground(Void... params) {

						try {

							int eventType = response.getEventType();

							PhotoInfo photoInfo = null;
							while (eventType != XmlPullParser.END_DOCUMENT) {
								switch (eventType) {

								case XmlPullParser.START_TAG:
									String nodeName = response.getName();

									if (nodeName.equals("n:Photos")) {
										photoInfo = new PhotoInfo();
									} else if (nodeName.equals("n:ID")) {
										photoInfo.setID(response.nextText());
									} else if (nodeName.equals("n:FamilyID")) {
										photoInfo.setFamilyID(response
												.nextText());
									} else if (nodeName.equals("n:IsFavourite")) {
										String s = response.nextText();
										photoInfo.setmIsFavor(!s.equals("0"));
									} else if (nodeName.equals("n:Description")) {
										photoInfo.setmDesc(response.nextText());
									} else if (nodeName.equals("n:Url")) {
										photoInfo.setmUrl(response.nextText());
										// Log.e("","u="+photoInfo.getmUrl());
									} else if (nodeName
											.equals("n:ThumbnailUrl")) {
										photoInfo.setmThumbnail(response
												.nextText());
									} else if (nodeName.equals("n:UploadTime")) {
										String tim = response.nextText()
												.replaceAll("[T]|(.000Z)", " ");
										photoInfo.setmTime(tim);
										String key = tim.substring(0, 10);
										if (sectionMap != null) {
											if (!sectionMap.containsKey(key)) {
												int secMax = sectionMap.size() + 1;
												sectionMap.put(key, secMax + 1);
											}

											photoInfo.setmSection(sectionMap
													.get(key));
										}
									}
									break;
								case XmlPullParser.END_TAG:
									if (response.getName().equalsIgnoreCase(
											"n:Photos")
											&& photoInfo != null) {
										mGridList.add(photoInfo);
										photoInfo = null;
									}
									break;
								}

								eventType = response.next();
							}

						} catch (Exception e) {
							Log.e("", "err=" + e.toString());
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						if(lis!=null)
							lis.onGetSuccess(mGridList);
					}
				}.execute(new Void[] {});

			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("getPhoto", "error=" + error.toString());
				if(lis!=null)
					lis.onGetSuccess(null);

			}
		});
	}
}
