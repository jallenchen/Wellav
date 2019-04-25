package com.wellav.dolphin.mmedia.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.wellav.dolphin.mmedia.commands.DolphinCmd;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.ClockInfo;
import com.wellav.dolphin.mmedia.entity.InviteMessage;
import com.wellav.dolphin.mmedia.entity.MessageInform;

public class JsonUtil {

	public static JSONObject getVolumeJsonObject(int value) {
		JSONObject jsonObject=new JSONObject();

		try {
			jsonObject.put("setting", "getVolume");
			jsonObject.put("volume",value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject getBrightnessJsonObject(int value) {
		JSONObject jsonObject=new JSONObject();

		try {
			jsonObject.put("setting", "getBrightness");
			jsonObject.put("brightness",value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject getVoiceJsonObject(int value) {
		JSONObject jsonObject=new JSONObject();

		try {
			jsonObject.put("setting", "getVoice");
			jsonObject.put("voice",value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject getFaceJsonObject(Boolean value) {
		JSONObject jsonObject=new JSONObject();

		try {
			jsonObject.put("setting", "getFace");
			jsonObject.put("face",value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject getAlbumInfoJsonObject(Boolean value) {
		JSONObject jsonObject=new JSONObject();

		try {
			jsonObject.put("setting", "getAlbum_info");
			jsonObject.put("album_info",value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject getWatchJsonObject(Boolean value) {
		JSONObject jsonObject=new JSONObject();

		try {
			jsonObject.put("setting", "getWatch");
			jsonObject.put("watch",value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject getStandbyTimeJsonObject(int value) {
		JSONObject jsonObject=new JSONObject();

		try {
			jsonObject.put("setting", "getStandByTime");
			jsonObject.put("standByTime",value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject getModeseclectJsonObject(int value) {
		JSONObject jsonObject=new JSONObject();
		JSONObject mode=new JSONObject();
		try {
			jsonObject.put("setting", "getModeSeclect");
	        mode.put("mode", value);
			jsonObject.put("standMode",mode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	//看家助手
	public static JSONObject getAssistantEnableJson(int value) {
		JSONObject jsonObject=new JSONObject();

		try {
			jsonObject.put("setting", "getAssistantEnable");
			jsonObject.put("assistant_enable",value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject getAssistantWorkdayJson(int value) {
		JSONObject jsonObject=new JSONObject();

		try {
			jsonObject.put("setting", "getAssistantWorkday");
			jsonObject.put("assistant_workday",value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	public static JSONObject getAssistantPeriodJson(int value) {
		JSONObject jsonObject=new JSONObject();

		try {
			jsonObject.put("setting", "getAssistantType");
			jsonObject.put("assistant_type",value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	public static JSONObject getAssistantCustomJson(ClockInfo value) {
		JSONObject jsonObject=new JSONObject();

		try {
			jsonObject.put("setting", "getAssistantCustom");
			jsonObject.put("starttime",value.getmStartTime());
			jsonObject.put("endtime",value.getmEndTime());
			jsonObject.put("customday",value.getmDays());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	public static JSONObject getModefirstJsonObject(int value) {
		JSONObject jsonObject=new JSONObject();

		try {
			jsonObject.put("setting", "getModeFirst");
			jsonObject.put("firstMode_stillTime",value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject getModesecondJsonObject(String tag,int value) {
		JSONObject jsonObject=new JSONObject();
        JSONObject modeSecondSeclect=new JSONObject();
		try {
			jsonObject.put("setting", "getModeSecond");
			modeSecondSeclect.put("secondChild", tag);
			if (tag.equals("intervalsTime")) {
				modeSecondSeclect.put("secondMode_intervalsTime", value);
			}else if (tag.equals("carouselTime")) {
				modeSecondSeclect.put("secondMode_carouselTime", value);
			}else if (tag.equals("which")) {
				modeSecondSeclect.put("secondMode_which", value);
			}
			jsonObject.put("modeSecondSeclect",modeSecondSeclect);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject getSettingTime1JsonObject(String tag,int hour,int minute,boolean set) {
		JSONObject jsonObject=new JSONObject();
        JSONObject time1=new JSONObject();
		try {
			jsonObject.put("setting", "getSetting_time1");
			
			time1.put("time", tag);
			
			if (tag.equals("time")) {
				time1.put("time1Hour", hour);
				time1.put("time1Minute", minute);
			}else if (tag.equals("toggle")) {
				time1.put("toggle1", set);
			}
			jsonObject.put("broadcast_time1",time1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject getSettingTime2JsonObject(String tag,int hour,int minute,boolean set) {
		JSONObject jsonObject=new JSONObject();
        JSONObject time2=new JSONObject();
		try {
			jsonObject.put("setting", "getSetting_time2");
			
			time2.put("time", tag);
			
			if (tag.equals("time")) {
				time2.put("time2Hour", hour);
				time2.put("time2Minute", minute);
			}else if (tag.equals("toggle")) {
				time2.put("toggle2", set);
			}
			jsonObject.put("broadcast_time2",time2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject getMostcareJsonObject(String name,String userid) {
		JSONObject jsonObject=new JSONObject();

		try {
			jsonObject.put("setting", "getMostCare");
			jsonObject.put("name",name);
			jsonObject.put("userid",userid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	public static JSONObject getPrivacyJsonObject(boolean is) {
		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", "setting");
			JSONObject jsonMessageContent = new JSONObject();
			jsonMessageContent.put("setting", "getPrivacy");
			jsonMessageContent.put("ISPrivacy",is);
			jsonObject.put("message",jsonMessageContent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static JSONObject getUserWatchAuthJson(String familyid,String userid,int auth) {
		
		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", "setting");
			JSONObject jsonMessageContent = new JSONObject();
			jsonMessageContent.put("setting", "getUserWatchAuth");
			jsonMessageContent.put("familyid",familyid);
			jsonMessageContent.put("userid",userid);
			jsonMessageContent.put("WatchAuth",auth);
			jsonObject.put("message",jsonMessageContent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	
	
    public static String getInviteJson(InviteMessage content ){
	//	String mBody = RequestString.UserJoinFamily(SysConfig.dtoken, SysConfig.uid, fUerInfo.getLoginName());
	//	requestXml(mBody,"UserJoinFamily");
    	
		JSONObject ImCmd = new JSONObject(); 
		try {
			ImCmd.put("username",content.getUserName());
			ImCmd.put("usernickname",content.getUserNickname());
			ImCmd.put("userid",content.getUserId());
			ImCmd.put("familyuserid",content.getUserFamilyId());
			ImCmd.put("familyid",content.getDeviceFamilyId());
			ImCmd.put("devicename",content.getDeviceName());
			ImCmd.put("deviceid",content.getDeviceId());
			ImCmd.put("deviceuserid",content.getDeviceUserID());
			ImCmd.put("managerid",content.getManagerId());
			ImCmd.put("managername",content.getManagerName());
			ImCmd.put("managernickname",content.getManagerNickname());
			ImCmd.put("type",content.getType());
			ImCmd.put("state",content.getStatus());
			ImCmd.put("action",content.getAction());
			ImCmd.put("content".toString(),content.getContent());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ImCmd.toString();
	}
    public static  InviteMessage getInviteMessage(String from,String time,JSONObject msg){
    	InviteMessage message = new InviteMessage();
    	try {
			message.setUserName(msg.getString("username"));
			message.setUserNickname(msg.getString("usernickname"));
			message.setUserId(msg.getString("userid"));
			message.setUserFamilyId(msg.getString("familyuserid"));
			message.setDeviceFamilyId(msg.getString("familyid"));
			message.setDeviceName(msg.getString("devicename"));
			message.setDeviceId(msg.getString("deviceid"));
			message.setDeviceUserID(msg.getString("deviceuserid"));
			message.setManagerId(msg.getString("managerid"));
			message.setManagerName(msg.getString("managername"));
			message.setManagerNickname(msg.getString("managernickname"));
			message.setContent(msg.getString("content"));
			message.setTime(time);
			message.setStatus(msg.getInt("state"));
			message.setAction(msg.getInt("action"));
			message.setType(msg.getString("type"));
		//	SQLiteManager.getInstance().saveInviteMessage(message);

		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return message;
	}
    
	
	// 发送给服务器的消息体，json格式
		public static String getBOxMsgJsonObject(MessageInform content) {
			
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("type", "normal");
				
				JSONObject jsonMessageContent = new JSONObject();
				jsonMessageContent.put("name",content.getName());
				jsonMessageContent.put("userid",content.getUserID());
				jsonMessageContent.put("familyid",content.getFamilyId());
				jsonMessageContent.put("deviceid",content.getDeviceID());
				jsonMessageContent.put("dolphinName",content.getDolphinName());
				jsonMessageContent.put("eventType",content.getEventType());
				jsonMessageContent.put("beenAnswered",content.getBeenAnswered());
				jsonMessageContent.put("num",content.getNum());
				jsonMessageContent.put("talkingID",content.getTalkingID());
				jsonMessageContent.put("meettingID",content.getMeettingID());
				
				jsonObject.put("message",jsonMessageContent);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			CommFunc.PrintLog(5,"getJsonObject", jsonObject.toString());
			return jsonObject.toString();
		}
		
		public static  MessageInform getBoxMessage(String from,String time,JSONObject msgjson){
			
			MessageInform message = new MessageInform();
			try {
			JSONObject msg = new JSONObject(msgjson.getString("message"));
			message.setEventType(msg.getString("eventType"));
			message.setUserID(msg.getString("userid"));
			message.setName(msg.getString("name"));
			message.setDolphinName(msg.getString("dolphinName"));
			message.setDeviceID(msg.getString("deviceid"));
			message.setFamilyId(msg.getString("familyid"));
			
			message.setTime(time);
			message.setBeenAnswered(msg.getString("beenAnswered"));
			message.setNum(msg.getString("num"));
			message.setTalkingID(msg.getString("talkingID"));
			message.setMeettingID(msg.getString("meettingID"));
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return message;
		}
		
		
		
		// 发送给服务器的xml格式
		public static String getXmlMessage(String Token,int RecipientType,String RecipientID,String MessageContent){
			
			StringBuilder xmlForServer = new StringBuilder();
			
			xmlForServer.append("<UploadMessageBoxRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">");
			xmlForServer.append("<Token>" + Token + "</Token>");
			xmlForServer.append("<RecipientType>" + RecipientType + "</RecipientType>");
			xmlForServer.append("<RecipientID>" + RecipientID + "</RecipientID>");
			xmlForServer.append("<Message>" + MessageContent + "</Message>");
			xmlForServer.append("</UploadMessageBoxRequest>");
			CommFunc.PrintLog(5,"getXmlMessage", xmlForServer.toString());
			return xmlForServer.toString();
		}
	
}
