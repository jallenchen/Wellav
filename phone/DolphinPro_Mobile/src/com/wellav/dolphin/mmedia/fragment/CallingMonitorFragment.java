package com.wellav.dolphin.mmedia.fragment;

import java.sql.Connection;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.WatchFamilyAvtivity;
import com.wellav.dolphin.mmedia.interfaces.ICallingVideo;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class CallingMonitorFragment extends BaseFragment  {
	public final static String TAG = "CallingMonitorFragment";
	private RelativeLayout monitorRemote, monitorLocal;
	private SurfaceView callerRemote;
	private RelativeLayout layoutdirct,layout2chat,layout2small;
	private LinearLayout layout2function;
	private AudioManager mAudioManager;// 系统声音控制
	Connection call;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		CommFunc.PrintLog(TAG, "onCreateView");
		View view =inflater.inflate(R.layout.activity_monitor, container, false);
		monitorRemote = (RelativeLayout) view.findViewById(R.id.watch_layout_video_other);
		monitorLocal = (RelativeLayout) view.findViewById(R.id.watch_layout_video_our);
		layoutdirct = (RelativeLayout) view.findViewById(R.id.video_dir);
		layout2chat = (RelativeLayout) view.findViewById(R.id.watch_to_chat);
		layout2small = (RelativeLayout) view.findViewById(R.id.layout_watch_close);
		layout2function = (LinearLayout) view.findViewById(R.id.watch_layout_video_function);
		mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		
		return view;
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
    
    // 主持人进入频道
    private void onJoin(String s) {
          //  SurfaceView surfaceView = AVChatManager.getInstance().getSurfaceRender(s);
            AVChatVideoRender smallRender = new AVChatVideoRender(getActivity());
            AVChatManager.getInstance().setupVideoRender(s, smallRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
           // setVideoSurfaceVisibility(View.VISIBLE);
            if (smallRender != null) {
            	callerRemote = smallRender;
                addIntoMasterPreviewLayout(smallRender);
            }
    }
    
    // 将主持人添加到主持人画布
    private void addIntoMasterPreviewLayout(SurfaceView surfaceView) {
    	if(surfaceView == null){
    		return;
    	}
        if (surfaceView.getParent() != null)
            ((ViewGroup)surfaceView.getParent()).removeView(surfaceView);
        monitorRemote.addView(surfaceView);
        surfaceView.setZOrderMediaOverlay(true);
    }


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(monitorRemote == null){
			return;
		}
		if(hidden == true){
			monitorRemote.removeAllViews();
		}else{
			onJoin(WatchFamilyAvtivity.callName);
		}
	}

}
