package com.wellav.dolphin.mmedia.commands;

import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.mmedia.entity.PhotoInfo_grpByUsr;
import com.wellav.dolphin.mmedia.net.XMLRequest;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * @date2016年2月23日
 * @author Cheng
 */
public class GetUserPhotoCount {
	public interface OnGetUserPhotoCountListener{
		
		/**
		 * @param ds 如果为null这表示服务器异常
		 */
		public void getUserPhotoCount(List<PhotoInfo_grpByUsr> ds);
	}
	/**
	 * @param url
	 * @param msg
	 * @param lis
	 * @return
	 */
	public static XMLRequest newXMLRequest(final String url, String msg,final OnGetUserPhotoCountListener lis) {		
		return new XMLRequest(url, msg, new Response.Listener<XmlPullParser>() {
			@Override
			public void onResponse(final XmlPullParser response) {
				// TODO Auto-generated method stub
				new AsyncTask<Void, Void, Void>() {
					List<PhotoInfo_grpByUsr> ds = new LinkedList<PhotoInfo_grpByUsr>();

					@Override
					protected Void doInBackground(Void... params) {
						try {
							
							int eventType = response.getEventType();

							PhotoInfo_grpByUsr photoInfo_grpByUsr = null;
							while (eventType != XmlPullParser.END_DOCUMENT) {
								switch (eventType) {

								case XmlPullParser.START_TAG:
									String nodeName = response.getName();
									
									if (nodeName.equals("n:UserPhotoCounts")) {
										photoInfo_grpByUsr=new PhotoInfo_grpByUsr();
									} else if (nodeName.equals("n:UserID")) {
										photoInfo_grpByUsr.setmUsrId(response.nextText());
									} else if (nodeName.equals("n:Count")) {
										photoInfo_grpByUsr.setmCount(Integer.parseInt(response
												.nextText()));
									} else if (nodeName.equals("n:ThumbnailUrl")) {
										photoInfo_grpByUsr.setmThumbUrl(response.nextText());
									} 
									break;
								case XmlPullParser.END_TAG:
									if (response.getName().equalsIgnoreCase(
											"n:UserPhotoCounts")
											&& photoInfo_grpByUsr != null) {
										ds.add(photoInfo_grpByUsr);
										photoInfo_grpByUsr = null;
									}
									break;
								}

								eventType = response.next();
							}

						} catch (Exception e) {
							Log.e("", "err="+e.getMessage());
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						lis.getUserPhotoCount(ds);
					}
				}.execute(new Void[] {});

			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("getPhoto", "error=" + error.toString());
				lis.getUserPhotoCount(null);
				
			}
		});
	}
}
