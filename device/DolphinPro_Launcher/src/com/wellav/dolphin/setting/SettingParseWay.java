package com.wellav.dolphin.setting;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.net.RequestAction;
import com.wellav.dolphin.net.XMLBody;

public class SettingParseWay {

	// 声音音量
	public static void getVolume(String volumeJsonData,Context ctx,String familyID){
		
		try {
			// 解析json数据,保存
			JSONObject volumeJson = new JSONObject(volumeJsonData);
			int volume_r = volumeJson.getInt("volume");
			
			Log.e("**********", volume_r + "");
			
			// 拼凑出xml格式的数据
			StringBuilder xmlForServer = new StringBuilder();
			xmlForServer.append("<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + SysConfig.token + "</Token>");
			xmlForServer.append("<FamilyID>" + familyID + "</FamilyID>");
			xmlForServer.append("<VoiceSize>" + volume_r + "</VoiceSize>");
			xmlForServer.append("</SetDolphinConfigRequest>");
			
			Log.e("**********", xmlForServer.toString());
			// 发送数据到服务器
			RequestAction.requestXml(xmlForServer.toString(), "SetDolphinConfig");
			
			//保存到本地
			AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume_r, 0);
			
		} catch (JSONException e) {
			Log.e("","e="+e.toString());
		}
	}

