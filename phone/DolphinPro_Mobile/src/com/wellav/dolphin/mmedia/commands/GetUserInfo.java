package com.wellav.dolphin.mmedia.commands;

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
 * @date2016年2月26日
 * @author Cheng
 */
public class GetUserInfo {
	public interface OnGetUserInfoListener{
		public void onGetUserInfo(List<PhotoInfo_grpByUsr> ds);
	}
	public static XMLRequest newXMLRequest(final List<PhotoInfo_grpByUsr> ds,final String url, String msg,final OnGetUserInfoListener lis) {		
		return new XMLRequest(url, msg, new Response.Listener<XmlPullParser>() {
			@Override
			public void onResponse(final XmlPullParser response) {
				// TODO Auto-generated method stub
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						try {
							
							int eventType = response.getEventType();

							PhotoInfo_grpByUsr photoInfo_grpByUsr = null;
							while (eventType != XmlPullParser.END_DOCUMENT) {
								switch (eventType) {

								case XmlPullParser.START_TAG:
									String nodeName = response.getName();
									
									if (nodeName.equals("n:UserInfo")) {
										photoInfo_grpByUsr=new PhotoInfo_grpByUsr();
									} else if (nodeName.equals("n:ID")) {
										photoInfo_grpByUsr.setmUsrId(response.nextText());
									} else if (nodeName.equals("n:NoteName")) {
										photoInfo_grpByUsr.setmUsrNam(response.nextText());
									} 
									break;
								case XmlPullParser.END_TAG:
									if (response.getName().equalsIgnoreCase(
											"n:UserInfo")
											&& photoInfo_grpByUsr != null) {
										for(int i=0;i<ds.size();i++)
										{
											if(ds.get(i).getmUsrId().equals(photoInfo_grpByUsr.getmUsrId()))
											{
												ds.get(i).setmUsrNam(photoInfo_grpByUsr.getmUsrNam());
												break;
											}
										}
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
						lis.onGetUserInfo(ds);
					}
				}.execute(new Void[] {});

			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("getPhoto", "error=" + error.toString());
				lis.onGetUserInfo(null);
				
			}
		});
	}
}
