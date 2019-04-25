package com.wellav.dolphin.mmedia.entity;

import java.io.Serializable;

public class Rank implements Serializable{
	
	private String RankID;
	private String UserID;
	private String Times;
	private String UserName;
	private String Icon;
	
	public void setRankID(String RankID){
		this.RankID = RankID;
	}
	
	public String getRankID(){
		return RankID;
	}
	
	public void setUserID(String UserID){
		this.UserID = UserID;
	}
	
	public String getUserID(){
		return UserID;
	}
	
	public void setTimes(String Times){
		this.Times = Times;
	}
	
	public String getTimes(){
		return Times;
	}
	
	public void setUserName(String UserName){
		this.UserName = UserName;
	}
	
	public String getUserName(){
		return UserName;
	}
	
	public void setUserIcon(String icon){
		this.Icon = icon;
	}
	
	public String getUserIcon(){
		return Icon;
	}
}
