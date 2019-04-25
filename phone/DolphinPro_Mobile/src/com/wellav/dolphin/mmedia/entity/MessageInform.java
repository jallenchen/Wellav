package com.wellav.dolphin.mmedia.entity;

public class MessageInform {	
	
    
    // MessageInform表列名
    public static String _ID = "_id";
	public static String _YEARANDMONTH = "_yearAndMonth";
	public static String _MONTHANDDAY = "_monthAndDay";
	public static String _NAME = "_name";
	public static String _USER_ID = "_userid";
	public static String _DOLPHINNAME = "_dolphinName";
	public static String _FAMILY_ID = "_familyid";
	public static String _DEVICE_ID = "_deviceid";
	public static String _EVENTTYPE = "_eventType";
	public static String _BEENANSWERED = "_beenAnswered"; 
	public static String _NUM = "_num"; 
	public static String _TIME = "_date"; 
	public static String _TALKINGID = "_talkingID";
	public static String _MEETTINGID = "_meettingID";

	// 群聊和会议ID，根据ID是否存在 判断 群聊和会议是否结束
	String talkingID="";
	String meettingID="";
	String yearAndMonth="";
	String monthAndDay="";
	String name="";
	String userid="";
	String familyid="";
	String deviceid="";

	String devicename="";
	String eventType="";
	String beenAnswered="";
	// 照片数量，异常照片，异常视频数量
	String num="";
	String time="";
	int _id= 0;
	public void setID(int id){
		this._id = id;
	}
	public int getID(){
		return _id;
	}
	
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
	
	public void setName(String name) {
		this.name = name;
	}
	public String getUserID() {
		return userid;
	}
	
	public void setUserID(String id) {
		this.userid = id;
	}
	public String getFamilyId() {
		return familyid;
	}
	public void setFamilyId(String fid) {
		this.familyid = fid;
	}
	
	public String getDolphinName() {
		return devicename;
	}
	public void setDolphinName(String name) {
		this.devicename = name;
	}
	public String getDeviceID() {
		return deviceid;
	}
	public void setDeviceID(String deviceid) {
		this.deviceid = deviceid;
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