	// 屏幕亮度
	public static void getBrightness(String brightnessJsonData, Context ctx, String familyID) {
		try {
			JSONObject brightnessJson = new JSONObject(brightnessJsonData);
			float brightness_r = brightnessJson.getInt("brightness");
			Log.e("**********", brightness_r + "");

			// 拼凑出xml格式的数据
			StringBuilder xmlForServer = new StringBuilder();
			xmlForServer.append("<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + SysConfig.token + "</Token>");
			xmlForServer.append("<FamilyID>" + familyID + "</FamilyID>");
			xmlForServer.append("<BrightnessSize>" + brightness_r + "</BrightnessSize>");
			xmlForServer.append("</SetDolphinConfigRequest>");
			
			// 发送数据到服务器
			RequestAction.requestXml(xmlForServer.toString(), "SetDolphinConfig");
			
			//保存到本地
			android.provider.Settings.System.putInt(ctx.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,(int)(brightness_r));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 高级选项中的语音提醒
	public static void getVoice(String voiceJsonData, Context ctx, String familyID) {
		try {
			JSONObject voiceJson = new JSONObject(voiceJsonData);
			// public static String[] language_str = {"普通话", "粤语", "客家话","闽南语"};
			int voice_r = Integer.valueOf(voiceJson.getString("voice"));

			Log.e("**********", voice_r+"");

			// 拼凑出xml格式的数据
			StringBuilder xmlForServer = new StringBuilder();
			xmlForServer.append("<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + SysConfig.token + "</Token>");
			xmlForServer.append("<FamilyID>" + familyID + "</FamilyID>");
			xmlForServer.append("<VoiceLanguage>" + voice_r + "</VoiceLanguage>");
			xmlForServer.append("</SetDolphinConfigRequest>");
			// 发送数据到服务器
			RequestAction.requestXml(xmlForServer.toString(), "SetDolphinConfig");
			
			//保存到本地
		//	int voice_int = Data.getIndex(Data.language_str, voice_r);
			
			
			
			SharedPreferences setting_sharedPreference = ctx.getSharedPreferences("setting",Context.MODE_PRIVATE);
			Editor editor = setting_sharedPreference.edit();
      	    editor.putInt("voice_reminder_lang_sp", voice_r);
      	    editor.commit();
      	    
      	    //add by jallen 
      	    Bundle bundle = new Bundle();
      	    bundle.putInt("voice_reminder_lang_sp", voice_r);
      	    sendRemoteSettingBC(bundle);
      	    
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 高级选项中的 人脸追踪
	public static void getFace(String faceJsonData, Context ctx, String familyID) {
		try {
			JSONObject faceJson = new JSONObject(faceJsonData);
			Boolean face_r = faceJson.getBoolean("face");
			Log.e("**********", face_r + "");

			// 传给服务器的boolean值是整型，true为1，false为0
			int faceInt = 0;
			if(true == face_r){
				faceInt = 1;
			}
			Log.e("**********", faceInt + "");
			
			// 拼凑出xml格式的数据
			StringBuilder xmlForServer = new StringBuilder();
			xmlForServer.append("<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + SysConfig.token + "</Token>");
			xmlForServer.append("<FamilyID>" + familyID + "</FamilyID>");
			xmlForServer.append("<ISFaceTrack>" + faceInt + "</ISFaceTrack>");
			xmlForServer.append("</SetDolphinConfigRequest>");
			new RequestAction();
			// 发送数据到服务器
			RequestAction.requestXml(xmlForServer.toString(), "SetDolphinConfig");
			
			//保存到本地
			SharedPreferences setting_sharedPreference = ctx.getSharedPreferences("setting",Context.MODE_PRIVATE);
			Editor editor = setting_sharedPreference.edit();
      	    editor.putBoolean("is_face_trace", face_r);
      	    editor.commit();
      	    
      	    Bundle bundle = new Bundle();
      	    bundle.putBoolean("is_face_trace", face_r);
      	    sendRemoteSettingBC(bundle);
      	    
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 高级选项中的 相册信息栏
	public static void getAlbum_info(String albumJsonData, Context ctx, String familyID) {
		try {
			JSONObject albumJson = new JSONObject(albumJsonData);
			Boolean album_r = albumJson.getBoolean("album_info");
			
			Log.e("**********", album_r + "");
			// 传给服务器的boolean值是整型，true为1，false为0
			int albumInt = 0;
			if(true == album_r){
				albumInt = 1;
			}
			Log.e("**********", albumInt + "");
			
			// 拼凑出xml格式的数据
			StringBuilder xmlForServer = new StringBuilder();
			xmlForServer.append("<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + SysConfig.token + "</Token>");
			xmlForServer.append("<FamilyID>" + familyID + "</FamilyID>");
			xmlForServer.append("<ISDisplayPhoto>" + albumInt + "</ISDisplayPhoto>");
			xmlForServer.append("</SetDolphinConfigRequest>");

			// 发送数据到服务器
			RequestAction.requestXml(xmlForServer.toString(), "SetDolphinConfig");

			//保存到本地
			SharedPreferences setting_sharedPreference = ctx.getSharedPreferences("setting",Context.MODE_PRIVATE);
			Editor editor = setting_sharedPreference.edit();
      	    editor.putBoolean("is_photo_info", album_r);
      	    editor.commit();
      	    
      	    Bundle bundle = new Bundle();
    	    bundle.putBoolean("is_photo_info", album_r);
    	    sendRemoteSettingBC(bundle);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 高级选项中的 观看提醒
	public static void getWatch(String watchJsonData, Context ctx, String familyID) {
		try {
			JSONObject watchJson = new JSONObject(watchJsonData);
			Boolean watch_r = watchJson.getBoolean("watch");

			Log.e("**********", watch_r + "");
			// 传给服务器的boolean值是整型，true为1，false为0
			int watchInt = 0;
			if(true == watch_r){
				watchInt = 1;
			}
			Log.e("**********", watchInt + "");
			// 拼凑出xml格式的数据
			StringBuilder xmlForServer = new StringBuilder();
			xmlForServer.append("<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + SysConfig.token + "</Token>");
			xmlForServer.append("<FamilyID>" + familyID + "</FamilyID>");
			xmlForServer.append("<ISWatchWarn>" + watchInt + "</ISWatchWarn>");
			xmlForServer.append("</SetDolphinConfigRequest>");

			// 发送数据到服务器
			RequestAction.requestXml(xmlForServer.toString(), "SetDolphinConfig");
			
			//保存到本地
			SharedPreferences setting_sharedPreference = ctx.getSharedPreferences("setting",Context.MODE_PRIVATE);
			Editor editor = setting_sharedPreference.edit();
      	    editor.putBoolean("is_watching_remind", watch_r);
      	    editor.commit();
      	    
      	  Bundle bundle = new Bundle();
  	      bundle.putBoolean("is_watching_remind", watch_r);
  	     sendRemoteSettingBC(bundle);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 高级选项中的待机模式设置，进入待机时间
	public static void getStandByTime(String standbyTimeJsonData, Context ctx, String familyID) {
		try {
			JSONObject standbyTimeJson = new JSONObject(standbyTimeJsonData);
			// public static int[] time_int= {30, 60, 120,300,600,1800,3600};
			int standbyTime_r = standbyTimeJson.getInt("standByTime");

			//int i = Data.getIndex(Data.time_int,standbyTime_r);

			Log.e("**********", Data.time_str[standbyTime_r]);

			// 拼凑出xml格式的数据
			StringBuilder xmlForServer = new StringBuilder();
			xmlForServer.append("<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + SysConfig.token + "</Token>");
			xmlForServer.append("<FamilyID>" + familyID + "</FamilyID>");
			xmlForServer.append("<StartTime>" + standbyTime_r + "</StartTime>");
			xmlForServer.append("</SetDolphinConfigRequest>");
			
			// 发送数据到服务器
			RequestAction.requestXml(xmlForServer.toString(), "SetDolphinConfig");
			
			//保存到本地
			SharedPreferences standby_sharedPreference = ctx.getSharedPreferences("standby", Context.MODE_PRIVATE);
			Editor editor = standby_sharedPreference.edit();
       	 	editor.putInt("standby_time_sp", Data.time_int[standbyTime_r]);
       	 	editor.commit();
       	 	
       	    Bundle bundle = new Bundle();
  	        bundle.putInt("standby_time_sp", standbyTime_r);
  	        bundle.putInt("standby", 0);
  	        sendRemoteSettingBC(bundle);
       	 	
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 高级选项中的待机模式设置, 模式设置
	public static void getModeSeclect(String modeJsonData, Context ctx, String familyID) {
		try {
			JSONObject modeJson = new JSONObject(modeJsonData);
			JSONObject modeJsons = modeJson.getJSONObject("standMode");
			// mpublic static String[] mode = {"时钟", "轮播相册", "直接休屏"};
			int mode_r = modeJsons.getInt("mode");

			Log.e("**********", Data.mode[mode_r]);

			// 拼凑出xml格式的数据
			StringBuilder xmlForServer = new StringBuilder();
			xmlForServer.append("<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + SysConfig.token + "</Token>");
			xmlForServer.append("<FamilyID>" + familyID + "</FamilyID>");
			xmlForServer.append("<WaitMode>" + mode_r + "</WaitMode>");
			
			if (mode_r == 0) {

				xmlForServer.append("<ContinueTime>" + 0 + "</ContinueTime>");
			} else if (mode_r == 1) {

				xmlForServer.append("<IntervalTime>" + 0 + "</IntervalTime>");
				xmlForServer.append("<ContinueTime>" + 0 + "</ContinueTime>");
				xmlForServer.append("<PlayPhotoType>" + 0 + "</PlayPhotoType>");
			}
			xmlForServer.append("</SetDolphinConfigRequest>");
			Log.e("**********", xmlForServer.toString());

			// 发送数据到服务器
			RequestAction.requestXml(xmlForServer.toString(), "SetDolphinConfig");
			
			//保存到本地
			SharedPreferences standby_sharedPreference = ctx.getSharedPreferences("standby", Context.MODE_PRIVATE);
			Editor editor = standby_sharedPreference.edit();
       	 	editor.putInt("mode_sp", mode_r);
       	 	editor.commit();
       	 	
       	    Bundle bundle = new Bundle();
  	        bundle.putInt("mode_sp", mode_r);
  	        bundle.putInt("standby", 100);
  	        sendRemoteSettingBC(bundle);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 第一种模式对应的各种设置
	public static void getModeFirst(String stillTimeJsonData, Context ctx, String familyID) {
		try {
			JSONObject stillTimeJson = new JSONObject(stillTimeJsonData);
			int stillTime_r = stillTimeJson.getInt("firstMode_stillTime");

		//	int i = Data.getIndex(Data.time_int, stillTime_r);

			Log.e("**********", Data.time_str[stillTime_r]);

			// 拼凑出xml格式的数据
			StringBuilder xmlForServer = new StringBuilder();
			xmlForServer.append("<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + SysConfig.token + "</Token>");
			xmlForServer.append("<FamilyID>" + familyID + "</FamilyID>");
			xmlForServer.append("<ContinueTime>" + stillTime_r + "</ContinueTime>");
			xmlForServer.append("</SetDolphinConfigRequest>");			

			// 发送数据到服务器
			RequestAction.requestXml(xmlForServer.toString(), "SetDolphinConfig");
			
			//保存到本地
			SharedPreferences standby_sharedPreference = ctx.getSharedPreferences("standby", Context.MODE_PRIVATE);
			Editor editor = standby_sharedPreference.edit();
       	 	editor.putInt("duration_sp", Data.time_int[stillTime_r]);
       	 	editor.commit();
       	 	
       	    Bundle bundle = new Bundle();
  	        bundle.putInt("duration_sp", stillTime_r);
  	       bundle.putInt("standby", 1);
  	        sendRemoteSettingBC(bundle);
  	       android.provider.Settings.System.putInt(ctx.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT,Data.time_int[stillTime_r]*1000);
  	        
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 第二种模式对应的各种设置
	public static void getModeSecond(String modesecondJsonData, Context ctx, String familyID) {
		  Bundle bundle = new Bundle();
		try {
			JSONObject modesecondJson = new JSONObject(modesecondJsonData);
			JSONObject secondmode = modesecondJson.getJSONObject("modeSecondSeclect");

			// 拼凑出xml格式的数据
			StringBuilder xmlForServer = new StringBuilder();
			xmlForServer.append("<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + SysConfig.token + "</Token>");
			xmlForServer.append("<FamilyID>" + familyID + "</FamilyID>");
			SharedPreferences standby_sharedPreference = ctx.getSharedPreferences("standby", Context.MODE_PRIVATE);
			Editor editor = standby_sharedPreference.edit();
       	 	
			if (secondmode.getString("secondChild").equals("intervalsTime")) {
				// 解析json数据,保存
				int secondmode_intervalsTime = secondmode.getInt("secondMode_intervalsTime");
				//int i = Data.getIndex(Data.playing_interval_int,secondmode_intervalsTime);

				Log.e("**********", Data.playing_interval_str[secondmode_intervalsTime]);

				xmlForServer.append("<IntervalTime>" + secondmode_intervalsTime + "</IntervalTime>");

				editor.putInt("interval_sp", secondmode_intervalsTime);
				bundle.putInt("interval_sp", secondmode_intervalsTime);
				
			} else if (secondmode.getString("secondChild").equals(
					"carouselTime")) {
				// 解析json数据,保存
				int secondmode_carouselTime = secondmode.getInt("secondMode_carouselTime");
			
			//	int i = Data.getIndex(Data.playing_how_long_int,secondmode_carouselTime);

				Log.e("**********", Data.playing_how_long_str[secondmode_carouselTime]);

				xmlForServer.append("<ContinueTime>" + secondmode_carouselTime + "</ContinueTime>");

				editor.putInt("playing_how_long_sp", secondmode_carouselTime);
				bundle.putInt("playing_how_long_sp", secondmode_carouselTime);
				
			} else if (secondmode.getString("secondChild").equals("which")) {
				// 解析json数据,保存
				int secondmode_which = secondmode.getInt("secondMode_which");
			//	int i = Data.getIndex(Data.photo_whichToPlay_int,secondmode_which);

				Log.e("**********", Data.photo_whichToPlay_str[secondmode_which]);

				xmlForServer.append("<PlayPhotoType>" + secondmode_which + "</PlayPhotoType>");
				
				editor.putInt("which_photo_sp", secondmode_which);
				bundle.putInt("which_photo_sp", secondmode_which);
			}
			xmlForServer.append("</SetDolphinConfigRequest>");

			// 发送数据到服务器
			RequestAction.requestXml(xmlForServer.toString(), "SetDolphinConfig");
			
			//保存到本地
       	 	editor.commit();
       	 bundle.putInt("standby", 2);
       	    sendRemoteSettingBC(bundle);
       	 	
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 高级选项中的资讯设置，播报时间1
	public static void getSetting_time1(String broadcast_time1JsonData,
			Context ctx, String familyID) {
		 Bundle bundle = new Bundle();
		try {
			JSONObject broadcast_time1 = new JSONObject(broadcast_time1JsonData);
			JSONObject broadcast_time1Json = broadcast_time1.getJSONObject("broadcast_time1");
			SharedPreferences news_sharedPreference = ctx.getSharedPreferences("news_setting", Context.MODE_PRIVATE);
			Editor editor = news_sharedPreference.edit();
			
			// 拼凑出xml格式的数据
			StringBuilder xmlForServer = new StringBuilder();
			xmlForServer.append("<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + SysConfig.token + "</Token>");
			xmlForServer.append("<FamilyID>" + familyID + "</FamilyID>");

			if (broadcast_time1Json.getString("time").equals("time")) {
				// 解析json数据,保存
				int time1hour = broadcast_time1Json.getInt("time1Hour");
				String time1minute = broadcast_time1Json.getString("time1Minute");

				Log.e("**********", time1hour + ":" + time1minute);

				xmlForServer.append("<BroadcastOneTime>" + time1hour + ":" + time1minute + "</BroadcastOneTime>");

				editor.putString("broadcast_time1", time1hour + ":" + time1minute);
				bundle.putString("broadcast_time1", time1hour + ":" + time1minute);
			} else if (broadcast_time1Json.getString("time").equals("toggle")) {
				// 解析json数据,保存
				Boolean toggle1 = broadcast_time1Json.getBoolean("toggle1");

				Log.e("**********", toggle1 + "");
				
				int toggle1Int = 0;
				if(true == toggle1){
					toggle1Int = 1;
				}
				Log.e("**********", toggle1Int + "");

				xmlForServer.append("<ISBroadcastOne>" + toggle1Int + "</ISBroadcastOne>");
				editor.putBoolean("broadcast_toggle1", toggle1);
				bundle.putBoolean("broadcast_toggle1", toggle1);
			}

			xmlForServer.append("</SetDolphinConfigRequest>");

			// 发送数据到服务器
			RequestAction.requestXml(xmlForServer.toString(), "SetDolphinConfig");
			
			//保存到本地
			editor.commit();
			bundle.putInt("time", 1);
			sendRemoteSettingBC(bundle);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 高级选项中的资讯设置，播报时间2
	public static void getSetting_time2(String broadcast_time2JsonData,
			Context ctx, String familyID) {
		try {
			 Bundle bundle = new Bundle();
			JSONObject broadcast_time2 = new JSONObject(broadcast_time2JsonData);
			JSONObject broadcast_time2Json = broadcast_time2.getJSONObject("broadcast_time2");
			SharedPreferences news_sharedPreference = ctx.getSharedPreferences("news_setting", Context.MODE_PRIVATE);
			Editor editor = news_sharedPreference.edit();
			// 拼凑出xml格式的数据
			StringBuilder xmlForServer = new StringBuilder();
			xmlForServer.append("<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + SysConfig.token + "</Token>");
			xmlForServer.append("<FamilyID>" + familyID + "</FamilyID>");

			if (broadcast_time2Json.getString("time").equals("time")) {
				// 解析json数据,保存
				int time2hour = broadcast_time2Json.getInt("time2Hour");
				String time2minute = broadcast_time2Json.getString("time2Minute");

				Log.e("**********", time2hour + ":" + time2minute);

				xmlForServer.append("<BroadcastTwoTime>" + time2hour + ":" + time2minute + "</BroadcastTwoTime>");
				editor.putString("broadcast_time2", time2hour+":"+time2minute);
				bundle.putString("broadcast_time2", time2hour+":"+time2minute);
			} else if (broadcast_time2Json.getString("time").equals("toggle")) {
				// 解析json数据,保存
				Boolean toggle2 = broadcast_time2Json.getBoolean("toggle2");

				Log.e("**********", toggle2 + "");
				
				int toggle2Int = 0;
				if(true == toggle2){
					toggle2Int = 1;
				}
				Log.e("**********", toggle2Int + "");
		
				xmlForServer.append("<ISBroadcastTwo>" + toggle2Int + "</ISBroadcastTwo>");
				editor.putBoolean("broadcast_toggle2", toggle2);
				bundle.putBoolean("broadcast_toggle2", toggle2);
			}
			xmlForServer.append("</SetDolphinConfigRequest>");

			// 发送数据到服务器
			RequestAction.requestXml(xmlForServer.toString(), "SetDolphinConfig");
			
			//保存到本地
			editor.commit();
			bundle.putInt("time", 2);
			sendRemoteSettingBC(bundle);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 紧急联系人
	public static void getMostCare(String mostcareJsonData, Context ctx, String familyID) {
		try {
			// 解析json数据
			JSONObject mostcareJson = new JSONObject(mostcareJsonData);
			String contectName = mostcareJson.getString("name");
			String contectuserid = mostcareJson.getString("userid");
			Log.e("**********", contectName);

			// 拼凑出xml格式的数据
			StringBuilder xmlForServer = new StringBuilder();
			xmlForServer.append("<SetDolphinConfigRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + SysConfig.token + "</Token>");
			xmlForServer.append("<FamilyID>" + familyID + "</FamilyID>");
			xmlForServer.append("<AgentContacts>" + contectuserid + "</AgentContacts>");
			xmlForServer.append("</SetDolphinConfigRequest>");

			// 发送数据到服务器
			RequestAction.requestXml(xmlForServer.toString(), "SetDolphinConfig");
			
			//保存到本地
			SharedPreferences mostCare_sharedPreference = ctx.getSharedPreferences("mostCare_setting", Context.MODE_PRIVATE);
			Editor editor = mostCare_sharedPreference.edit();
		    editor.putString("mostCareName", contectuserid);
		    editor.commit();  
		    
		    Bundle bundle = new Bundle();
  	        bundle.putString("mostCareName", contectName);
  	        sendRemoteSettingBC(bundle);
		    
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void getAssistantEnable(String JsonData, Context ctx, String familyID){
		// 解析json数据
		Log.e("getAssistantEnable", JsonData);
		JSONObject Json;
		try {
			Json = new JSONObject(JsonData);
			int enable = Json.getInt("assistant_enable");
			// 发送数据到服务器
			RequestAction.requestXml(XMLBody.SetDolphinHouseAssistant(SysConfig.DolphinToken, familyID, "ISHousekeeping", enable), "SetDolphinHouseAssistant");
			 Bundle bundle = new Bundle();
	  	     bundle.putInt("assistant_enable", enable);
	  	     sendRemoteVoiceBC(bundle);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static void getAssistantWorkday(String JsonData, Context ctx, String familyID){
		// 解析json数据
		Log.e("getAssistantWorkday", JsonData);
		JSONObject Json;
		try {
			Json = new JSONObject(JsonData);
			int workday = Json.getInt("assistant_workday");
			// 发送数据到服务器
			RequestAction.requestXml(XMLBody.SetDolphinHouseAssistant(SysConfig.DolphinToken, familyID, "ISOnlyWorkingDay", workday), "SetDolphinHouseAssistant");
			 Bundle bundle = new Bundle();
	  	     bundle.putInt("assistant_workday", workday);
	  	     sendRemoteVoiceBC(bundle);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void getAssistantType(String JsonData, Context ctx, String familyID){
		Log.e("getAssistantType", JsonData);
		// 解析json数据
		JSONObject Json;
		try {
			Json = new JSONObject(JsonData);
			int period = Json.getInt("assistant_type");
			RequestAction.requestXml(XMLBody.SetDolphinHouseAssistant(SysConfig.DolphinToken, familyID, "HouseKeepingType", period), "SetDolphinHouseAssistant");
			 Bundle bundle = new Bundle();
	  	     bundle.putInt("assistant_type", period);
	  	     sendRemoteVoiceBC(bundle);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void getAssistantCustom(String JsonData, Context ctx, String familyID){
		Log.e("getAssistantCustom", JsonData);
		// 解析json数据
		JSONObject Json;
		try {
			Json = new JSONObject(JsonData);
			String st = Json.getString("starttime");
			String et = Json.getString("endtime");
			int customday = Json.getInt("customday");
			RequestAction.requestXml(XMLBody.SetDolphinHouseAssistantCustom(SysConfig.DolphinToken, familyID, st,et,customday), "SetDolphinHouseAssistant");
			
			 Bundle bundle = new Bundle();
			 bundle.putBoolean("custom", true);
	  	     bundle.putString("starttime", st);
	  	     bundle.putString("endtime", et);
	  	     bundle.putInt("customday", customday);
	  	     sendRemoteVoiceBC(bundle);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void sendRemoteSettingBC(Bundle value){
		Intent in = new Intent(SysConfig.REMOTE_SETTING_ACTION);
		in.putExtras(value);
		LauncherApp.getInstance().sendBroadcast(in);
	}
	private static void sendRemoteVoiceBC(Bundle value){
		Intent in = new Intent(SysConfig.REMOTE_ASSISTANT_ACTION);
		in.putExtras(value);
		LauncherApp.getInstance().sendBroadcast(in);
	}
}
