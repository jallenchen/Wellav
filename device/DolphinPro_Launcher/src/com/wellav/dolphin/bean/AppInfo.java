package com.wellav.dolphin.bean;
/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * @date2016年4月11日
 * @author Cheng
 */
public class AppInfo {
	private int appName;
	private String packageName;
	private String clsName;
	public String getClsName() {
		return clsName;
	}
	public void setClsName(String clsName) {
		this.clsName = clsName;
	}
	private int appIcon;
	
	public AppInfo(int appName, String packageName,String clsName, int appIcon) {
		super();
		this.appName = appName;
		this.packageName = packageName;
		this.clsName=clsName;
		this.appIcon = appIcon;
	}
	public int getAppName() {
		return appName;
	}
	public void setAppName(int appName) {
		this.appName = appName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public int getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(int appIcon) {
		this.appIcon = appIcon;
	}
}
