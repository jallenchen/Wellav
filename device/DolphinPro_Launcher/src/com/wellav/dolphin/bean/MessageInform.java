package com.wellav.dolphin.bean;

/**
 * 
 * @author JingWen.Li
 *
 */
public class MessageInform {	

	// 群聊和会议ID，根据ID是否存在 判断 群聊和会议是否结束
	String talkingID="";
	String meettingID="";
	String yearAndMonth="";
	String monthAndDay="";
	String name="";
	String dolphinName="";
	String eventType="0";
	String beenAnswered="";
	// 照片数量，异常照片，异常视频数量
	String num="";
	String userid="";
	String familyid="";
	String deviceid="";
	
	
	public String getYearAndMonth() {
		return yearAndMonth;
	}
	public void setYearAndMonth(String yearAndMonth) {
		this.yearAndMonth = yearAndMonth;
	}
	public String getMonthAndDay() {
		return monthAndDay;
	}
	public void setMonthAndDay(String monthAndDay) {
		this.monthAndDay = monthAndDay;
	}
	public String getName() {
		return name;
	}
	public void setUserID(String userid) {
		this.userid = userid;
	}
	public String getUserID() {
		return userid;
	}
	public void setFamilyID(String familyid) {
		this.familyid = familyid;
	}
	public String getFamilyID() {
		return familyid;
	}
	public void setDeviceID(String deviceid) {
		this.deviceid = deviceid;
	}
	public String getDeviceID() {
		return deviceid;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getBeenAnswered() {
		return beenAnswered;
	}
	public void setBeenAnswered(String beenAnswered) {
		this.beenAnswered = beenAnswered;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getTalkingID() {
		return talkingID;
	}
	public void setTalkingID(String talkingID) {
		this.talkingID = talkingID;
	}
	public String getMeettingID() {
		return meettingID;
	}
	public void setMeettingID(String meettingID) {
		this.meettingID = meettingID;
	}
	public String getDolphinName() {
		return dolphinName;
	}
	public void setDolphinName(String dolphinName) {
		this.dolphinName = dolphinName;
	}
	
}
