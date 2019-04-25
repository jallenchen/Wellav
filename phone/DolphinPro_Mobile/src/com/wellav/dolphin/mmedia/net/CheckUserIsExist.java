package com.wellav.dolphin.mmedia.net;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.CheckExistData;

public class CheckUserIsExist {
	String mBody;
	String mRequest;
	Handler handler ;
	int Code =-1;
	
	public CheckUserIsExist(String body) {
		mBody = body;
		mRequest = "RegisterUserIfExist";
	}
	
	public void isCheckUserExist(final checkDataCallBack dataCallBack){
		 handler = new Handler() {
	            @Override
	            public void handleMessage(Message msg) {
	                if (msg.what == Code && dataCallBack != null) {
	                	CheckExistData info = (CheckExistData) msg.obj;
	                    if (info != null) {
	                        dataCallBack.onDataCallBack(info);
	                    } else {

	                    }
	                }
	            }
	        };
	        new Thread(){
				@Override
				public void run() {
					requestXml(mBody,mRequest);
				}
	        }.start();
	}
	
	public void requestXml(String requestBody, final String request) {

		XMLRequest xmlRequest = new XMLRequest(SysConfig.ServerUrl + request,
		// "https://www.baidu.com/",
				requestBody, new Response.Listener<XmlPullParser>() {
					@Override
					public void onResponse(XmlPullParser response) {
						CheckExistData  infos =  isUserExist(response);
						 Message msg = new Message();
                        msg.what = infos.getCode();
                        msg.obj = infos;
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
	
	public  CheckExistData isUserExist(XmlPullParser response){
		CheckExistData user = new CheckExistData(); 
		try {
			int eventType = response.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {

				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					 if (nodeName.equals("n:Code")) {
						 Code  = Integer.valueOf(response.nextText());
						 user.setCode(Code);
					} else if (nodeName.equals("n:IfExist")){
						boolean isExist = Integer.valueOf(response.nextText())==1?true :false;
						user.setExist(isExist);
					} else if(nodeName.equals("n:UserID")){
						user.setUserID(response.nextText());
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
		return user;
	}

	
	   /**
  * 网路访问调接口
  * 
  */
 public interface checkDataCallBack {
     void onDataCallBack(CheckExistData data);
 }
	
}
