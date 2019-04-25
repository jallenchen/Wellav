package com.wellav.dolphin.mmedia.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RegisterUserInfo implements Serializable{
	private String mLoginName;
	private String mPassword;
	private String mDeviceType;
	private String mNickname;
	private String mBirthday;
	private String mSex;
	private String mIcon;
	private String mCity;
	
	
	
	public String getCity() {
		return mCity;
	}
	public void setCity(String city) {
		mCity = city;
	}
	public String getLoginName() {
		return mLoginName;
	}
	public void setLoginName(String loginName) {
		mLoginName = loginName;
	}
	public String getPassword() {
		return mPassword;
	}
	public void setPassword(String password) {
		mPassword = password;
	}
	public String getDeviceType() {
		return mDeviceType;
	}
	public void setDeviceType(String deviceType) {
		mDeviceType = deviceType;
	}
	public String getNickname() {
		return mNickname;
	}
	public void setNickname(String nickname) {
		mNickname = nickname;
	}
	public String getBirthday() {
		return mBirthday;
	}
	public void setBirthday(String birthday) {
		mBirthday = birthday;
	}
	public String getSex() {
		return mSex;
	}
	public void setSex(String sex) {
		mSex = sex;
	}
	public String getIcon() {
		return mIcon;
	}
	public void setIcon(String icon) {
		mIcon = icon;
	}
	
	
	

}
