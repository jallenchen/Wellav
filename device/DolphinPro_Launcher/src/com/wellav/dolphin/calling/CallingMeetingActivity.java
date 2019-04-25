package com.wellav.dolphin.calling;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatAudioFrame;
import com.netease.nimlib.sdk.avchat.model.AVChatChannelInfo;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatOptionalConfig;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoFrame;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;
import com.wellav.dolphin.adapter.MeetingContactAdapter;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.launcher.BaseActivity;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.netease.avchat.AVChatSoundPlayer;
import com.wellav.dolphin.netease.config.ChatRoomMemberCache;
import com.wellav.dolphin.netease.config.DialogMaker;
import com.wellav.dolphin.netease.config.LogUtil;
import com.wellav.dolphin.netease.config.NetworkUtil;
import com.wellav.dolphin.netease.config.SendNitificationMsgUtil;
import com.wellav.dolphin.ui.CircleImageView;
import com.wellav.dolphin.utils.NameUtils;

public class CallingMeetingActivity extends BaseActivity implements OnClickListener ,AVChatStateObserver{
	private static final String TAG = "CallingGroupChatActivity";
	private static final String KEY_IN_CALLING = "KEY_IN_CALLING";
	private static final String KEY_ROOMNAME = "KEY_ROOMNAME";
	private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
	private static final String KEY_ACCNAME = "KEY_ACCNAME";
	private boolean mIsInComingCall = false;// is incoming call or outgoing call
	private static boolean needFinish = true; // 若来电或去电未接通时，点击home。另外一方挂断通话。从最近任务列表恢复，则finish
	private View mVideoLayout;
	private View mReceiveLayout;
	private ViewGroup masterVideoLayout; //主播显示区域
	//recevicelayout
	private CircleImageView headIcon;
	private TextView nameTxt;
	//meeting layout
	private SlidingDrawer SlidingDrawer;
	private TextView creatorName;
	private TextView meetingCount;
	private ImageView creatorHead;
	private ImageView myVolume;
	private ImageView myMicro;
	private Button mirc_btn, exitMeeting;
	// private ImageButton mHandle;
	private ListView listView;
	private FamilyUserInfo info;//creator
	private MeetingContactAdapter meetingMeberAdapter;
	private LoadUserAvatarFromLocal mHead;
	private String roomName;
	private String mAccName;// 邀请人的账号
	private ArrayList<String> mCheckName;
	
	public static void start(Context context, ArrayList<String> checkedName) {
		SysConfig.getInstance().setStatus(SysConfig.MeetingCall);
		needFinish = false;
		Intent intent = new Intent();
		intent.setClass(context, CallingMeetingActivity.class);
		intent.putStringArrayListExtra(KEY_ACCOUNT, checkedName);
		intent.putExtra(KEY_IN_CALLING, false);
		context.startActivity(intent);
	}

