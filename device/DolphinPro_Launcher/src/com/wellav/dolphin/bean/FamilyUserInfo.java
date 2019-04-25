package com.wellav.dolphin.bean;


import java.io.Serializable;

import com.wellav.dolphin.config.SysConfig;

public class FamilyUserInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String _ID = "_id";
	public static String _FAMILY_ID = "_family_id";
	public static String _FAMILY_USER_ID = "_family_user_id";
	public static String _FAMILY_USER_AUTHORITY = "_family_user_authority";
	public static String _FAMILY_USER_NAME = "_family_user_name";
	public static String _FAMILY_USER_NICK_NAME = "_family_user_nick_name";
	public static String _FAMILY_USER_NOTE_NAME = "_family_user_note_name";
	public static String _FAMILY_USER_DEVICE_TYPE = "_family_user_device_type";
	public static String _FAMILY_USER_BIRTHDAY = "_family_user_birthday";
	public static String _FAMILY_USER_SEX = "_family_user_sex";
	public static String _FAMILY_USER_CITY= "_family_user_city";
	public static String _FAMILY_USER_PHOTO_ID = "_family_user_photo_id";
	public static String _FAMILY_USER_FIRST_KEY = "_family_user_first_key";
	
	String UserID;
	String Authority;
	String FamilyID;
	String NickName;
	String LoginName;
	String Sex;
	String Icon;
    String firstChar;// 首字母
    String DeviceType;
    String NoteName="";
    String Birthday;
    String City;
    int State = SysConfig.FamilyUserInfoAudioState;
    boolean audioState = false;
	
    
	public int getState() {
		return State;
	}

	public void setState(int state) {
		State = state;
	}

	public boolean getRemoteAudioMute() {
		return audioState;
	}

	public void setRemoteAudioMute(boolean audioState) {
		this.audioState = audioState;
	}

	public void setUserID(String UserID){
		this.UserID = UserID;
	}
	
	public String getUserID(){
		return UserID;
	}
	
	public void setAuthority(String Authority){
		this.Authority = Authority;
	}
	
	public String getAuthority(){
		return Authority;
	}
	
	public void setFamilyID(String FamilyID){
		this.FamilyID = FamilyID;
	}
	
	public String getFamilyID(){
		return FamilyID;
	}
	public void setNickName(String NickName){
		this.NickName = NickName;
	}
	
	public String getNickName(){
		return NickName;
	}
	public void setDeviceType(String DeviceType){
		this.DeviceType = DeviceType;
	}
	
	public String getDeviceType(){
		return DeviceType;
	}
	public void setNoteName(String NoteName){
		this.NoteName = NoteName;
	}
	
	public String getNoteName(){
		return NoteName;
	}
	public void setBirthday(String Birthday){
		this.Birthday = Birthday;
	}
	
	public String getBirthday(){
		return Birthday;
	}
	
	public void setLoginName(String LoginName){
		this.LoginName = LoginName;
	}
	
	public String getLoginName(){
		return LoginName;
	}
	public void setSex(String Sex){
		this.Sex = Sex;
	}
	
	public String getSex(){
		return Sex;
	}
	
	public void setIcon(String Icon){
		this.Icon = Icon;
	}
	
	public String getIcon(){
		return Icon;
	}
	
	public String getFirstChar() {
		return firstChar;
	}

	public void setFirstChar(String firstChar) {
		this.firstChar = firstChar;
	}
	public void setCity(String City){
		this.City = City;
	}
	
	public String getCity(){
		return City;
	}
}
