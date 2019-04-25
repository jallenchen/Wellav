package com.wellav.dolphin.mmedia.entity;

import java.io.Serializable;

public class AdminInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mFamilyId;
	private String mAdminUserId;
	private String mAdminUserName;
	private String mDeviceUserId;
	
	public String getDeviceUserId() {
		return mDeviceUserId;
	}
	public void setDeviceUserId(String deviceUserId) {
		mDeviceUserId = deviceUserId;
	}
	public String getFamilyId() {
		return mFamilyId;
	}
	public void setFamilyId(String familyId) {
		mFamilyId = familyId;
	}
	public String getAdminUserId() {
		return mAdminUserId;
	}
	public void setAdminUserId(String adminUserId) {
		mAdminUserId = adminUserId;
	}
	public String getAdminUserName() {
		return mAdminUserName;
	}
	public void setAdminUserName(String adminUserName) {
		mAdminUserName = adminUserName;
	}
	
	
}
