package com.wellav.dolphin.fragment;

import java.io.IOException;
import java.util.Timer;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.interfaces.ICallingVideo;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.ui.CircleImageView;
import com.wellav.dolphin.ui.ImageButtonText;
import com.wellav.dolphin.utils.Util;

public class DailAcceptFragment extends BaseFragment implements ICallingVideo{
	private static final String TAG ="DailAcceptFragment";
	private RelativeLayout acceptlayout;
	private CircleImageView Head;
	private ImageView type;
	private TextView MsgTv;
	private TextView name;
	private ImageButtonText dialhangupBtn;
	private static FamilyUserInfo mUser;
	private LoadUserAvatarFromLocal mHead;
	private int calltype;
	private MediaPlayer mediaPlayer;// 铃音播放
	private Vibrator vibrator;// 震动
	private AudioManager mAudioManager;// 系统声音控制
	private int musicVolume;// 记录当前媒体音量
	private int audiomode;// 记录当前audio mode
	private Timer noTimer;
    private boolean notimerFlag = false;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		musicVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		audiomode = mAudioManager.getMode();
		
		initIncallMode();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view =inflater.inflate(R.layout.avchat_groupchat_receive_layout, container, false);
		acceptlayout = (RelativeLayout) view.findViewById(R.id.accept_layout);
		Head = (CircleImageView) view.findViewById(R.id.icon);
		type = (ImageView) view.findViewById(R.id.type);
		MsgTv = (TextView) view.findViewById(R.id.txt);
		name = (TextView) view.findViewById(R.id.name);
	//	dialhangupBtn = (ImageButtonText) view.findViewById(R.id.hangup);
		
		getIntentData();
		return view;
	}

	private void getIntentData(){
		calltype = getArguments().getInt("CallType");
		Util.PrintLog(TAG, "getIntentData:"+calltype);
		switch(calltype){
		case SysConfig.ChatCall:
			acceptlayout.setVisibility(View.INVISIBLE);
			setViewDisplay();
			LauncherApp.getInstance().MakeCall(mUser.getLoginName());
			break;
		case SysConfig.ChatAccept:
			dialhangupBtn.setVisibility(View.INVISIBLE);
			setViewDisplay();
			MsgTv.setText(R.string.invitechat);
			break;
		case SysConfig.MutilAccept:
			dialhangupBtn.setVisibility(View.INVISIBLE);
			setViewDisplay();
			MsgTv.setText(R.string.invitegroup);
			break;
		case SysConfig.MeetingAccept:
			dialhangupBtn.setVisibility(View.INVISIBLE);
			setViewDisplay();
			MsgTv.setText(R.string.invitemeeting);
			break;
		}
	}
	
	private void setViewDisplay(){
		if(mUser == null){
			return;
		}
		mHead = new LoadUserAvatarFromLocal();
		Bitmap bm3 = mHead.loadImage(mUser.getLoginName());
		if(bm3 != null){
			Head.setImageBitmap(bm3);
		}
		if(mUser.getDeviceType().equals("1")){
			type.setImageResource(R.drawable.type_dolphin_blue);
		}
		String disname = TextUtils.isEmpty(mUser.getNoteName())? mUser.getNickName(): mUser.getNoteName();
		name.setText(disname);
	}
	
	
	
	@Override
	public void onDestroy() {
		initCallingMode();
		super.onDestroy();
	}

	public  DailAcceptFragment newInstance(FamilyUserInfo user){
		
		DailAcceptFragment DailAccept = this;
		mUser = user;
		return DailAccept;
	}

	@Override
	public void opt_Request(String parm) {
		
	}

	@Override
	public void opt_Response(int action, String parm) {
		
	}

	@Override
	public void ViewAction(int type) {
		Util.PrintLog(TAG, "onViewAction");
	}
	
	/**
	 * 通话中状态 TODO 需要更改
	 */

	private void initCallingMode() {
		if (noTimer != null) {
			noTimer.cancel();
			noTimer = null;
		}
		stopRing(); // 停止铃声
	}
	
	/**
	 * 关闭铃音
	 */
	private void stopRing() {
		Util.PrintLog(TAG, "stopRing");
		// 还原媒体播放音量大小
		if (mAudioManager != null) {
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					musicVolume, 0);
			mAudioManager.setMode(audiomode);
		}
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		if (vibrator != null) {
			vibrator.cancel();
			vibrator = null;
		}
	}
	
	
	/**
     * 初始化来电 TODO 需要更改
     */
    private void initIncallMode() {
        // initCallIn();
    	Util.PrintLog(TAG, "initIncallMode");
        if ((mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)) {
            // do something
        } else if ((mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)) {
            long[] number = { 0, 1000, 1000 };
            vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(number, 1);
        } else {
            stopRing();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mAudioManager.setMode(AudioManager.MODE_RINGTONE);
            try {
                mediaPlayer
                .setDataSource(getActivity(), Uri
                        .parse(Settings.System.DEFAULT_RINGTONE_URI
                                .toString()));
                // 设置当铃音循环播放时，设置两次播放间隔为1.5秒
                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mAudioManager.setMode(AudioManager.MODE_RINGTONE);
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mediaPlayer.prepare();
                mAudioManager.setMode(AudioManager.MODE_RINGTONE);
                mediaPlayer.start();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (notimerFlag == false) {
            notimerFlag = true;
            noTimer = new Timer(true);
         }
    }
    
    /**
     * 播放资源铃音
     * 
     * @param resId
     * @param hanguped
     */
    public void playRing(int resId, boolean hanguped) {
    	
        playRing(resId, true, hanguped);
    }
    private int count;
    private void playRing(int resId, boolean bSpeaker, boolean hanguped) {
        stopRing();
        Util.PrintLog(TAG, "playRing:"+mAudioManager.getRingerMode());
        count = 0;
        if ((mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)) {

        } else if (bSpeaker
                && (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)) {
            long[] number = { 0, 1000, 1000 };
            vibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(number, 1);
        } else {
            mediaPlayer = MediaPlayer.create(getActivity(), resId);
            if (mediaPlayer == null) {
                return;
            }
            if (mAudioManager != null) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 
                    (int) (mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.7), 0);
                mAudioManager.setMode(AudioManager.MODE_RINGTONE);
            }
            if (hanguped == true) {
                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        count++;
                        if (count >= 2) {
                            count = 0;
                            closeUI(500);
                        } else {
                            mAudioManager.setMode(AudioManager.MODE_RINGTONE);
                            mediaPlayer.start();
                        }
                    }
                });
            } else {
                mediaPlayer.setLooping(true);
            }
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                mediaPlayer.prepare();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mAudioManager.setMode(AudioManager.MODE_RINGTONE);
            mediaPlayer.start();
        }
    }
    public void closeUI(int time) {
    	Util.PrintLog(TAG, "closeUI");
        HBaseApp.post2UIDelayed(new Runnable(){
            @Override
            public void run() {
                stopRing();
                getFragmentManager().beginTransaction().remove(DailAcceptFragment.this).commit();
            }

        }, time);
    }
}
