package com.wellav.dolphin.mmedia.entity;

import java.io.Serializable;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * @date2016年5月19日
 * @author Cheng
 */
public class ClockInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean mEanbled;
	private int mDays;
	private String mStartTime;
	private String mEndTime;
	public boolean ismEanbled() {
		return mEanbled;
	}
	public void setmEanbled(boolean mEanbled) {
		this.mEanbled = mEanbled;
	}
	public int getmDays() {
		return mDays;
	}
	public void setmDays(int mDays) {
		this.mDays = mDays;
	}
	public String getmStartTime() {
		return mStartTime;
	}
	public void setmStartTime(String mStartTime) {
		this.mStartTime = mStartTime;
	}
	public String getmEndTime() {
		return mEndTime;
	}
	public void setmEndTime(String mEndTime) {
		this.mEndTime = mEndTime;
	}
	
}
