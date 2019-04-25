package com.wellav.dolphin.mmedia.entity;

import java.io.Serializable;
import java.util.List;

public class MyStatistcsInfo implements Serializable {

	private List<Rank> Ranks;
	private List<MyTime> myTimes;
	
	public void setRanks(List<Rank> Ranks){
		this.Ranks = Ranks;
	}
	
	public List<Rank> getRanks(){
		return Ranks;
	}
	
	public void setMyTime(List<MyTime> myTimes){
		this.myTimes = myTimes;
	}
	
	public List<MyTime> getMyTime(){
		return myTimes;
	}
}
