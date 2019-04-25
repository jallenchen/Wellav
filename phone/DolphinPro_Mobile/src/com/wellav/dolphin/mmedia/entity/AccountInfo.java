package com.wellav.dolphin.mmedia.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AccountInfo implements Serializable {
	String Token;
	String RTCAccount;
	String CapabilityToken;
	String Code = "-1";
	String Userid;
	List<FamilyInfo> Familys = new ArrayList<FamilyInfo>();
	
	
	public void setUserId(String Userid){
		this.Userid = Userid;
	}
	
	public String getUserId(){
		return Userid;
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
	
}
