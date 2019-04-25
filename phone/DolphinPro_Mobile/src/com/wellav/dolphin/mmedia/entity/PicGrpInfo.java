package com.wellav.dolphin.mmedia.entity;

import java.io.Serializable;

/**
 * Copyright 2015 Wellav
 * 
 * All rights reserved.
 * @date2015年12月23日
 * @author Cheng
 */
public class PicGrpInfo implements Serializable{
	private String mDirNam;
	private String mDirPath;
	private String mFirstChildPath;
	private int mChildCnt;
	private boolean mIsChked;
	public String getmFirstChildPath() {
		return mFirstChildPath;
	}
	public void setmFirstChildPath(String mFirstChildPath) {
		this.mFirstChildPath = mFirstChildPath;
	}
	
	public String getmDirNam() {
		return mDirNam;
	}
	public void setmDirNam(String mDirNam) {
		this.mDirNam = mDirNam;
	}
	public String getmDirPath() {
		return mDirPath;
	}
	public void setmDirPath(String mDirPath) {
		this.mDirPath = mDirPath;
	}
	public int getmChildCnt() {
		return mChildCnt;
	}
	public void setmChildCnt(int mChildCnt) {
		this.mChildCnt = mChildCnt;
	}
	public boolean ismIsChked() {
		return mIsChked;
	}
	public void setmIsChked(boolean mIsChked) {
		this.mIsChked = mIsChked;
	}	
	public PicGrpInfo getCopy(PicGrpInfo src)
	{
		PicGrpInfo dst=new PicGrpInfo();
		dst.setmChildCnt(src.getmChildCnt());
		dst.setmDirNam(src.getmDirNam());
		dst.setmDirPath(src.getmDirPath());
		dst.setmFirstChildPath(src.getmFirstChildPath());
		dst.setmIsChked(src.ismIsChked());
		return dst;
	}
}
