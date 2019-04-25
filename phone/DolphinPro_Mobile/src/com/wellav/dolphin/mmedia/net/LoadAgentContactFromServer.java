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
import com.wellav.dolphin.mmedia.entity.AgentContact;

public class LoadAgentContactFromServer {
	

	String mBody;
	String mRequest;
	Handler handler;
	int Code = -1;

	public LoadAgentContactFromServer(String body) {
		// TODO Auto-generated constructor stub
		mBody = body;
		mRequest = "GetDolphinAgentContact";
	}

	@SuppressLint("HandlerLeak")
	public void getAgentContact(final AgentContactDataCallBack dataCallBack) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Code && dataCallBack != null) {
					AgentContact info = (AgentContact) msg.obj;
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
						AgentContact info = ParseXMLAgentContact(response);
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

	public AgentContact ParseXMLAgentContact(XmlPullParser response) {
		AgentContact agentContact = new AgentContact();
		try {
			int eventType = response.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("GetDolphinAgentContactResponse")) {
					} else if (nodeName.equals("n:Code")) {
						Code = Integer.valueOf(response.nextText());
					} else if (nodeName.equals("n:AgentContactUserID")) {
						agentContact.setUserID(response.nextText());
					} else if (nodeName.equals("n:NickName")) {
						agentContact.setUserNickname(response.nextText());
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

		return agentContact;
	}

	/**
	 * 网路访问调接口
	 * 
	 */
	public interface AgentContactDataCallBack {
		void onDataCallBack(int code, AgentContact data);
	}



}
