package com.wellav.dolphin.mmedia.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright 2015 Wellav
 * 
 * All rights reserved.
 * 
 * @date2015年12月21日
 * @author Cheng
 */
public class TimeUtils {
	public static String paserTimeToYM(long time) {
		// System.setProperty("user.timezone", "Asia/Shanghai");
		// TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		// TimeZone.setDefault(tz);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(time * 1000L));
	}

}
