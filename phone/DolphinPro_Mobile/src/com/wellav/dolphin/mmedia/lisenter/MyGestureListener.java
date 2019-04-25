package com.wellav.dolphin.mmedia.lisenter;


import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.WindowManager;

public class MyGestureListener extends SimpleOnGestureListener {


	private int maxVolume;
	public int volume = -1;
	public float brightness = -1f;
	private Context mContext;
	private Handler myHandler;
	private AudioManager mAudioManager;
	private static final int LIMIT = 5;

	/**
	 * @param videoCallingActivity
	 */
	public MyGestureListener(Context ct,Handler handler,AudioManager audioManager) {
		this.mContext = ct;
		this.myHandler = handler;
		this.mAudioManager = audioManager;
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	/** 双击 */
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return true;
	}

	/** 滑动 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2,
			float distanceX, float distanceY) {

		float mOldX = e1.getX(), mOldY = e1.getY();
		int x = (int) e2.getRawX();
		int y = (int) e2.getRawY();
		 DisplayMetrics dm = new DisplayMetrics();   
		dm = mContext.getResources().getDisplayMetrics(); 
		int screenWidth = dm.widthPixels;   
		int screenHeight = dm.heightPixels; 

		 Log.e("MyGestureListener",
		"mOldX:"+mOldX+"-mOldY:"+mOldY+"-distanceX:"+distanceX+"-distanceY:"+distanceY);

		// 横向的距离变化大则调整音量，纵向的变化大则调整亮度
		if (Math.abs(distanceX) >= Math.abs(distanceY)  && (Math.abs(distanceX) >= LIMIT)) {
			onVolumeSlide((x - mOldX) / screenWidth);
		} else if(Math.abs(distanceY) >= Math.abs(distanceX)  && (Math.abs(distanceY) >= LIMIT)) {
			onBrightnessSlide((mOldY - y) / screenHeight);
		}
		return super.onScroll(e1, e2, distanceX, distanceY);
	}
	
	
	
	
	/**
	 * 滑动改变声音大小
	 * 
	 * @param percent
	 */
	void onVolumeSlide(float percent) {
		if (volume == -1) {
			volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			// int current = mAudioManager.getStreamVolume(
			// AudioManager.STREAM_VOICE_CALL );
			if (volume < 0) {
				volume = 0;
			}
			// 显示
			// mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			myHandler.sendEmptyMessage(1);
		}

		int nowVolume = (int) (percent * maxVolume) + volume;
		if (nowVolume > maxVolume) {
			nowVolume = maxVolume;
		} else if (nowVolume < 0) {
			nowVolume = 0;
		}
		// 变更声音
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, nowVolume, 0);

		int volumePer = (nowVolume * 100) / maxVolume;
		Message msg = new Message();
		msg.what = 2;
		msg.arg1 = volumePer;
		myHandler.sendMessage(msg);
		Log.e("TAG", "volume:" + volumePer);
		// 变更进度条
		// ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		// lp.width = findViewById(R.id.operation_full).getLayoutParams().width
		// * index / mMaxVolume;
		// mOperationPercent.setLayoutParams(lp);
	}
	
	/**
	 * 滑动改变亮度
	 * 
	 * @param percent
	 */
	void onBrightnessSlide(float percent) {
		if (brightness < 0) {
			brightness = ((Activity) mContext).getWindow().getAttributes().screenBrightness;
			if (brightness <= 0.00f) {
				brightness = 0.50f;
			}
			if (brightness < 0.01f) {
				brightness = 0.01f;
			}
			// 显示
			myHandler.sendEmptyMessage(3);
		}

		WindowManager.LayoutParams lpa = ((Activity) mContext).getWindow().getAttributes();
		lpa.screenBrightness = brightness + percent;

		if (lpa.screenBrightness > 1.0f) {
			lpa.screenBrightness = 1.0f;
		} else if (lpa.screenBrightness < 0.01f) {
			lpa.screenBrightness = 0.01f;
		}

		((Activity) mContext).getWindow().setAttributes(lpa);
		int brightPer = (int) (lpa.screenBrightness * 100);
		
		Message msg = new Message();
		msg.what = 4;
		msg.arg1 = brightPer;
		myHandler.sendMessage(msg);

		Log.e("TAG", "Brightness:" + brightPer);
		// ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		// lp.width = (int)
		// (findViewById(R.id.operation_full).getLayoutParams().width *
		// lpa.screenBrightness);
		// mOperationPercent.setLayoutParams(lp);
	}

}
