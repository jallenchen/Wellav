package com.wellav.dolphin.bean;

import com.wellav.dolphin.utils.Util;

/**
 * 
 * @author JingWen.Li
 *
 */
public class MessageInfo{
	
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
		mUploadTime = Util.changeTimeZone(uploadTime);
	}
	public String getMessage() {
		return mMessage;
	}
	public void setMessage(String message) {
		mMessage = message;
	}
}