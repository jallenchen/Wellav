package com.wellav.dolphin.mmedia.net;

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
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.entity.AccountInfo;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.utils.BitmapCache;
import com.wellav.dolphin.mmedia.utils.BitmapUtils;
import com.wellav.dolphin.mmedia.utils.FileUtil;

public class LoadAccountInfoFromServer {
	String mBody;
	String mRequest;
	Handler handler;
	static int Code= -1;
    // 一级内存缓存基于 LruCache
    private BitmapCache bitmapCache;
    // 二级文件缓存
    private FileUtil fileUtil;

	public LoadAccountInfoFromServer(String body) {
		// TODO Auto-generated constructor stub
		mBody = body;
		mRequest = "NeteaseLogin";
		bitmapCache = new BitmapCache();
	    fileUtil = new FileUtil();
	}

	@SuppressLint("HandlerLeak")
	public void getData(final DataCallBack dataCallBack) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Code && dataCallBack != null) {
					AccountInfo info = (AccountInfo) msg.obj;
					if (info != null) {
						DolphinApp.getInstance().setAccountInfo(info);
						dataCallBack.onDataCallBack(Code,info);

					} else {

					}
				}else{
					dataCallBack.onDataCallBack(msg.what,null);
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
						AccountInfo info = ParseXMLLogin(response);
						Message msg = new Message();
						msg.what = Code;
						msg.obj = info;
						handler.sendMessage(msg);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Message msg = new Message();
						msg.what = -Code;
						handler.sendMessage(msg);
						Log.e("LOGIN-ERROR", error.getMessage(), error);
						//byte[] htmlBodyBytes = error.networkResponse.data;
						//Log.e("LOGIN-ERROR", new String(htmlBodyBytes), error);
					}
				});

		VolleyRequestQueueManager.addRequest(xmlRequest, null);
	}

	public  AccountInfo ParseXMLLogin(XmlPullParser response) {
		AccountInfo info = new AccountInfo();
		List<FamilyInfo> familys = new ArrayList<FamilyInfo>();
		try {
			int eventType = response.getEventType();
			FamilyInfo family = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {

				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:NeteaseLoginResponse")) {
						familys = new ArrayList<FamilyInfo>();  
					} else if (nodeName.equals("n:Code")) {
						Code = Integer.parseInt(response.nextText());
					} else if (nodeName.equals("n:Token")) {
						SysConfig.dtoken = response.nextText();
						info.setToken(SysConfig.dtoken);
					} else if (nodeName.equals("n:NeteaseToken")) {
						SysConfig.rtoken = response.nextText();
						info.setCapabilityToken(SysConfig.rtoken);
					} else if (nodeName.equals("n:UserID")) {
						SysConfig.userid = response.nextText();
						info.setUserId(SysConfig.userid);
					} else if (nodeName.equals("n:Familys")) {
						family = new FamilyInfo();
					} else if (family != null) {
						if (nodeName.equals("n:FamilyID")) {
							family.setFamilyID(response.nextText());
						} else if (nodeName.equals("n:DeviceUserID")) {
							family.setDeviceUserID(response.nextText());
						} else if (nodeName.equals("n:MangerID")) {
							family.setMangerID(response.nextText());
						} else if (nodeName.equals("n:DeviceID")) {
							family.setDeviceID(response.nextText());
						} else if (nodeName.equals("n:Authority")) {
							family.setAuthority(response.nextText());
						} else if (nodeName.equals("n:Nickname")) {
							family.setNickName(response.nextText());
						} else if (nodeName.equals("n:Note")) {
							family.setNote(response.nextText());
						} else if (nodeName.equals("n:City")) {
							family.setCity(response.nextText());
						} else if (nodeName.equals("n:Icon")) {
							String icon = response.nextText();
							if(icon != null){
								family.setDeviceIcon(family.getDeviceID()+".jpg");
								saveAvatar(icon,family.getDeviceID());
							}
							
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if (response.getName().equalsIgnoreCase("n:Familys")&& family != null) {
						if(info.getUserId().equals(family.getMangerID())){
							familys.add(0,family);
							DolphinApp.getInstance().getMyFamilyInfos().add(family);
						}else{
							familys.add(family);
						}
						
						family = null;
					}
					break;
				case XmlPullParser.END_DOCUMENT:

					break;

				}

				eventType = response.next();
			}

			DolphinApp.getInstance().setFamilyInfos(familys);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;
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
	public interface DataCallBack {
		void onDataCallBack(int code,AccountInfo data);
	}

}
