package com.wellav.dolphin.mmedia.fragment;

import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.RemoteAdvanceActivity;
import com.wellav.dolphin.mmedia.commands.DolphinImMime;
import com.wellav.dolphin.mmedia.commands.MessageEventType;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.DolphinConfigInfo;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.MessageInform;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.net.UploadData2Server;
import com.wellav.dolphin.mmedia.net.UploadData2Server.codeDataCallBack;
import com.wellav.dolphin.mmedia.net.VolleyRequestQueueManager;
import com.wellav.dolphin.mmedia.net.XMLParser;
import com.wellav.dolphin.mmedia.net.XMLRequest;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.JsonUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class RemoteSettingEnterFragment extends BaseFragment implements OnClickListener {
	
	private static final int GET_DOLPHIN_SETTING_OK = 0;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private TextView mDeviceName;
	private CircleImageView mDeviceIcon;
	private SeekBar mVolume;
	private SeekBar mBrightness;
	private TextView senior_setting;
	private TextView aboutDolphin;
	private String deviceID;
	private String familyID;
	private TextView networkname;
	private FamilyInfo callInfo;
	public SharedPreferences remoteshareprefences;
	
	private DolphinConfigInfo dolphinConfigInfo;
	private Boolean volume_flag = false;
	private Boolean bribhtness_flag = false;
	private FamilyInfo info;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.remote_setting_enter, container, false);
		
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mDeviceName = (TextView) view.findViewById(R.id.device_name);
		mDeviceIcon = (CircleImageView) view.findViewById(R.id.device_icon);
		mVolume = (SeekBar)view.findViewById(R.id.seekbar_voice);
		mBrightness = (SeekBar)view.findViewById(R.id.seekbar_bright);
		senior_setting = (TextView)view.findViewById(R.id.remote_senior);
		networkname = (TextView)view.findViewById(R.id.remote_networkname);
		aboutDolphin = (TextView)view.findViewById(R.id.remote_about);
		mActionbarPrev.setOnClickListener(this);
		senior_setting.setOnClickListener(this);
		aboutDolphin.setOnClickListener(this);
		mActionbarName.setText(R.string.remote_control);
		
		Bundle bundle = getArguments();
		callInfo = (FamilyInfo) bundle.getSerializable("FamilyInfo");
		deviceID = callInfo.getDeviceID();
		familyID = callInfo.getFamilyID();
		
		LoadUserAvatarFromLocal task = new LoadUserAvatarFromLocal();
		Bitmap head = task.loadImage(deviceID);
		
		 if(head !=null){
			 mDeviceIcon.setImageBitmap(head);
		 }else{
			 mDeviceIcon.setImageResource(R.drawable.defaulthead_home_40dp);
		 }
		 String name = TextUtils.isEmpty(callInfo.getNote() ) ? callInfo.getNickName():callInfo.getNote();
		 mDeviceName.setText(name);
        
		remoteshareprefences = getActivity().getSharedPreferences(SysConfig.userid+"_"+deviceID, Context.MODE_PRIVATE);
		
		GetDolphinConfig();
		return view;
	}
	
	public static RemoteSettingEnterFragment newInstance(Bundle bundle){
		RemoteSettingEnterFragment rse = new RemoteSettingEnterFragment();
		rse.setArguments(bundle);
		return rse;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_prev:
			if(getSettingChanged()){
				uploadMsg();
			}
			getActivity().finish(); 			
			break;
		case R.id.remote_senior:
			Bundle bundle=new Bundle();
			bundle.putString("deviceID", deviceID);
			bundle.putString("familyID", familyID);
			
			Intent intent=new Intent(getActivity(),RemoteAdvanceActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
        case R.id.remote_about:
        	Bundle bundle2=new Bundle();
        	bundle2.putString("familyID", familyID);
        	RemoteSettingAboutFragment rsa=new RemoteSettingAboutFragment();
        	rsa.setArguments(bundle2);
        	FragmentManager fm1=getActivity().getSupportFragmentManager();
			FragmentTransaction ft1=fm1.beginTransaction();
			ft1.add(R.id.container, rsa);
			ft1.addToBackStack(null);
			ft1.commit();
			break;
		default:
			break;
		}	
	}
	
	/**
	 * 向服务器发出请求，并解析返回的数据
	 * @param requestBody
	 * @param request
	 */
	private void requestXml(String requestBody,final String request){
		XMLRequest xmlRequest=new XMLRequest(SysConfig.ServerUrl + request,
		requestBody, new Response.Listener<XmlPullParser>(){
			@Override
			public void onResponse(XmlPullParser response) {
                dolphinConfigInfo = XMLParser.ParseXMLConfig(response);
				setSetingValue(dolphinConfigInfo, getActivity());
				mHandler.sendEmptyMessage(GET_DOLPHIN_SETTING_OK);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error){
				Log.e("TAG", error.getMessage(), error);
			}
		});
		VolleyRequestQueueManager.addRequest(xmlRequest, null);
	}

	public void GetDolphinConfig() {
	    DolphinApp.post2WorkRunnable(new Runnable() {
		    @Override
		    public void run() {
			    String token = SysConfig.dtoken;
			    // 要发送的xml数据
			    String GetDolphinConfig = RequestString.GetDolphinConfig(token, familyID);  
		        requestXml(GetDolphinConfig, "GetDolphinConfig");
		    }
	    });
    }
   
	@SuppressLint("HandlerLeak") Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case GET_DOLPHIN_SETTING_OK:
				mVolume.setProgress(remoteshareprefences.getInt("volume",0));
				mBrightness.setProgress(remoteshareprefences.getInt("brightness",0));
				
				mVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						//存入数据库
						Editor editor = remoteshareprefences.edit();
						editor.putInt("volume",mVolume.getProgress());
						editor.commit();	
						
