/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wellav.dolphin.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.wellav.dolphin.config.MsgKey;
import com.wellav.dolphin.config.SysConfig;

public class PreferenceUtils {
	/**
	 * 保存Preference的name
	 */
	public static final String PREFERENCE_NAME = "saveInfo";
	private static SharedPreferences mSharedPreferences;
	private static PreferenceUtils mPreferenceUtils;
	private static SharedPreferences.Editor editor;

	@SuppressLint({ "WorldWriteableFiles", "WorldReadableFiles" }) @SuppressWarnings("deprecation")
	private PreferenceUtils(Context cxt) {
		mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		editor = mSharedPreferences.edit();
	}

	public static synchronized void init(Context cxt){
	    if(mPreferenceUtils == null){
	        mPreferenceUtils = new PreferenceUtils(cxt);
	    }
	}

	/**
	 * 单例模式，获取instance实例
	 * 
	 * @param cxt
	 * @return
	 */
	public static PreferenceUtils getInstance() {
		if (mPreferenceUtils == null) {
			throw new RuntimeException("please init first!");
		}
		
		return mPreferenceUtils;
	}

	/**
	 * 获取xml中的int类型
	 * 
	 * @param key
	 * @param defValue
	 *            没有取到值时给的默认值
	 * @return
	 */
	public int getIntSharedXml(String key, int defValue) {
		String ret = mSharedPreferences.getString(key, "" + defValue);
		if (ret != null && ret.equals("") == false)
			return Integer.parseInt(ret);
		return defValue;
	}

	/**
	 * Token记录在SharePreferences
	 * 
	 * */
	public void saveAccountSharePreferences() {
		//	editor.putString(MsgKey.Uid, SysConfig.uid).commit();
			editor.putString(MsgKey.DToken, SysConfig.DolphinToken).commit();
			editor.putString(MsgKey.RToken, SysConfig.NeteaseToken).commit();
			editor.putString(MsgKey.key_familyid, SysConfig.DeviceFamilyId).commit();
	}
	public void clearAccountSharePreferences() {
	//	editor.putString(MsgKey.Uid, "").commit();
		editor.putString(MsgKey.DToken, "").commit();
		editor.putString(MsgKey.RToken, "").commit();
		editor.putString(MsgKey.key_familyid, "").commit();
}

	public void saveSharePreferences(String key, String value) {
		editor.putString(key, value).commit();
	}
	
	public String getSharePreferences(String key) {
		return mSharedPreferences.getString(key, "");
	}
	
	public void saveBooleanSharePreferences(String key, boolean value) {
		editor.putBoolean(key, value).commit();
	}
	public boolean getBooleanShare(String key){
		return mSharedPreferences.getBoolean(key, false);
	}
	public boolean getBooleanShare(String key,boolean defaulevalue){
		return mSharedPreferences.getBoolean(key, defaulevalue);
	}
	public void saveIntSharePreferences(String key, int value) {
		editor.putInt(key, value).commit();
	}

	public void getAccountSharePreferences() {
	//	SysConfig.uid = mSharedPreferences.getString(MsgKey.Uid, "");
		SysConfig.DolphinToken = mSharedPreferences.getString(MsgKey.DToken, "");
		SysConfig.NeteaseToken = mSharedPreferences.getString(MsgKey.RToken, "");
		SysConfig.DeviceFamilyId = mSharedPreferences.getString(MsgKey.key_familyid, "");
	}

}