	/**
	 * incoming call
	 * 
	 * @param context
	 */
	public static void launch(Context context, String acc, String roomname) {
		SysConfig.getInstance().setStatus(SysConfig.MeetingAccept);
		needFinish = false;
		Intent intent = new Intent();
		intent.setClass(context, CallingMeetingActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(KEY_ROOMNAME, roomname);
		intent.putExtra(KEY_ACCNAME, acc);
		intent.putExtra(KEY_IN_CALLING, true);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (needFinish) {
			finish();
			return;
		}
		View root = LayoutInflater.from(this).inflate(
				R.layout.activity_meeting_layout, null);
		setContentView(root);
		
		registerAVChatStateObserver(true);
		mVideoLayout = root.findViewById(R.id.avchat_video_layout);
		mReceiveLayout = root.findViewById(R.id.avchat_receive_layout);
		
		findVideoViews();
		mIsInComingCall = getIntent().getBooleanExtra(KEY_IN_CALLING, false);

		if (mIsInComingCall) {
			roomName = getIntent().getStringExtra(KEY_ROOMNAME);
			mAccName = getIntent().getStringExtra(KEY_ACCNAME);
			inComingCalling();
		} else {
			mCheckName = getIntent().getStringArrayListExtra(KEY_ACCOUNT);
			outgoingCalling();
		}

	}
	
	private void findVideoViews() {
		headIcon = (CircleImageView) mReceiveLayout.findViewById(R.id.icon);
		nameTxt = (TextView) mReceiveLayout.findViewById(R.id.name);
		
		masterVideoLayout = (ViewGroup) mVideoLayout
				.findViewById(R.id.video_meeting_rl);
		myVolume = (ImageView) mVideoLayout.findViewById(R.id.video_volume_control_iv);
		myMicro = (ImageView) mVideoLayout.findViewById(R.id.video_micr__control_iv);
		creatorHead = (ImageView) mVideoLayout
				.findViewById(R.id.meeting_manage_head_iv);
		creatorName = (TextView) mVideoLayout.findViewById(R.id.meeting_manage_name_tv);
		meetingCount = (TextView) mVideoLayout.findViewById(R.id.meeting_member_count);
		mirc_btn = (Button) mVideoLayout.findViewById(R.id.meeting_disable_mirc_btn);
		exitMeeting = (Button) mVideoLayout.findViewById(R.id.meeting_over_btn);
		SlidingDrawer = (SlidingDrawer) mVideoLayout.findViewById(R.id.SlidingDrawer);
		listView = (ListView) SlidingDrawer
				.findViewById(R.id.meeting_right_mebers_lv);
		
		meetingMeberAdapter = new MeetingContactAdapter(this);
		listView.setAdapter(meetingMeberAdapter);

		mHead = new LoadUserAvatarFromLocal();
		
		myVolume.setOnClickListener(this);
		myMicro.setOnClickListener(this);
		mirc_btn.setOnClickListener(this);
		exitMeeting.setOnClickListener(this);
		
	}
	
	/**
	 * 接听界面和铃声
	 */
	private void inComingCalling() {
		info = LauncherApp.getInstance().getDBHelper()
				.getFamilyUser(this, mAccName);
		mReceiveLayout.setVisibility(View.VISIBLE);
		nameTxt.setText(info.getNickName());
		Bitmap head = mHead.loadImage(mAccName);
		headIcon.setImageBitmap(head);
		AVChatSoundPlayer.instance(this).play(
				AVChatSoundPlayer.RingerTypeEnum.RING);
	}
	
	private void showCreateorPf(){
		creatorName.setText(info.getNickName());

		Bitmap head = mHead.loadImage(info.getLoginName());
		if (head != null) {
			creatorHead.setImageBitmap(head);
		} else {
			// creatorHead.setBackgroundResource(R.drawable.call_video_default_avatar);
		}
	}
	
	/**
	 * 拨打直接群聊界面和创建多人视频房间
	 */
	private void outgoingCalling() {
		if (!NetworkUtil.isNetAvailable(CallingMeetingActivity.this)) { // 网络不可用
			Toast.makeText(this, R.string.network_is_not_available,
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		info = LauncherApp.getInstance().getDBHelper()
				.getFamilyUser(this, SysConfig.uid);
		mVideoLayout.setVisibility(View.VISIBLE);
		onCreateAVChatRoom();
	}
	
	/**
	 * 创建视频群聊房间
	 */
	private void onCreateAVChatRoom() {
		DialogMaker.showProgressDialog(this, "会议创建中，请等候...", false);
		roomName = System.currentTimeMillis() + "";
		AVChatManager.getInstance().createRoom(roomName, null,
				new AVChatCallback<AVChatChannelInfo>() {

					@Override
					public void onException(Throwable arg0) {
						// TODO Auto-generated method stub
						DialogMaker.dismissProgressDialog();
					}

					@Override
					public void onFailed(int code) {
						// TODO Auto-generated method stub
						DialogMaker.dismissProgressDialog();
						finish();
						Toast.makeText(CallingMeetingActivity.this,
								"失败, code:" + code, Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(AVChatChannelInfo info) {
						// TODO Auto-generated method stub
						DialogMaker.dismissProgressDialog();
						Toast.makeText(CallingMeetingActivity.this, "成功",
								Toast.LENGTH_SHORT).show();

						SysConfig.getInstance().setGroupName(roomName);
						onJoinAVChatRoom(roomName);
						LogUtil.e("createRoom", roomName);
					}
				});
	}

	/**
	 * 加入群聊房间
	 * 
	 * @param channelName
	 */
	public void onJoinAVChatRoom(String channelName) {
		SysConfig.getInstance().setGroupName(channelName);
		AVChatOptionalConfig avChatOptionalParam = new AVChatOptionalConfig();
		avChatOptionalParam.enableAudienceRole(false);
		AVChatManager.getInstance().joinRoom(roomName, AVChatType.VIDEO,
				avChatOptionalParam, new AVChatCallback<AVChatData>() {
					@Override
					public void onSuccess(AVChatData avChatData) {
						LogUtil.d(TAG, "join channel success");
						showCreateorPf();
						sendInviteChatMsg(mCheckName);
					}

					@Override
					public void onFailed(int i) {
						LogUtil.d(TAG, "join channel failed, code:" + i);
						if (i == 404) {
							Toast.makeText(CallingMeetingActivity.this,
									"对方已挂断", Toast.LENGTH_SHORT).show();
							finish();
						}
					}

					@Override
					public void onException(Throwable throwable) {

					}
				});
	}

	/**
	 * 离开群聊房间
	 */
	public void onLeaveAVChatRoom() {
		LogUtil.d(TAG, "chat room do clear");
		AVChatManager.getInstance().leaveRoom(new AVChatCallback<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				LogUtil.d(TAG, "leave channel success");
				NameUtils.remoteNamesClean();
			}

			@Override
			public void onFailed(int i) {
				LogUtil.d(TAG, "leave channel failed, code:" + i);
			}

			@Override
			public void onException(Throwable throwable) {

			}
		});
		ChatRoomMemberCache.getInstance().clearRoomCache(roomName);
	}
	
	/**
	 * 发送房间名给被邀请的人员
	 * 
	 * @param inviteName
	 */
	private void sendInviteChatMsg(ArrayList<String> inviteName) {

		JSONObject obj = new JSONObject();
		obj.put("type", "meeting");
		obj.put("content", roomName);
		String jsonContent = obj.toJSONString();
		for (int i = 0; i < inviteName.size(); i++) {
			SendNitificationMsgUtil.sendCustomNotification(this,
					inviteName.get(i), jsonContent);
		}

	}
	
	/**
	 * 注册监听
	 * 
	 * @param register
	 */
	private void registerAVChatStateObserver(boolean register) {
		AVChatManager.getInstance().observeAVChatState(this, register);
	}

	/**
	 * 接听群聊
	 * 
	 * @param view
	 */
	public void onReceviceAccept(View view) {
		AVChatSoundPlayer.instance(this).stop();
		onJoinAVChatRoom(roomName);
	}

	/**
	 * 拒绝群聊
	 * 
	 * @param view
	 */
	public void onReceviceHangup(View view) {
		AVChatSoundPlayer.instance(this).stop();
		finish();
	}
	
	// 主持人进入频道
	private void onMasterJoin(String s) {
		if (s.equals(SysConfig.uid)) {
			AVChatVideoRender MasteRender = new AVChatVideoRender(this);
			AVChatManager.getInstance().setupVideoRender(s, MasteRender, false,
					AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
			if (MasteRender != null) {
				addIntoMasterPreviewLayout(MasteRender);
			}
		}
	}

	// 将主持人添加到主持人画布
	private void addIntoMasterPreviewLayout(SurfaceView surfaceView) {
		if (surfaceView.getParent() != null)
			((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
		masterVideoLayout.addView(surfaceView);
		surfaceView.setZOrderMediaOverlay(true);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		onLeaveAVChatRoom();
		SysConfig.getInstance().setStatus(SysConfig.Free);
		registerAVChatStateObserver(false);
		SysConfig.getInstance().setGroupName(null);
		needFinish = true;
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	public int onAudioFrameFilter(AVChatAudioFrame arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onAudioOutputDeviceChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCallEstablished() {
		// TODO Auto-generated method stub
		onMasterJoin(SysConfig.uid);
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
	public void onFirstVideoFrameAvailable(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFirstVideoFrameRendered(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onJoinedChannel(int arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		
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
	public void onReportSpeaker(Map<String, Integer> speaker, int mix) {
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

	@Override
	public void onTakeSnapshotResult(String arg0, boolean arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserJoined(String acc) {
		// TODO Auto-generated method stub
		NameUtils.remoteNamesAdd(acc);
		meetingMeberAdapter.refreshAdd(acc);
	}

	@Override
	public void onUserLeave(String acc, int arg1) {
		// TODO Auto-generated method stub
		NameUtils.remoteNamesRemove(acc);
		meetingMeberAdapter.refreshRemove(acc);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.meeting_disable_mirc_btn:
//			if (enabelAll == false) {
//				mirc_btn.setText(R.string.forbid_talking);
//				enabelAll = true;
//			} else {
//				mirc_btn.setText(R.string.cancel_forbid_talking);
//				enabelAll = false;
//			}

			break;
		case R.id.meeting_over_btn:
			finish();
			break;
		case R.id.video_volume_control_iv:
			LogUtil.e(TAG, "speakerEnabled"+AVChatManager.getInstance().speakerEnabled());
			if(AVChatManager.getInstance().speakerEnabled()){
				AVChatManager.getInstance().setSpeaker(false);
				myVolume.setSelected(false);
			}else{
				AVChatManager.getInstance().setSpeaker(true);
				myVolume.setSelected(true);
			}
			
			break;
		case R.id.video_micr__control_iv:
			if(AVChatManager.getInstance().isLocalAudioMuted()){
				AVChatManager.getInstance().muteLocalAudio(false);
				myMicro.setSelected(false);
			}else{
				AVChatManager.getInstance().muteLocalAudio(true);
				myMicro.setSelected(true);
			}
			break;
		}
	}
}
