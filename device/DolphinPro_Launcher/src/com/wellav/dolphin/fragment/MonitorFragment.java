package com.wellav.dolphin.fragment;

import java.util.Map;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoQuality;
import com.netease.nimlib.sdk.avchat.model.AVChatAudioFrame;
import com.netease.nimlib.sdk.avchat.model.AVChatChannelInfo;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatOptionalConfig;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoFrame;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.FamilyInfo;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.bean.MessageEventType;
import com.wellav.dolphin.bean.MessageInform;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.net.RequestAction;
import com.wellav.dolphin.netease.avchat.ModuleProxy;
import com.wellav.dolphin.netease.config.DialogMaker;
import com.wellav.dolphin.netease.config.LogUtil;
import com.wellav.dolphin.netease.config.SendNitificationMsgUtil;
import com.wellav.dolphin.ui.CircleImageView;
import com.wellav.dolphin.utils.JsonUtils;
import com.wellav.dolphin.utils.NameUtils;

public class MonitorFragment extends BaseFragment   implements AVChatStateObserver{

	private static final String TAG = "MonitorFragment";
	private RelativeLayout mOurLayout;
	private RelativeLayout mRemoteLayout;
	private SurfaceView mLocalView;
	private SurfaceView mRemotelView;
	private CircleImageView head;
	private TextView name;
	private String Callname;
	private FamilyInfo mUsers;
	private String mMonitorTxt="";
	public static String roomName;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_monitor, container,
				false);
		mOurLayout = (RelativeLayout) view
				.findViewById(R.id.calling_layout_multivideo_our);
		head = (CircleImageView) view.findViewById(R.id.icon);
		name = (TextView) view.findViewById(R.id.name);
		Callname = getArguments().getString("CallName");
		FamilyUserInfo callMonitor = LauncherApp.getInstance().getDBHelper()
				.getFamilyUser(getActivity(), Callname);
		name.setText(callMonitor.getNickName());
		LoadUserAvatarFromLocal tack = new LoadUserAvatarFromLocal();
		Bitmap bm = tack.loadImage(callMonitor.getLoginName());
		
		mMonitorTxt= getActivity().getString(R.string.watch);
		if (bm != null) {
			head.setImageBitmap(bm);
		}
		registerObservers(true);
		
		createRoom();
		//uploadMonitorMsg(Callname);
		return view;
	}

	

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	 private void registerObservers(boolean register) {
	        AVChatManager.getInstance().observeAVChatState(this, register);
	    }

	public static MonitorFragment newInstanceMonitor(String name){
		MonitorFragment frag = new MonitorFragment();
		Bundle bundle = new Bundle();
		bundle.putString("CallName", name);
		frag.setArguments(bundle);
		return frag;
	}
	
	private void createRoom(){
		DialogMaker.showProgressDialog(getActivity(), "请等候...", false);
		roomName = System.currentTimeMillis()+"";
    	AVChatManager.getInstance().createRoom(roomName, null, new AVChatCallback<AVChatChannelInfo>() {

			@Override
			public void onException(Throwable arg0) {
				// TODO Auto-generated method stub
				LogUtil.e("createRoom", roomName);
				 DialogMaker.dismissProgressDialog();
			}

			@Override
			public void onFailed(int code) {
				// TODO Auto-generated method stub
				  DialogMaker.dismissProgressDialog();
	              Toast.makeText(getActivity(), "失败, code:" + code, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(AVChatChannelInfo info) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "成功", Toast.LENGTH_SHORT).show();
				DialogMaker.dismissProgressDialog();
			    initLiveVideo(roomName);
			    LogUtil.e("createRoom", roomName);
			}});
	}
	
    // 初始化UI
    public void initLiveVideo(String channelName) {
      //  roomNameText.setText(channelName);
    	SysConfig.getInstance().setStatus(SysConfig.MonitorCall);
    	SysConfig.getInstance().setGroupName(channelName);
        AVChatOptionalConfig avChatOptionalParam = new AVChatOptionalConfig();
        avChatOptionalParam.enableAudienceRole(false);
       // avChatOptionalParam.setVideoQuality(AVChatVideoQuality.QUALITY_LOW);
        AVChatManager.getInstance().joinRoom(channelName, AVChatType.VIDEO, avChatOptionalParam, new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData avChatData) {
                LogUtil.e(TAG, "join channel success");
                JSONObject obj = new JSONObject();
    	        obj.put("type", "monitor");
    	        obj.put("content", roomName);
    	        String jsonContent = obj.toJSONString();
             
             SendNitificationMsgUtil.sendCustomNotification(getActivity(), Callname, jsonContent);
            }

            @Override
            public void onFailed(int i) {
                LogUtil.d(TAG, "join channel failed, code:" + i);
                Toast.makeText(getActivity(), "join channel failed, code:" + i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }
    
    public void clearChatRoom() {
        LogUtil.d(TAG, "chat room do clear");
        AVChatManager.getInstance().leaveRoom(new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                LogUtil.d(TAG, "leave channel success");
            }

            @Override
            public void onFailed(int i) {
                LogUtil.d(TAG, "leave channel failed, code:" + i);
            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		clearChatRoom();
		SysConfig.getInstance().setStatus(SysConfig.Free);
		SysConfig.getInstance().setGroupName(null);
		registerObservers(false);
		super.onDestroy();
	}


	private void uploadMonitorMsg(String name) {
		mUsers = LauncherApp.getInstance().getDBHelper().getFamilyInfo();
		String nickName = mUsers.getNickName();

		String familyID = mUsers.getFamilyID();
		FamilyUserInfo callMonitor = LauncherApp.getInstance().getDBHelper()
				.getFamilyUser(getActivity(), name);

		// 当有人观看时，把这一消息上传到服务器
		MessageInform msg = new MessageInform();
		msg.setEventType(MessageEventType.EVENTTYPE_LOOK_YOU);
		msg.setName(callMonitor.getNickName());
		msg.setDolphinName(nickName);
		msg.setDeviceID(mUsers.getDeviceID());
		msg.setFamilyID(familyID);
		String messageContent = JsonUtils.getJsonObject(msg, "normal");
		String xmlMessage = JsonUtils.getXmlMessage(SysConfig.DolphinToken, 1,
				familyID, messageContent);
		RequestAction.requestXml(xmlMessage, "UploadMessageBox");

		LauncherApp.getInstance().notifyMsgRTC2Family(
				getActivity(),
				JsonUtils.getNotifyJson(callMonitor.getNickName(), nickName,
						MessageEventType.EVENTTYPE_LOOK_YOU));
	}



	@Override
	public int onAudioFrameFilter(AVChatAudioFrame arg0) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void onCallEstablished() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onConnectionTypeChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onDeviceEvent(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onDisconnectServer() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onFirstVideoFrameAvailable(String acc) {
		// TODO Auto-generated method stub
		LogUtil.e(TAG,"onFirstVideoFrameAvailable"+ acc);
	}

	@Override
	public void onFirstVideoFrameRendered(String acc) {
		// TODO Auto-generated method stub
		LogUtil.e(TAG,"onFirstVideoFrameRendered"+ acc);
	}



	@Override
	public void onJoinedChannel(int arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		LogUtil.e(TAG,"onJoinedChannel"+ arg1);
	}



	@Override
	public void onLeaveChannel() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onLocalRecordEnd(String[] arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onNetworkQuality(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onProtocolIncompatible(int arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onTakeSnapshotResult(String arg0, boolean arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onUserJoined(String name) {
		// TODO Auto-generated method stub
		LogUtil.e(TAG,"onUserJoined"+ name);
		NameUtils.remoteNamesAdd(name);
	}



	@Override
	public void onUserLeave(String name, int arg1) {
		// TODO Auto-generated method stub
		NameUtils.remoteNamesRemove(name);
		if(NameUtils.remoteNamesCount() == 0){
			getActivity().finish();
		}
	}



	@Override
	public void onVideoFpsReported(String arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int onVideoFrameFilter(AVChatVideoFrame arg0) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void onVideoFrameResolutionChanged(String arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onAudioOutputDeviceChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onReportSpeaker(Map<String, Integer> arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onStartLiveResult(int arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onStopLiveResult(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
