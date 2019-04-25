package com.wellav.dolphin.mmedia;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.commands.DolphinImMime;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal.userAvatarDataCallBack;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class DolphinSettingFragmentActivity extends BaseActivity implements OnClickListener,OnCheckedChangeListener{
	
	//private ListView mList;
	//private DolphinSettingAdapter adapter;
	
	private ToggleButton mWatchSwitch;
	private ToggleButton mCallSwitch;
	private ToggleButton mPhotoSwitch;
	private TextView mRemoteSetting;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private CircleImageView homeIcon;
	private TextView homeNote;
	private TextView homeNick;
	private View view3;
	private TextView setting_power;
    private TextView setting_net;
    private MyReceiver receiver = null;
    
	private SharedPreferences DolphinSettingSharedPreferences;
	boolean notify[]  = new boolean[3];
	private FamilyInfo callInfo;
	private LoadUserAvatarFromLocal userAvatar;

	@Override
	protected void onCreate(Bundle arg0) {
		
		super.onCreate(arg0);
		setContentView(R.layout.fragmentsettingdolphin);
		
		//mList = (ListView) findViewById(R.id.setting_list);
		mRemoteSetting = (TextView) findViewById(R.id.remotetxt);
		view3 = findViewById(R.id.view3);
		mWatchSwitch = (ToggleButton) findViewById(R.id.setting_switch);
		mCallSwitch = (ToggleButton) findViewById(R.id.setting_switch2);
		mPhotoSwitch = (ToggleButton) findViewById(R.id.setting_switch3);
		mActionbarName = (TextView) findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) findViewById(R.id.actionbar_prev);
		homeIcon= (CircleImageView) findViewById(R.id.setting_home_icon);
		homeNote = (TextView) findViewById(R.id.setting_note_name);
		homeNick = (TextView) findViewById(R.id.setting_nick_name);
		setting_power = (TextView) findViewById(R.id.setting_power);
		setting_net = (TextView) findViewById(R.id.setting_net);
		initData();
		mWatchSwitch.setChecked(notify[0]);
		mCallSwitch.setChecked(notify[1]);
		mPhotoSwitch.setChecked(notify[2]);
		
		mActionbarName.setText(R.string.dolphin_setting);
		mWatchSwitch.setOnCheckedChangeListener(this);
		mCallSwitch.setOnCheckedChangeListener(this);
		mPhotoSwitch.setOnCheckedChangeListener(this);
		mRemoteSetting.setOnClickListener(this);
		mActionbarPrev.setOnClickListener(this);
		
		// 接收广播，设置上电量和网络状态
		//注册广播
		receiver = new MyReceiver();  
		IntentFilter intentFilter = new IntentFilter();  
		intentFilter.addAction(SysConfig.RecevierNetPowerAction);  
		registerReceiver(receiver, intentFilter);
	}
	
	// 广播接收器
	@SuppressLint("ResourceAsColor") public class MyReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String power = bundle.getString("power");
			String net = bundle.getString("net");
			setting_power.setText(power);
			setting_net.setText(net);
			
			if(!net.contains(getResources().getString(R.string.strong))){
				setting_power.setTextColor(Color.RED);
			}
			if(power.length() == 2){
				setting_power.setTextColor(Color.RED);
			}
			
		}
	} 
	
	
	
	private void initData(){
		userAvatar = new LoadUserAvatarFromLocal();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
		
			callInfo = (FamilyInfo) getIntent().getSerializableExtra("Family");
			// 发送请求，请求获取电量，网络等状态信息
		//	DolphinApp.getInstance().makeSendIm(callInfo.getDeviceID(), DolphinImMime.DplMime_power_net_request_json,null);
		}
		String note = TextUtils.isEmpty(callInfo.getNote())? callInfo.getNickName() : callInfo.getNote();
		homeNote.setText(note);
		homeNick.setText(getResources().getString(R.string.nickName)+callInfo.getNickName());
		Bitmap bitmap = userAvatar.loadImage(callInfo.getDeviceID());
		if(bitmap != null){
			homeIcon.setImageBitmap(bitmap);
		}else{
			userAvatar.getData(callInfo.getDeviceUserID(), new userAvatarDataCallBack() {
				
				@Override
				public void onDataCallBack(int code, Bitmap avatar) {
					// TODO Auto-generated method stub
					homeIcon.setImageBitmap(avatar);
				}
			});
		}
		
		getNotifySetting();
	}
	
	private void getNotifySetting(){
	
		DolphinSettingSharedPreferences = getSharedPreferences(SysConfig.userid+"_"+callInfo.getDeviceID(), MODE_PRIVATE);
		notify[0] = DolphinSettingSharedPreferences.getBoolean("watch_notification", false);
		notify[1] = DolphinSettingSharedPreferences.getBoolean("call_notification", false);
		notify[2] = DolphinSettingSharedPreferences.getBoolean("checkPhoto_notification", false);
		
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.actionbar_prev:
			finish();
			break;
		case R.id.remotetxt:
			remoteSetting();
			break;
		}
	}

	public void remoteSetting(){
			if(!callInfo.getMangerID().equals((SysConfig.userid))){
				CommFunc.DisplayToast(getBaseContext(), getResources().getString(R.string.no_manager));
				return;
			}
			
			Bundle bundle=new Bundle();
			bundle.putSerializable("FamilyInfo", callInfo);
			Intent intent=new Intent(DolphinSettingFragmentActivity.this,RemoteSettingActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch(buttonView.getId()){
		case R.id.setting_switch:
			DolphinSettingSharedPreferences.edit().putBoolean("watch_notification", isChecked).commit();
			break;
		case R.id.setting_switch2:
			DolphinSettingSharedPreferences.edit().putBoolean("call_notification", isChecked).commit();
			break;
		case R.id.setting_switch3:
			DolphinSettingSharedPreferences.edit().putBoolean("checkPhoto_notification", isChecked).commit();
		break;
		}
	}
	
}
