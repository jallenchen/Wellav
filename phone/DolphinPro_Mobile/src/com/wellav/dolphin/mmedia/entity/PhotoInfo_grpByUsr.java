package com.wellav.dolphin.mmedia.entity;

import java.io.Serializable;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * @date2016年2月22日
 * @author Cheng
 */
public class PhotoInfo_grpByUsr implements Serializable{
	private int mCount;
	private String mThumbUrl;
	private String mUsrNam;
	private String mUsrId;
	public String getmUsrId() {
		return mUsrId;
	}
	public void setmUsrId(String string) {
		this.mUsrId = string;
	}
	public int getmCount() {
		return mCount;
	}
	public void setmCount(int mCount) {
		this.mCount = mCount;
	}
	public String getmThumbUrl() {
		return mThumbUrl;
	}
	public void setmThumbUrl(String mThumbUrl) {
		this.mThumbUrl = mThumbUrl;
	}
	public String getmUsrNam() {
		return mUsrNam;
	}
	public void setmUsrNam(String mUsrNam) {
		this.mUsrNam = mUsrNam;
	}
}
