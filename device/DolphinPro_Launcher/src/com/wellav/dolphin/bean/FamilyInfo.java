package com.wellav.dolphin.bean;

import java.io.Serializable;

public class FamilyInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String _ID = "_id";
	public static String _FAMILY_ID = "_family_id";
	public static String _FAMILY_DEVICE_ID = "_family_device_id";
	public static String _FAMILY_MANGER_ID = "_family_manger_id";
	public static String _FAMILY_DEVICE_USER_ID = "_family_device_user_id";
	public static String _FAMILY_NICKNAME = "_family_nickname";
	public static String _FAMILY_NOTE = "_family_note";
	public static String _FAMILY_AUTHORITY = "_family_authorty";
	public static String _FAMILY_CITY = "_family_city";
	public static String _FAMILY_ICON = "_family_icon";
	public static String _FAMILY_AGENT_ID = "_family_agent_id";
	
	String NickName;
	String Note;
	String DeviceID;
	String Authority;
	String FamilyID;
	String DeviceUserID;
	String MangerID;
	String City;
	String Icon;
	String agentid="0";
	
	public void setNickName(String nickName){
		this.NickName = nickName;
	}
	
	public String getNickName(){
		return NickName;
	}
	
	public void setNote(String Note){
		this.Note = Note;
	}
	
	public String getNote(){
		return Note;
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
	
	public void setMangerID(String MangerID){
		this.MangerID = MangerID;
	}
	
	public String getMangerID(){
		return MangerID;
	}
	
	public void setDeviceUserID(String DeviceUserID){
		this.DeviceUserID = DeviceUserID;
	}
	
	public String getDeviceUserID(){
		return DeviceUserID;
	}
	public void setDeviceID(String DeviceID){
		this.DeviceID = DeviceID;
	}
	
	public String getDeviceID(){
		return DeviceID;
	}
	public void setCity(String City){
		this.City = City;
	}
	
	public String getCity(){
		return City;
	}
	public void setDeviceIcon(String Icon){
		this.Icon = Icon;
	}
	
	public String getDeviceIcon(){
		return Icon;
	}
	
	public void setAgentContactID(String userid){
		this.agentid = userid;
	}
	
	public String getAgentContactID(){
		return agentid;
	}
}
