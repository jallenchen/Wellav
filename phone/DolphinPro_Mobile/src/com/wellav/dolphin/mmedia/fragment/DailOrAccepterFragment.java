package com.wellav.dolphin.mmedia.fragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.interfaces.ICallingVideo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;

public class DailOrAccepterFragment extends BaseFragment implements ICallingVideo{
	private static final String TAG ="DailAcceptFragment";
	private RelativeLayout acceptlayout;
	private CircleImageView Head;
	private TextView MsgTv;
	private TextView name;
	private Button accepthangupBtn;
	private Button acceptBtn;
	private Button dialhangupBtn;
	private FamilyUserInfo mUser;
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
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view =inflater.inflate(R.layout.fragment_ring, container, false);
		acceptlayout = (RelativeLayout) view.findViewById(R.id.accept_layout);
		Head = (CircleImageView) view.findViewById(R.id.headicon);
		MsgTv = (TextView) view.findViewById(R.id.ring);
		name = (TextView) view.findViewById(R.id.ring_name);
		accepthangupBtn = (Button) view.findViewById(R.id.reject);
		dialhangupBtn = (Button) view.findViewById(R.id.hangup);
		acceptBtn = (Button) view.findViewById(R.id.accept_acc);
		getIntentData();
		return view;
	}



	private void getIntentData(){
		calltype = getArguments().getInt("CallType");
		 mUser = (FamilyUserInfo) getArguments().getSerializable("userinfo");
		switch(calltype){
		case SysConfig.ChatCall:
			acceptlayout.setVisibility(View.INVISIBLE);
			setViewDisplay(SysConfig.ChatCall);
			String msg = getString(R.string.calling_msg);
			MsgTv.setText(msg+" "+mUser.getNickName());
			//TODO
		//	DolphinApp.getInstance().MakeCall(mUser.getLoginName());
			
			//playRing(R.raw.ring180, false); // 正在呼叫铃声
			break;
		case SysConfig.ChatAccept:
			dialhangupBtn.setVisibility(View.INVISIBLE);
			setViewDisplay(SysConfig.ChatAccept);
			MsgTv.setText(R.string.invitechat);
			break;
		case SysConfig.MutilAccept:
			dialhangupBtn.setVisibility(View.INVISIBLE);
			setViewDisplay(SysConfig.MutilAccept);
			MsgTv.setText(R.string.invitegroup);
			break;
		case SysConfig.MeetingAccept:
			dialhangupBtn.setVisibility(View.INVISIBLE);
			setViewDisplay(SysConfig.MeetingAccept);
			MsgTv.setText(R.string.invitemeeting);
			break;
		}
	}
	private void setViewDisplay(int type){
		mHead = new LoadUserAvatarFromLocal();
		Bitmap bm3 = mHead.loadImage(mUser.getLoginName());
		if(bm3 != null){
			Head.setImageBitmap(bm3);
		}
		if(type == SysConfig.ChatCall){
			name.setText("");
		}else{
			name.setText(mUser.getNickName());
		}
		
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		initCallingMode();
		super.onDestroy();
	}
	
	public static DailOrAccepterFragment newInstance(){
		DailOrAccepterFragment DailAccept = new DailOrAccepterFragment();
		return DailAccept;
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
     * 初始化来电 TODO 需要更改
     */
    private void initIncallMode() {
        // initCallIn();
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
        playRing(resId, false, hanguped);
    }
    private int count;
    private void playRing(int resId, boolean bSpeaker, boolean hanguped) {
        stopRing();
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
    
	/**
	 * 关闭铃音
	 */
	private void stopRing() {
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
	
    public void closeUI(int time) {
    	Log.e(TAG, "closeUI");
        HBaseApp.post2UIDelayed(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                stopRing();
                if(getActivity() != null){
                	 getActivity().getSupportFragmentManager().beginTransaction().remove(DailOrAccepterFragment.this).commit();
                }
               
            }

        }, time);
    }


	@Override
	public void opt_Request(String parm) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void opt_Response(int action, String parm) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void ViewAction(int type) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void setViewUIVisible(int visible) {
		// TODO Auto-generated method stub
		
	}
}
