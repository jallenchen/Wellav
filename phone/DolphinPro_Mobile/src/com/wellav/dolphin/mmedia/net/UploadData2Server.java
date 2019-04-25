package com.wellav.dolphin.mmedia.net;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class UploadData2Server {
	String mBody;
	String mRequest;
	Handler handler;
	static int Code= -1;

	public UploadData2Server(String body,String request) {
		// TODO Auto-generated constructor stub
		mBody = body;
		mRequest = request;
	}
	
	
	@SuppressLint("HandlerLeak")
	public void getData(final codeDataCallBack dataCallBack) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Code && dataCallBack != null) {
					int  code = msg.what;
					CommFunc.PrintLog("codeDataCallBack", "code:"+code);
						dataCallBack.onDataCallBack(mRequest,code);
				}
			}
		};

		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				requestXml(mBody, mRequest);

			}

		}.start();

	}
	
	public void requestXml(String requestBody, final String request) {

		XMLRequest xmlRequest = new XMLRequest(SysConfig.ServerUrl + request,
		// "https://www.baidu.com/",
				requestBody, new Response.Listener<XmlPullParser>() {

					@Override
					public void onResponse(XmlPullParser response) {
						// TODO Auto-generated method stub
						Message msg = new Message();
						msg.what = responseCode(response);
						handler.sendMessage(msg);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("LOGIN-ERROR", error.getMessage(), error);
					}
				});

		VolleyRequestQueueManager.addRequest(xmlRequest, null);
	}
	
	public int  responseCode(XmlPullParser response){
		try {
			int eventType = response.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {

				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					 if (nodeName.equals("n:Code")) {
						 Code = Integer.valueOf(response.nextText());
					} else if (nodeName.equals("n:Description")){
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
		return Code;
		
	}
	/**
	 * 网路访问调接口
	 * 
	 */
	public interface codeDataCallBack {
		void onDataCallBack(String request,int code);
	}
}
