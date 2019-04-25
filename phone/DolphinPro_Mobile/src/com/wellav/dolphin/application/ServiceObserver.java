package com.wellav.dolphin.application;

import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.TeamServiceObserver;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.activity.CallingGroupChatActivity;
import com.wellav.dolphin.mmedia.activity.CallingMeetingActivity;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.netease.LogUtil;
import com.wellav.dolphin.mmedia.netease.SendNitificationMsgUtil;
import com.wellav.dolphin.mmedia.netease.avchat.AVChatActivity;
import com.wellav.dolphin.mmedia.netease.avchat.AVChatProfile;

public class ServiceObserver {
	private static String TAG = "ServiceObserver";
	private Context mContext;

	public ServiceObserver(Context context) {
		mContext = context;
		// 注册自定义通知
		regiesterCustomNotification(true);
		// 注册登录状态
		registerOnlineStateObservers(true);
		// 注册网络通话来电
		registerEnableAVChat();
		//注册群组监听
		registerTeamObserver();
		//注册监听系统消息
		registerSystemMessageObserver();
		//注册监听IM消息
		registerIMMessageObserver();
	}

	private void regiesterCustomNotification(boolean register) {
		NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(commandObserver, register);
	}
	private void registerIMMessageObserver(){
		 NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, true);
	}

	private void registerOnlineStateObservers(boolean register) {
		NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, register);
	}
	private void registerTeamObserver() {
		NIMClient.getService(TeamServiceObserver.class).observeTeamUpdate(teamUpdateObserver, true);
		NIMClient.getService(TeamServiceObserver.class).observeTeamRemove(teamRemoveObserver, true);
		NIMClient.getService(TeamServiceObserver.class).observeMemberUpdate(memberUpdateObserver, true);
		NIMClient.getService(TeamServiceObserver.class).observeMemberRemove(memberRemoveObserver, true);
	}
	

	/**
	 * 音视频通话配置与监听
	 */
	private void registerEnableAVChat() {
		registerAVChatIncomingCallObserver(true);
	}
	
	Observer<List<IMMessage>> incomingMessageObserver =
		    new Observer<List<IMMessage>>() {
		        /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
		        public void onEvent(List<IMMessage> messages) {
		            // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
		        }
		    };
	
	private void registerSystemMessageObserver(){
		NIMClient.getService(SystemMessageObserver.class)
	    .observeReceiveSystemMsg(new Observer<SystemMessage>() {
	            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

				@Override
	            public void onEvent(SystemMessage message) {
	            }
	        }, true);
	}
	
	@SuppressWarnings("serial")
	private void registerAVChatIncomingCallObserver(boolean register) {
		AVChatManager.getInstance().observeIncomingCall(
				new com.netease.nimlib.sdk.Observer<AVChatData>() {
					@Override
					public void onEvent(AVChatData data) {
						String extra = data.getExtra();
						Log.e("Extra", "Extra Message->" + extra);
						// 有网络来电打开AVChatActivity
						AVChatProfile.getInstance().setAVChatting(true);
						AVChatActivity.launch(DemoCache.getContext(), data,
								AVChatActivity.FROM_BROADCASTRECEIVER);
					}
				}, register);
	}

	@SuppressWarnings("serial")
	com.netease.nimlib.sdk.Observer<StatusCode> userStatusObserver = new com.netease.nimlib.sdk.Observer<StatusCode>() {

		@Override
		public void onEvent(StatusCode code) {
			LogUtil.e(TAG, "StatusCode:" + code);
			if (code.wontAutoLogin()) {
				kickOut(code);
			} else {
				if (code == StatusCode.NET_BROKEN) {
				} else if (code == StatusCode.UNLOGIN) {
				} else if (code == StatusCode.CONNECTING) {
				} else if (code == StatusCode.LOGINING) {
				} else if (code == StatusCode.LOGINED) {
					queryTeams();
				} else {

				}
			}
		}
	};
	
	/*
	 * 查询所加的家庭组
	 */
	private void queryTeams(){
		NIMClient.getService(TeamService.class).queryTeamList().setCallback(new RequestCallbackWrapper<List<Team>>() {

			@Override
			public void onResult(int code,
					List<Team> ts, Throwable arg2) {
				if (code == ResponseCode.RES_SUCCESS) {
					SysConfig.getInstance().setTeams(ts);
				}
			}
		});
	}

	private void kickOut(StatusCode code) {
		if (code == StatusCode.PWD_ERROR) {
			LogUtil.e("Auth", "user password error");
			Toast.makeText(mContext, R.string.login_failed, Toast.LENGTH_SHORT)
					.show();
		} else {
			LogUtil.i("Auth", "Kicked!");
		}
	}

	   @SuppressWarnings("serial")
		com.netease.nimlib.sdk.Observer<CustomNotification> commandObserver =  new com.netease.nimlib.sdk.Observer<CustomNotification>() {
		        @Override
		        public void onEvent(CustomNotification message) {
		            if (message.getSessionType() != SessionTypeEnum.P2P ) {
		            	
		            	 String content = message.getContent();
		 	            JSONObject  data = JSON.parseObject(content);
		 	            String type  = data.getString("type");
		 	             String  name = data.getString("name");
		 	             boolean ischk = data.getBooleanValue("value");
		 	             LogUtil.e(TAG, message.getFromAccount()+type+":"+name+":"+ischk);
		 	            if(type.equals("audiocontrol")){
		 	            	if(SysConfig.getInstance().getStatus() == SysConfig.MeetingAccept){
		 	            	   Intent intent = new Intent(SysConfig.Broadcast_Action_MuteAudio);
		 	 	     	       intent.putExtra("value", ischk);
		 	 	     	       intent.putExtra("name", name);
		 	 	     	    mContext.sendBroadcast(intent);
		 	            	}
		 	            }
		                return;
		            }
		            
		            String content = message.getContent();
		            JSONObject  data = JSON.parseObject(content);
		            String type  = data.getString("type");
		            content = data.getString("content");
		            LogUtil.e(TAG, message.getFromAccount()+type+":"+content+":"+SysConfig.getInstance().getStatus());
		            if(type.equals("monitor")){
		             if(SysConfig.getInstance().getStatus() == SysConfig.Free){
		     	       Intent intent = new Intent(SysConfig.Broadcast_Action_Roomname);
		     	       intent.putExtra("roomname", content);
		     	      mContext.sendBroadcast(intent);
		            	 return;
		             }
		            }else if(type.equals("groupchat")){
		            	 if(SysConfig.getInstance().getStatus() == SysConfig.Free){
			            		CallingGroupChatActivity.launch(mContext,message.getFromAccount(), content);
		  	            }
		            }else if(type.equals("meeting")){
		            	 if(SysConfig.getInstance().getStatus() == SysConfig.Free){
		            		 CallingMeetingActivity.launch(mContext,message.getFromAccount(), content);
		  	            }
		             }
		        }
		    };
	
	// 创建群组资料变动观察者
	Observer<List<Team>> teamUpdateObserver = new Observer<List<Team>>() {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
	    public void onEvent(List<Team> teams) {
	    }
	};
	
	// 创建群组被移除的观察者。在退群，被踢，群被解散时会收到该通知。
	Observer<Team> teamRemoveObserver = new Observer<Team>() {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
	    public void onEvent(Team team) {
	    // 由于界面上可能仍需要显示群组名等信息，因此参数中会返回 Team 对象。
	    // 该对象的 isMyTeam 接口返回为 false。
	    }
	};
	
	// 群成员资料变化观察者通知。群组添加新成员，成员资料变化会收到该通知。
	// 返回的参数为有更新的群成员资料列表。
	Observer<List<TeamMember>> memberUpdateObserver = new Observer<List<TeamMember>>() {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
	    public void onEvent(List<TeamMember> members) {
	    }
	};
	
	// 移除群成员的观察者通知。
	private Observer<TeamMember> memberRemoveObserver = new Observer<TeamMember>() {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
	    public void onEvent(TeamMember member) {
	    }
	};
}
