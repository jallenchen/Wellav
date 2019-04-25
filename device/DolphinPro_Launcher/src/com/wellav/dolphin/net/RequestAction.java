package com.wellav.dolphin.net;


import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.utils.Util;

public class RequestAction {
	 static String code = "-1";
	
	public static String requestXml(String requestBody,final String request){
	
        XMLRequest xmlRequest = new XMLRequest(SysConfig.ServerUrl+request,
				requestBody, new Response.Listener<XmlPullParser>() {

					@Override
					public void onResponse(XmlPullParser response) {
						// TODO Auto-generated method stub
						code =  responseCode(response);
						Util.PrintLog("RequestAction", request+":"+code);
					}	
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("LOGIN-ERROR", error.getMessage(), error);
						//byte[] htmlBodyBytes = error.networkResponse.data;
						//Log.e("LOGIN-ERROR", new String(htmlBodyBytes), error);
					}
				});

		VolleyRequestQueueManager.addRequest(xmlRequest, null);
		
		return code;
	}
	
	public static String responseCode(XmlPullParser response){
		String code = "-1";
		try {
			int eventType = response.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {

				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					 if (nodeName.equals("n:Code")) {
						 code = response.nextText();
					} else if (nodeName.equals("n:Description")){
					} else if (nodeName.equals("n:UserID")){
						return response.nextText();
					} 
					break;
				case XmlPullParser.END_TAG:
					
					break;
				}
				eventType = response.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return code;
	}
}
