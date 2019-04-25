package com.wellav.dolphin.bean;

import java.io.Serializable;

public class AccountInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String Token;
	String RTCAccount;
	String CapabilityToken;
	String Code = "-1";
    FamilyInfo Family;
	String UserId;
	
	
	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public void setToken(String token){
		this.Token = token;
	}
	
	public String getToken(){
		return Token;
	}
	
	public void setRTCAccount(String count){
		this.RTCAccount = count;
	}
	
	public String getRTCAccount(){
		return RTCAccount;
	}
	public void setCapabilityToken(String ctoken){
		this.CapabilityToken = ctoken;
	}
	
	public String getCapabilityToken(){
		return CapabilityToken;
	}
	
	public void setCode(String Code){
		this.Code = Code;
	}
	public String getCode(){
		return Code;
	}
	
	public void setFamilys(FamilyInfo Family){
		this.Family = Family;
	}
	
	public FamilyInfo getFamilys(){
		return Family;
		
		
	}
	
}
