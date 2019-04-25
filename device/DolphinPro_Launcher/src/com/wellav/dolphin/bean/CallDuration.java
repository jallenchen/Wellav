package com.wellav.dolphin.bean;

import java.io.Serializable;

public class CallDuration implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mSenderId="";
	private String mRecipientID ="";
	private String mStartTime="";
	private String mEndTime="";
	public String getSenderId() {
		return mSenderId;
	}
	public void setSenderId(String senderId) {
		mSenderId = senderId;
	}
	public String getRecipientID() {
		return mRecipientID;
	}
	public void setRecipientID(String recipientID) {
		mRecipientID = recipientID;
	}
	public String getStartTime() {
		return mStartTime;
	}
	public void setStartTime(String startTime) {
		mStartTime = startTime;
	}
	public String getEndTime() {
		return mEndTime;
	}
	public void setEndTime(String endTime) {
		mEndTime = endTime;
	}
	
	

}
