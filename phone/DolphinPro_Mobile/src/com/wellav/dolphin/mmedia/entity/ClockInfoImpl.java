package com.wellav.dolphin.mmedia.entity;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * 
 * @date2016年5月19日
 * @author Cheng
 */
public class ClockInfoImpl {
	public static final String sp_name = "sp_intrusion_detect";
	public static final String key_enabled = "key_enabled";
	public static final String key_days = "key_days";
	public static final String key_startTime = "key_startTime";
	public static final String key_endTime = "key_endTime";
	public static final String key_workDayTime = "key_daytime";
	public static final String[] daytime_str = { "仅白天", "全天24小时", "自定义" };

	public static ClockInfo getClockInfo(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(sp_name,
				Context.MODE_PRIVATE);
		ClockInfo clockInfo = new ClockInfo();
		clockInfo.setmDays(sp.getInt(key_days, 0x0000007f));
		clockInfo.setmEanbled(sp.getBoolean(key_enabled, false));
		clockInfo.setmEndTime(sp.getString(key_endTime, "23:59"));
		clockInfo.setmStartTime(sp.getString(key_startTime, "08:00"));
		return clockInfo;
	}
}
