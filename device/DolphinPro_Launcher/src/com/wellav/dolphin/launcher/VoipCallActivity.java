package com.wellav.dolphin.launcher;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.ui.CircleImageView;
import com.wellav.dolphin.ui.ImageButtonText;
import com.wellav.dolphin.utils.Util;
import com.wellav.dolphin.voip.SDKCoreHelper;
import com.wellav.dolphin.voip.VoIPCallHelper;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECVoIPCallManager.CallType;
import com.yuntongxun.ecsdk.VideoRatio;

public class VoipCallActivity extends BaseActivity implements VoIPCallHelper.OnCallEventNotifyListener  {
	private static final String TAG = "VoipCallActivity";
	private FamilyUserInfo mUser;
	private String mCallName;
	private String mCallID="";
	private RelativeLayout acceptlayout;
	private CircleImageView Head;
	private TextView MsgTv;
	private TextView name;
	private ImageButtonText dialhangupBtn;
	private LoadUserAvatarFromLocal mHead;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.avchat_groupchat_receive_layout);
		
		acceptlayout = (RelativeLayout) findViewById(R.id.accept_layout);
		Head = (CircleImageView) findViewById(R.id.icon);
		MsgTv = (TextView) findViewById(R.id.txt);
		name = (TextView) findViewById(R.id.name);
		//dialhangupBtn = (ImageButtonText) findViewById(R.id.hangup);
		acceptlayout.setVisibility(View.GONE);
		
		Bundle bundle = getIntent().getExtras();
		mUser = (FamilyUserInfo) bundle.getSerializable("UserInfo");
		mCallName = mUser.getLoginName();
		setViewDisplay();
		SDKCoreHelper.init(VoipCallActivity.this, ECInitParams.LoginMode.FORCE_LOGIN);
		
	}
	
	private void setViewDisplay(){
		if(mUser == null){
			Util.PrintLog(TAG, "mUser:null");
			return;
		}
		mHead = new LoadUserAvatarFromLocal();
		Bitmap bm3 = mHead.loadImage(mUser.getLoginName());
		if(bm3 != null){
			Head.setImageBitmap(bm3);
		}
		MsgTv.setText(R.string.offline_callphone);
		name.setText(mUser.getNickName());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		  VoIPCallHelper.setOnCallEventNotifyListener(this);
	}


	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		 ECDevice.unInitial();
		super.onDestroy();
	}

	public void onHangUp(View view){
		VoIPCallHelper.releaseCall(mCallID);
		finish();
	}
	public void onNetWorkNotify(ECDevice.ECConnectState connect) {
		Util.PrintLog(TAG, connect.toString());
		if(connect == ECDevice.ECConnectState.CONNECT_SUCCESS){
			mCallID = VoIPCallHelper.makeCall(CallType.DIRECT, mCallName);
		}else if(connect == ECDevice.ECConnectState.CONNECT_FAILED){
			finish();
		}
	}
	@Override
	public void onCallProceeding(String callId) {
		// TODO Auto-generated method stub
		Util.PrintLog(TAG, "onCallProceeding:");
	}

	@Override
	public void onMakeCallback(ECError arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		Util.PrintLog(TAG, "onMakeCallback:");
	}

	@Override
	public void onCallAlerting(String callId) {
		// TODO Auto-generated method stub
		Util.PrintLog(TAG, "onCallAlerting:");
	}

	@Override
	public void onCallAnswered(String callId) {
		// TODO Auto-generated method stub
		MsgTv.setText(R.string.voip_calling);
		Util.PrintLog(TAG, "onCallAnswered:");
	}

	@Override
	public void onMakeCallFailed(String callId, int reason) {
		// TODO Auto-generated method stub
		Util.PrintLog(TAG, "onMakeCallFailed:"+reason);
		VoIPCallHelper.releaseCall(mCallID);
		finish();
	}

	@Override
	public void onCallReleased(String callId) {
		// TODO Auto-generated method stub
		VoIPCallHelper.releaseCall(mCallID);
		finish();
		Util.PrintLog(TAG, "onCallReleased:");
		
	}

	@Override
	public void onVideoRatioChanged(VideoRatio videoRatio) {
		// TODO Auto-generated method stub
		
	}

}
