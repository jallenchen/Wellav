package com.wellav.dolphin.bean;
/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * @date2016年4月20日
 * @author Cheng
 */
public class WeatherInfo {
	private String city;
	private String status1;
	private String status2;
	public String getStatus2() {
		return status2;
	}
	public void setStatus2(String status2) {
		this.status2 = status2;
	}
	private String temprature1;
	private String temprature2;
	public String getTemprature2() {
		return temprature2;
	}
	public void setTemprature2(String temprature2) {
		this.temprature2 = temprature2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStatus1() {
		return status1;
	}
	public void setStatus1(String weather) {
		this.status1 = weather;
	}
	public String getTemprature1() {
		return temprature1;
	}
	public void setTemprature1(String temprature) {
		this.temprature1 = temprature;
	}
}
