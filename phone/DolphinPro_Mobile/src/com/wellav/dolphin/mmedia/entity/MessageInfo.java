package com.wellav.dolphin.mmedia.entity;

import java.io.Serializable;

public class MessageInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mSendID;
	private String mUploadTime;
	private String mMessage;
	public String getSendID() {
		return mSendID;
	}
	public void setSendID(String sendID) {
		mSendID = sendID;
	}
	public String getUploadTime() {
		return mUploadTime;
	}
	public void setUploadTime(String uploadTime) {
		mUploadTime = uploadTime;
	}
	public String getMessage() {
		return mMessage;
	}
	public void setMessage(String message) {
		mMessage = message;
	}
	
	
}
