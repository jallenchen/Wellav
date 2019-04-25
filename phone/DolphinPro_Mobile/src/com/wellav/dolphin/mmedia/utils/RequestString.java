package com.wellav.dolphin.mmedia.utils;

import java.util.ArrayList;

import com.wellav.dolphin.mmedia.entity.RegisterUserInfo;

import android.util.Log;

public class RequestString {
	private static final String TAG = "RequestString";

	public static String getFamilyUsers(String token, ArrayList<String> FamilyId) {
		String mToken = "<GetFamilyUsersRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>" + token + "</Token>";
		String mUploadFamilyUsersBody;
		for (int i = 0; i < FamilyId.size(); i++) {
			mToken = mToken + "<FamilyID>" + FamilyId.get(i) + "</FamilyID>";
		}
		mUploadFamilyUsersBody = mToken + "</GetFamilyUsersRequest>";
		Log.e(TAG, mUploadFamilyUsersBody);
		return mUploadFamilyUsersBody;
	}
	public static String getFamilyIfo(String token, String FamilyId) {
		String mUploadFamilyUsersBody = "<GetFamilyInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<FamilyID>"
				+ FamilyId
				+ "</FamilyID>" + "</GetFamilyInfoRequest>";
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
	//	Log.e(TAG, mUploadFamilyUsersBody);
		return mUploadFamilyUsersBody;
	}
	public static String getFamilyUserInfo(String token, String FamilyId,String userid) {
		String mUploadFamilyUsersBody = "<GetFamilyUserInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<FamilyID>"
				+ FamilyId
				+ "</FamilyID>" 
				+ "<UserID>"
				+ userid
				+ "</UserID>" + "</GetFamilyUserInfoRequest>";
	//	Log.e(TAG, mUploadFamilyUsersBody);
		return mUploadFamilyUsersBody;
	}

	public static String getUserInfo(String token, String userId) {
		String mUploadUserInfoBody = "<GetUserInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"
				+ token
				+ "</Token>"
				+ "<UserID>"
				+ userId
				+ "</UserID>" + "</GetUserInfoRequest>";
		Log.e(TAG, mUploadUserInfoBody);
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
		Log.e(TAG, mUploadUserInfoBody);
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
				+ "<ClientType>"+"Phone"+"</ClientType>"
				+ "</NeteaseLoginRequest>";
	//	Log.e(TAG, mUploadLoginBody);
		return mUploadLoginBody;
	}
	public static String updateUserHead(String token,String userid ,String icon){
		String mUploadUserHeadBody = "<UpdateUserInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<Token>"+token
				+"</Token>"+"<UserID>"+userid+"</UserID>"+"<Icon>"+icon+"</Icon>"+"</UpdateUserInfoRequest>";
		
		return mUploadUserHeadBody;
	}
	
	public static String updateUserNoteName(String token,String NoteName,String userId){
		String mUploadUserNoteNameBody = "<UpdateUserInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<Token>"+token
				+"</Token>"+"<UserID>"+userId+"</UserID>"+"<NoteName>"+NoteName+"</NoteName>"+"</UpdateUserInfoRequest>";
	//	Log.e(TAG, mUploadUserNoteNameBody);
		return mUploadUserNoteNameBody;
	}
	public static String updateMyUserCity(String token,String city){
		String mUpdateUserCityBody = "<UpdateUserInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<Token>"+token
				+"</Token>"+"<City>"+city+"</City>"+"</UpdateUserInfoRequest>";
	//	Log.e(TAG, mUpdateUserCityBody);
		return mUpdateUserCityBody;
	}
	
	public static String updateUserNickName(String token,String Nickname){
		String mUploadUserNicknameBody = "<UpdateUserInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<Token>"+token
				+"</Token>"+"<Nickname>"+Nickname+"</Nickname>"+"</UpdateUserInfoRequest>";
	//	Log.e(TAG, mUploadUserNicknameBody);
		return mUploadUserNicknameBody;
	}
	public static String updateFamilyNickName(String token,String Nickname,String familyId){
		String mUploadUserNicknameBody = "<UpdateFamilyInfoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<Token>"+token
				+"</Token>"+"<FamilyID>"+familyId+"</FamilyID>"+"<Nickname>"+Nickname+"</Nickname>"+"</UpdateFamilyInfoRequest>";
	//	Log.e(TAG, mUploadUserNicknameBody);
		return mUploadUserNicknameBody;
	}
	public static String forgetUserPsw(String loginName,String psw){
		String mUploadforgetUserPswBody = "<ForgetUserPWRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<UserName>"+loginName
				+"</UserName>"+"<NewPassword>"+psw+"</NewPassword>"+"</ForgetUserPWRequest>";
	//	Log.e(TAG, mUploadforgetUserPswBody);
		return mUploadforgetUserPswBody;
	}
	
