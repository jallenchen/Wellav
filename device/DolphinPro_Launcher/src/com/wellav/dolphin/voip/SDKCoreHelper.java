package com.wellav.dolphin.voip;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wellav.dolphin.bean.VoipSubAccount;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.launcher.VoipCallActivity;
import com.wellav.dolphin.net.LoadVoipSubAccount;
import com.wellav.dolphin.net.LoadVoipSubAccount.voipAccDataCallBack;
import com.wellav.dolphin.net.XMLBody;
import com.wellav.dolphin.utils.Util;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECInitParams.LoginAuthType;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.ECVoIPSetupManager;

/**
 * Created by Jorstin on 2015/3/17.
 */
public class SDKCoreHelper implements ECDevice.InitListener,
		ECDevice.OnECDeviceConnectListener, ECDevice.OnLogoutListener {

	public static final String TAG = "SDKCoreHelper";
	public static final String ACTION_LOGOUT = "com.yuntongxun.ECDemo_logout";
	public static final String ACTION_SDK_CONNECT = "com.yuntongxun.Intent_Action_SDK_CONNECT";
	public static final String ACTION_KICK_OFF = "com.yuntongxun.Intent_ACTION_KICK_OFF";
	private static SDKCoreHelper sInstance;
	private LoadVoipSubAccount mVoipAcc;
	private Context mContext;
	private ECDevice.ECConnectState mConnect = ECDevice.ECConnectState.CONNECT_FAILED;
	private ECInitParams mInitParams;
	private ECInitParams.LoginMode mMode = ECInitParams.LoginMode.FORCE_LOGIN;
	/** 初始化错误 */
	private boolean mKickOff = false;

	public static SDKCoreHelper getInstance() {
		if (sInstance == null) {
			sInstance = new SDKCoreHelper();
		}
		return sInstance;
	}

	public static boolean isKickOff() {
		return getInstance().mKickOff;
	}

	public static void init(Context ctx) {
		init(ctx, ECInitParams.LoginMode.AUTO);
	}

	public static void init(Context ctx, ECInitParams.LoginMode mode) {
		getInstance().mKickOff = false;
		Log.d(TAG, "[init] start regist..");
		// ctx = LauncherApp.getInstance().getApplicationContext();
		getInstance().mMode = mode;
		getInstance().mContext = ctx;
		// 判断SDK是否已经初始化，没有初始化则先初始化SDK
		if (!ECDevice.isInitialized()) {
			getInstance().mConnect = ECDevice.ECConnectState.CONNECTING;
			// ECSDK.setNotifyOptions(getInstance().mOptions);
			ECDevice.initial(ctx, getInstance());

			postConnectNotify();
			return;
		}
		Log.d(TAG, " SDK has inited , then regist..");
		// 已经初始化成功，直接进行注册
		getInstance().onInitialized();
	}

	@Override
	public void onInitialized() {
		Log.d(TAG, "ECSDK is ready");

		// 设置接收VoIP来电事件通知Intent
		// 呼入界面activity、开发者需修改该类
		Intent intent = new Intent(getInstance().mContext,
				VoipCallActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				getInstance().mContext, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		ECDevice.setPendingIntent(pendingIntent);
		// 设置SDK注册结果回调通知，当第一次初始化注册成功或者失败会通过该引用回调
		// 通知应用SDK注册状态
		// 当网络断开导致SDK断开连接或者重连成功也会通过该设置回调
		ECDevice.setOnDeviceConnectListener(this);

		// 设置VOIP 自定义铃声路径
		ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
		if (setupManager != null) {
			// 目前支持下面三种路径查找方式
			// 1、如果是assets目录则设置为前缀[assets://]
			setupManager.setOutGoingRingUrl(true, "assets://phonering.mp3");
			setupManager.setBusyRingTone(true, "assets://playend.mp3");
			// 2、如果是raw目录则设置为前缀[raw://]
			// 3、如果是SDCard目录则设置为前缀[file://]
		}
		
		if(!SysConfig.Voip_Acc.equals("")){
			initParams();
			return;
		}
		
		String body = XMLBody.GetVOIPSubAccount(SysConfig.DolphinToken);
		mVoipAcc = new LoadVoipSubAccount(getInstance().mContext, body);
		mVoipAcc.getVoipAccData(new voipAccDataCallBack() {

			@Override
			public void onDataCallBack(int code, VoipSubAccount data) {
				// TODO Auto-generated method stub
				if (code == 0) {
					SysConfig.Voip_Acc = data.getVoipAcc();
					SysConfig.Voip_Token =  data.getSubToken();
					SysConfig.Voip_Psw = data.getMVoipPsw();
					initParams();
				}
			}
		});

	}
	
	private void initParams(){
		if (mInitParams == null) {
			mInitParams = ECInitParams.createParams();
		}
		Util.PrintLog(TAG, SysConfig.Voip_Token+"-"+SysConfig.Voip_Acc+"-"+SysConfig.Voip_Psw);
		mInitParams.reset();
		// 如：VoIP账号/手机号码/..
		mInitParams.setUserid(SysConfig.Voip_Acc);
		// appkey
		mInitParams.setAppKey(SysConfig.Voip_AppID);
		// mInitParams.setAppKey(/*clientUser.getAppKey()*/"ff8080813d823ee6013d856001000029");
		// appToken
		mInitParams.setToken(SysConfig.Voip_Token);
		// mInitParams.setToken(/*clientUser.getAppToken()*/"d459711cd14b443487c03b8cc072966e");
		// ECInitParams.LoginMode.FORCE_LOGIN
		mInitParams.setMode(getInstance().mMode);

		// 如果有密码（VoIP密码，对应的登陆验证模式是）
		// ECInitParams.LoginAuthType.PASSWORD_AUTH
		mInitParams.setPwd(SysConfig.Voip_Psw);

		// 设置登陆验证模式（是否验证密码/如VoIP方式登陆）
		mInitParams.setAuthType(LoginAuthType.PASSWORD_AUTH);

		if (!mInitParams.validate()) {
			Log.e(TAG, "!mInitParams.validate()");
			return;
		}

		ECDevice.login(mInitParams);
		// 调式
	}

	@Override
	public void onConnect() {
		// Deprecated
	}

	@Override
	public void onDisconnect(ECError error) {
		// SDK与云通讯平台断开连接
		// Deprecated
	}

	@Override
	public void onConnectState(ECDevice.ECConnectState state, ECError error) {
		getInstance().mConnect = state;
		postConnectNotify();
	}

	/**
	 * 当前SDK注册状态
	 * 
	 * @return
	 */
	public static ECDevice.ECConnectState getConnectState() {
		return getInstance().mConnect;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onLogout() {
		getInstance().mConnect = ECDevice.ECConnectState.CONNECT_FAILED;
		if (mInitParams != null && mInitParams.getInitParams() != null) {
			mInitParams.getInitParams().clear();
		}
		mInitParams = null;
		mContext.sendBroadcast(new Intent(ACTION_LOGOUT));
	}

	@Override
	public void onError(Exception exception) {
		Log.e(TAG, "ECSDK couldn't start: " + exception.getLocalizedMessage());
		ECDevice.unInitial();
	}

	/**
	 * 状态通知
	 */
	private static void postConnectNotify() {
		if (getInstance().mContext instanceof VoipCallActivity) {
			((VoipCallActivity) getInstance().mContext)
					.onNetWorkNotify(getConnectState());
		}
	}

	public static void logout(boolean isNotice) {
		ECDevice.NotifyMode notifyMode = (isNotice) ? ECDevice.NotifyMode.IN_NOTIFY
				: ECDevice.NotifyMode.NOT_NOTIFY;
		ECDevice.logout(notifyMode, getInstance());
		release();
	}

	public static void release() {
		getInstance().mKickOff = false;
	}

	/**
	 * VoIP呼叫接口
	 * 
	 * @return
	 */
	public static ECVoIPCallManager getVoIPCallManager() {
		return ECDevice.getECVoIPCallManager();
	}

	public static ECVoIPSetupManager getVoIPSetManager() {
		return ECDevice.getECVoIPSetupManager();
	}

	/**
	 * 
	 * 是否支持voip及会议功能 true 表示支持 false表示不支持 请在sdk初始化完成之后调用
	 */
	public boolean isSupportMedia() {

		return ECDevice.isSupportMedia();
	}

	long t = 0;

	/**
	 * 判断服务是否自动重启
	 * 
	 * @return 是否自动重启
	 */
	public static boolean isUIShowing() {
		return ECDevice.isInitialized();
	}

}
