package com.wellav.dolphin.calling;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
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
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.launcher.AddCallingMemberActivity;
import com.wellav.dolphin.launcher.BaseActivity;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.netease.avchat.AVChatSoundPlayer;
import com.wellav.dolphin.netease.avchat.CallStateEnum;
import com.wellav.dolphin.netease.config.ChatRoomMemberCache;
import com.wellav.dolphin.netease.config.DialogMaker;
import com.wellav.dolphin.netease.config.LogUtil;
import com.wellav.dolphin.netease.config.NetworkUtil;
import com.wellav.dolphin.netease.config.SendNitificationMsgUtil;
import com.wellav.dolphin.ui.CircleImageView;
import com.wellav.dolphin.utils.NameUtils;
import com.wellav.dolphin.utils.Util;

public class CallingGroupChatActivity extends BaseActivity implements
		AVChatStateObserver {
	// constant
	private static final String TAG = "CallingGroupChatActivity";
	private static final String KEY_IN_CALLING = "KEY_IN_CALLING";
	private static final String KEY_ROOMNAME = "KEY_ROOMNAME";
	private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
	private static final String KEY_ACCNAME = "KEY_ACCNAME";

	public static final String INTENT_ACTION_AVCHAT = "INTENT_ACTION_AVCHAT";

	private static boolean needFinish = true; // 若来电或去电未接通时，点击home。另外一方挂断通话。从最近任务列表恢复，则finish
	private boolean mIsInComingCall = false;// is incoming call or outgoing call
	private View mVideoLayout;
	private View mReceiveLayout;
	/**
	 * 直播区域（上）
	 */
	private RelativeLayout videoLayout; // 直播/播放区域
	private SurfaceView selfRender;
	private ViewGroup masterVideoLayout; // 左上，主播显示区域
	private ViewGroup firstRightVideoLayout; // 右上，第一个观众显示区域
	private ViewGroup secondLeftVideoLayout; // 左下，第二个观众显示区域
	private ViewGroup thirdRightVideoLayout; // 右下， 第三个观众显示区域
	private ViewGroup[] viewLayouts = new ViewGroup[3];
	// record
	private View recordView;
	private View recordTip;
	private View recordWarning;
	private CircleImageView headIcon;
	private TextView nameTxt;

	private String roomName;
	private String mAccName;// 邀请人的账号
	private ArrayList<String> mCheckName;

	public static void start(Context context, ArrayList<String> checkedName) {
		SysConfig.getInstance().setStatus(SysConfig.MutilCall);
		needFinish = false;
		Intent intent = new Intent();
		intent.setClass(context, CallingGroupChatActivity.class);
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
		SysConfig.getInstance().setStatus(SysConfig.MutilAccept);
		needFinish = false;
		Intent intent = new Intent();
		intent.setClass(context, CallingGroupChatActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(KEY_ROOMNAME, roomname);
		intent.putExtra(KEY_ACCNAME, acc);
		intent.putExtra(KEY_IN_CALLING, true);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (needFinish) {
			finish();
			return;
		}
		View root = LayoutInflater.from(this).inflate(
				R.layout.activity_groupchat, null);
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

	/**
	 * ********************************** View初始化
	 * **********************************
	 */

	private void findVideoViews() {

		videoLayout = (RelativeLayout) mVideoLayout
				.findViewById(R.id.view_layout);

		masterVideoLayout = (ViewGroup) mVideoLayout
				.findViewById(R.id.master_video_layout);
		firstRightVideoLayout = (ViewGroup) mVideoLayout
				.findViewById(R.id.first_video_layout);
		secondLeftVideoLayout = (ViewGroup) mVideoLayout
				.findViewById(R.id.second_video_layout);
		thirdRightVideoLayout = (ViewGroup) mVideoLayout
				.findViewById(R.id.third_video_layout);

		viewLayouts[0] = firstRightVideoLayout;
		viewLayouts[1] = secondLeftVideoLayout;
		viewLayouts[2] = thirdRightVideoLayout;

		headIcon = (CircleImageView) mReceiveLayout.findViewById(R.id.icon);
		nameTxt = (TextView) mReceiveLayout.findViewById(R.id.name);
		
		recordView = mVideoLayout.findViewById(R.id.avchat_record_layout);
		recordTip = recordView.findViewById(R.id.avchat_record_tip);
		recordWarning = recordView.findViewById(R.id.avchat_record_warning);

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

	/**
	 * 邀请群聊 通话中邀请群聊
	 * 
	 * @param view
	 */
	public void onAddMember(View view) {
		Intent intent = new Intent(CallingGroupChatActivity.this,
				AddCallingMemberActivity.class);
		startActivityForResult(intent, 0);
	}

	/**
	 * 静音 mute true 静音 mute false 非静音
	 * 
	 * @param view
	 */
	public void onMute(View view) {
		if (AVChatManager.getInstance().isLocalAudioMuted()) {
			AVChatManager.getInstance().muteLocalAudio(false);
			view.setSelected(false);
		} else {
			AVChatManager.getInstance().muteLocalAudio(true);
			view.setSelected(true);
		}
	}

	/**
	 * 本地视频开关
	 * 
	 * @param view
	 */
	public void onCaptureWnd(View view) {
		if (AVChatManager.getInstance().isLocalVideoMuted()) {
			AVChatManager.getInstance().muteLocalVideo(false);
			view.setSelected(false);
			masterVideoLayout.setVisibility(View.VISIBLE);
		} else {
			AVChatManager.getInstance().muteLocalVideo(true);
			view.setSelected(true);
			masterVideoLayout.setVisibility(View.INVISIBLE);
		}

	}

	/**
	 * 结束通话
	 * 
	 * @param view
	 */
	public void onHangUp(View view) {
		finish();
	}

	/**
	 * 录制视频通话
	 * 
	 * @param view
	 */
	public void onRecordVideo(View view) {
		if (AVChatManager.getInstance().isLocalRecording()) {
			AVChatManager.getInstance().stopLocalRecord();
			view.setSelected(false);
			uiHandler.removeCallbacks(runnable);
			recordwarning = false;
		} else {
			recordwarning = false;

			if (AVChatManager.getInstance().startLocalRecord()) {
				uiHandler.post(runnable);
				view.setSelected(true);

			} else {
				Toast.makeText(this, "录制失败", Toast.LENGTH_SHORT).show();
			}
			
		}
		updateRecordTip();
	}

	private void updateRecordTip() {
		showRecordView(AVChatManager.getInstance().isLocalRecording(),
				recordwarning);
	}

	public void showRecordView(boolean show, boolean warning) {
		if (show) {
			recordView.setVisibility(View.VISIBLE);
			recordTip.setVisibility(View.VISIBLE);
			if (warning) {
				recordWarning.setVisibility(View.VISIBLE);
			} else {
				recordWarning.setVisibility(View.GONE);
			}
		} else {
			recordView.setVisibility(View.INVISIBLE);
			recordTip.setVisibility(View.INVISIBLE);
			recordWarning.setVisibility(View.GONE);
		}
	}

	public void resetRecordTip() {
		uiHandler.removeCallbacks(runnable);
		recordwarning = false;
		showRecordView(AVChatManager.getInstance().isLocalRecording(),
				recordwarning);
	}

	// 检查存储
	private Handler uiHandler = new Handler(Looper.getMainLooper());
	private boolean recordwarning = false;
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			File dir = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(dir.getPath());
			long blockSize;
			if (Build.VERSION.SDK_INT >= 18) {
				blockSize = stat.getBlockSizeLong();
			} else {
				blockSize = stat.getBlockSize();
			}
			long availableBlocks;
			if (Build.VERSION.SDK_INT >= 18) {
				availableBlocks = stat.getAvailableBlocksLong();
			} else {
				availableBlocks = stat.getAvailableBlocks();
			}

			long size = availableBlocks * blockSize;

			if (size <= 10 * 1024 * 1024) {
				recordwarning = true;
				updateRecordTip();
			} else {
				uiHandler.postDelayed(this, 1000);
			}
		}
	};

	@Override
	protected void onDestroy() {
		onLeaveAVChatRoom();
		SysConfig.getInstance().setStatus(SysConfig.Free);
		registerAVChatStateObserver(false);
		SysConfig.getInstance().setGroupName(null);
		needFinish = true;
		super.onDestroy();
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
	 * 接听界面和铃声
	 */
	private void inComingCalling() {
		FamilyUserInfo info = LauncherApp.getInstance().getDBHelper()
				.getFamilyUser(this, mAccName);
		mReceiveLayout.setVisibility(View.VISIBLE);
		nameTxt.setText(info.getNickName());
		LoadUserAvatarFromLocal avater = new LoadUserAvatarFromLocal();
		Bitmap head = avater.loadImage(mAccName);
		headIcon.setImageBitmap(head);
		AVChatSoundPlayer.instance(this).play(
				AVChatSoundPlayer.RingerTypeEnum.RING);
	}

	/**
	 * 拨打直接群聊界面和创建多人视频房间
	 */
	private void outgoingCalling() {
		if (!NetworkUtil.isNetAvailable(CallingGroupChatActivity.this)) { // 网络不可用
			Toast.makeText(this, R.string.network_is_not_available,
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		mVideoLayout.setVisibility(View.VISIBLE);
		onCreateAVChatRoom();
	}

	/**
	 * 创建视频群聊房间
	 */
	private void onCreateAVChatRoom() {
		DialogMaker.showProgressDialog(this, "群聊创建中，请等候...", false);
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
						Toast.makeText(CallingGroupChatActivity.this,
								"失败, code:" + code, Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(AVChatChannelInfo info) {
						// TODO Auto-generated method stub
						DialogMaker.dismissProgressDialog();
						Toast.makeText(CallingGroupChatActivity.this, "成功",
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
					}

					@Override
					public void onFailed(int i) {
						LogUtil.d(TAG, "join channel failed, code:" + i);
						if (i == 404) {
							Toast.makeText(CallingGroupChatActivity.this,
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

	// 通话中邀请成员
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		super.onActivityResult(arg0, arg1, data);
		if (data != null) {
			ArrayList<String> name = data.getStringArrayListExtra("invitename");
			sendInviteChatMsg(name);
		}
	}

	/**
	 * 发送房间名给被邀请的人员
	 * 
	 * @param inviteName
	 */
	private void sendInviteChatMsg(ArrayList<String> inviteName) {

		JSONObject obj = new JSONObject();
		obj.put("type", "groupchat");
		obj.put("content", roomName);
		String jsonContent = obj.toJSONString();
		for (int i = 0; i < inviteName.size(); i++) {
			SendNitificationMsgUtil.sendCustomNotification(this,
					inviteName.get(i), jsonContent);
		}

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

	// 将有权限的成员添加到画布
	public void onVideoOn(String account) {
		Map<Integer, String> imageMap = ChatRoomMemberCache.getInstance()
				.getImageMap(roomName);
		if (imageMap == null) {
			imageMap = new HashMap<>();
		}

		showView(imageMap, account);

		ChatRoomMemberCache.getInstance().saveImageMap(roomName, imageMap);
	}

	// 显示成员图像
	private void showView(Map<Integer, String> imageMap, String a) {
		if (!imageMap.containsValue(a) && imageMap.size() < 3) {
			for (int i = 0; i < 3; i++) {
				if (!imageMap.containsKey(i)) {
					AVChatVideoRender surfaceView = new AVChatVideoRender(this);
					AVChatManager.getInstance().setupVideoRender(a,
							surfaceView, false,
							AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
					if (surfaceView != null) {
						imageMap.put(i, a);
						addIntoPreviewLayout(surfaceView, viewLayouts[i]);
					}
					break;
				}
			}
		}
	}

	// 添加到成员显示的画布
	private void addIntoPreviewLayout(SurfaceView surfaceView,
			ViewGroup viewLayout) {
		if (surfaceView == null) {
			return;
		}
		if (surfaceView.getParent() != null)
			((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
		viewLayout.addView(surfaceView);
		surfaceView.setZOrderMediaOverlay(true);
	}

	// 将被取消权限的成员从画布移除
	public void onVideoOff(String account) {
		Map<Integer, String> imageMap = ChatRoomMemberCache.getInstance()
				.getImageMap(roomName);
		if (imageMap == null) {
			return;
		}
		removeView(imageMap, account);
	}

	// 移除成员图像
	private void removeView(Map<Integer, String> imageMap, String account) {
		Iterator<Map.Entry<Integer, String>> it = imageMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, String> entry = it.next();
			if (entry.getValue().equals(account)) {
				viewLayouts[entry.getKey()].removeAllViews();
				it.remove();
				break;
			}
		}
	}

	@Override
	public int onAudioFrameFilter(AVChatAudioFrame arg0) {
		return 0;
	}

	@Override
	public void onCallEstablished() {
		// TODO Auto-generated method stub

		if (mIsInComingCall == true) {
			// 应该可以移动到接听处
			mReceiveLayout.setVisibility(View.INVISIBLE);
			mVideoLayout.setVisibility(View.VISIBLE);
		} else {
			sendInviteChatMsg(mCheckName);
		}
		onMasterJoin(SysConfig.uid);
	}

	@Override
	public void onConnectionTypeChanged(int arg0) {
	}

	@Override
	public void onDeviceEvent(int arg0, String arg1) {
		LogUtil.e(TAG, "onDeviceEvent" + arg1 + ":" + arg0);
	}

	@Override
	public void onDisconnectServer() {
	}

	@Override
	public void onFirstVideoFrameAvailable(String arg0) {
	}

	@Override
	public void onFirstVideoFrameRendered(String arg0) {
	}

	@Override
	public void onJoinedChannel(int arg0, String arg1, String arg2) {
	}

	@Override
	public void onLeaveChannel() {
	}

	@Override
	public void onLocalRecordEnd(String[] arg0, int event) {
		LogUtil.e(TAG, "onLocalRecordEnd" + arg0[0]);
		if (event == 1) {
			resetRecordTip();
		}
	}

	@Override
	public void onNetworkQuality(String arg0, int arg1) {
	}

	@Override
	public void onProtocolIncompatible(int arg0) {
	}

	@Override
	public void onTakeSnapshotResult(String arg0, boolean arg1, String arg2) {
	}

	@Override
	public void onUserJoined(String acc) {
		// TODO Auto-generated method stub
		NameUtils.remoteNamesAdd(acc);
		onVideoOn(acc);
		LogUtil.e(TAG, "onUserJoined" + acc);
	}

	@Override
	public void onUserLeave(String acc, int arg1) {
		// TODO Auto-generated method stub
		LogUtil.e(TAG, "onUserLeave" + acc);
		onVideoOff(acc);

		NameUtils.remoteNamesRemove(acc);
		if (NameUtils.remoteNamesCount() == 0) {
			finish();
		}
	}

	@Override
	public void onVideoFpsReported(String arg0, int arg1) {
	}

	@Override
	public int onVideoFrameFilter(AVChatVideoFrame arg0) {
		return 0;
	}

	@Override
	public void onVideoFrameResolutionChanged(String arg0, int arg1, int arg2,
			int arg3) {
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
