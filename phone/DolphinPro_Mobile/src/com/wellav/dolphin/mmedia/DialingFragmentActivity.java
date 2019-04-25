package com.wellav.dolphin.mmedia;

import java.io.IOException;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.fragment.DailOrAccepterFragment;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class DialingFragmentActivity extends BaseActivity {
//	public static final String BROADCAST_DIAL_ACTION = "com.wellav.dial";
//	private TextView dialName;
//	private Button dialReject;
//	private CircleImageView dialHead;
//	private MediaPlayer mediaPlayer;// 铃音播放
//	private Vibrator vibrator;// 震动
//	private AudioManager mAudioManager;// 系统声音控制
//	private int musicVolume;// 记录当前媒体音量
//	private int audiomode;// 记录当前audio mode
//	private int count;
//	private Timer noTimer;
//	private boolean notimerFlag = false;
//	private PowerManager.WakeLock wakeLock;
//	private String callNumber; // 主叫、被叫电话号码
//	private boolean unregistFlag = false;
//	private FamilyInfo callInfo;
//	private int callType;
//
//	@Override
//	protected void onCreate(Bundle arg0) {
//		// TODO Auto-generated method stub
//		super.onCreate(arg0);
//
//		setContentView(R.layout.main);
//		
//		newAcceptFragment(SysConfig.ChatAccept,"87654321");
//		
//		//getExtras();
//		//initView();
//	//	DolphinApp.getInstance().getAction()
//	//			.makeVideoCall(callNumber, MsgKey.CALL_CHAT);
//		//DolphinApp.getInstance()
//		//.MakeCall(callNumber);
//	}
//	FamilyUserInfo user;
//	DailOrAccepterFragment DailAccept;
//	private void newAcceptFragment(int calltype,String userName){
//		user = SQLiteManager.getInstance().geFamilyUserInfoLoginName(userName);
//	 //   DailAccept = new DialOrAccapterFragment().newInstance(user);
//		Bundle bundle= new Bundle();
//		bundle.putInt("CallType", calltype);
//		DailAccept.setArguments(bundle);
//		FragmentManager fm = this.getSupportFragmentManager();
//		FragmentTransaction ft = fm.beginTransaction();
//		ft.add(R.id.container, DailAccept);
//		ft.commit();
//	}
//
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//	}
//	
//	
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		unregisterReceiver(receiver);
//	}
//
//	private void getExtras() {
//		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//		musicVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//		audiomode = mAudioManager.getMode();
//
//		registerReceiver(receiver, new IntentFilter(CallingActivity.BROADCAST_CALLING_ACTION));
//
//		Bundle bundle = getIntent().getExtras();
//		if (bundle != null) {
//			callType = bundle.getInt("CallType");
//			callNumber = bundle.getString("CallName");
//			callInfo = SQLiteManager.getInstance().getFamilyInfoDeviceID(callNumber);
//			DolphinApp.getInstance().getSysConfig().setbFamilyId(callInfo.getFamilyID());
//		}
//	}
//
//	private void initView() {
//		dialHead =  (CircleImageView) findViewById(R.id.dial_head);
//		dialName = (TextView) findViewById(R.id.dial_name);
//		dialReject = (Button) findViewById(R.id.dial_reject);
//		
//		LoadUserAvatarFromLocal task = new LoadUserAvatarFromLocal();
//		Bitmap bitmap = task.loadImage(callInfo.getDeviceID());
//		if(bitmap != null){
//			dialHead.setImageBitmap(bitmap);
//		}
//		dialName.setText(callInfo.getNickName());
//	}
//
//	public void onHangUp(View view) {
//	    DolphinApp.getInstance().onCallHangup();
//		closeUI(500);
//	}
//
//	public void closeUI(int initiative_time) {
//		HBaseApp.post2UIDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				stopRing();
//				finish();
//			}
//
//		}, initiative_time);
//	}
//
//	/**
//	 * 关闭铃音
//	 */
//	private void stopRing() {
//		// 还原媒体播放音量大小
//		if (mAudioManager != null) {
//			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
//					musicVolume, 0);
//			mAudioManager.setMode(audiomode);
//		}
//		if (mediaPlayer != null) {
//			mediaPlayer.stop();
//			mediaPlayer.release();
//			mediaPlayer = null;
//		}
//		if (vibrator != null) {
//			vibrator.cancel();
//			vibrator = null;
//		}
//	}
//
//	/**
//	 * 播放资源铃音
//	 * 
//	 * @param resId
//	 * @param hanguped
//	 */
//	private void playRing(int resId, boolean hanguped) {
//		playRing(resId, false, hanguped);
//	}
//
//	private void playRing(int resId, boolean bSpeaker, boolean hanguped) {
//		stopRing();
//		count = 0;
//		if ((mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)) {
//
//		} else if (bSpeaker
//				&& (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)) {
//			long[] number = { 0, 1000, 1000 };
//			vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
//			vibrator.vibrate(number, 1);
//		} else {
//			mediaPlayer = MediaPlayer.create(this, resId);
//			if (mediaPlayer == null) {
//				return;
//			}
//			if (mAudioManager != null) {
//				mAudioManager
//						.setStreamVolume(
//								AudioManager.STREAM_MUSIC,
//								(int) (mAudioManager
//										.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.7),
//								0);
//				mAudioManager.setMode(AudioManager.MODE_RINGTONE);
//			}
//			if (hanguped == true) {
//				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//					@Override
//					public void onCompletion(MediaPlayer mp) {
//						count++;
//						if (count >= 2) {
//							count = 0;
//							closeUI(5000);
//						} else {
//							mAudioManager.setMode(AudioManager.MODE_RINGTONE);
//							mediaPlayer.start();
//						}
//					}
//				});
//			} else {
//				mediaPlayer.setLooping(true);
//			}
//			try {
//				if (mediaPlayer != null) {
//					mediaPlayer.stop();
//				}
//				mediaPlayer.prepare();
//			} catch (IllegalStateException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			mAudioManager.setMode(AudioManager.MODE_RINGTONE);
//			mediaPlayer.start();
//		}
//	}
//
//	/**
//	 * 处理响铃问题
//	 * 
//	 * @param msg
//	 */
//	private void handleCallMessage(Message msg) {
//		switch (msg.what) {
//		case MsgKey.SLN_180Ring:
//			playRing(R.raw.ring180, false); // 正在呼叫铃声
//			break;
//		case MsgKey.SLN_CallVideo:
//			break;
//		case MsgKey.SLN_CallAccepted:
//
//			Intent in = new Intent(this,CallingActivity.class);
//			 Bundle bundle = new Bundle();
//			 bundle.putString("CallName",callNumber);//对象集合存入方式；
//			 bundle.putInt("CallType", callType);
//			 in.putExtras(bundle);
//			 startActivity(in);
//			// 应该需要专区callingVideo
//			closeUI(0);
//			break;
//		case MsgKey.SLN_CallFailed:
//			// 如果呼叫过程中出现失败不应该提示
//			if (msg.arg1 == RtcConst.CallCode_Busy) { // 用户忙486
//				playRing(R.raw.ring486, true);
//			} else if (msg.arg1 == RtcConst.CallCode_Reject) { // 正在通话 603
//				playRing(R.raw.ring486, true);
//			} else if (msg.arg1 >= RtcConst.CallCode_RequestErr
//					&& msg.arg1 < RtcConst.CallCode_ServerErr) { // 408铃声-对方无法接通
//				playRing(R.raw.ring4xx, true);
//			} else if (msg.arg1 >= RtcConst.CallCode_ServerErr
//					&& msg.arg1 < RtcConst.CallCode_GlobalErr) {
//				playRing(R.raw.ring4xx, true);
//			} else if (msg.arg1 >= RtcConst.CallCode_GlobalErr) {
//				playRing(R.raw.ring4xx, true);
//			}
//			closeUI(5000);
//			break;
//		case MsgKey.SLN_CallClosed:
//			playRing(R.raw.ring4xx, false); // 无应答铃声
//			closeUI(5000);
//			break;
//		}
//	}
//
//	private BroadcastReceiver receiver = new BroadcastReceiver() {
//		@SuppressLint("NewApi")
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent.getAction().equals(CallingActivity.BROADCAST_CALLING_ACTION)) {
//				Bundle bundle = intent.getExtras();
//				if (bundle != null) {
//					Message msg = new Message();
//					msg.what = bundle.getInt("what", 0);
//					msg.arg1 = bundle.getInt("arg1", 0);
//
//					if (bundle.containsKey("info"))
//						msg.obj = bundle.getString("info");
//					// if(bundle.containsKey("info"))
//					// msg.obj = bundle.getString(,"");//by cpl
//					 CommFunc.PrintLog(5, "DialingFragmentActivity", msg.what+"");
//					handleCallMessage(msg);
//				} else {
//					// do something
//				}
//			}
//		}
//	};

}
