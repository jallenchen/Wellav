package com.wellav.dolphin.mmedia.entity;

import java.io.Serializable;

public class PhotoInfo extends BasePhotoInfo implements Serializable{
	private String ID;
	
	private String FamilyID;
	
	private String ThumbnailUrl;	
	private String Url;
	private String UploadTime;
	private int mSection;
	private String mUploadBy;
	private String Description="";
	private boolean mIsFavor;
	private boolean mIsNew;
	private boolean mIsChked;
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(ID!=null)
			return ID.equals(((PhotoInfo)o).getID());
		else
			return Url.equals(((PhotoInfo)o).getmUrl());
	}
	public boolean ismIsChked() {
		return mIsChked;
	}
	public void setmIsChked(boolean mIsChked) {
		this.mIsChked = mIsChked;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getFamilyID() {
		return FamilyID;
	}
	public void setFamilyID(String familyID) {
		FamilyID = familyID;
	}
	public String getmThumbnail() {
		return ThumbnailUrl;
	}
	public void setmThumbnail(String mThumbnail) {
		this.ThumbnailUrl = mThumbnail;
	}
	
	public String getmUrl() {
		return Url;
	}
	public void setmUrl(String mUrl) {
		this.Url = mUrl;
	}
	public String getmTime() {
		return UploadTime;
	}
	public void setmTime(String mTime) {
		this.UploadTime = mTime;
	}
	public int getmSection() {
		return mSection;
	}
	public void setmSection(int mSection) {
		this.mSection = mSection;
	}
	public String getmUploadBy() {
		return mUploadBy;
	}
	public void setmUploadBy(String mUploadBy) {
		this.mUploadBy = mUploadBy;
	}
	public String getmDesc() {
		return Description;
	}
	public void setmDesc(String mDesc) {
		this.Description = mDesc;
	}
	public boolean ismIsFavor() {
		return mIsFavor;
	}
	public void setmIsFavor(boolean mIsFavor) {
		this.mIsFavor = mIsFavor;
	}
	public boolean ismIsNew() {
		return mIsNew;
	}
	public void setmIsNew(boolean mIsNew) {
		this.mIsNew = mIsNew;
	}
}