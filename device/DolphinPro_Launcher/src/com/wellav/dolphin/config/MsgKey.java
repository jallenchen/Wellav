package com.wellav.dolphin.config;

public class MsgKey {
	public static final int KEY_RESULT_SUCCESS = 0; // 用户登录 成功
	public static final int KEY_RESULT_FAIL = -1; // 失败
	public static final int KEY_STATUS_200 = 200; // 失败
	public static final int KEY_STATUS_403 = 403; // 失败
	public static final int SLN_CallClosed = 1001;
	public static final int SLN_CallFailed = 1002;
	public static final int SLN_180Ring = 1003;
	public static final int SLN_CallAccepted = 1004;
	public static final int SLN_CallVideo = 1005;
	public static final int SLN_NetWorkChange = 1006;
	public static final int SLN_WebRTCStatus = 1007;// by cpl
	public static final int SLN_CallHasAccepted = 1008;
	public static final int SLN_CallChat2GroupAccepted = 1009;
	public static final int SLN_CallHangUp = 1010;
	public static final int SLN_NewCallMessage = 1011;
	public static final int SLN_CloseMonitor = 1012;
	public static int broadmsg_sip = 2000;
	public static int grpv_listener_onResponse = 2001;
	public static int grpv_listener_onRequest = 2002;
	public static int grpv_listener_onCreate = 2003;
	public static int grpv_listener_inviteMebmer = 2004;
	
	public static final String Broadcast_start_videoPhone="Broadcast_start_videoPhone";//开始通话广播
	public static final String Broadcast_end_videoPhone="Broadcast_end_videoPhone";//结束通话广播

	public static final String BROADCAST_MANAGER_ACTION="Broadcast_been_manager";//结束通话广播
	
	public static final String Uid = "loginname";
	public static final String DToken = "wellav_token";
	public static final String RToken = "rtctoken";
	public static String key_familyid = "family_id";
	public static String key_msg_kickoff = "kickoff";
	public static String key_privacy= "ISPrivacy";
	public static String Key_FirstEnter = "firstenter";
	public static String Key_TeamID = "teamId";
	public static String DEVICE= "1";
	public static String PHONE= "0";
	public static int ON = 0;
	public static int OFF= 1;
	public static int NO_NET= -111;
	public static int BEINVITEED=3;//邀请

	/**
	 * 空闲状态
	 */
	public static final int DOLPHINSTATE_FREE = 0;
	/**
	 * 监控输出状态
	 */
	public static final int DOLPHINSTATE_SHOW = 1;
	/**
	 * 双人视频对话状态
	 */
	public static final int DOLPHINSTATE_CHAT = 2;
	/**
	 * 群组对话状态
	 */
	public static final int DOLPHINSTATE_MULTICHAT = 3;
	/**
	 * 会议模式
	 */
	public static final int DOLPHINSTATE_MEETING = 4;
	/**
	 * 正在呼叫监控通话
	 */
	public static final int DOLPHINSTATE_CALLING_MONITOR = 5;
	/**
	 * 正在呼叫视频通话
	 */
	public static final int DOLPHINSTATE_CALLING_CHAT = 6;
	/**
	 * 仅用于海豚端，表示正在初始化视频通话
	 */
	public static final int DOLPHINSTATE_CALLING_MULTICHAT = 7;
	/**
	 * 仅限于海豚端，表示正在初始化会议模式
	 */
	public static final int DOLPHINSTATE_CALLING_MEETING = 8;
	
	public static final int BUSY = 1;
}
