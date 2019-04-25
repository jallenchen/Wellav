package com.wellav.dolphin.net;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.bean.VoipSubAccount;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.net.LoadFamilyUserFromServer.familyUserDataCallBack;
import com.wellav.dolphin.utils.PinYinManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LoadVoipSubAccount {
	String mBody;
	String mRequest;
	Handler handler ;
	int Code =-1;
	
	
	public LoadVoipSubAccount(Context ct,String body) {
		mBody = body;
		mRequest = "GetVOIPSubAccount";
	}
	

	  @SuppressLint("HandlerLeak")
	    public void getVoipAccData(final voipAccDataCallBack dataCallBack) {
	        handler = new Handler() {
	            @Override
	            public void handleMessage(Message msg) {
	                if (msg.what == Code && dataCallBack != null) {
	                	VoipSubAccount info = (VoipSubAccount) msg.obj;
	                    if (info != null) {
	                    	
	                        dataCallBack.onDataCallBack(Code,info);
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
	  
	  public void  requestXml(String requestBody,final String request){
	        XMLRequest xmlRequest = new XMLRequest(SysConfig.ServerUrl+request,
	        		//"https://www.baidu.com/",
					requestBody, new Response.Listener<XmlPullParser>() {
						@Override
						public void onResponse(XmlPullParser response) {
							VoipSubAccount  infos =  ParseXMLVoipAcc(response);
							 Message msg = new Message();
	                         msg.what = Code;
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
	
		public  VoipSubAccount ParseXMLVoipAcc(XmlPullParser response) {
			VoipSubAccount voipSubAccount = null;  
			try {
				int eventType = response.getEventType();
				
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:
						String nodeName = response.getName();
						if (nodeName.equals("n:GetVOIPSubAccountResponse")) {
						}else if (nodeName.equals("n:Code")) {
							Code = Integer.valueOf(response.nextText());
						} else if (nodeName.equals("n:SubToken")){
							voipSubAccount = new VoipSubAccount();
							voipSubAccount.setSubToken(response.nextText());
						} else if (nodeName.equals("n:VoipAccount")){
							voipSubAccount.setVoipAcc(response.nextText());
						} else if (nodeName.equals("n:VoipPwd")){
							voipSubAccount.setMVoipPsw(response.nextText());
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
			return voipSubAccount;
		}
		
	
	   /**
     * 网路访问调接口
     * 
     */
    public interface voipAccDataCallBack {
        void onDataCallBack(int code ,VoipSubAccount data);
    }
	
}
