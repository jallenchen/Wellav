package com.wellav.dolphin.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MsgService;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.AccountInfo;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.UserInfo;
import com.wellav.dolphin.mmedia.net.LoadBoxMsgFromServer;
import com.wellav.dolphin.mmedia.net.LoadBoxMsgFromServer.MsgDataCallBack;
import com.wellav.dolphin.mmedia.netease.FlavorDependent;
import com.wellav.dolphin.mmedia.netease.LogUtil;
import com.wellav.dolphin.mmedia.netease.ScreenUtil;
import com.wellav.dolphin.mmedia.netease.StorageType;
import com.wellav.dolphin.mmedia.netease.StorageUtil;
import com.wellav.dolphin.mmedia.netease.SystemUtil;
import com.wellav.dolphin.mmedia.netease.UserPreferences;
import com.wellav.dolphin.mmedia.netease.WelcomeActivity;
import com.wellav.dolphin.mmedia.utils.LocalActManager;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class DolphinApp extends HBaseApp {

	private static String LOGTAG = "DolphinApp";
	private AccountInfo mAccountInfo = null;
	private SysConfig sysconfig;
	private SQLiteManager sqLiteManager;
	private LocalActManager localactmanager = null;
	public static final boolean isDebug = false;
	private String fileFolder = "wellav"; // 日志存储目录 //mnt/sdcard/logfolder/log
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, List<FamilyUserInfo>> map = new HashMap<Integer, List<FamilyUserInfo>>();
	private HashMap<String, Boolean> onlineMap = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> onlineMapPrev = new HashMap<String, Boolean>();
	private List<FamilyInfo> familyinfos = new ArrayList<FamilyInfo>();
	private List<FamilyInfo> myfamilyinfos = new ArrayList<FamilyInfo>();
	private UserInfo mMyUserInfo;
	private int notifyId = 20;

	public static void MyLoge(String tag, String msg) {
		if (isDebug) {
			Log.e(tag, msg);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceUtils.init(getApplicationContext());
		
		DemoCache.setContext(this);

	    NIMClient.init(this, getLoginInfo(), getOptions());


	    if (inMainProcess()) {
	        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(FlavorDependent.getInstance().getMsgAttachmentParser());
	        // init tools
	        StorageUtil.init(this, null);
	        ScreenUtil.init(this);
	        DemoCache.initImageLoaderKit();
	     // 注册监听器
	        new ServiceObserver(getInstance());
	        initLog();
	        FlavorDependent.getInstance().onApplicationCreate();
	    }
	}
	
	//////////////////云信//////////////////////

	
    private LoginInfo getLoginInfo() {
        PreferenceUtils.getInstance().getSharePreferencesAccount();
        if (!TextUtils.isEmpty(SysConfig.uid) && !TextUtils.isEmpty(SysConfig.rtoken)) {
            DemoCache.setAccount(SysConfig.uid.toLowerCase());
            return new LoginInfo(SysConfig.uid, SysConfig.rtoken);
        } else {
            return null;
        }
    }

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

	public static DolphinApp getInstance() {
		return (DolphinApp) HBaseApp.getInstance();
	}

	public void setAccountInfo(AccountInfo accountInfo) {
		mAccountInfo = accountInfo;
	}

	public AccountInfo getAccountInfo() {
		if (mAccountInfo == null) {
			mAccountInfo = new AccountInfo();
		}
		return mAccountInfo;
	}

	public void setMyUserInfo(UserInfo userInfo) {
		mMyUserInfo = userInfo;
	}

	public UserInfo getMyUserInfo() {
		if (mMyUserInfo == null) {
			mMyUserInfo = new UserInfo();
		}
		return mMyUserInfo;
	}
	
	
	public HashMap<Integer, List<FamilyUserInfo>> getFamilyUserMap() {

		return map;
	}

	public void setFamilyUserMap(HashMap<Integer, List<FamilyUserInfo>> map) {

		this.map = map;
	}

	public HashMap<String, Boolean> getFamilyOnlineMap() {

		return onlineMap;
	}

	public void setFamilyOnlineMap(HashMap<String, Boolean> map) {

		this.onlineMap = map;
	}

	public HashMap<String, Boolean> getFamilyOnlinePrevMap() {

		return onlineMapPrev;
	}

	public void setFamilyOnlinePrevMap(HashMap<String, Boolean> map) {

		this.onlineMapPrev = map;
	}

	public void setFamilyInfos(List<FamilyInfo> info) {
		this.familyinfos = info;
	}

	public List<FamilyInfo> getFamilyInfos() {
		return this.familyinfos;
	}

	public void setMyFamilyInfos(List<FamilyInfo> info) {
		this.myfamilyinfos = info;
	}

	public List<FamilyInfo> getMyFamilyInfos() {
		return this.myfamilyinfos;
	}

	public String getAppAccountID() {
		if (mAccountInfo != null) {
			return SysConfig.uid;
		}
		return "";
	}

	public String getFileFolder() {
		return fileFolder;
	}

	public LocalActManager getLocalActManager() {
		if (localactmanager == null) {
			localactmanager = new LocalActManager();
		}
		return localactmanager;
	}

	/**
	 * 获取SQLiteManager
	 * 
	 * @return
	 */
	public synchronized SQLiteManager getSqlManager() {
		if (sqLiteManager == null) {
			sqLiteManager = new SQLiteManager();
		}
		return sqLiteManager;
	}

	public void setSqulManagerNull() {
		sqLiteManager = null;
	}



	/**
	 * 
	 * @return
	 */
	public SysConfig getSysConfig() {
		if (sysconfig == null) {
			sysconfig = new SysConfig();
		}
		return sysconfig;
	}

	// 获取通知栏
	public void showNotification(String notifyTitle, String notifycontext) {
		if (PreferenceUtils.getInstance().getNotifySetting()) {
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			NotificationCompat.Builder mbulider = new NotificationCompat.Builder(
					getApplicationContext());
			mbulider.setContentTitle(notifyTitle)
					.setContentText(notifycontext)
					.setTicker(
							getResources().getString(
									R.string.get_dolphin_message))// 通知首次出现在通知栏，带上升动画效果的
					.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
					.setSmallIcon(R.drawable.tab1_dolphin_foc);
			// .setDefaults(Notification.DEFAULT_VIBRATE)
			if (PreferenceUtils.getInstance().getVoiceSetting()) {
				String ringtonechoice = PreferenceUtils.getInstance()
						.getRingtoneSetting();
				if (ringtonechoice != null && ringtonechoice.length() != 0) {
					mbulider.setSound(Uri.parse(ringtonechoice));
				} else {
					mbulider.setDefaults(Notification.DEFAULT_SOUND);
				}
			}
			mNotificationManager.notify(notifyId, mbulider.build());
		}
	}



	public void getMessageBox() {
		String mBoody = RequestString.GetMessageBox(SysConfig.dtoken);
		LoadBoxMsgFromServer task = new LoadBoxMsgFromServer(mBoody);
		task.getAllMsgData(new MsgDataCallBack() {
			@Override
			public void onDataCallBack(int code) {
				if (code == 0) {
				}
			}
		});
	}

}