//						DolphinApp.getInstance().makeSendIm(deviceID, 
//						         DolphinImMime.DplMime_settingResult_json,
//						         JsonUtil.getVolumeJsonObject(mVolume.getProgress()));
					}
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						//当声音大小发生改变的时候，才发送数据到海豚端
						volume_flag = true;
					}
				});
				
				mBrightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						//存入数据库
						Editor editor=remoteshareprefences.edit();
						editor.putInt("brightness",mBrightness.getProgress());
						editor.commit();
						
//						DolphinApp.getInstance().makeSendIm(deviceID, 
//			   				     DolphinImMime.DplMime_settingResult_json,
//			   				     JsonUtil.getBrightnessJsonObject(mBrightness.getProgress()));
					}
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						//当亮度发生改变的时候，才发送数据到海豚端
						bribhtness_flag = true;
					}
				});
				break;
			}
			super.handleMessage(msg);
		}
	};
		
   // 设置初始值
   private void setSetingValue(DolphinConfigInfo dolphinConfigInfo,Context ctx){
	    // 保存声音和音量
	    Editor editor_remote = remoteshareprefences.edit();
	    editor_remote.putInt("volume", dolphinConfigInfo.getVoiceSize());
	    editor_remote.putInt("brightness",dolphinConfigInfo.getBrightnessSizer());

	    // 保存播报语音，相册信息栏
		int voice_reminder_lang_sp = Integer.valueOf(dolphinConfigInfo.getVoiceLanguage());
		editor_remote.putInt("voice_reminder_lang_sp",voice_reminder_lang_sp);
		
		Boolean flag_is_photo_info = getFlag(dolphinConfigInfo.getISDisplayPhoto());
		editor_remote.putBoolean("is_photo_info", flag_is_photo_info);
		
		// 保存待机模式设置
		// 模式选择
		int i_mode_sp = Integer.valueOf(dolphinConfigInfo.getWaitMode());
		editor_remote.putInt("mode_sp",i_mode_sp);
		// 进入待机时间
		int i_standby_time_sp = Integer.valueOf(dolphinConfigInfo.getStartTime());
		editor_remote.putInt("standby_time_sp",i_standby_time_sp);
		
		int i_interval_sp = Integer.valueOf(dolphinConfigInfo.getIntervalTime());
		editor_remote.putInt("interval_sp",i_interval_sp);
		
		int i_duration_sp = Integer.valueOf(dolphinConfigInfo.getContinueTime());
		editor_remote.putInt("duration_sp",i_duration_sp);
		
		int i_playing_how_long_str = Integer.valueOf(dolphinConfigInfo.getContinueTime());
		editor_remote.putInt("playing_how_long_sp",i_playing_how_long_str);
		
		int i_photo_whichToPlay_str =  Integer.valueOf(dolphinConfigInfo.getPlayPhotoType());
		editor_remote.putInt("which_photo_sp",i_photo_whichToPlay_str);
		
		
		// 保存资讯设置
		Boolean flag_broadcast_toggle1 = getFlag(dolphinConfigInfo.getISBroadcastOne());
		editor_remote.putBoolean("broadcast_toggle1", flag_broadcast_toggle1);
		editor_remote.putString("broadcast_time1",dolphinConfigInfo.getBroadcastOneTime());

		Boolean flag_broadcast_toggle2 = getFlag(dolphinConfigInfo.getISBroadcastTwo());
		editor_remote.putBoolean("broadcast_toggle2", flag_broadcast_toggle2);
		editor_remote.putString("broadcast_time2",dolphinConfigInfo.getBroadcastTwoTime());
		
		//保存紧急联系人id,发送过来的是紧急联系人id
		String agentContactUserId = dolphinConfigInfo.getAgentContact();
		//更新紧急联系人id，选择时候划勾的判断依据
		info = SQLiteManager.getInstance().getFamilyInfoDeviceID(deviceID);
		SQLiteManager.getInstance().updateFamilyInfo(info.getDeviceUserID(), FamilyInfo._FAMILY_AGENTCONTACT_ID, agentContactUserId, false);
		
		//ver
		editor_remote.putString("softVersion",dolphinConfigInfo.getSoftwareVersion());
		editor_remote.putString("hardVersion",dolphinConfigInfo.getHardwareVersion());
		editor_remote.putString("seriaNumber",dolphinConfigInfo.getSerialNumber());
		
		editor_remote.commit();
   }
   
   private Boolean getFlag(int flag){
	   Boolean booleanFlag = false;
	   switch (flag) {
		case 0:
			booleanFlag = false;
			break;
		case 1:
			booleanFlag = true;
			break;
		}
	   return booleanFlag;
   }
   
   /**
    * 判断最终的设置是否被改动
    * @return
    */
   private Boolean getSettingChanged(){
       if(dolphinConfigInfo == null || remoteshareprefences==null){
           return false;
       }
	   Boolean changedFlag = !(dolphinConfigInfo.getBrightnessSizer()==remoteshareprefences.getInt("brightness", 0) &&
			   dolphinConfigInfo.getVoiceSize()==remoteshareprefences.getInt("volume", 0) &&
			   getFlag(dolphinConfigInfo.getISDisplayPhoto())==remoteshareprefences.getBoolean("is_photo_info", false) &&
			   Integer.valueOf(dolphinConfigInfo.getVoiceLanguage())==remoteshareprefences.getInt("voice_reminder_lang_sp", 0) &&
			   Integer.valueOf(dolphinConfigInfo.getWaitMode())==remoteshareprefences.getInt("mode_sp", 0) &&
			   Integer.valueOf(dolphinConfigInfo.getStartTime())==remoteshareprefences.getInt("standby_time_sp", 0) &&
			   Integer.valueOf(dolphinConfigInfo.getIntervalTime())==remoteshareprefences.getInt("interval_sp", 0) &&
			   Integer.valueOf(dolphinConfigInfo.getContinueTime())==remoteshareprefences.getInt("duration_sp", 0) &&
			   Integer.valueOf(dolphinConfigInfo.getContinueTime())==remoteshareprefences.getInt("playing_how_long_sp", 0) &&
			   Integer.valueOf(dolphinConfigInfo.getPlayPhotoType())==remoteshareprefences.getInt("which_photo_sp", 0) &&
			   getFlag(dolphinConfigInfo.getISBroadcastOne())==remoteshareprefences.getBoolean("broadcast_toggle1", false) &&
			   dolphinConfigInfo.getBroadcastOneTime().equals(remoteshareprefences.getString("broadcast_time1","00:00")) &&
			   getFlag(dolphinConfigInfo.getISBroadcastTwo())==remoteshareprefences.getBoolean("broadcast_toggle2", false) &&
			   dolphinConfigInfo.getBroadcastTwoTime().equals(remoteshareprefences.getString("broadcast_time2","00:00")) &&
			   dolphinConfigInfo.getAgentContact().equals(SQLiteManager.getInstance().getAgentContactIDByDeviceUserID(info.getDeviceUserID())));
	return changedFlag;
   }
   
   LoadUserAvatarFromLocal task = new LoadUserAvatarFromLocal();
   Bitmap head = task.loadImage(deviceID);
   private void uploadMsg(){
		final MessageInform  msgInfo = new MessageInform();
		msgInfo.setEventType(MessageEventType.EVENTTYPE_DISTANCE_SETTING);
		msgInfo.setDeviceID(deviceID);
		msgInfo.setFamilyId(familyID);
		msgInfo.setDolphinName(callInfo.getNickName());
		msgInfo.setUserID(DolphinApp.getInstance().getMyUserInfo().getUserID());
		msgInfo.setName(DolphinApp.getInstance().getMyUserInfo().getNickName());
		String msgContent = JsonUtil.getBOxMsgJsonObject(msgInfo);
   	
		String mBody = RequestString.UploadMessageBox(SysConfig.dtoken, MsgKey.TO_FAMILY, familyID,msgContent );
		UploadData2Server task = new UploadData2Server(mBody,"UploadMessageBox");
		task.getData(new codeDataCallBack() {
			@Override
			public void onDataCallBack(String request, int code) {
				if(code == 0){
					//DolphinApp.getInstance().notifyMsgRTC2Family(msgInfo.getFamilyId());
				}
				getActivity().finish();
			}
		});
	}
}
