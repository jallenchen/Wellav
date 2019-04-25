package com.wellav.dolphin.mmedia.net;

import java.io.IOException;

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
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.utils.BitmapCache;
import com.wellav.dolphin.mmedia.utils.BitmapUtils;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.FileUtil;
import com.wellav.dolphin.mmedia.utils.PinYinManager;

public class LoadFamilyUserFromServer {
	String mBody;
	String mRequest;
	Handler handler ;
	int Code=-1;
    // 一级内存缓存基于 LruCache
    private BitmapCache bitmapCache;
    // 二级文件缓存
    private FileUtil fileUtil;
    
	public LoadFamilyUserFromServer(String body) {
		// TODO Auto-generated constructor stub
		mBody = body;
		mRequest = "GetFamilyUserInfo";
		bitmapCache = new BitmapCache();
	    fileUtil = new FileUtil();
	}
	
	  @SuppressLint("HandlerLeak")
	    public void getFamilyUserData(final familyUserDataCallBack dataCallBack) {
	        handler = new Handler() {
	            @Override
	            public void handleMessage(Message msg) {
	                if (msg.what == Code && dataCallBack != null) {
						FamilyUserInfo info = (FamilyUserInfo) msg.obj;
	                    if (info != null) {
	                    	SQLiteManager.getInstance().saveFamilyUserInfo(info, true);
	                        dataCallBack.onDataCallBack(Code,info);

	                    } else {

	                    }
	                }
	            }
	        };
	        
	        new Thread(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
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
						// TODO Auto-generated method stub
						FamilyUserInfo  info =  ParseXMLFamilyUser(response);
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
	
	public  FamilyUserInfo ParseXMLFamilyUser(XmlPullParser response) {
		FamilyUserInfo familyUserInfo = null;  
		try {
			
			int eventType = response.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
//                     break;
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:GetFamilyUserInfoResponse")) {
					}else if (nodeName.equals("n:Code")) {
						Code = Integer.parseInt(response.nextText());
					} else if (nodeName.equals("n:Users")){
						familyUserInfo = new FamilyUserInfo();
					} else if(familyUserInfo != null){
						 if (nodeName.equals("n:FamilyID")) {
							 familyUserInfo.setFamilyID(response.nextText());
							} else if (nodeName.equals("n:UserID")) {
								familyUserInfo.setUserID(response.nextText());
							} else if (nodeName.equals("n:Authority")) {
								familyUserInfo.setAuthority(response.nextText());;
							} else if (nodeName.equals("n:LoginName")) {
								familyUserInfo.setLoginName(response.nextText());
							} else if (nodeName.equals("n:DeviceType")) {
								familyUserInfo.setDeviceType(response.nextText());
							} else if (nodeName.equals("n:NoteName")) {
								String strutf8 = response.nextText();
							 	familyUserInfo.setNoteName(strutf8);
							} else if (nodeName.equals("n:Sex")) {
							
							 	familyUserInfo.setSex(response.nextText());
							}  else if (nodeName.equals("n:Birthday")) {
								familyUserInfo.setBirthday(response.nextText());
							}else if (nodeName.equals("n:Nickname")) {
							
							 	String name = response.nextText();
							 	familyUserInfo.setNickName(name);
								
							} else if (nodeName.equals("n:Icon")) {
								String icon = response.nextText();
								if(icon != null){
									familyUserInfo.setIcon(familyUserInfo.getLoginName()+".jpg");
									saveAvatar(icon,familyUserInfo.getLoginName());
								}
							}else if(nodeName.equals("n:City")){
								familyUserInfo.setCity(response.nextText());
							}
						 
					}
					break;
				case XmlPullParser.END_TAG:
					if (response.getName().equalsIgnoreCase("n:Users") && familyUserInfo != null) {
						 String[] pinyin = PinYinManager.toPinYin(familyUserInfo.getNoteName());
						 familyUserInfo.setFirstChar(pinyin[0]);
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
		return familyUserInfo;
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
    public interface familyUserDataCallBack {
        void onDataCallBack(int code,FamilyUserInfo data);
    }
}
