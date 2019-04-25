package com.wellav.dolphin.mmedia.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.wellav.dolphin.mmedia.BaseActivity;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.adapter.MeetingContactAdapter;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.netease.ChatRoomMemberCache;
import com.wellav.dolphin.mmedia.netease.DialogMaker;
import com.wellav.dolphin.mmedia.netease.LogUtil;
import com.wellav.dolphin.mmedia.netease.NetworkUtil;
import com.wellav.dolphin.mmedia.netease.SendNitificationMsgUtil;
import com.wellav.dolphin.mmedia.netease.avchat.AVChatSoundPlayer;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.NameUtils;

public class CallingMeetingActivity extends BaseActivity implements OnClickListener ,AVChatStateObserver{
	private static final String TAG = "CallingMeetingActivity";
	private static final String KEY_ROOMNAME = "KEY_ROOMNAME";
	private static final String KEY_ACCNAME = "KEY_ACCNAME";
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
	private Button  exitMeeting;
	// private ImageButton mHandle;
	private ListView listView;
	private FamilyUserInfo info;//creator
	private MeetingContactAdapter meetingMeberAdapter;
	private LoadUserAvatarFromLocal mHead;
	private String roomName;
	private String mAccName;// 邀请人的账号
	private ArrayList<String> mCheckName;
	private HashMap<String, Boolean> mAudioState = new HashMap<>();
	private boolean mMyLoadMute = false;

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
		registerReceiver(receiver, new IntentFilter(SysConfig.Broadcast_Action_MuteAudio));
		mVideoLayout = root.findViewById(R.id.avchat_video_layout);
		mReceiveLayout = root.findViewById(R.id.avchat_receive_layout);
		
		findVideoViews();

			roomName = getIntent().getStringExtra(KEY_ROOMNAME);
			mAccName = getIntent().getStringExtra(KEY_ACCNAME);
			inComingCalling();

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
		//meetingCount = (TextView) mVideoLayout.findViewById(R.id.meeting_member_count);
		exitMeeting = (Button) mVideoLayout.findViewById(R.id.meeting_over_btn);
		SlidingDrawer = (SlidingDrawer) mVideoLayout.findViewById(R.id.SlidingDrawer);
		listView = (ListView) SlidingDrawer
				.findViewById(R.id.meeting_right_mebers_lv);
		
		meetingMeberAdapter = new MeetingContactAdapter(this);
		listView.setAdapter(meetingMeberAdapter);

		mHead = new LoadUserAvatarFromLocal();
		
		myVolume.setOnClickListener(this);
		myMicro.setOnClickListener(this);
		exitMeeting.setOnClickListener(this);
		
	}
	
	/**
	 * 接听界面和铃声
	 */
	private void inComingCalling() {
		info = SQLiteManager.getInstance().geFamilyUserInfoLoginName(mAccName);
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
	 * 加入群聊房间
	 * 
	 * @param channelName
	 */
	public void onJoinAVChatRoom(String channelName) {
		SysConfig.getInstance().setGroupName(channelName);
		AVChatOptionalConfig avChatOptionalParam = new AVChatOptionalConfig();
		avChatOptionalParam.enableAudienceRole(false);
		avChatOptionalParam.enableVideoRotate(false);
		AVChatManager.getInstance().joinRoom(roomName, AVChatType.VIDEO,
				avChatOptionalParam, new AVChatCallback<AVChatData>() {
					@Override
					public void onSuccess(AVChatData avChatData) {
						LogUtil.d(TAG, "join channel success");
						showCreateorPf();
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
		if (s.equals(mAccName)) {
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
		unregisterReceiver(receiver);
		SysConfig.getInstance().setGroupName(null);
		needFinish = true;
		super.onDestroy();
	}
	/**
//	 * 接收通话的消息广播
//	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent == null)
				return;
			String name = intent.getStringExtra("name");
			boolean ischk = intent.getBooleanExtra("value", false);
			if(name.equals(SysConfig.uid)){
				if(mMyLoadMute){
					return;
				}
				AVChatManager.getInstance().muteLocalAudio(ischk);
				myMicro.setSelected(ischk);
			}
			
			mAudioState.put(name, ischk);
			meetingMeberAdapter.refreshMuteState(mAudioState);
		}
	};
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
		mReceiveLayout.setVisibility(View.INVISIBLE);
		mVideoLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void onConnectionTypeChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeviceEvent(int arg0, String arg1) {
		// TODO Auto-generated method stub
		LogUtil.e(TAG, "onDeviceEvent"+arg1+arg0);
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
		onMasterJoin(acc);
		NameUtils.remoteNamesAdd(acc);
		if(acc.equals(mAccName)){
			meetingMeberAdapter.refreshAdd(SysConfig.uid);
		}else{
			meetingMeberAdapter.refreshAdd(acc);
		}

	}

	@Override
	public void onUserLeave(String acc, int arg1) {
		// TODO Auto-generated method stub
		NameUtils.remoteNamesRemove(acc);
		meetingMeberAdapter.refreshRemove(acc);
		if(acc.equals(mAccName)){
			finish();
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
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
			if(mAudioState.containsKey(SysConfig.uid) && mAudioState.get(SysConfig.uid)){
				CommFunc.DisplayToast(CallingMeetingActivity.this, "你已被主持人禁言，无法说话");
				return;
			}
			if(AVChatManager.getInstance().isLocalAudioMuted()){
				AVChatManager.getInstance().muteLocalAudio(false);
				myMicro.setSelected(false);
				mMyLoadMute = false;
			}else{
				AVChatManager.getInstance().muteLocalAudio(true);
				myMicro.setSelected(true);
				mMyLoadMute = true;
			}
			break;
		}
	}
}
