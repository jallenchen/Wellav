package com.wellav.dolphin.mmedia.entity;

import java.io.Serializable;


public class UserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String _ID = "_id";
	public static String _USER_ID = "_user_id";
	public static String _USER_NAME = "_user_name";
	public static String _USER_NICK_NAME = "_user_nick_name";
	public static String _USER_NOTE_NAME = "_user_note_name";
	public static String _USER_DEVICE_TYPE = "_user_device_type";
	public static String _USER_BIRTHDAY = "_user_birthday";
	public static String _USER_SEX = "_user_sex";
	public static String _USER_CITY= "_user_city";
	public static String _USER_PHOTO_ID = "_user_photo_id";
	public static String _USER_FIRST_KEY = "_user_first_key";
	
	String NickName="";
	String LoginName="";
	String Sex="";
	String ID="";
	String Icon;
	String status = "0";
    String firstChar="";// 首字母
    String DeviceType="";
    String NoteName="";
    String Birthday="";
    String City="";
    
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
	
	public void setUserID(String ID){
		this.ID = ID;
	}
	
	
	public String getUserID(){
		return ID;
	}
	
	
	public void setIcon(String Icon){
		this.Icon = Icon;
	}
	
	public String getIcon(){
		return Icon;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
