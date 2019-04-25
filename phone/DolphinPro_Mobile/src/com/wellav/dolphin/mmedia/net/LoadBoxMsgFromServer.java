package com.wellav.dolphin.mmedia.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
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
import com.wellav.dolphin.mmedia.commands.MessageEventType;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.MessageInfo;
import com.wellav.dolphin.mmedia.entity.MessageInform;
import com.wellav.dolphin.mmedia.net.LoadAllFamilyUsersFromServer.allUserDataCallBack;
import com.wellav.dolphin.mmedia.net.LoadFamilyUserFromServer.familyUserDataCallBack;
import com.wellav.dolphin.mmedia.net.LoadFamilysFromServer.familyInfoDataCallBack;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal.userAvatarDataCallBack;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.JsonUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class LoadBoxMsgFromServer {
	String mBody;
	String mRequest;
	Handler handler;
	int Code = -1;
	public LoadBoxMsgFromServer(String body) {
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
					
					if (infos.size() != 0) {
						setMessagesToDB(infos);
					} else {
					}
					dataCallBack.onDataCallBack(Code);
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
			try {
				json = new JSONObject(messages.get(i).getMessage());
				if (json.getString("type").equals("invite")) {
					getInviteMsg(messages.get(i).getSendID(), messages.get(i)
							.getUploadTime(), json);
					PreferenceUtils.getInstance().saveBooleanSharePreferences(MsgKey.newInviteMsg, true);
				} else if (json.getString("type").equals("normal")) {
					getBoxMsg(messages.get(i).getSendID(), messages.get(i)
							.getUploadTime(), json);
					PreferenceUtils.getInstance().saveBooleanSharePreferences(MsgKey.newBoxMsg, true);
				}else if (json.getString("type").equals("setting")){
					getSettingWatchUserAuth(json.getString("message"));
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
				if(updateType==1){
					SQLiteManager.getInstance().updateFamilyInfo(userid, FamilyInfo._FAMILY_NICKNAME, msg.getString("name"), true);
					SQLiteManager.getInstance().updateFamilyUserInfo(userid, FamilyUserInfo._FAMILY_USER_NICK_NAME, msg.getString("name"), true);
				}else if(updateType==2 || updateType == 0){
					LoadUserAvatarFromLocal avatar = new LoadUserAvatarFromLocal();
					avatar.getData(userid, new userAvatarDataCallBack() {
						@Override
						public void onDataCallBack(int code, Bitmap avatar) {
							SQLiteManager.getInstance().updateData();
						}
					});
				}else if(updateType==3){
					SQLiteManager.getInstance().updateFamilyUserInfo(userid, FamilyUserInfo._FAMILY_USER_NICK_NAME, msg.getString("name"), true);
				}else if(updateType==4){
					SQLiteManager.getInstance().updateFamilyUserInfo(userid, FamilyUserInfo._FAMILY_USER_CITY, msg.getString("city"), true);
				}else if(updateType==5){
					SQLiteManager.getInstance().updateFamilyInfo(userid, FamilyInfo._FAMILY_CITY, msg.getString("city"), true);
					SQLiteManager.getInstance().updateFamilyUserInfo(userid, FamilyUserInfo._FAMILY_USER_CITY, msg.getString("city"), true);
				}else if(updateType==6){
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}
	
	private void getSettingWatchUserAuth(String msgcontent){
		try {
			JSONObject msg = new JSONObject(msgcontent);
			String auth = String.valueOf( msg.getInt("WatchAuth"));
			SQLiteManager.getInstance().updateFamilyUserInfoAuthor(msg.getString("familyid"), msg.getString("userid"), FamilyUserInfo._FAMILY_USER_AUTHORITY,auth, true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void getInviteMsg(String from, String time, JSONObject msg) {
		SQLiteManager.getInstance().saveInviteMessage(JsonUtil.getInviteMessage(from, time, msg),true);
	}

	private void getBoxMsg(String from, String time, JSONObject msg) {
		
		MessageInform message = JsonUtil.getBoxMessage(from, time, msg);
		familyUserAction(message);
		SQLiteManager.getInstance().saveBoxMessage(message);
		
	}
	
	private void familyUserAction(final MessageInform msg){
		String actionType;
		actionType = msg.getEventType();
		if(actionType.equals(MessageEventType.EVENTTYPE_EXIT_FAMILY)){
			if(msg.getUserID().equals(DolphinApp.getInstance().getAccountInfo().getUserId())){
				SQLiteManager.getInstance().deleteFamilyInfoById(msg.getFamilyId(), true);	
			}else{
				SQLiteManager.getInstance().deleteFamilyUserInfoById(msg.getFamilyId(), msg.getUserID(), true);
			}
			
		}else if(actionType.equals(MessageEventType.EVENTTYPE_JOIN_FANILY)){
			if(msg.getUserID().equals(DolphinApp.getInstance().getAccountInfo().getUserId())){
				String body = RequestString.getFamilyIfo(SysConfig.dtoken, msg.getFamilyId());
				 LoadFamilysFromServer  family = new LoadFamilysFromServer(body);
				 family.getFamilyData(new familyInfoDataCallBack() {
					@Override
					public void onDataCallBack(int code, FamilyInfo data) {
						Log.e("NewFriendsAdapter", "LoadFamilysFromServer:"+code);
						if(code == 0){
							String body = RequestString.getFamilyUsers(SysConfig.dtoken, msg.getFamilyId());
							LoadAllFamilyUsersFromServer allFamilyUsers = new LoadAllFamilyUsersFromServer(body);
							 allFamilyUsers.getAllUserData(new allUserDataCallBack() {
								@Override
								public void onDataCallBack(int code, List<FamilyUserInfo> data) {
									Log.e("NewFriendsAdapter", "LoadAllFamilyUsersFromServer:"+code);
								}
							});
						}
					}
				});
			}else{
				String  body = RequestString.getFamilyUserInfo(SysConfig.dtoken, msg.getFamilyId(), msg.getUserID());
				 LoadFamilyUserFromServer  familyUser = new LoadFamilyUserFromServer(body);
				 familyUser.getFamilyUserData(new familyUserDataCallBack() {
					@Override
					public void onDataCallBack(int code, FamilyUserInfo data) {
						Log.e("EVENTTYPE_JOIN_FANILY",data.getNickName());
					}
				});
			}
		}else if(actionType.equals(MessageEventType.EVENTTYPE_PHONE_MANAGER)){
			String managerid = SQLiteManager.getInstance().getFamilyInfoManagerByFamilyId(msg.getFamilyId());
			SQLiteManager.getInstance().updateFamilyAdmin(msg.getFamilyId(), managerid, msg.getUserID(), true);
		}else if(actionType.equals(MessageEventType.EVENTTYPE_RELEASE_FAMILY)){
			SQLiteManager.getInstance().deleteFamilyInfoById(msg.getFamilyId(), true);	
		}else if(actionType.equals(MessageEventType.EVENTTYPE_BEEN_MANAGER)){
			String body = RequestString.getFamilyIfo(SysConfig.dtoken, msg.getFamilyId());
			 LoadFamilysFromServer  family = new LoadFamilysFromServer(body);
			 family.getFamilyData(new familyInfoDataCallBack() {
				@Override
				public void onDataCallBack(int code, FamilyInfo data) {
					Log.e("NewFriendsAdapter", "LoadFamilysFromServer:"+code);
					if(code == 0){
						String body = RequestString.getFamilyUsers(SysConfig.dtoken, msg.getFamilyId());
						LoadAllFamilyUsersFromServer allFamilyUsers = new LoadAllFamilyUsersFromServer(body);
						 allFamilyUsers.getAllUserData(new allUserDataCallBack() {
							@Override
							public void onDataCallBack(int code, List<FamilyUserInfo> data) {
								Log.e("NewFriendsAdapter", "LoadAllFamilyUsersFromServer:"+code);
							}
						});
					}
				}
			});
		}
	}

	public void requestXml(String requestBody, final String request) {
		XMLRequest xmlRequest = new XMLRequest(SysConfig.ServerUrl + request,
		// "https://www.baidu.com/",
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
					if (nodeName.equals("n:GetMessageBoxResponse")) {
					} else if (nodeName.equals("n:Code")) {
						Code = Integer.valueOf(response.nextText());
					} else if (nodeName.equals("n:Messages")) {
						info = new MessageInfo();
					} else if (info != null) {

						if (nodeName.equals("n:SendID")) {
							info.setSendID(response.nextText());
						} else if (nodeName.equals("n:UploadTime")) {
							info.setUploadTime(response.nextText());
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
