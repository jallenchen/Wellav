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
package com.wellav.dolphin.mmedia.db;

import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.UserInfo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
	/**
	 * 保存Preference的name
	 */
	public static final String PREFERENCE_NAME = "saveInfo";
	private static SharedPreferences mSharedPreferences;
	private static PreferenceUtils mPreferenceUtils;
	private static SharedPreferences.Editor editor;

	private String SHARED_KEY_SETTING_NOTIFICATION = "shared_key_setting_notification";
	private String SHARED_KEY_SETTING_SOUND = "shared_key_setting_sound";
	private String SHARED_KEY_SETTING_VIBRATE = "shared_key_setting_vibrate";
	private String SHARED_KEY_SETTING_SPEAKER = "shared_key_setting_speaker";

	@SuppressWarnings("deprecation")
	private PreferenceUtils(Context cxt) {
		mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE | Context.MODE_PRIVATE);
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

	public void setSettingMsgNotification(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_NOTIFICATION, paramBoolean);
		editor.commit();
	}

	public boolean getSettingMsgNotification() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_NOTIFICATION, true);
	}

	public void setSettingMsgSound(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_SOUND, paramBoolean);
		editor.commit();
	}

	public boolean getSettingMsgSound() {

		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_SOUND, true);
	}

	public void setSettingMsgVibrate(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_VIBRATE, paramBoolean);
		editor.commit();
	}

	public boolean getSettingMsgVibrate() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_VIBRATE, true);
	}

	public void setSettingMsgSpeaker(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_SPEAKER, paramBoolean);
		editor.commit();
	}

	public boolean getSettingMsgSpeaker() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_SPEAKER, true);
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
	 * 如果登录成功过,则将登陆用户名和密码记录在SharePreferences
	 * 
	 * @param saveUserName
	 *            是否将用户名保存到SharePreferences
	 * @param savePassword
	 *            是否将密码保存到SharePreferences
	 * */
	public void saveSharePreferences(boolean saveUserName, boolean savePassword) {
		if (saveUserName) {
			editor.putString(UserInfo._USER_NAME, SysConfig.uid)
					.commit();
			editor.putString(UserInfo._USER_NICK_NAME, SysConfig.nickname)
					.commit();
			editor.putString(UserInfo._USER_ID, SysConfig.userid)
			.commit();
		} else {
			editor.putString(UserInfo._USER_NAME, "").commit();
			editor.putString(UserInfo._USER_NICK_NAME, "").commit();
			editor.putString(UserInfo._USER_ID, "")
			.commit();
		}
		if (savePassword) {
			editor.putString(SysConfig.key_loginpsw, SysConfig.psw)
					.commit();
			editor.putString(SysConfig.key_dolphintoken, SysConfig.dtoken).commit();
			editor.putString(SysConfig.key_neteasetoken, SysConfig.rtoken).commit();
		} else {
			editor.putString(SysConfig.key_loginpsw, "").commit();
			editor.putString(SysConfig.key_dolphintoken, "").commit();
			editor.putString(SysConfig.key_neteasetoken, "").commit();
		}
	}

	public void saveSharePreferences(String key, String value) {
		editor.putString(key, value).commit();
	}
	public String getStringSharePreferences(String key) {
		return mSharedPreferences.getString(key, "");
	}
	
	public void saveBooleanSharePreferences(String key, boolean value) {
		editor.putBoolean(key, value).commit();
	}
	public boolean getBooleanSharePreferences(String key) {
		return mSharedPreferences.getBoolean(key, false);
	}
	public void saveIntSharePreferences(String key, int value) {
		editor.putInt(key, value).commit();
	}
	public int getIntSharePreferences(String key, int defValue) {
		return mSharedPreferences.getInt(key, defValue);
	}

	public void getSharePreferencesAccount() {
		SysConfig.uid = mSharedPreferences.getString(UserInfo._USER_NAME, "");
		SysConfig.nickname = mSharedPreferences.getString(UserInfo._USER_NICK_NAME, "");
		SysConfig.psw = mSharedPreferences.getString(SysConfig.key_loginpsw, "");
		SysConfig.rtoken = mSharedPreferences.getString(SysConfig.key_neteasetoken, "");
		SysConfig.dtoken = mSharedPreferences.getString(SysConfig.key_dolphintoken, "");
		SysConfig.userid = mSharedPreferences.getString(UserInfo._USER_ID, "");
	}

	public void clearSharePreferencesToken() {
		editor.putString(SysConfig.key_neteasetoken, "").commit();
		editor.putString(SysConfig.key_dolphintoken, "").commit();
	}

	public void clearSharePreferencesAccount() {
		editor.putString(UserInfo._USER_NAME, "").commit();
		editor.putString(UserInfo._USER_NICK_NAME, "").commit();
		editor.putString(SysConfig.key_loginpsw, "").commit();
		editor.putString(SysConfig.key_neteasetoken, "").commit();
		editor.putString(SysConfig.key_dolphintoken, "").commit();
	}
	
	/**
	 * 保存消息提醒设置的内容
	 * 
	 * @param saveNotifySetting 是否提醒
	 * @param saveVoiceSetting 是否有提示音
	 *         
	 * @return
	 */
	
	public void saveNotifySetting(boolean isNotify){
		editor.putBoolean(SysConfig.IS_NOTIFY, isNotify).commit();
	}
	public void saveVoiceSetting(boolean hasVoice){
		editor.putBoolean(SysConfig.HAS_VOICE, hasVoice).commit();
	}
	public  boolean getNotifySetting() {
		boolean isNotify=mSharedPreferences.getBoolean(SysConfig.IS_NOTIFY, true);
		return isNotify;
		
	}
	public boolean getVoiceSetting() {
		boolean hasVoice=mSharedPreferences.getBoolean(SysConfig.HAS_VOICE, true);
		return hasVoice;
	}
	
	public void saveRingtoneSetting(String uri) {
		editor.putString(SysConfig.choseRingtone, uri).commit();
	}
	public String getRingtoneSetting(){
		String ringchose=mSharedPreferences.getString(SysConfig.choseRingtone, "");
		return ringchose;
	}
	
	/**
	 * 保存海豚设置
	 *        
	 */
	public void saveWatchingNotifySetting(boolean watchingNotify){
		editor.putBoolean(SysConfig.WatchingNotify, watchingNotify).commit();
	}
	public void saveCallingNotifySetting(boolean callingNotify){
		editor.putBoolean(SysConfig.CallingNotify, callingNotify).commit();
	}
	public void saveLookingPhotoNotifySetting(boolean lookingPhotoNotify){
		editor.putBoolean(SysConfig.LookingPhotoNotify, lookingPhotoNotify).commit();
	}
	public  boolean getWatchingNotifySetting() {
		boolean watchingNotify=mSharedPreferences.getBoolean(SysConfig.WatchingNotify, true);
		return watchingNotify;
	}
	public  boolean getCallingNotifySetting() {
		boolean callingNotify=mSharedPreferences.getBoolean(SysConfig.CallingNotify, true);
		return callingNotify;
	}
	public  boolean getLookingPhotoNotifySetting() {
		boolean lookingPhotoNotify=mSharedPreferences.getBoolean(SysConfig.LookingPhotoNotify, true);
		return lookingPhotoNotify;
	}

}
