package com.wellav.dolphin.application;

import java.util.ArrayList;
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
import com.netease.nimlib.sdk.RequestCallback;
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
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.wellav.dolphin.calling.CallingGroupChatActivity;
import com.wellav.dolphin.calling.CallingMeetingActivity;
import com.wellav.dolphin.calling.CallingMonitorActivity;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.netease.avchat.AVChatActivity;
import com.wellav.dolphin.netease.avchat.AVChatProfile;
import com.wellav.dolphin.netease.config.DemoCache;
import com.wellav.dolphin.netease.config.LogUtil;
import com.wellav.dolphin.netease.config.SendNitificationMsgUtil;

public class ServiceObserver {
	private static String TAG = "ServiceObserver";
	private static int FIRSTMEMBER = 2;
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
	
	/**
	 * IM消息监听
	 */
	Observer<List<IMMessage>> incomingMessageObserver =
		    new Observer<List<IMMessage>>() {
		        /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
		        public void onEvent(List<IMMessage> messages) {
					addFirstMemberToManager(messages);
		            // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
					
		        }
		    };
		    
		    
	/**
	 * 过滤出第一个加入群组的成员信息    
	 * @param messages
	 */
	private void addFirstMemberToManager(List<IMMessage> messages){
		for(int i = 0; i<messages.size(); i++){
			MemberChangeAttachment ment = (MemberChangeAttachment) messages.get(i).getAttachment();
			if(ment != null && ment.getType().name().equals("AcceptInvite")){
				if(SysConfig.getInstance().getMyTeam().getMemberCount() == FIRSTMEMBER){
					List<String> acc = new ArrayList<String>();
					acc.add(messages.get(i).getFromAccount());
					addTeamManager(SysConfig.getInstance().getTeamId(),acc);
				}
			}
		}
	}
	
	/**
	 * 拥有者添加管理员
	 * @param teamId 群 ID
	 * @param accounts 待提升为管理员的用户帐号列表
	 * @return InvocationFuture 可以设置回调函数,如果成功，参数为新增的群管理员列表
	 */
	private void addTeamManager(String teamId,List<String> acc){
	
		NIMClient.getService(TeamService.class)
		    .addManagers(teamId, acc)
		    .setCallback(new RequestCallback<List<TeamMember>>() {

				@Override
				public void onException(Throwable arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onFailed(int arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(List<TeamMember> arg0) {
					// TODO Auto-generated method stub
					LogUtil.e(TAG, "addTeamManager onSuccess");
				}
		});
	}
		    
	/**
	 * 系统消息监听
	 */
	private void registerSystemMessageObserver(){
		NIMClient.getService(SystemMessageObserver.class)
	    .observeReceiveSystemMsg(new Observer<SystemMessage>() {
	            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

				@Override
	            public void onEvent(SystemMessage message) {
					LogUtil.e(TAG, "SystemMessageObserver"+message.getAttach());
	            }
	        }, true);
	}
	
	/**
	 * 网络来电监听
	 * @param register
	 */
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

	/**
	 * 账户状态监听
	 */
	@SuppressWarnings("serial")
	Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

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
					queryTeam();
				} else {

				}
			}
		}
	};
	
	/*
	 * 查询所加的家庭组
	 */
	private void queryTeam(){
		NIMClient.getService(TeamService.class).queryTeamList().setCallback(new RequestCallbackWrapper<List<Team>>() {

			@Override
			public void onResult(int code,
					List<Team> ts, Throwable arg2) {
				if (code == ResponseCode.RES_SUCCESS && ts.size() == 1) {
					SysConfig.getInstance().setMyTeam(ts.get(0));
					LogUtil.e("team","queryTeamList:"+ ts.get(0).getId());
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

	/**
	 * 自定义通知监听
	 */
	@SuppressWarnings("serial")
	Observer<CustomNotification> commandObserver = new Observer<CustomNotification>() {
		@Override
		public void onEvent(CustomNotification message) {
			if (message.getSessionType() != SessionTypeEnum.P2P) {
				return;
			}

			String content = message.getContent();
			JSONObject data = JSON.parseObject(content);
			String type = data.getString("type");
			content = data.getString("content");
			if (type.equals("monitor")) {
				LogUtil.e(TAG, message.getFromAccount());

				if (SysConfig.getInstance().getStatus() == SysConfig.MonitorCall) {
					JSONObject obj = new JSONObject();
					obj.put("type", "monitor");
					obj.put("content", SysConfig.getInstance().getGroupName());
					String jsonContent = obj.toJSONString();
					SendNitificationMsgUtil.sendCustomNotification(mContext,
							message.getFromAccount(), jsonContent);
					return;
				} else if (SysConfig.getInstance().getStatus() == SysConfig.Free) {
					Intent intent = new Intent(mContext,
							CallingMonitorActivity.class);

					intent.putExtra("CallName", message.getFromAccount());
					AlarmManager am = (AlarmManager) mContext
							.getSystemService(Context.ALARM_SERVICE);
					PendingIntent pendingIntent = PendingIntent.getActivity(
							mContext, 2, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					// long time = Calendar.getInstance().getTimeInMillis();
					am.set(AlarmManager.RTC_WAKEUP, 200, pendingIntent);
				}

			} else if (type.equals("groupchat")) {
				if (SysConfig.getInstance().getStatus() == SysConfig.Free) {
					CallingGroupChatActivity.launch(mContext,
							message.getFromAccount(), content);
				}

			} else if (type.equals("meeting")) {
				if (SysConfig.getInstance().getStatus() == SysConfig.Free) {
					CallingMeetingActivity.launch(mContext,
							message.getFromAccount(), content);
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
			LogUtil.e(TAG, "teamUpdateObserver"+teams.get(0).getMemberCount());
			Team team = teams.get(0);
			SysConfig.getInstance().setMyTeam(team);
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
			LogUtil.e(TAG, "teamRemoveObserver"+team.getType());
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
			LogUtil.e(TAG, "memberUpdateObserver"+members.get(0).getType());
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
			LogUtil.e(TAG, "memberUpdateObserver"+member.getType());
	    }
	};
}
