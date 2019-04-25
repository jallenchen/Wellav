package com.wellav.dolphin.bean;

/**
 * 
 * @author JingWen.Li
 *
 */
public class MessageInformSafe {
	
	String yearAndMonth;
	String monthAndDay;
	String name;
	String eventType;
	// 照片数量，异常照片，异常视频数量
	String num;	
	
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
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	
}
