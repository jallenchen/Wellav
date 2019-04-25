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
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.utils.BitmapCache;
import com.wellav.dolphin.mmedia.utils.BitmapUtils;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.FileUtil;

public class LoadFamilysFromServer {
	String mBody;
	String mRequest;
	Handler handler;
	// 一级内存缓存基于 LruCache
	private BitmapCache bitmapCache;
	// 二级文件缓存
	private FileUtil fileUtil;
	int Code = -1;

	public LoadFamilysFromServer(String body) {
		// TODO Auto-generated constructor stub
		mBody = body;
		mRequest = "GetFamilyInfo";
		bitmapCache = new BitmapCache();
		fileUtil = new FileUtil();
	}

	@SuppressLint("HandlerLeak")
	public void getFamilyData(final familyInfoDataCallBack dataCallBack) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Code && dataCallBack != null) {
					FamilyInfo info = (FamilyInfo) msg.obj;
					if (info != null) {
						DolphinApp.getInstance().getFamilyInfos().add(info);
						SQLiteManager.getInstance().saveFamilyInfo(info, true);
						dataCallBack.onDataCallBack(Code, info);
					} else {
					}
				}
			}
		};

		new Thread() {
			@Override
			public void run() {
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
						FamilyInfo info = ParseXMLFamilyInfo(response);
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

	public FamilyInfo ParseXMLFamilyInfo(XmlPullParser response) {
		FamilyInfo family = null;
		try {

			int eventType = response.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				// break;
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:GetFamilyInfoResponse")) {
					} else if (nodeName.equals("n:Code")) {
						Code = Integer.parseInt(response.nextText());
					}
					if (nodeName.equals("n:Familys")) {
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
							if (icon != null) {
								family.setDeviceIcon(family.getDeviceID()
										+ ".jpg");
								saveAvatar(icon, family.getDeviceID());
							}

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
		return family;
	}

	private void saveAvatar(String icon, String name) {
		Bitmap bitmap = BitmapUtils.base64ToBitmap(icon);
		// 图片下载成功后缓存并执行回调刷新界面
		if (bitmap != null) {

			// 先缓存到内存
			bitmapCache.putBitmap(name, bitmap);
			// 缓存到文件系统
			fileUtil.saveBitmap(name + ".jpg", bitmap);
		}
	}

	/**
	 * 网路访问调接口
	 * 
	 */
	public interface familyInfoDataCallBack {
		void onDataCallBack(int code, FamilyInfo data);
	}

}
