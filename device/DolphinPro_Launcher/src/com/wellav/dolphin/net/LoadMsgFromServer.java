package com.wellav.dolphin.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.FamilyInfo;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.bean.MessageEventType;
import com.wellav.dolphin.bean.MessageInfo;
import com.wellav.dolphin.bean.MessageInform;
import com.wellav.dolphin.bean.MessageInformSafe;
import com.wellav.dolphin.config.MsgKey;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.interfaces.IProviderContactMetaData.ContactTableMetaData;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadFamilyUserFromServer.familyUserDataCallBack;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal.userAvatarDataCallBack;
import com.wellav.dolphin.utils.JsonUtils;
import com.wellav.dolphin.utils.PinYinManager;
import com.wellav.dolphin.utils.Util;

public class LoadMsgFromServer {
	String mBody;
	String mRequest;
	Handler handler;
	Context mContext;
	int Code = -1;
	public LoadMsgFromServer(Context ct,String body) {
		mContext = ct;
		mBody = body;
		mRequest = "GetMessageBox";
	}

	@SuppressLint("HandlerLeak")
	public void getAllMsgData(final MsgDataCallBack dataCallBack) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Code && dataCallBack != null) {
					@SuppressWarnings("unchecked")
					List<MessageInfo> infos = (List<MessageInfo>) msg.obj;
					if (infos != null) {
						setMessagesToDB(infos);
						dataCallBack.onDataCallBack(Code);
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

	public void setMessagesToDB(List<MessageInfo> messages) {
		JSONObject json = null;
		for (int i = 0; i < messages.size(); i++) {
			// 得到时间字符串,拼凑出年月，月日时分
			// 得到json数据
			String timeStr = messages.get(i).getUploadTime();
			//String sendID = messages.get(i).getSendID();
			// 时间的获取
			String year = timeStr.substring(0, 4);
			String month = timeStr.substring(5, 7);
			String day = timeStr.substring(8, 10);
			String hourAndMin = timeStr.substring(11, 16);
			
			String yearAndMonth = year + LauncherApp.getInstance().getResources().getString(R.string.year) + month + LauncherApp.getInstance().getResources().getString(R.string.month);
			String monthAndDay = month + LauncherApp.getInstance().getResources().getString(R.string.month) + day + LauncherApp.getInstance().getResources().getString(R.string.day) + " " + hourAndMin;
			Log.e("setMessagesToDB","size:"+ messages.size());
			try {
				json = new JSONObject(messages.get(i).getMessage());
				if (json.getString("type").equals("safe")) {
					getBoxSafeMsg(yearAndMonth,monthAndDay, json);
					 SysConfig.getInstance().setNewMsg(true);
				} else if (json.getString("type").equals("normal")) {
					getBoxNormalMsg(yearAndMonth,monthAndDay, json);
					 SysConfig.getInstance().setNewMsg(true);
				}else if(json.getString("type").equals("setting")) {
					getSettingMsg(json);
				}else if(json.getString("type").equals("invite")) {
					String userid = json.getString("userid");
					Intent in = new Intent(MsgKey.BROADCAST_MANAGER_ACTION);
					in.putExtra("userid", userid);
				    LauncherApp.getInstance().sendBroadcast(in);
				    Util.PrintLog("setMessagesToDB","invite");
				}else if (json.getString("type").equals("update")){
					getUpdateInfo(json);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void getUpdateInfo(JSONObject msg){
		String userid="";
		int updateType;
			try {
				userid = msg.getString("userid");
				updateType = msg.getInt("eventtype");
				if(updateType==0 || updateType ==2){
					LoadUserAvatarFromLocal avatar = new LoadUserAvatarFromLocal();
					avatar.getData(userid, new userAvatarDataCallBack() {
						
						@Override
						public void onDataCallBack(int code, Bitmap avatar) {
							LauncherApp.getInstance().getContentResolver().notifyChange(ContactTableMetaData.CONTENT_URI, null);
						}
					});
					
				}else if(updateType==1 ){
					LauncherApp.getInstance().getDBHelper().updateFamilyInfo(userid, FamilyInfo._FAMILY_NICKNAME, msg.getString("name"));
					LauncherApp.getInstance().getDBHelper().updateFamilyUserByUserid(LauncherApp.getInstance(), FamilyUserInfo._FAMILY_USER_NICK_NAME,msg.getString("name"), userid);
				}else if(updateType==3){
					LauncherApp.getInstance().getDBHelper().updateFamilyUserByUserid(LauncherApp.getInstance(), FamilyUserInfo._FAMILY_USER_NICK_NAME,msg.getString("name"), userid);
					FamilyUserInfo info = LauncherApp.getInstance().getDBHelper().getFamilyUserByUserid(LauncherApp.getInstance(),userid);
					if(info.getNoteName().equals("")){
						String[] pinyin = PinYinManager.toPinYin(msg.getString("name"));
						LauncherApp.getInstance().getDBHelper().updateFamilyUserByUserid(LauncherApp.getInstance(), FamilyUserInfo._FAMILY_USER_FIRST_KEY,pinyin[0], userid);
					}
				}else if(updateType==4){
					LauncherApp.getInstance().getDBHelper().updateFamilyUserByUserid(LauncherApp.getInstance(), FamilyUserInfo._FAMILY_USER_CITY,msg.getString("city"), userid);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}
	
	private void getSettingMsg(JSONObject json){
		try {
			JSONObject messageContent = json.getJSONObject("message");
			String auth = String.valueOf( messageContent.getInt("WatchAuth"));
			String userid = messageContent.getString("userid");
			LauncherApp.getInstance().getDBHelper().updateFamilyUserByUserid(LauncherApp.getInstance(), FamilyUserInfo._FAMILY_USER_AUTHORITY,auth, userid);
			if(LauncherApp.getInstance().getFamily().getDeviceUserID().equals(userid)){
				LauncherApp.getInstance().getDBHelper().updateFamilyInfo(userid, FamilyInfo._FAMILY_AUTHORITY,auth);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void getBoxSafeMsg(String yearAndMonth, String monthAndDay, JSONObject json) {
	
		MessageInformSafe mMessageInformSafe = new MessageInformSafe();
		
		String name = null;
		String eventType = null;
		String num = null;
		try {
			JSONObject messageContent = json.getJSONObject("message");
			name = messageContent.getString("name");
			eventType = messageContent.getString("eventType");
			num = messageContent.getString("num");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		mMessageInformSafe.setYearAndMonth(yearAndMonth);
		mMessageInformSafe.setMonthAndDay(monthAndDay);
		mMessageInformSafe.setName(name);
		mMessageInformSafe.setNum(num);
		mMessageInformSafe.setEventType(eventType);
		
		LauncherApp.getInstance().getDBHelper().saveMessageInfoSafe(mMessageInformSafe);
	}

	private void getBoxNormalMsg(String yearAndMonth, String monthAndDay, JSONObject msg) {
		MessageInform message = JsonUtils.getBoxMessage(msg);
		message.setYearAndMonth(yearAndMonth);
		message.setMonthAndDay(monthAndDay);
		LauncherApp.getInstance().getDBHelper().saveMessageInfo(message);
		familyUserAction(message);
	}
	
	private void familyUserAction(final MessageInform msg){
		String actionType;
		
		actionType = msg.getEventType();
		if(actionType.equals(MessageEventType.EVENTTYPE_RELEASE_FAMILY)){
			LauncherApp.getInstance().getDBHelper().delFamilyUserExUserid(mContext,LauncherApp.getInstance().getFamily().getDeviceUserID());
			LauncherApp.getInstance().setFamilyManager(null);
			
			//解散家庭组
			Bundle bundle = new Bundle();
		  	bundle.putBoolean("releaseFamily", true);
		  	sendRemoteSettingBC(bundle);
		}else if(actionType.equals(MessageEventType.EVENTTYPE_EXIT_FAMILY)){
			LauncherApp.getInstance().getDBHelper().delFamilyUserByUserid(mContext,msg.getUserID());
		}else if(actionType.equals(MessageEventType.EVENTTYPE_JOIN_FANILY)){
				String  body = XMLBody.getFamilyUserInfo(SysConfig.DolphinToken, msg.getFamilyID(), msg.getUserID());
				 LoadFamilyUserFromServer  familyUser = new LoadFamilyUserFromServer(mContext,body);
				 familyUser.getFamilyUserData(new familyUserDataCallBack() {
					
					@Override
					public void onDataCallBack(int code, FamilyUserInfo data) {
					}
				});
			
			
		}else if(actionType.equals(MessageEventType.EVENTTYPE_PHONE_MANAGER)){
			//String managerid = SQLiteManager.getInstance().getFamilyInfoManagerByFamilyId(msg.getFamilyId());
			LauncherApp.getInstance().getDBHelper().updateFamilyUserByUserid(mContext, FamilyUserInfo._FAMILY_USER_AUTHORITY, "5", msg.getUserID());
			LauncherApp.getInstance().getDBHelper().updateFamilyUserByUserid(mContext, FamilyUserInfo._FAMILY_USER_AUTHORITY, "4", LauncherApp.getInstance().getFamilyManager().getUserID());
			LauncherApp.getInstance().setFamilyManager(LauncherApp.getInstance().getDBHelper().getFamilyUserByUserid(mContext, msg.getUserID()));
		}else {
			
		}
	}

	private static void sendRemoteSettingBC(Bundle value){
		Intent in = new Intent(SysConfig.REMOTE_SETTING_ACTION);
		in.putExtras(value);
		LauncherApp.getInstance().sendBroadcast(in);
	}

	public void requestXml(String requestBody, final String request) {

		XMLRequest xmlRequest = new XMLRequest(SysConfig.ServerUrl + request,
				requestBody, new Response.Listener<XmlPullParser>() {
					@Override
					public void onResponse(XmlPullParser response) {
						List<MessageInfo> infos = ParseXMLMessage(response);
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

	public List<MessageInfo> ParseXMLMessage(XmlPullParser response) {
		List<MessageInfo> msgs = new ArrayList<MessageInfo>();
		MessageInfo info = null;
		try {
			int eventType = response.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String nodeName = response.getName();
					// Log.e("***************", nodeName);

					if (nodeName.equals("n:GetMessageBoxResponse")) {
					} else if (nodeName.equals("n:Code")) {
						Code = Integer.valueOf(response.nextText());
					} else if (nodeName.equals("n:Messages")) {
						info = new MessageInfo();
					} else if (info != null) {

						if (nodeName.equals("n:SendID")) {
							info.setSendID(response.nextText());
						} else if (nodeName.equals("n:UploadTime")) {
							info.setUploadTime(response.nextText().replaceAll(
									"[T]|(.000Z)", " "));
						} else if (nodeName.equals("n:Message")) {
							info.setMessage(response.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if (response.getName().equalsIgnoreCase("n:Messages")
							&& info != null) {
						msgs.add(info);
						info = null;
					}
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
		return msgs;
	}

	/**
	 * 网路访问调接口
	 * 
	 */
	public interface MsgDataCallBack {
		void onDataCallBack(int code);
	}
}
