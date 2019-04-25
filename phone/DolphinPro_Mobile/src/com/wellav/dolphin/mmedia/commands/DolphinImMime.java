package com.wellav.dolphin.mmedia.commands;

public class DolphinImMime {
	/**
	 * 解析json消息有误，反馈消息中附带有误的命令
	 */
	public static final String DplMime_jsonErr_json = "jsonErr/json";
	/**
	 * 手机端呼叫发送的消息类型，消息中会有呼叫类型
	 */
	public static final String DplMime_chatcall_json = "chatcall/json";
	public static final String DplMime_multicall_json = "multicall/json";
	/**
	 * 海豚端回复的消息类型，消息中会有呼叫结果
	 */
	public static final String DplMime_callBack_json = "callBack/json";
	/**
	 * 海豚端在需要变更会话模式后，创建好了群组会话，会告知所有参会人员群组会话ID
	 */
	public static final String DplMime_grpId_json = "grpId/json";
	/**
	 * 海豚端在需要变更会话模式后，创建好了群组会话，会告知所有参会人员群组会话ID
	 */
	public static final String DplMime_callChange_json = "callChange/json";	
	/**
	 * 海豚端在需要变更会话模式后，创建好了群组会话，会告知所有参会人员群组会话ID
	 */
	public static final String DplMime_callCancel_json = "callCancel/json";	
	/**
	 * 手机端在点对点对话时，邀请某人，向海豚端告知，请求其操作
	 */
	public static final String DplMime_invite_json = "invite/json";	
	public static final String DplMime_monitor_json = "monitor/json";	
	public static final String DplMime_monitorClose_json = "monitorclose/json";	
	
	public static final String DplMime_msgnotify_json = "msgnotify/json";
	public static final String DplMime_settingResult_json = "remotesetting/json";
	
	public static final String DplMime_notification_json = "notification/json";
	public static final String DplMime_online_json = "online/json";
	
	public static final String DplMime_power_net_request_json = "power_net_request/json";
	public static final String DplMime_power_net_response_json = "power_net_response/json";
	
}
