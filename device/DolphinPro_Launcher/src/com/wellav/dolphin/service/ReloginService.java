//package com.wellav.dolphin.service;
//
//import java.util.List;
//
//import rtc.sdk.common.NetWorkUtil;
//import android.annotation.SuppressLint;
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.support.v4.content.LocalBroadcastManager;
//
//import com.wellav.dolphin.application.HBaseApp;
//import com.wellav.dolphin.application.LauncherApp;
//import com.wellav.dolphin.bean.FamilyInfo;
//import com.wellav.dolphin.bean.FamilyUserInfo;
//import com.wellav.dolphin.config.MsgKey;
//import com.wellav.dolphin.config.SysConfig;
//import com.wellav.dolphin.db.PreferenceUtils;
//import com.wellav.dolphin.interfaces.IProviderContactMetaData.ContactTableMetaData;
//import com.wellav.dolphin.net.LoadAccountInfoFromServer;
//import com.wellav.dolphin.net.LoadAccountInfoFromServer.DataCallBack;
//import com.wellav.dolphin.net.LoadAllFamilyUserFromServer;
//import com.wellav.dolphin.net.LoadAllFamilyUserFromServer.allUserDataCallBack;
//import com.wellav.dolphin.net.UploadData2Server;
//import com.wellav.dolphin.net.UploadData2Server.codeDataCallBack;
//import com.wellav.dolphin.net.XMLBody;
//import com.wellav.dolphin.utils.Util;
//
//public class ReloginService extends Service {
//	private static ReloginService instance = null;
//	private static String TAG = "ReloginService";
//	private boolean bstarttimer = true;
//
//	public static ReloginService getInstance() {
//		if (instance == null) {
//			Util.PrintLog(TAG, "instance() = null");
//		}
//		return instance;
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		instance = this;
//
//		Util.PrintLog(TAG, "onCreate()");
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(SysConfig.BROADCAST_RELOGIN_SERVICE);
//		registerReceiver(receiver, intentFilter);
//		PreferenceUtils.getInstance().getAccountSharePreferences();
//		Util.PrintLog(TAG, "onCreate()" + SysConfig.RTCToken);
//		CallSdk();
//	}
//
//	private void getData() {
//		FamilyInfo family = LauncherApp.getInstance().getFamily();
//
//		
//		try {
//			String city = Util.getLocation();
//			family.setCity(city);
//			LauncherApp
//					.getInstance()
//					.getDBHelper()
//					.updateFamilyInfo(family.getDeviceUserID(),
//							FamilyInfo._FAMILY_CITY, city);
//			LauncherApp
//			.getInstance()
//			.getDBHelper()
//			.updateFamilyUserByUserid(LauncherApp.getInstance(),FamilyUserInfo._FAMILY_USER_CITY,city,family.getDeviceUserID());
//			
//			this.getContentResolver().notifyChange(
//					ContactTableMetaData.CONTENT_URI, null);
//			
//			Message msg = new Message();
//			msg.what = 0;
//			msg.obj = city;
//			mHandler.sendMessageDelayed(msg, 1000);
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		LauncherApp.getInstance().setFamily(family);
//	}
//
//	@Override
//	@Deprecated
//	public void onStart(Intent intent, int startId) {
//		super.onStart(intent, startId);
//
//		if (intent == null) {
//			return;
//		}
//		if (bstarttimer == true) {
//			bstarttimer = false;
//			StartAlarmTimer(); // 初始从非登陆页面及网路异常等情况进入开启定时器
//		} else if (reLoginAlarm != null) // 定时器调用
//		{
//			if (SysConfig.RTCToken != "") {
//				//InitLogin();
//			} else {
//				//RestartLogin();
//			}
//			 RestartLogin();
//		}
//	}
//
//	@Override
//	public void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//	}
//
//	public void StartAlarmTimer() {
//		Relogincount = 0;
//		removeLoginAlarm();
//		reLoginAlarm();
//	}
//
//	public void InitLogin() {
//		Util.PrintLog(TAG, "InitLogin");
//		if (!SysConfig.getInstance().ismLoginOK()) {
//			boolean bret = NetWorkUtil.isNetConnect(LauncherApp.getInstance());
//			if (bret) { // 如果有网络连接
//				if (LauncherApp.getInstance().getRtcClient() != null) {
//					LauncherApp.getInstance().disposeSipRegister();
//				} else {
//					ReloginService.getInstance().CallSdk();
//				}
//			} else {
//				removeLoginAlarm();
//			}
//		} else {
//			removeLoginAlarm();
//
//		}
//	}
//
//	public void CallSdk()// 只有调用sdk获取服务器地址成功后才能走sip注册流程
//	{
//		// if(SysConfig.getInstance().isLoginByBtn())
//		LauncherApp.getInstance().InitSdk(); // initSdk
//	}
//
//	public void RestartLogin() {
//		if (SysConfig.getInstance().ismLoginOK() == true) {
//			return;
//		}
//		Util.PrintLog(TAG, "RestartLogin");
//
//		String loginBody = XMLBody.getLogin(SysConfig.uid, SysConfig.psw);
//		LoadAccountInfoFromServer task = new LoadAccountInfoFromServer(
//				loginBody);
//		task.getData(new DataCallBack() {
//
//			@Override
//			public void onDataCallBack(int code, FamilyInfo data) {
//				// TODO Auto-generated method stub
//				if (code == 0) {
//					
//					LauncherApp.getInstance().setFamily(data);
//					returnValueBroadcast(SysConfig.MSG_LOGIN_OK, code,
//							"get token");
//					
//				} else {
//					returnValueBroadcast(SysConfig.MSG_GETTOKEN_ERROR, code,
//							"can't get token");
//				}
//
//			}
//
//		});
//	}
//
//	public void getFamilyUsers() {
//		String loginBody = XMLBody.getFamilyUsers(SysConfig.DolphinToken,
//				LauncherApp.getInstance().getFamily().getFamilyID());
//		LoadAllFamilyUserFromServer task = new LoadAllFamilyUserFromServer(
//				loginBody);
//		task.getAllUserData(new allUserDataCallBack() {
//
//			@Override
//			public void onDataCallBack(int code, final List<FamilyUserInfo> data) {
//				// TODO Auto-generated method stub
//				if (code == 0) {
//				//	LauncherApp.getInstance().setFamilyUsers(data);
//					// LauncherApp.getInstance().disposeSipRegister();
//					InitLogin();
//				} else {
//					returnValueBroadcast(SysConfig.MSG_GETUSERS_NG, code,
//							"获取FamilyUser失败");
//				}
//			}
//		});
//	}
//
//	private void OnLoginResult(Intent intent) {
//		int result = intent.getIntExtra("arg1", -1);
//		String desc = intent.getStringExtra("arg2");
//		Util.PrintLog(TAG, "OnLoginResult:" + result + " desc" + desc
//				+ " curloginstate:" + SysConfig.getInstance().ismLoginOK());
//		if (result == MsgKey.KEY_STATUS_200
//				|| result == MsgKey.KEY_RESULT_SUCCESS) {
//			removeLoginAlarm();
//			Relogincount = 0;
//			if (SysConfig.getInstance().ismLoginOK() == false) // 防止注销处理
//			{
//				SysConfig.getInstance().setmLoginOK(true);
//			}
//			HBaseApp.post2WorkRunnable(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					getData();
//					LauncherApp.getInstance().getBoxMessage(null);
//				}
//			});
//			LocalBroadcastManager lbm = LocalBroadcastManager
//					.getInstance(getApplication());
//			lbm.sendBroadcast(new Intent(SysConfig.ACTION_BR_NET_UNOUTABLE_LOGNIN_OK));
//		} else if (result == MsgKey.KEY_STATUS_403) {
//			Relogincount++;
//			SysConfig.getInstance().setmLoginOK(false);
//			PreferenceUtils.getInstance().clearAccountSharePreferences();
//			SysConfig.RTCToken = "";
//		} else {
//			Relogincount++;
//			SysConfig.getInstance().setmLoginOK(false);
//			Util.PrintLog(TAG, "OnLoginResult 登陆失败 错误码[:" + result + "]desc:"
//					+ desc);
//		}
//	}
//
//	/**
//	 * 带值通知界面处理广播
//	 * 
//	 * @param msg
//	 */
//	private static void returnValueBroadcast(int nCmdID, int nCmdArg,
//			String sExtra) {
//		Intent intent = new Intent(SysConfig.BROADCAST_RELOGIN_SERVICE);
//		intent.putExtra("what", nCmdID);
//		intent.putExtra("arg1", nCmdArg);
//		intent.putExtra("arg2", sExtra);
//		LauncherApp.getInstance().sendBroadcast(intent);
//	}
//
//	private BroadcastReceiver receiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
//			if (intent.getAction().equals(SysConfig.BROADCAST_RELOGIN_SERVICE)) {
//
//				switch (intent.getIntExtra("what", -1)) {
//				case SysConfig.MSG_SIP_REGISTER: {
//					Util.PrintLog(TAG, "MSG_SIP_REGISTER");
//					OnLoginResult(intent);
//					break;
//				}
//				case SysConfig.MSG_SDKInitOK: {
//					if (SysConfig.RTCToken != "") {
//						//InitLogin();
//					} else {
//						
//					}
//					RestartLogin();
//					break;
//				}
//				case SysConfig.MSG_GETTOKEN_ERROR: {
//					Util.PrintLog(TAG, "MSG_GETTOKEN_ERROR");
//					// RestartLogin();
//					break;
//				}
//				case SysConfig.MSG_GETTOKEN_SUCCESS:
//
//					break;
//				case SysConfig.MSG_LOGIN_OK:
//					getFamilyUsers();
//					break;
//				case SysConfig.MSG_SDKInitNG:
//					LocalBroadcastManager lbm = LocalBroadcastManager
//							.getInstance(getApplication());
//					lbm.sendBroadcast(new Intent(
//							SysConfig.ACTION_BR_NET_UNOUTABLE));
//					HBaseApp.post2WorkDelayed(new Runnable() {
//
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							CallSdk();
//						}
//					}, 10000);
//
//					break;
//				case SysConfig.MSG_LOGIN_RTC_NG:
//					LocalBroadcastManager lbm2 = LocalBroadcastManager
//					.getInstance(getApplication());
//					lbm2.sendBroadcast(new Intent(
//					SysConfig.ACTION_BR_NET_UNOUTABLE));
//					break;
//
//				}
//			}
//		}
//	};
//
//	private AlarmManager reLoginAlarm = null;// 重登陆定时器
//	private PendingIntent reLoginPI = null;
//	private int Relogincount = 0;
//
//	// 重登录定时器 time 单位 ms 毫秒 bRepeating 是否重复设置
//	private void reLoginAlarm() {
//		if (reLoginAlarm == null) {
//			Intent intent = new Intent(this, ReloginService.class);
//			reLoginPI = PendingIntent.getService(LauncherApp.getInstance(), 0,
//					intent, 0);
//			reLoginAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);
//		}
//
//		// 前三次每隔20秒启动一次。
//		if (Relogincount < 3) {
//			// miui手机定时器不准
//			reLoginAlarm.setRepeating(AlarmManager.RTC_WAKEUP,
//					System.currentTimeMillis() + 1000, 16 * 1000, reLoginPI);
//		} else if (Relogincount == 3) {
//			reLoginAlarm.cancel(reLoginPI);
//			reLoginAlarm.setRepeating(AlarmManager.RTC_WAKEUP,
//					System.currentTimeMillis(), 3 * 60 * 1000, reLoginPI);
//
//		}
//	}
//
//	// 销毁重登陆定时器
//	public void removeLoginAlarm() {
//		Relogincount = 0;
//		if (reLoginAlarm != null) {
//			reLoginAlarm.cancel(reLoginPI);
//		}
//		reLoginAlarm = null;
//		reLoginPI = null;
//	}
//
//
//	@SuppressLint("HandlerLeak")
//	Handler mHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			switch (msg.what) {
//			case 0:
//				final String city = (String) msg.obj;
//				String body = XMLBody.updateMyUserCity(SysConfig.DolphinToken,
//						city);
//				UploadData2Server task = new UploadData2Server(body,
//						"UpdateUserInfo");
//				task.getData(new codeDataCallBack() {
//
//					@Override
//					public void onDataCallBack(String request, int code) {
//						// TODO Auto-generated method stub
//					}
//				});
//
//				break;
//			}
//			super.handleMessage(msg);
//		}
//
//	};
//}
