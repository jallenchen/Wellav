package com.wellav.dolphin.mmedia.entity;

public class AssistantSetting {
	private String mFamilyId;
	private int mIsHousekeeping;
	private int mHousekeepingPeriod;
	private ClockInfo CustomPeriod;
	private int mIsOnlyWorkingDay;
	public String getFamilyId() {
		return mFamilyId;
	}
	public void setFamilyId(String familyId) {
		mFamilyId = familyId;
	}
	public int getIsHousekeeping() {
		return mIsHousekeeping;
	}
	public void setIsHousekeeping(int isHousekeeping) {
		mIsHousekeeping = isHousekeeping;
	}
	public int getHousekeepingPeriod() {
		return mHousekeepingPeriod;
	}
	public void setHousekeepingPeriod(int housekeepingPeriod) {
		mHousekeepingPeriod = housekeepingPeriod;
	}
	public ClockInfo getCustomPeriod() {
		return CustomPeriod;
	}
	public void setCustomPeriod(ClockInfo customPeriod) {
		CustomPeriod = customPeriod;
	}
	public int getIsOnlyWorkingDay() {
		return mIsOnlyWorkingDay;
	}
	public void setIsOnlyWorkingDay(int isOnlyWorkingDay) {
		mIsOnlyWorkingDay = isOnlyWorkingDay;
	}
	
	
}
