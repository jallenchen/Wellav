package com.wellav.dolphin.mmedia.commands;

public enum DolphinCmd {
	/**
	 * DolphineImMime.DplMime_jsonErr_json
	 */	
	Dlp_errCmd,
	Dlp_errJson,
	/**
	 * DolphineImMime.DplMime_call_json
	 */
	Dlp_callType, //告知海豚自己期望的呼叫类型chat或monitor
	/**
	 * DolphineImMime.DplMime_callBack_json
	 * 0手机端收到了消息呼叫
	 * 1为海豚端calling正忙,或者有另一个账号正在呼叫
	 * 2为有人正在进行视频，不能监控，只能选择申请加入视频 
	 * 3开启了隐私模式拒绝监听，因为自动否决的只能是隐私模式的监听
	 * 4隐私模式下手动拒绝呼叫了
	 * 5隐私模式下呼叫超时了
	 * 6接听了
	 * 7为海豚端收到onNewCall
	 * 8为手机端正忙着进行另一个会话
	 */
	Dlp_callResult,
	/**
	 * DolphineImMime.DplMime_callChange_json
	 */
	Dlp_callChangeMulti,
	/**
	 * DolphineImMime.DplMime_grpId_json
	 */
	Dlp_GrpShowMeetId, 
	/**
	 * DplMime_invite_json
	 */
	Dlp_inviteName,
	Dlp_creatorName,

}
