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
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.AdminInfo;

public class LoadDeviceAdmin {
	String mBody;
	String mRequest;
	Handler handler;
	int Code = -1;

	public LoadDeviceAdmin(String body) {
		// TODO Auto-generated constructor stub
		mBody = body;
		mRequest = "GetDeviceAdmin";
	}

	@SuppressLint("HandlerLeak")
	public void getAdminData(final deviceAdminDataCallBack dataCallBack) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Code && dataCallBack != null) {
					AdminInfo info = (AdminInfo) msg.obj;
					if (info != null) {
						dataCallBack.onDataCallBack(Code, info);
					} else {

					}
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
						AdminInfo info = ParseXMLAdmin(response);
						Message msg = new Message();
						msg.what = Code;
						msg.obj = info;
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

	public AdminInfo ParseXMLAdmin(XmlPullParser response) {
		AdminInfo adminInfo = new AdminInfo();
		try {
			int eventType = response.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:GetDeviceAdminResponse")) {
					} else if (nodeName.equals("n:Code")) {
						Code = Integer.valueOf(response.nextText());
					} else if (nodeName.equals("n:FamilyID")) {
						adminInfo.setFamilyId(response.nextText());
					} else if (nodeName.equals("n:AdminUserID")) {
						adminInfo.setAdminUserId(response.nextText());
					} else if (nodeName.equals("n:AdminUserName")) {
						adminInfo.setAdminUserName(response.nextText());
					}else if (nodeName.equals("n:DeviceUserID")) {
						adminInfo.setDeviceUserId(response.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.END_DOCUMENT:

					break;

				}

				eventType = response.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return adminInfo;
	}

	/**
	 * 网路访问调接口
	 * 
	 */
	public interface deviceAdminDataCallBack {
		void onDataCallBack(int code, AdminInfo data);
	}

}