	public static String userRegister(String loginName,String pwd,int deviceType){
		String mUploadUserRegisterBody = "<RegisterRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<LoginName>"+loginName
				+"</LoginName>"+"<Password>"+pwd+"</Password>" +
				"<DeviceType>"+deviceType+"</DeviceType>" +
						""+"</RegisterRequest>";
	//	Log.e(TAG, mUploadUserRegisterBody);
		return mUploadUserRegisterBody;
	}
	
	public static String userRegister(RegisterUserInfo rUsernfo){
		
		String mUploadUserRegisterBody = "<RegisterRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
		+"<LoginName>"+rUsernfo.getLoginName()+"</LoginName>"
		+"<Password>"+rUsernfo.getPassword()+"</Password>" 
		+"<DeviceType>"+rUsernfo.getDeviceType()+"</DeviceType>" 
		+"<Nickname>"+rUsernfo.getNickname()+"</Nickname>" 
		+"<Icon>"+rUsernfo.getIcon()+"</Icon>"
		+"<Sex>"+rUsernfo.getSex()+"</Sex>" 
		+"<Birthday>"+rUsernfo.getBirthday()+"</Birthday>" 
		+"<City>"+rUsernfo.getCity()+"</City>" 
		+""+"</RegisterRequest>";
		Log.e(TAG, mUploadUserRegisterBody);
		return mUploadUserRegisterBody;
	}
	
	public static String AddUser2Family(String token,String FamilyID,String UserID){
		String mUploadUserJoinFamilyBody = "<UserJoinFamilyRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<Token>"+token
				+"</Token>"+"<FamilyID>"+FamilyID+"</FamilyID>" +
				"<UserID>"+UserID+"</UserID>" +
						""+"</UserJoinFamilyRequest>";
	//	Log.e(TAG, mUploadUserJoinFamilyBody);
		return mUploadUserJoinFamilyBody;
	}
	public static String SetDolphinConfig(String token,String FamilyID,String mode,boolean value){
		String mSetDolphinConfigBody = "<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<Token>"+token
				+"</Token>"+"<FamilyID>"+FamilyID+"</FamilyID>" +
				"<"+mode+">"+value+"</"+mode+">" +""+"</SetDolphinConfigRequest>";
	//	Log.e(TAG, mSetDolphinConfigBody);
		return mSetDolphinConfigBody;
	}
	public static String SetDolphinConfig(String token,String FamilyID,String mode,String value){
		String mSetDolphinConfigBody = "<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<Token>"+token
				+"</Token>"+"<FamilyID>"+FamilyID+"</FamilyID>" +
				"<"+mode+">"+value+"</"+mode+">" +""+"</SetDolphinConfigRequest>";
		Log.e(TAG, mSetDolphinConfigBody);
		return mSetDolphinConfigBody;
	}
	public static String SetUserWatchDolphinAuth(String token,String FamilyID,String userid,int value){
		String mWatchAuthBody = "<SetUserWatchDolphinAuthRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<Token>"+token
				+"</Token>"+"<FamilyID>"+FamilyID+"</FamilyID>" +
				"<UserID>"+userid+"</UserID>" +
				"<IfAllowWatchDolphin>"+value+"</IfAllowWatchDolphin>" +
				""+"</SetUserWatchDolphinAuthRequest>";
	//	Log.e(TAG, mWatchAuthBody);
		return mWatchAuthBody;
	}
	
	public static String SearchUser(String token,String loginname){
		String mSearchUseBody = "<SearchUserRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<Token>"+token
				+"</Token>"+"<UserName>"+loginname+"</UserName>" +
				""+"</SearchUserRequest>";
	//	Log.e(TAG, mSearchUseBody);
		return mSearchUseBody;
	}
	

