package com.wellav.dolphin.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.wellav.dolphin.bean.InviteMessage;
import com.wellav.dolphin.bean.MessageInform;

public class JsonUtils {
	
    public static JSONObject msgJson(InviteMessage content ){
    	JSONObject ImCmd = new JSONObject(); 
		try {
			ImCmd.put("username",content.getUserName());
			ImCmd.put("usernickname",content.getUserNickname());
			ImCmd.put("userid",content.getUserId());
			ImCmd.put("familyid",content.getFamilyId());
			ImCmd.put("familyuserid",content.getDeviceUserId());
			ImCmd.put("devicename",content.getDeviceName());
			ImCmd.put("deviceid",content.getDeviceId());
			ImCmd.put("deviceuserid",content.getDeviceUserId());
			ImCmd.put("managerid",content.getManagerId());
			ImCmd.put("managername",content.getManagerName());
			ImCmd.put("managernickname",content.getManagerNickname());
			ImCmd.put("type",content.getType());
			ImCmd.put("state",content.getStatus());
			ImCmd.put("action",content.getAction());
			ImCmd.put("content".toString(),content.getContent());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ImCmd;
	}

	// 发送给服务器的消息体，json格式
	public static String getJsonObject(MessageInform content,String type) {
		
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			
			JSONObject jsonMessageContent = new JSONObject();
			jsonMessageContent.put("name",content.getName());
			jsonMessageContent.put("dolphinName",content.getDolphinName());
			jsonMessageContent.put("deviceid",content.getDeviceID());
			jsonMessageContent.put("eventType",content.getEventType());
			jsonMessageContent.put("beenAnswered",content.getBeenAnswered());
			jsonMessageContent.put("num",content.getNum());
			jsonMessageContent.put("talkingID",content.getTalkingID());
			jsonMessageContent.put("meettingID",content.getMeettingID());
			jsonMessageContent.put("userid",content.getUserID());
			jsonMessageContent.put("familyid",content.getFamilyID());
			jsonObject.put("message",jsonMessageContent);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Util.PrintLog("getJsonObject", jsonObject.toString());
		return jsonObject.toString();
	}
	
	// 发送给服务器的消息体，json格式
		public static String getNotifyJson(String name,String deviceName,String type) {
			
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("type", "notify");
				
				JSONObject jsonMessageContent = new JSONObject();
				jsonMessageContent.put("name",name);
				jsonMessageContent.put("dolphinName",deviceName);
				jsonMessageContent.put("eventType",type);
				jsonObject.put("message",jsonMessageContent);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Util.PrintLog("getJsonObject", jsonObject.toString());
			return jsonObject.toString();
		}
		
	
	public static  MessageInform getBoxMessage(JSONObject msgjson){
		
		MessageInform message = new MessageInform();
		try {
		JSONObject msg = new JSONObject(msgjson.getString("message"));
		message.setEventType(msg.getString("eventType"));
		message.setUserID(msg.getString("userid"));
		message.setName(msg.getString("name"));
		message.setDolphinName(msg.getString("dolphinName"));
		message.setDeviceID(msg.getString("deviceid"));
		message.setFamilyID(msg.getString("familyid"));
		
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
		Util.PrintLog("getXmlMessage", xmlForServer.toString());
		return xmlForServer.toString();
	}
}
