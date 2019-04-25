package com.wellav.dolphin.mmedia.entity;

import java.io.Serializable;
import java.util.List;

public class MyTime implements Serializable {
	
	private String Day;
	private String Times;
	
	
	public void setDay(String Day){
		this.Day = Day;
	}
	
	public String getDay(){
		return Day;
	}
	
	public void setTimes(String Times){
		this.Times = Times;
	}
	
	public String getTimes(){
		return Times;
	}
	


}
