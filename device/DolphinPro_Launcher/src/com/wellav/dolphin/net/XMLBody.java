package com.wellav.dolphin.net;

import java.util.ArrayList;

import android.util.Log;

import com.wellav.dolphin.bean.CallDuration;

public class XMLBody {
	private static final String TAG = "XMLBody";

	public static String getFamilyUsers(String token, ArrayList<String> FamilyId) {
		String mToken = "<GetFamilyUsersRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>" + token + "</Token>";
		String mUploadFamilyUsersBody;
		for (int i = 0; i < FamilyId.size(); i++) {
			mToken = mToken + "<FamilyID>" + FamilyId.get(i) + "</FamilyID>";
		}
		mUploadFamilyUsersBody = mToken + "</GetFamilyUsersRequest>";
	//	Log.e(TAG, mUploadFamilyUsersBody);
		return mUploadFamilyUsersBody;
	}

	public static String getFamilyUsers(String token, String FamilyId) {
		String mUploadFamilyUsersBody = "<GetFamilyUsersRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<FamilyID>"
				+ FamilyId
				+ "</FamilyID>" + "</GetFamilyUsersRequest>";
		//Log.e(TAG, mUploadFamilyUsersBody);
		return mUploadFamilyUsersBody;
	}

	public static String getFamilyUserInfo(String token, String FamilyId,
			String userid) {
		String mUploadFamilyUsersBody = "<GetFamilyUserInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<FamilyID>"
				+ FamilyId
				+ "</FamilyID>"
				+ "<UserID>"
				+ userid
				+ "</UserID>"
				+ "</GetFamilyUserInfoRequest>";
		//Log.e(TAG, mUploadFamilyUsersBody);
		return mUploadFamilyUsersBody;
	}

	public static String getFamilyInfo(String token, String FamilyId) {
		String mUploadFamilyInfoBody = "<GetFamilyInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<FamilyID>"
				+ FamilyId
				+ "</FamilyID>" + "</GetFamilyInfoRequest>";
		//Log.e(TAG, mUploadFamilyInfoBody);
		return mUploadFamilyInfoBody;
	}

	public static String getUserInfo(String token, String userId) {
		String mUploadUserInfoBody = "<GetUserInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<UserID>"
				+ userId
				+ "</UserID>" + "</GetUserInfoRequest>";
		//Log.e(TAG, mUploadUserInfoBody);
		return mUploadUserInfoBody;
	}

	public static String getUserInfo(String token, ArrayList<String> userId) {
		String mToken = "<GetUserInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>" + token + "</Token>";
		String mUploadUserInfoBody;
		for (int i = 0; i < userId.size(); i++) {
			mToken = mToken + "<UserID>" + userId.get(i) + "</UserID>";
		}
		mUploadUserInfoBody = mToken + "</GetUserInfoRequest>";
		//Log.e(TAG, mUploadUserInfoBody);
		return mUploadUserInfoBody;
	}

	public static String getLogin(String name, String pwd) {
		String mUploadLoginBody = "<NeteaseLoginRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Username>"
				+ name
				+ "</Username>"
				+ "<Password>"
				+ pwd
				+ "</Password>"
				+ "<ClientType>"
				+ "Phone"
				+ "</ClientType>" + "</NeteaseLoginRequest>";
		//Log.e(TAG, mUploadLoginBody);
		return mUploadLoginBody;
	}

	public static String updateUserHead(String token, String icon) {
		String mUploadUserHeadBody = "<UpdateUserInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<Icon>"
				+ icon
				+ "</Icon>"
				+ "</UpdateUserInfoRequest>";

		return mUploadUserHeadBody;
	}
	public static String updateMyUserCity(String token,String city){
		String mUpdateUserCityBody = "<UpdateUserInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<Token>"+token
				+"</Token>"+"<City>"+city+"</City>"+"</UpdateUserInfoRequest>";
		//Log.e(TAG, mUpdateUserCityBody);
		return mUpdateUserCityBody;
	}

	public static String updateUserNoteName(String token, String NoteName) {
		String mUploadUserNoteNameBody = "<UpdateUserInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<NoteName>"
				+ NoteName
				+ "</NoteName>" + "</UpdateUserInfoRequest>";
		//Log.e(TAG, mUploadUserNoteNameBody);
		return mUploadUserNoteNameBody;
	}

	public static String updateUserNickName(String token, String Nickname) {
		String mUploadUserNicknameBody = "<UpdateUserInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<Nickname>"
				+ Nickname
				+ "</Nickname>" + "</UpdateUserInfoRequest>";
		//Log.e(TAG, mUploadUserNicknameBody);
		return mUploadUserNicknameBody;
	}

