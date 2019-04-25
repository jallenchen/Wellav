package com.wellav.dolphin.mmedia.entity;
/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * @date2016年3月15日
 * @author Cheng
 */
public class PhotoInfo_grpByDay extends BasePhotoInfo{
	private String thumbnial;
	private String date;
	public String getThumbnial() {
		return thumbnial;
	}
	public void setThumbnial(String thumbnial) {
		this.thumbnial = thumbnial;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}
