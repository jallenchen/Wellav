package com.wellav.dolphin.mmedia.entity;

import java.io.Serializable;

public class AgentContact implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mUserID="";
	private String mUserNickname="";
	public String getUserID() {
		return mUserID;
	}
	public void setUserID(String userID) {
		mUserID = userID;
	}
	public String getUserNickname() {
		return mUserNickname;
	}
	public void setUserNickname(String userNickname) {
		mUserNickname = userNickname;
	}
	
	

}
