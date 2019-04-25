package com.wellav.dolphin.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.bean.UserInfo;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.utils.BitmapCache;
import com.wellav.dolphin.utils.BitmapUtils;
import com.wellav.dolphin.utils.FileUtil;
import com.wellav.dolphin.utils.PinYinManager;

public class LoadUsersFromServer {
	String mBody;
	String mRequest;
	Handler handler;
	static int Code= -1;
    // 一级内存缓存基于 LruCache
    private BitmapCache bitmapCache;
    // 二级文件缓存
    private FileUtil fileUtil;
	
	public LoadUsersFromServer(String body) {
		mBody = body;
		mRequest = "GetUserInfo";
		bitmapCache = new BitmapCache();
	    fileUtil = new FileUtil();
	}
	
	@SuppressLint("HandlerLeak")
	public void getData(final userDataCallBack dataCallBack) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Code && dataCallBack != null) {
					UserInfo  info = (UserInfo) msg.obj;
					if (info != null) {
						dataCallBack.onDataCallBack(Code,info);

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
						UserInfo info = ParseXMLGetUserInfo(response);
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
	
	
	public   UserInfo ParseXMLGetUserInfo(XmlPullParser response) {
		UserInfo UserInfo = null;  
       
		try {
			int eventType = response.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
//				 case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理  
//					 familyUserInfoes = new ArrayList<FamilyUserInfo>();  
//                     break;
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:GetUserInfoResponse")) {
					}else if (nodeName.equals("n:Code")) {
						Code = Integer.valueOf(response.nextText());
					} else if (nodeName.equals("n:Users")){
						UserInfo = new UserInfo();
					} else if(UserInfo != null){
						 if (nodeName.equals("n:ID")) {
							 UserInfo.setUserID(response.nextText());
							} else if (nodeName.equals("n:LoginName")) {
								UserInfo.setLoginName(response.nextText());
							} else if (nodeName.equals("n:DeviceType")) {
								UserInfo.setDeviceType(response.nextText());
							} else if (nodeName.equals("n:NoteName")) {
								UserInfo.setNoteName(response.nextText());
							} else if (nodeName.equals("n:Sex")) {
								UserInfo.setSex(response.nextText());
							}  else if (nodeName.equals("n:Birthday")) {
								UserInfo.setBirthday(response.nextText());
							}else if (nodeName.equals("n:Nickname")) {
								UserInfo.setNickName(response.nextText());
								Log.e("n:Nickname", UserInfo.getNickName());
							} else if (nodeName.equals("n:Icon")) {
								String icon = response.nextText();
								if(icon != null){
									UserInfo.setIcon(UserInfo.getLoginName()+".jpg");
									saveAvatar(icon,UserInfo.getLoginName());
								}
							}else if(nodeName.equals("n:City")){
								UserInfo.setCity(response.nextText());
							}
					}
					break;
				case XmlPullParser.END_TAG:
					if (response.getName().equalsIgnoreCase("n:Users") && UserInfo != null) {
						 String[] pinyin = PinYinManager.toPinYin(UserInfo.getNoteName());
						 UserInfo.setFirstChar(pinyin[0]);
					}
					break;
				}

				eventType = response.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return UserInfo;
	}
	
	private void saveAvatar(String icon,String name){
		Bitmap bitmap = BitmapUtils.base64ToBitmap(icon);
        // 图片下载成功后缓存并执行回调刷新界面
        if (bitmap != null) {

            // 先缓存到内存
            bitmapCache.putBitmap(name, bitmap);
            // 缓存到文件系统
            fileUtil.saveBitmap(name+".jpg", bitmap);
        }
	}
	
	/**
	 * 网路访问调接口
	 * 
	 */
	public interface userDataCallBack {
		void onDataCallBack(int code,UserInfo data);
	}
}