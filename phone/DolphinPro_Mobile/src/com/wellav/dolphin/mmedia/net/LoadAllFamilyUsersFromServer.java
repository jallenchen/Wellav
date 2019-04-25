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
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.utils.BitmapCache;
import com.wellav.dolphin.mmedia.utils.BitmapUtils;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.FileUtil;
import com.wellav.dolphin.mmedia.utils.PinYinManager;

public class LoadAllFamilyUsersFromServer {

	String mBody;
	String mRequest;
	Handler handler;
	int Code = -1;
	// 一级内存缓存基于 LruCache
	private BitmapCache bitmapCache;
	// 二级文件缓存
	private FileUtil fileUtil;

	public LoadAllFamilyUsersFromServer(String body) {
		mBody = body;
		mRequest = "GetFamilyUsers";
		bitmapCache = new BitmapCache();
		fileUtil = new FileUtil();
	}

	@SuppressLint("HandlerLeak")
	public void getAllUserData(final allUserDataCallBack dataCallBack) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Code && dataCallBack != null) {
					@SuppressWarnings("unchecked")
					List<FamilyUserInfo> infos = (List<FamilyUserInfo>) msg.obj;
					if (infos != null) {
						SQLiteManager.getInstance().saveFamilyUserInfoList(
								infos, true);
						dataCallBack.onDataCallBack(Code, infos);

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
						List<FamilyUserInfo> infos = ParseXMLFamilyUsers(response);
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

	public List<FamilyUserInfo> ParseXMLFamilyUsers(XmlPullParser response) {
		FamilyUserInfo familyUserInfo = null;
		List<FamilyUserInfo> familyUserInfoes = null;
		try {

			int eventType = response.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				// case XmlPullParser.START_DOCUMENT://文档开始事件,可以进行数据初始化处理
				// familyUserInfoes = new ArrayList<FamilyUserInfo>();
				// break;
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:GetFamilyUsersResponse")) {
						familyUserInfoes = new ArrayList<FamilyUserInfo>();
					} else if (nodeName.equals("n:Code")) {
						Code = Integer.parseInt(response.nextText());
					} else if (nodeName.equals("n:Users")) {
						familyUserInfo = new FamilyUserInfo();
					} else if (familyUserInfo != null) {
						if (nodeName.equals("n:FamilyID")) {
							familyUserInfo.setFamilyID(response.nextText());
						} else if (nodeName.equals("n:UserID")) {
							familyUserInfo.setUserID(response.nextText());
						} else if (nodeName.equals("n:Authority")) {
							familyUserInfo.setAuthority(response.nextText());
							;
						} else if (nodeName.equals("n:LoginName")) {
							familyUserInfo.setLoginName(response.nextText());
						} else if (nodeName.equals("n:DeviceType")) {
							familyUserInfo.setDeviceType(response.nextText());
						} else if (nodeName.equals("n:NoteName")) {
							String strutf8 = response.nextText();
							familyUserInfo.setNoteName(strutf8);
						} else if (nodeName.equals("n:Sex")) {

							familyUserInfo.setSex(response.nextText());
						} else if (nodeName.equals("n:Birthday")) {
							familyUserInfo.setBirthday(response.nextText());
						} else if (nodeName.equals("n:Nickname")) {

							String name = response.nextText();
							familyUserInfo.setNickName(name);

						} else if (nodeName.equals("n:Icon")) {
							String icon = response.nextText();
							if (icon != null) {
								familyUserInfo.setIcon(familyUserInfo
										.getLoginName() + ".jpg");
								saveAvatar(icon, familyUserInfo.getLoginName());
							}
						} else if (nodeName.equals("n:City")) {
							familyUserInfo.setCity(response.nextText());
						}

					}
					break;
				case XmlPullParser.END_TAG:
					if (response.getName().equalsIgnoreCase("n:Users")
							&& familyUserInfo != null) {
						String[] pinyin;
						if (familyUserInfo.getNoteName().equals("")) {
							pinyin = PinYinManager.toPinYin(familyUserInfo
									.getNickName());
						} else {
							pinyin = PinYinManager.toPinYin(familyUserInfo
									.getNoteName());
						}
						familyUserInfo.setFirstChar(pinyin[0]);
						familyUserInfoes.add(familyUserInfo);
						//
						familyUserInfo = null;
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
		return familyUserInfoes;
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
	public interface allUserDataCallBack {
		void onDataCallBack(int code, List<FamilyUserInfo> data);
	}
}
