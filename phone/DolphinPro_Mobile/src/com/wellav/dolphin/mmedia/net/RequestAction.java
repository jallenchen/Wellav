package com.wellav.dolphin.mmedia.net;


import org.xmlpull.v1.XmlPullParser;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.imagelru.VolleyRequestQueueManager;
import com.wellav.dolphin.mmedia.commands.SysConfig;

public class RequestAction {
	 static String code = "-1";
	
	public static String requestXml(String requestBody,final String request){
	
        XMLRequest xmlRequest = new XMLRequest(SysConfig.ServerUrl+request,
        		//"https://www.baidu.com/",
				requestBody, new Response.Listener<XmlPullParser>() {
					@Override
					public void onResponse(XmlPullParser response) {
						// TODO Auto-generated method stub
						Log.e("RequestAction", request);
						
						code =  XMLParser.responseCode(response);
						
					}	
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("LOGIN-ERROR", error.getMessage(), error);
						byte[] htmlBodyBytes = error.networkResponse.data;
						Log.e("LOGIN-ERROR", new String(htmlBodyBytes), error);
					}
				});

		VolleyRequestQueueManager.addRequest(xmlRequest, null);
		
		return code;
	}
	
	public static void requestXmlMsg(String requestBody, final String request) {

		XMLRequest xmlRequest = new XMLRequest(SysConfig.ServerUrl + request,
				requestBody, new Response.Listener<XmlPullParser>() {

					@Override
					public void onResponse(XmlPullParser response) {
						
					//	DolphinApp.getInstance().setMessagesToDB(XMLParser.ParseXMLMessage(response));
						Log.e("requestXmlMsg", XMLParser.GetMessageBoxCode);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("LOGIN-ERROR", error.getMessage(), error);
						// byte[] htmlBodyBytes = error.networkResponse.data;
						// Log.e("LOGIN-ERROR", new String(htmlBodyBytes),
						// error);
					}
				});

		VolleyRequestQueueManager.addRequest(xmlRequest, null);

	}

}