	public static String UserJoinFamily(String token,String loginname,String familyid){
		String mUserJoinFamilyBody = "<UserJoinFamilyRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<Token>"+token
				+"</Token>"+"<UserName>"+loginname+"</UserName>" +
				"<FamilyID>"+familyid+"</FamilyID>" +
				""+"</UserJoinFamilyRequest>";
	//	Log.e(TAG, mUserJoinFamilyBody);
		return mUserJoinFamilyBody;
	}
	
	public static String LinkTwoDevice(String token,String DeviceName1,String DeviceName2){
		String mLinkTwoDeviceBody = "<LinkTwoDeviceRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+"<Token>"+token
				+"</Token>"+"<DeviceID1>"+DeviceName1+"</DeviceID1>" +
				"<DeviceID2>"+DeviceName2+"</DeviceID2>" +
				""+"</LinkTwoDeviceRequest>";
	//	Log.e(TAG, mLinkTwoDeviceBody);
		return mLinkTwoDeviceBody;
	}
	public static String UploadMessageBox(String token,int type,ArrayList<String> id,String Json){
		String ids="";
		for (int i = 0; i < id.size(); i++) {
			 ids = ids + "<RecipientID>" + id.get(i) + "</RecipientID>";
			}
		String mUploadMessageBoxBody = "<UploadMessageBoxRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<RecipientType>"+type+"</RecipientType>" +
				ids +
				"<Message>"+Json+"</Message>" +
				""+"</UploadMessageBoxRequest>";
	//	Log.e(TAG, mUploadMessageBoxBody);
		return mUploadMessageBoxBody;
	}
	public static String UploadMessageBox(String token,int type,String id,String Json){
		String mUploadMessageBoxBody = "<UploadMessageBoxRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<RecipientType>"+type+"</RecipientType>" +
				"<RecipientID>"+id+"</RecipientID>" +
				"<Message>"+Json+"</Message>" +
				""+"</UploadMessageBoxRequest>";
	//	Log.e(TAG, mUploadMessageBoxBody);
		return mUploadMessageBoxBody;
	}
	
	public static String GetMessageBox(String token){
		String mMessageBoxBody = "<GetMessageBoxRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				""+"</GetMessageBoxRequest>";
	//	Log.e(TAG, mMessageBoxBody);
		return mMessageBoxBody;
	}
	public static String GetDeviceAdmin(String token,String devicename){
		String mGetDeviceAdminBody = "<GetDeviceAdminRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<DeviceName>"+devicename+"</DeviceName>" +
				""+"</GetDeviceAdminRequest>";
	//	Log.e(TAG, mGetDeviceAdminBody);
		return mGetDeviceAdminBody;
	}
	
	public static String ModifyUserPW(String token,String OldPassword,String NewPassword) {
		String mModifyUserPWBody = "<ModifyUserPWRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<OldPassword>"+OldPassword+"</OldPassword>" +
				"<NewPassword>"+NewPassword+"</NewPassword>" +
				""+"</ModifyUserPWRequest>";
	//	Log.e(TAG, mModifyUserPWBody);
		return mModifyUserPWBody;
	}

