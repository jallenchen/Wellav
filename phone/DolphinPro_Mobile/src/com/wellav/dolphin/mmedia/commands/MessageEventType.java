package com.wellav.dolphin.mmedia.commands;

public class MessageEventType {

//	// 视频类 ，本地获取消息
//	eventType = look_you;  查看了您
	public static final String EVENTTYPE_LOOK_HOME = "1"; 
//	eventType = call_you;  呼叫了您
	public static final String EVENTTYPE_CALL_HOME = "2"; 
//	eventType = invite_talk;  邀您加入群聊
	public static final String EVENTTYPE_INVITE_TALK = "3"; 
//	eventType = invite_meetting;  邀您加入会议
	public static final String EVENTTYPE_INVITE_MEETTING = "4"; 
//	eventType = call_urgency;  紧急呼叫了您
	public static final String EVENTTYPE_CALL_URGENCY = "5"; 
	
//	// 照片类  ，服务器获取消息
//	eventType = post_pic;   上传照片
	public static final String EVENTTYPE_POST_PIC = "6"; 
	public static final String EVENTTYPE_LOOK_PIC = "16"; 

//	// 联系人类 ，服务器获取消息
//	eventType = phone_manager;  成为本机管理者
	public static final String EVENTTYPE_PHONE_MANAGER = "7"; 
//	eventType = join_family;   加入家庭组
	public static final String EVENTTYPE_JOIN_FANILY = "8"; 
//	eventType = exit_family;  退出家庭组
	public static final String EVENTTYPE_EXIT_FAMILY = "9"; 
	public static final String EVENTTYPE_RELEASE_FAMILY = "14"; 
	public static final String EVENTTYPE_BEEN_MANAGER = "15"; 

//	// 设置类 ，服务器获取消息
//	eventType = distance_setting;  远程修改设置
	public static final String EVENTTYPE_DISTANCE_SETTING = "10"; 
	
//	// 异常类 ，服务器获取消息
//	eventType = wrong_pic;  异常照片
	public static final String EVENTTYPE_WRONG_PIC = "11"; 
//	eventType = wrong_video;  异常视频
	public static final String EVENTTYPE_WRONG_VIDEO = "12"; 
	public static final String EVENTTYPE_WATCH_PIC = "13"; 
	
	// 未接听或者未加入       //	已接听或者已加入
	public static final String BEEN_ANSWERED = "1"; 
	public static final String NO_ANSWERED = "0"; 
}
