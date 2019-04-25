package com.wellav.dolphin.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;
import com.wellav.dolphin.bean.FamilyInfo;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.bind.BindBeManagerActivity;
import com.wellav.dolphin.calling.CallingGroupChatActivity;
import com.wellav.dolphin.calling.CallingMeetingActivity;
import com.wellav.dolphin.calling.CallingMonitorActivity;
import com.wellav.dolphin.cmd.DolphinImMime;
import com.wellav.dolphin.config.MsgKey;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.db.DBHelper;
import com.wellav.dolphin.db.PreferenceUtils;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadMsgFromServer;
import com.wellav.dolphin.net.LoadMsgFromServer.MsgDataCallBack;
import com.wellav.dolphin.net.VolleyRequestQueueManager;
import com.wellav.dolphin.net.XMLBody;
import com.wellav.dolphin.netease.avchat.AVChatActivity;
import com.wellav.dolphin.netease.avchat.AVChatProfile;
import com.wellav.dolphin.netease.config.DemoCache;
import com.wellav.dolphin.netease.config.FlavorDependent;
import com.wellav.dolphin.netease.config.LogUtil;
import com.wellav.dolphin.netease.config.ScreenUtil;
import com.wellav.dolphin.netease.config.SendNitificationMsgUtil;
import com.wellav.dolphin.netease.config.StorageType;
import com.wellav.dolphin.netease.config.StorageUtil;
import com.wellav.dolphin.netease.config.SystemUtil;
import com.wellav.dolphin.netease.config.UserPreferences;
import com.wellav.dolphin.netease.config.WelcomeActivity;
import com.wellav.dolphin.setting.SettingObservable;
import com.wellav.dolphin.setting.SettingServerThread;
import com.wellav.dolphin.utils.LocalActManager;
import com.wellav.dolphin.utils.Util;

public class LauncherApp extends HBaseApp {

	private static String TAG = "LauncherApp";
	private SysConfig sysconfig;
	private FamilyInfo mFamily;
	private List<FamilyUserInfo> Users;
	private FamilyUserInfo mManager;
	private DBHelper dBHelper;
	private LocalActManager localactmanager = null;
	private ServiceObserver mServiceObserver;
	private WifiInfo wifiInfo = null;
	private WifiManager wifiManager = null;
	private Handler handler;
	private int level;
	private LoginInfo mInfo;
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceUtils.init(getInstance());
		DemoCache.setContext(this);

	    NIMClient.init(this, getLoginInfo(), getOptions());
	  

