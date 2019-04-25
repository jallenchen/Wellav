package com.wellav.dolphin.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * 
 * @date2016年5月18日
 * @author Cheng
 */
@SuppressLint("WorldReadableFiles") public class BaseInterProcessSharedPreference<T> {
	/**
	 * @param ctx
	 * @param packageNam
	 * @param preferenceNam
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T getConfig(Context ctx, String packageNam, String preferenceNam, String key) {
		T result = null;
		try {
			
			Type type = getClass().getGenericSuperclass();

			Type trueType = ((ParameterizedType) type).getActualTypeArguments()[0];

			Class<T> entityClass = (Class<T>) trueType;
			Context c = ctx.createPackageContext(packageNam, Context.CONTEXT_IGNORE_SECURITY);
			
			@SuppressWarnings("deprecation")
			SharedPreferences sp = c.getSharedPreferences(preferenceNam,
					Context.MODE_MULTI_PROCESS | Context.MODE_WORLD_READABLE);
			
			String simpleNam = entityClass.getSimpleName();
			if (simpleNam.equals("Integer")) {
				result = (T) Integer.valueOf(sp.getInt(key, 0));
			} else if (simpleNam.equals("String")) {
				result = (T) sp.getString(key, "true");
				Log.e("1",
						"r="+result);
			}
			else if(simpleNam.equals("Boolean"))
			{
				result=(T) Boolean.valueOf(sp.getBoolean(key, true));
			}
		} catch (NameNotFoundException e) {
			Log.e("1",
					e.toString());
		}
		
		return result;
	}
}