	public static String RealseFamily(String token,String familyId){
		String mRealseFamilyBody = "<RealseFamilyRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<FamilyID>"+familyId+"</FamilyID>" +
				""+"</RealseFamilyRequest>";
	//	Log.e(TAG, mRealseFamilyBody);
		return mRealseFamilyBody;
	}
	public static String UserLeaveFamily(String token,String userName ,String familyId){
		String mUserLeaveFamilyBody = "<UserLeaveFamilyRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<UserName>"+userName+"</UserName>" +
				"<FamilyID>"+familyId+"</FamilyID>" +
				""+"</UserLeaveFamilyRequest>";
	//	Log.e(TAG, mUserLeaveFamilyBody);
		return mUserLeaveFamilyBody;
	}
	public static String ModifyFamilyAdmin(String token,String familyId ,String userId){
		String mModifyFamilyAdminBody = "<ModifyFamilyAdminRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<FamilyID>"+familyId+"</FamilyID>" +
				"<UserID>"+userId+"</UserID>" +
				""+"</ModifyFamilyAdminRequest>";
	//	Log.e(TAG, mModifyFamilyAdminBody);
		return mModifyFamilyAdminBody;
	}
	public static String GetIfSecPWSeted(String token,String familyId){
		String GetIfSecPWSetedBody = "<GetIfSecPWSetedRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<FamilyID>"+familyId+"</FamilyID>"+
				""+"</GetIfSecPWSetedRequest>";
//		Log.e(TAG, GetIfSecPWSetedBody);
		return GetIfSecPWSetedBody;
	}
	public static String CheckFamilySecPW(String token,String familyId,String secPsw){
		String CheckFamilySecPWBody = "<CheckFamilySecPWRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<FamilyID>"+familyId+"</FamilyID>"+
				"<SecPassword>"+secPsw+"</SecPassword>" +
				""+"</CheckFamilySecPWRequest>";
	//	Log.e(TAG, CheckFamilySecPWBody);
		return CheckFamilySecPWBody;
	}
	public static String SetFamilySecPW(String token,String familyId,String secPsw){
		String SetFamilySecPWBody = "<SetFamilySecPWRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<FamilyID>"+familyId+"</FamilyID>"+
				"<SecPassword>"+secPsw+"</SecPassword>" +
				""+"</SetFamilySecPWRequest>";
	//	Log.e(TAG, SetFamilySecPWBody);
		return SetFamilySecPWBody;
	}
	public static String ModifyFamilySecPW(String token,String familyId,String OldsecPsw,String NewsecPsw){
		String ModifyFamilySecPWBody = "<ModifyFamilySecPWRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<FamilyID>"+familyId+"</FamilyID>"+
				"<OldSecPassword>"+OldsecPsw+"</OldSecPassword>" +
				"<NewSecPassword>"+NewsecPsw+"</NewSecPassword>"+
				""+"</ModifyFamilySecPWRequest>";
	//	Log.e(TAG, ModifyFamilySecPWBody);
		return ModifyFamilySecPWBody;
	}
	public static String StatPhoneRecord(String token,String familyId,int TimeZone){
		String StatPhoneRecordBody = "<StatPhoneRecordRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<FamilyID>"+familyId+"</FamilyID>"+
				"<TimeZone>"+TimeZone+"</TimeZone>"+
				""+"</StatPhoneRecordRequest>";
	//	Log.e(TAG, StatPhoneRecordBody);
		return StatPhoneRecordBody;
	}
	
	public static String GetDolphinConfig(String token,String familyId){
		String GetDolphinConfigBody = "<GetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<FamilyID>"+familyId+"</FamilyID>"+
				""+"</GetDolphinConfigRequest>";
	//	Log.e(TAG, GetDolphinConfigBody);
		return GetDolphinConfigBody;
	}
	public static String SetUserFeedback(String token ,String message){
		String SetUserFeedback = "<SetUserFeedbackRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<Message>"+message+"</Message>"+
				""+"</SetUserFeedbackRequest>";
	//	Log.e(TAG, SetUserFeedback);
		return SetUserFeedback;
	}
	public static String GetDolphinAgentContact(String token ,String devivename){
		String AgentContact = "<GetDolphinAgentContactRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<DeviceName>"+devivename+"</DeviceName>"+
				""+"</GetDolphinAgentContactRequest>";
	//	Log.e(TAG, AgentContact);
		return AgentContact;
	}
	public static String UploadPhoto(String token ,String familyid,String phototype,String fileExt,String base64){
		String UploadPhoto = "<UploadPhotoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<FamilyID>"+familyid+"</FamilyID>"+
				"<PhotoType>"+phototype+"</PhotoType>"+
				"<FileExt>"+fileExt+"</FileExt>"+
				"<Content>"+base64+"</Content>"+
				""+"</UploadPhotoRequest>";
		Log.e(TAG, UploadPhoto);
		return UploadPhoto;
	}
	
	public static String getAssitant(String token,String FamilyId){
		String body = "<GetDolphinHouseAssistantRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"+
				"<Token>"+token+"</Token>"+
				"<FamilyID>"+FamilyId+"</FamilyID>"+
				"</GetDolphinHouseAssistantRequest>";
		Log.e(TAG, body);
		return body;
	}
	public static String UserIfExist(String userName){
		String mUploadUserIfExistBody = "<RegisterUserIfExistRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+"<UserName>"+userName+"</UserName>" +"</RegisterUserIfExistRequest>";
		Log.e(TAG, mUploadUserIfExistBody);
		return mUploadUserIfExistBody;
	}
}