	public static String UserLeaveFamily(String token, String userName,
			String familyId) {
		String mUserLeaveFamilyBody = "<UserLeaveFamilyRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<UserName>"
				+ userName
				+ "</UserName>"
				+ "<FamilyID>"
				+ familyId
				+ "</FamilyID>"
				+ ""
				+ "</UserLeaveFamilyRequest>";
		//Log.e(TAG, mUserLeaveFamilyBody);
		return mUserLeaveFamilyBody;
	}

	public static String userRegister(String loginName, String pwd,
			int deviceType) {
		String mUploadUserRegisterBody = "<RegisterRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<LoginName>"
				+ loginName
				+ "</LoginName>"
				+ "<Password>"
				+ pwd
				+ "</Password>"
				+ "<DeviceType>"
				+ deviceType
				+ "</DeviceType>" + "" + "</RegisterRequest>";
		//Log.e(TAG, mUploadUserRegisterBody);
		return mUploadUserRegisterBody;
	}

	public static String AddUser2Family(String token, String FamilyID,
			String UserName) {
		String mUploadUserJoinFamilyBody = "<UserJoinFamilyRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<UserName>"
				+ UserName
				+ "</UserName>"
				+ "<FamilyID>"
				+ FamilyID
				+ "</FamilyID>"
				+ ""
				+ "</UserJoinFamilyRequest>";
		//Log.e(TAG, mUploadUserJoinFamilyBody);
		return mUploadUserJoinFamilyBody;
	}

	public static String UserIfExist(String token, String userName) {
		String mUploadUserIfExistBody = "<UserIfExistRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<UserName>"
				+ userName
				+ "</UserName>" + "" + "</UserIfExistRequest>";
		//Log.e(TAG, mUploadUserIfExistBody);
		return mUploadUserIfExistBody;
	}

	public static String UploadMessageBox(String token, int type, String id,
			String Json) {
		String mUploadMessageBoxBody = "<UploadMessageBoxRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<RecipientType>"
				+ type
				+ "</RecipientType>"
				+ "<RecipientID>"
				+ id
				+ "</RecipientID>"
				+ "<Message>"
				+ Json
				+ "</Message>"
				+ ""
				+ "</UploadMessageBoxRequest>";
		//Log.e(TAG, mUploadMessageBoxBody);
		return mUploadMessageBoxBody;
	}

	public static String getMessage(String token) {
		String mMessageBody = "<GetMessageBoxRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>" + token + "</Token>" + "</GetMessageBoxRequest>";
		//Log.e(TAG, mMessageBody);
		return mMessageBody;
	}

	public static String AddPhoneRecord(String token, CallDuration record) {
		String mMessageBody = "<AddPhoneRecordRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<SenderID>"
				+ record.getSenderId()
				+ "</SenderID>"
				+ "<RecipientID>"
				+ record.getRecipientID()
				+ "</RecipientID>"
				+ "<StartTime>"
				+ record.getStartTime()
				+ "</StartTime>"
				+ "<EndTime>"
				+ record.getEndTime()
				+ "</EndTime>"
				+ "</AddPhoneRecordRequest>";
		//Log.e(TAG, mMessageBody);
		return mMessageBody;
	}
	
	public static String SetDolphinHouseAssistant(String token,String familyid,String key,int value){
		String body = "<SetDolphinHouseAssistantRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"+ token+ "</Token>"+ "<FamilyID>"+ familyid+ "</FamilyID>"
				+ "<"+key+">"+ value+ "</"+key+">"
				+ "</SetDolphinHouseAssistantRequest>";
		//Log.e(TAG, body);
		return body;
	}
	public static String SetDolphinHouseAssistantCustom(String token,String familyid,String starttime,String endtime,int day){
		String body = "<SetDolphinHouseAssistantRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"+ token+ "</Token>"+ "<FamilyID>"+ familyid+ "</FamilyID>"
				+ "<CustomDay>"+ day+ "</CustomDay>"
				+ "<StartTime>"+ starttime+ "</StartTime>"
				+ "<EndTime>"+ endtime+ "</EndTime>"
				+ "</SetDolphinHouseAssistantRequest>";
		Log.e(TAG, body);
		return body;
	}
	
	public static String GetVOIPSubAccount(String token){
		String body = "<GetVOIPSubAccountRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"+ token+ "</Token>"
				+ "</GetVOIPSubAccountRequest>";
		//Log.e(TAG, body);
		return body;
	}
}
