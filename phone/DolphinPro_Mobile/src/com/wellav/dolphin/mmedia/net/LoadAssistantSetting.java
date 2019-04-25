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
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.AssistantSetting;
import com.wellav.dolphin.mmedia.entity.ClockInfo;
import com.wellav.dolphin.mmedia.entity.ClockInfoImpl;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class LoadAssistantSetting {
	String mBody;
	String mRequest;
	Handler handler;
	int Code = -1;

	public LoadAssistantSetting(String body) {
		// TODO Auto-generated constructor stub
		mBody = body;
		mRequest = "GetDolphinHouseAssistant";
	}

	@SuppressLint("HandlerLeak")
	public void getAssistantData(final assistantInfoDataCallBack dataCallBack) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Code && dataCallBack != null) {
					AssistantSetting info = (AssistantSetting) msg.obj;
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
						AssistantSetting info = ParseXMLAssisInfo(response);
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

	public AssistantSetting ParseXMLAssisInfo(XmlPullParser response) {
		AssistantSetting assis = new AssistantSetting();
		ClockInfo clockInfo = null;
		try {

			int eventType = response.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				// break;
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					if (nodeName.equals("n:GetDolphinHouseAssistantResponse")) {

					} else if (nodeName.equals("n:Code")) {
						Code = Integer.parseInt(response.nextText());
					} else if (nodeName.equals("n:FamilyID")) {
						assis.setFamilyId(response.nextText());
					}else if (nodeName.equals("n:ISHousekeeping")) {
						assis.setIsHousekeeping(Integer.parseInt(response
								.nextText()));
					} else if (nodeName.equals("n:HousekeepingType")) {
						assis.setHousekeepingPeriod(Integer.parseInt(response
								.nextText()));
					} else if (nodeName.equals("n:ISOnlyWorkingDay")) {
						assis.setIsOnlyWorkingDay(Integer.parseInt(response
								.nextText()));
					} else if (nodeName.equals("n:CustomPeriods")) {
						clockInfo = new ClockInfo();
					} else if (clockInfo != null) {
						if (nodeName.equals("n:StartTime")) {
							clockInfo.setmStartTime(response.nextText());
						} else if (nodeName.equals("n:EndTime")) {
							clockInfo.setmEndTime(response.nextText());
						} else if (nodeName.equals("n:CustomDay")) {
							clockInfo.setmDays(Integer.parseInt(response
									.nextText()));
						}

					}
					break;
				case XmlPullParser.END_TAG:
					if (response.getName().equalsIgnoreCase("n:CustomPeriods")
							&& clockInfo != null) {
						clockInfo.setmEanbled(assis.getIsHousekeeping()==1);
						assis.setCustomPeriod(clockInfo);
						clockInfo = null;
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
		return assis;
	}

	/**
	 * 网路访问调接口
	 * 
	 */
	public interface assistantInfoDataCallBack {
		void onDataCallBack(int code, AssistantSetting data);
	}
}
