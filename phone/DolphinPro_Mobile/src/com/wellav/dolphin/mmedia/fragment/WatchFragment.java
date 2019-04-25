package com.wellav.dolphin.mmedia.fragment;

import java.sql.Connection;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoQuality;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatOptionalConfig;
import com.netease.nimlib.sdk.avchat.model.AVChatParameters;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoRender;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.WatchFamilyAvtivity;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.netease.LogUtil;
import com.wellav.dolphin.mmedia.netease.SendNitificationMsgUtil;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class WatchFragment extends BaseFragment implements OnClickListener {
	private static final String TAG = "WatchFragment";
	private RelativeLayout watchRL;
	private RelativeLayout watchOur;
	private RelativeLayout watchOpenLayout;
	private ImageView mActionbarPrev;
	private TextView mActionbarName;
	
	private LinearLayout watchLY;
	private ImageView watchBtn;
	private ImageView watchCloseBtn;
	private ImageView watchFullBtn;
	public SurfaceView callerRemote;
	CallingMonitorFragment Monitor;
	Connection call;
	boolean isFull = false;
	boolean isAnimation = false;
	Animation animation;
	private WatchFragment ctx;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
		this.ctx = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		CommFunc.PrintLog(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_watching, container, false);
		watchRL = (RelativeLayout) view.findViewById(R.id.watch_other);
		watchOur = (RelativeLayout) view.findViewById(R.id.watch_our);
		watchOpenLayout = (RelativeLayout) view.findViewById(R.id.watch_open_layout);
		watchLY = (LinearLayout) view.findViewById(R.id.watch_ll);
		watchBtn = (ImageView) view.findViewById(R.id.watch_open);
		watchCloseBtn = (ImageView) view.findViewById(R.id.watch_close);
		watchFullBtn = (ImageView) view.findViewById(R.id.watch_fullscreen);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		
		RelativeLayout rl = new RelativeLayout(getActivity()); 

		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams
		(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); 
		lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP); 
		lp1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE); 
		// btn1 位于父 View 的顶部，在父 View 中水平居中 
	     
		animation = AnimationUtils.loadAnimation(getActivity(), R.anim.watch_rotate);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				isAnimation = false;
			}
		});
		watchLY.addView(rl);
		mActionbarName.setText(R.string.dolphin_action);
		mActionbarPrev.setOnClickListener(this);
		watchBtn.setOnClickListener(this);
		watchCloseBtn.setOnClickListener(this);
		return view;
	}
	
	 
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		CommFunc.PrintLog(TAG, "onHiddenChanged"+hidden);
		if(hidden == false){
			if(SysConfig.getInstance().getStatus() == SysConfig.MonitorAccept){
				onJoin(WatchFamilyAvtivity.callName);
			}else{
				setVideoSurfaceVisibility(View.INVISIBLE);
			}
		}else{
			removeSurfaceView();
		}
	}

	@Override
	public void onResume() {
		CommFunc.PrintLog(TAG, "onResume");
		if(SysConfig.getInstance().getStatus() != SysConfig.MonitorAccept){
			setVideoSurfaceVisibility(View.INVISIBLE);
		}
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void removeSurfaceView(){
		watchOur.removeAllViews();
		watchRL.removeAllViews();
	
	}

	@Override
	public void onPause() {
		CommFunc.PrintLog(TAG, "onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		CommFunc.PrintLog(TAG, "onStop");
		super.onStop();
	}

 

	/**
	 * 
	 * @param visible
	 */
	public void setVideoSurfaceVisibility(int visible) {
		isAnimation = false;
		if(visible == View.INVISIBLE){
			watchOpenLayout.setVisibility(View.VISIBLE);
			watchCloseBtn.setVisibility(View.INVISIBLE);
			watchFullBtn.setVisibility(View.INVISIBLE);
		}else{
			watchBtn.clearAnimation();
			watchOpenLayout.setVisibility(View.INVISIBLE);
			watchCloseBtn.setVisibility(View.VISIBLE);
			watchFullBtn.setVisibility(View.VISIBLE);
		}
	}

	private void newInstanceMonitor(){
		Monitor = new CallingMonitorFragment();
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.container, Monitor);
		ft.commit();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.watch_open:
			JSONObject obj = new JSONObject();
		    obj.put("type", "monitor");
	        obj.put("content", "request");
		    String jsonContent = obj.toJSONString();
			SendNitificationMsgUtil.sendCustomNotification(getActivity(),WatchFamilyAvtivity.callName,jsonContent);
			
			 if(isAnimation == true){
				 CommFunc.DisplayToast(getActivity(),ctx.getResources().getString(R.string.requesting_wait));
				 return;
			 }
			 isAnimation = true;
			 watchBtn.startAnimation(animation);
			break;
		case R.id.watch_close:
			clearChatRoom();
			
			break;
		case R.id.watch_fullscreen:
			removeSurfaceView();
			newInstanceMonitor();
			break;
		case R.id.actionbar_prev:
			clearChatRoom();
			getActivity().finish();
			break;
		}
	}
	
	String creator;
    // 初始化UI
    public void initLiveVideo(String channelName) {
        AVChatOptionalConfig avChatOptionalParam = new AVChatOptionalConfig();
        avChatOptionalParam.enableAudienceRole(true);
        avChatOptionalParam.enableVideoRotate(false);
        avChatOptionalParam.setVideoQuality(AVChatVideoQuality.QUALITY_LOW);
        avChatOptionalParam.setVideoDecoderMode(AVChatParameters.MEDIA_CODEC_SOFTWARE);
        AVChatManager.getInstance().joinRoom(channelName, AVChatType.VIDEO, avChatOptionalParam, new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData avChatData) {
                LogUtil.d(TAG, "join channel success");
                SysConfig.getInstance().setStatus(SysConfig.MonitorAccept);
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

        // 主播少一个页面。必须要从多到少，否则初始化过的数组会溢出
        // 如果界面被销毁，adapter要在系统回来的时候即刻初始化，所以不能等到进入房间，获得
        // 是否主持人信息时，才初始化adapter
    }
    
    public void clearChatRoom() {
        LogUtil.d(TAG, "chat room do clear");
        AVChatManager.getInstance().leaveRoom(new AVChatCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                LogUtil.d(TAG, "leave channel success");
                setVideoSurfaceVisibility(View.INVISIBLE);
    			removeSurfaceView();
            }

            @Override
            public void onFailed(int i) {
                LogUtil.d(TAG, "leave channel failed, code:" + i);
            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
        SysConfig.getInstance().setStatus(SysConfig.Free);
    }
    
    // 主持人进入频道
    public void onJoin(String s) {
          //  SurfaceView surfaceView = AVChatManager.getInstance().getSurfaceRender(s);
            AVChatVideoRender smallRender = new AVChatVideoRender(getActivity());
            AVChatManager.getInstance().setupVideoRender(s, smallRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED);
            setVideoSurfaceVisibility(View.VISIBLE);
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
        watchRL.addView(surfaceView);
        surfaceView.setZOrderMediaOverlay(true);
    }
}