	    if (inMainProcess()) {
	        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(FlavorDependent.getInstance().getMsgAttachmentParser());
	        NIMClient.getService(AuthService.class).login(mInfo).setCallback(callback);
	        // init tools
	        StorageUtil.init(this, null);
	        ScreenUtil.init(this);
	        DemoCache.initImageLoaderKit();
	    	mServiceObserver = new ServiceObserver(getInstance());
	        // init log
	        initLog();
	        FlavorDependent.getInstance().onApplicationCreate();
	    }
	}
	

	//////////////////云信//////////////////////
	
	
    private LoginInfo getLoginInfo() {
    	LogUtil.e(TAG, "getLoginInfo()");
        boolean isFirstEnter = PreferenceUtils.getInstance().getBooleanShare(MsgKey.Key_FirstEnter,true);
        if(isFirstEnter == true){
        	return null;
        }
        if (!TextUtils.isEmpty(SysConfig.uid) && !TextUtils.isEmpty(SysConfig.NeteaseToken)) {
            DemoCache.setAccount(SysConfig.uid.toLowerCase());
            mInfo = new LoginInfo(SysConfig.uid, SysConfig.NeteaseToken);
            return mInfo;
        } else {
            return null;
        }
    }
    
    	  RequestCallback<LoginInfo> callback =
    	            new RequestCallback<LoginInfo>() {

						@Override
						public void onException(Throwable arg0) {
							// TODO Auto-generated method stub
							LogUtil.e(TAG, "onException()"+arg0.toString());
						}

						@Override
						public void onFailed(int arg0) {
							// TODO Auto-generated method stub
							LogUtil.e(TAG, "onFailed()"+arg0);
						}

						@Override
						public void onSuccess(LoginInfo arg0) {
							// TODO Auto-generated method stub
							LogUtil.e(TAG, "onSuccess()");
						}
    	            // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
    	        };
  

    private SDKOptions getOptions() {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
        StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
        if (config == null) {
            config = new StatusBarNotificationConfig();
        }
        // 点击通知需要跳转到的界面
        config.notificationEntrance = WelcomeActivity.class;
        config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;

        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
        options.statusBarNotificationConfig = config;
        UserPreferences.setStatusConfig(config);

        // 配置保存图片，文件，log等数据的目录
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim/";
        options.sdkStorageRootPath = sdkPath;
        Log.i("demo", FlavorDependent.getInstance().getFlavorName() + " demo nim sdk log path=" + sdkPath);

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "NETEASE";

        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小，
        options.thumbnailSize = (int) (0.5 * ScreenUtil.screenWidth);

        // 用户信息提供者
        options.userInfoProvider = null;

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = null;

        return options;
    }

    private boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = SystemUtil.getProcessName(this);
        return packageName.equals(processName);
    }
    
    private void initLog() {
        String path = StorageUtil.getDirectoryByDirType(StorageType.TYPE_LOG);
        LogUtil.init(path, Log.DEBUG);
        LogUtil.i("demo", FlavorDependent.getInstance().getFlavorName() + " demo log path=" + path);
    }
    
	
	
	//////////////////////////////////////////////////////

	/**
	 * 
	 * @return APP实例
	 */
	public static LauncherApp getInstance() {
		return (LauncherApp) HBaseApp.getInstance();
	}

	
	public LocalActManager getLocalActManager() {
		if (localactmanager == null) {
			localactmanager = new LocalActManager();
		}
		return localactmanager;
	}

	/**
	 * 
	 * @return Config
	 */
	public SysConfig getSysConfig() {
		if (sysconfig == null) {
			sysconfig = new SysConfig();
		}
		return sysconfig;
	}

	/**
	 * 保存账户信息到内存
	 * 
	 * @param acc
	 */
	public void setFamily(FamilyInfo acc) {
		this.mFamily = acc;
	}

	/**
	 * @return 账户信息
	 */
	public FamilyInfo getFamily() {
		if (this.mFamily == null) {
			mFamily = new FamilyInfo();
		}
		return mFamily;
	}

	/**
	 * @return 数据库实例
	 */
	public DBHelper getDBHelper() {
		if (this.dBHelper == null) {
			dBHelper = new DBHelper(this);
		}
		return dBHelper;
	}

	/**
	 * 保存联系人信息到内存
	 * 
	 * @param acc
	 */
	public void setFamilyManager(FamilyUserInfo user) {
		this.mManager = user;
	}

	/**
	 * @return 获取联系人信息
	 */
	public FamilyUserInfo getFamilyManager() {
		if (this.mManager == null) {
			mManager = new FamilyUserInfo();
		}
		return mManager;
	}

	/**
	 * 保存联系人信息到内存
	 * 
	 * @param acc
	 */
	public void setFamilyUsers(List<FamilyUserInfo> user) {
		this.Users = user;
	}

	/**
	 * @return 获取联系人信息
	 */
	public List<FamilyUserInfo> getFamilyUsers() {
		if (this.Users == null) {
			Users = new ArrayList<FamilyUserInfo>();
		}
		return Users;
	}

	public void exit() {
		Util.PrintLog(TAG, "exit()");
		try {
			VolleyRequestQueueManager.release(null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HBaseApp.post2WorkDelayed(new Runnable() {
				@Override
				public void run() {
					sysconfig = null;
					System.exit(0);
					int pid = android.os.Process.myPid();
					android.os.Process.killProcess(pid);
				}
			}, 1000);
		}
	}


	// 获取网络状态信息
	@SuppressLint("HandlerLeak")
	public void getNet() {
		// 监听网络状态
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				wifiInfo = wifiManager.getConnectionInfo();
				level = wifiInfo.getRssi();
				Message msg = new Message();
				msg.what = 1;
				if (level <= 0 && level >= -50) {
					msg.obj = LauncherApp.getInstance().getResources().getString(R.string.wifi_1);
				} else if (level < -50 && level >= -70) {
					msg.obj = LauncherApp.getInstance().getResources().getString(R.string.wifi_2);
				} else if (level < -70 && level >= -80) {
					msg.obj = LauncherApp.getInstance().getResources().getString(R.string.wifi_3);
				} else if (level < -80 && level >= -100) {
					msg.obj = LauncherApp.getInstance().getResources().getString(R.string.wifi_4);
				} else {
					msg.obj = LauncherApp.getInstance().getResources().getString(R.string.wifi_5);
				}
				if (handler != null) {
					handler.sendMessage(msg);
				}

			}
		}, 0, 15000);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String net = LauncherApp.getInstance().getResources().getString(R.string.wifi_0);
				switch (msg.what) {
				case 1:
					net = (String) msg.obj;
					break;
				default:
					net = LauncherApp.getInstance().getResources().getString(R.string.wifi_0);
				}
				SysConfig.getInstance().setmNetState(net);
			}
		};
	}


	/**
	 * 当传过来的值发生改变时
	 * 
	 * @author wen
	 */
	public class SettingObserver implements Observer {
		SettingServerThread settingServerThread = new SettingServerThread();

		// 当值发生变化时，即有数据发送过来时，启动解析线程
		public SettingObserver(SettingObservable so) {
			so.addObserver(this);
		}

		public void update(final Observable o, Object arg) {
			Thread tParseCommonds = new Thread(new Runnable() {
				public void run() {
					settingServerThread.parseCommands(LauncherApp.this,
							((SettingObservable) o).getData(), LauncherApp
									.getInstance().getFamily().getFamilyID());
				}
			});
			tParseCommonds.start();
		}
	}


	public void getBoxMessage(String action) {
		String body = XMLBody.getMessage(SysConfig.DolphinToken);
		LoadMsgFromServer task = new LoadMsgFromServer(getInstance(), body);
		task.getAllMsgData(new MsgDataCallBack() {
			@Override
			public void onDataCallBack(int code) {
			}
		});
	}

	/**
	 * @param to
	 *            消息接收者
	 * @param mime
	 *            消息类型
	 * @param imCmd
	 *            消息内容
	 */
	public void makeSendIm(String to, String mime, JSONObject imCmd) {
	}

	public void notifyOnline2Family(Context ct, String content) {

	}

	public void notifyMsgRTC2Family(Context ct, String content) {

	}

	public void notifyMsgRTC2User(String name) {
		makeSendIm(name, DolphinImMime.DplMime_msgnotify_json, null);
	}

	public boolean OnLoginFailReLogin() {
		return false;
	}

	public void MakeCall(String remoteuser) {
	}

	public void CreateConf(String params) {

	}



	public void BeManagerFragment(String userid) {
		Intent intent = new Intent(LauncherApp.this,
				BindBeManagerActivity.class);
		intent.putExtra("userid", userid);

		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				LauncherApp.this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// long time = Calendar.getInstance().getTimeInMillis();
		am.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
	}
}
