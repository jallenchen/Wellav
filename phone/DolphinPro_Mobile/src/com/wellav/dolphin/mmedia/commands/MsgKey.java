package com.wellav.dolphin.mmedia.commands;

public class MsgKey {

    public static String KEY_ACODEC="key_acodec";
    public static String KEY_VCODEC="key_vcodec";
    public static String KEY_VFORMAT="key_vformat"; //视频格式
    public static String KEY_VFRAMES="key_vframes";//视频帧数 
    public static String KEY_VOGL="key_vogl";//opengl开关
    public static String KEY_AUTOACP="key_autoacp";
    
    public static final int KEY_RESULT_SUCCESS = 0; //用户登录 成功
    public static final int KEY_RESULT_FAIL = -1; //失败
    public static final int KEY_STATUS_200 = 200; //失败
    
    public static final int SLN_CallClosed = 1001;
    public static final int SLN_CallFailed = 1002;   
    public static final int SLN_180Ring = 1003;
    public static final int SLN_CallAccepted = 1004;
    public static final int SLN_CallVideo = 1005;
    public static final int SLN_NetWorkChange = 1006;
    public static final int SLN_WebRTCStatus = 1007;//by cpl
    public static final int SLN_CallHasAccepted = 1008;
    public static final int SLN_CallChat2GroupAccepted = 1009;
    public static final int SLN_CallHangUp = 1010;
    public static final int SLN_NewCallMessage = 1011;
	public static final int SLN_Monitor2ChatCall = 1023;
	public static final int SLN_MonitorAccept = 1024;
	
	
    public static int broadmsg_sip = 2000;
    public static int grpv_listener_onResponse = 2001;
    public static int grpv_listener_onRequest = 2002;
    public static int grpv_listener_onCreate = 2003;
    public static int grpv_listener_inviteMebmer = 2004;
    
    public static String intent_key_object = "intent_key_object";
    public static String key_watch = "WatchAuth";
    public static String newBoxMsg = "newBox";
    public static String newInviteMsg = "newInvite";
    
    public static int ACODEC_ILBC = 0;
    public static int ACODEC_OPUS = 1;
    
    public static int VCODEC_VP8 = 0;
    public static int VCODEC_H264 = 1;
    
    public static int VIDEO_SD = 0;
    public static int VIDEO_FL = 1;
    public static int VIDEO_HD = 2;
    public static int AGREED = 0;//同意
    public static int BELINK =1;//连接
    public static int BEAPPLYED =2;//对方申请加入
    public static int BEINVITEED=3;//邀请
    public static int BEMANAGER=4;//邀请
    public static int BEINVITEEDBYDV=100;//邀请
    /**
	 * 空闲状态
	 */
	public static final int DOLPHINSTATE_FREE = 0;
	/**
	 * 监控输出状态
	 */
	public static final int DOLPHINSTATE_SHOW =1;
	/**
	 * 双人视频对话状态
	 */
	public static final int DOLPHINSTATE_CHAT=2;
	/**
	 * 群组对话状态
	 */
	public static final int DOLPHINSTATE_MULTICHAT=3;
	/**
	 * 会议模式
	 */
	public static final int DOLPHINSTATE_MEETING=4;
	/**
	 * 正在呼叫监控通话
	 */
	public static final int DOLPHINSTATE_CALLING_MONITOR=5;
	/**
	 * 正在呼叫视频通话
	 */
	public static final int DOLPHINSTATE_CALLING_CHAT=6;
	/**
	 * 仅用于海豚端，表示正在初始化视频通话
	 */
	public static final int DOLPHINSTATE_CALLING_MULTICHAT=7;
	/**
	 * 仅限于海豚端，表示正在初始化会议模式
	 */
	public static final int DOLPHINSTATE_CALLING_MEETING=8;
	public static final int DOLPHINSTATE_ACCEPTING_MONITOR=9;
	/**
	 * 正在呼叫视频通话
	 */
	public static final int DOLPHINSTATE_ACCEPTING_CHAT=10;
	/**
	 * 仅用于海豚端，表示正在初始化视频通话
	 */
	public static final int DOLPHINSTATE_ACCEPTING_MULTICHAT=11;
	/**
	 * 仅限于海豚端，表示正在初始化会议模式
	 */
	public static final int DOLPHINSTATE_ACCEPTING_MEETING=12;
	
	public static final int CALL_MONITOR=0;
	/**
	 * 正在呼叫视频通话
	 */
	public static final int CALL_CHAT=1;
	/**
	 * 仅用于海豚端，表示正在初始化视频通话
	 */
	public static final int CALL_MULTI_CHAT=2;
	/**
	 * 仅限于海豚端，表示正在初始化会议模式
	 */
	public static final int CALL_MEETING=3;
	public static final int CLOSE_FAIL=-1;
	public static final int CLOSE_SUCCESS = 0;
	public static final int CALLING_BUSY = 1;
	public static final int VIDEO_BUSY = 2;
	public static final int MEETING_BUSY = 3;
	public static final int AUTO_HANGUP = 0;
	public static final int BTN_HANGUP = 1;
	public static final int TIMEOUT_HANGUP = 2;
	
	
	public static final String EVENTTYPE_WATCH = "1"; 
	public static final String EVENTTYPE_CALL = "2"; 
	public static final String EVENTTYPE_CHECKPHOTO = "16"; 
	
	public static final String PHONE = "0"; 
	public static final String DEVICE = "1"; 
	//////////////////////////////////////////
	public static final int TO_FAMILY = 1;
	
	public static final int Up_FamilyNickName = 1;
	public static final int Up_Avater = 2;
	public static final int Up_Nickname = 3;
	public static final int Up_City = 4;
	
}
