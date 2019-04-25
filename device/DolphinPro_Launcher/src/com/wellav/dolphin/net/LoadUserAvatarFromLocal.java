package com.wellav.dolphin.net;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.utils.BitmapCache;
import com.wellav.dolphin.utils.BitmapUtils;
import com.wellav.dolphin.utils.FileUtil;

public class LoadUserAvatarFromLocal {
	// 一级内存缓存基于 LruCache
    private BitmapCache bitmapCache;
    // 二级文件缓存
    private FileUtil fileUtil;
	Handler handler;
	static int Code= -1;
    
    public LoadUserAvatarFromLocal() {
 		// TODO Auto-generated constructor stub
    	 bitmapCache = new BitmapCache();
         fileUtil = new FileUtil();
 	}
    
    public Bitmap loadImage(String name){
    	String filepath = fileUtil.getAbsolutePath() + name+".jpg";
    	 // 先从内存中拿
        Bitmap bitmap = bitmapCache.getBitmap(name);

        if (bitmap != null) {
          //  Util.PrintLog("loadImage", name+":image exists in memory");

            return bitmap;
        }

        // 从文件中找
        if (fileUtil.isBitmapExists(name+".jpg")) {
        //	Util.PrintLog("loadImage", name+":image exists in file");

            bitmap = BitmapFactory.decodeFile(filepath);
            // 先缓存到内存
            bitmapCache.putBitmap(name, bitmap);
            return bitmap;

        }
        
        return null;
    }
    
	@SuppressLint("HandlerLeak")
	public void getData(final String userid,final userAvatarDataCallBack dataCallBack) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Code && dataCallBack != null) {
					 Bitmap bitmap = (Bitmap) msg.obj;
					if (bitmap != null) {
						dataCallBack.onDataCallBack(Code,bitmap);

					} else {

					}
				}
			}
		};

		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String 	userInfo = XMLBody.getUserInfo(SysConfig.DolphinToken,userid);
				requestXml(userInfo, "GetUserInfo");
			}

		}.start();

	}
    
	private Bitmap saveAvatar(String icon,String name){
		Bitmap bitmap = BitmapUtils.base64ToBitmap(icon);
        // 图片下载成功后缓存并执行回调刷新界面
        if (bitmap != null) {

            // 先缓存到内存
            bitmapCache.putBitmap(name, bitmap);
            // 缓存到文件系统
            fileUtil.saveBitmap(name+".jpg", bitmap);
        }
        
        return bitmap;
	}
    
	public void requestXml(String requestBody, final String request) {

		XMLRequest xmlRequest = new XMLRequest(SysConfig.ServerUrl + request,
		// "https://www.baidu.com/",
				requestBody, new Response.Listener<XmlPullParser>() {

					@Override
					public void onResponse(XmlPullParser response) {
						// TODO Auto-generated method stub
						Bitmap avatar = ParseXMLGetUserInfo(response);
						Message msg = new Message();
						msg.what = Code;
						msg.obj = avatar;
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
    
    public  Bitmap ParseXMLGetUserInfo(XmlPullParser response) {
		Bitmap avatar = null;
		String name = null;
       
		try {
			int eventType = response.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:LoginName")) {
						name = response.nextText();
					}else if (nodeName.equals("n:Code")) {
						Code = Integer.valueOf(response.nextText());
					}else if (nodeName.equals("n:Icon")) {
						String icon = response.nextText();
						if(icon != null){
							avatar = saveAvatar(icon,name);
						}
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
		return avatar;
	}

    /**
	 * 网路访问调接口
	 * 
	 */
	public interface userAvatarDataCallBack {
		void onDataCallBack(int code,Bitmap avatar);
	}
}
