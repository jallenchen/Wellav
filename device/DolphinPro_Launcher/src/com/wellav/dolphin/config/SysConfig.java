package com.wellav.dolphin.config;

import android.os.Environment;
import android.util.Log;

import com.netease.nimlib.sdk.team.model.Team;
import com.wellav.dolphin.application.LauncherApp;

public class SysConfig {
	// RTC
	public static String APP_ID = "70292";//
	public static String APP_KEY = "dc6c1a07-7215-4a28-85c5-d74d33e0241d";//
	//public static String APP_ID = "70038";//
	//public static String APP_KEY = "MTQxMDkyMzU1NTI4Ng==";//
	//public static String APP_ID = "70063";//
   // public static String APP_KEY = "MTQzMTQyNTM2MzA4NQ==";//
	//阿里云IP  120.24.18.223
	//自理的IP 10.200.0.94:9090
	public final static String SHARE_NAME = "save_info";
	public static final String token="28213306:CNUuDvlHKbzlNZ6rvXwR50L+E3R0KA5BRbVKkAA36aVlVaFfYRGwDGDkaTNnccB6hlIDDF+/qXeqkr/qqKocFw==";
	//本地服务器地址：10.200.0.94   替换为阿里云服务器地址：112.74.143.22
	public static String ServerUrl= "http://120.76.220.183:9090/services/hiapi/";
	public static String locationUrl = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json";
	//容联Voip 
	public static String Voip_AppID="aaf98f8953acd61f0153ad310d0b00b7";//by cpl
	public static String Voip_Acc="";//"8011813100000013";//by cpl
	public static String Voip_Psw="";//"VGwnay7K";//by cpl
	public static String Voip_Token="";//"87696d5141053d56ffa178ccd940c829";//by cpl
	 //broadcast define
    public final static String BROADCAST_RELOGIN_SERVICE="com.wellav.dolphin.services.ReloginService";
    public final static String REMOTE_SETTING_ACTION =  "com.wellav.dolphin.ACTION.remote.setting";
    public final static String REMOTE_ASSISTANT_ACTION =  "com.wellav.dolphin.camera.ACTION.remote.assistant";
   
    //那就用新的账号：11234567 88765432  11123456
    public static String uid="11123456";//by cpl
    public static String psw="123456";//by cpl
    public static String Userid="0";//by cpl
    public static String DolphinToken="";//by cpl
    public static String NeteaseToken="";//by cpl
    public static String DeviceFamilyId="";//by cpl

    
    public static final String CONTACTS="1";
    public static final String CONTACT="2";
    
    //通话类型
    public static final int Free=0;
    public static final int ChatCall=1;
    public static final int ChatAccept=2;
    public static final int MutilCall=3;
    public static final int MutilAccept=4;
    public static final int MeetingCall=5;
    public static final int MeetingAccept=6;
    public static final int MonitorCall=7;
    public static final int MutilJoin=8;
    public static final int MeetingJoin=9;
    public static final int MSG_SDKInitNG=1005;
    public static final int MSG_GETTOKEN_SUCCESS=1006;
    public static final int MSG_GETTOKEN_ERROR=1007;
    public static final int MSG_LOGIN_OK=1008;
    public static final int MSG_GETUSERS_NG=1009;
    public static final int MSG_SIP_REGISTER=1010;//by cpl
    public static final int MSG_SDKInitOK=1011;//by cpl
    public static final int MSG_LOGIN_NOFAMILY=1012;//by cpl
    public static final int MSG_LOGIN_RTC_NG=1013;//by cpl
    
    /**
     * @author JingWen.Li
     */
    public final static String WEATHER_BAIDUURL = "http://api.map.baidu.com/telematics/v3/weather?location=[?]&output=json&ak=ORoH2LoQkKGfxnjOkM5uLhvLs4697RCY" +
			"&mcode=1C:EF:3D:48:7C:53:11:BE:7F:DE:07:F1:46:0D:0E:A4:BB:2D:21:91;com.wellav.dolphin.launcher";
    
    public static final int FamilyUserInfoState = 0;
    public static final int FamilyUserInfoAudioState = 0;
    public static final int InviteMessageStatus = 1;
    public static final int InviteMessageAction = 0;
    public static final String UserInfoStatus = "0";
    public static final String BeenAnswered = "1";
    public static final String NoAnswered = "0";
    
    public static final String BROADCAST_CALLING_ACTION = "com.wellav.dolphin.calling";
    public static final String MEDIA_ACTIVITY_ACTION = "com.wellav.dolphin.media.activity";
    public static final String MEDIA_MAIN_ACTIVITY_ACTION = "com.wellav.dolphin.media.activity.MainActivity";
    public static final String MSC_ACTIVITY_ACTION = "com.wellav.dolphin.msc.activity";
    public static final String MSC_MAIN_ACTIVITY_ACTION = "com.wellav.dolphin.msc.activity.MainActivity";
    public static final String DOLPHIN_CONTACT_ACTIVITY_ACTION = "com.wellav.dolphin.contact";
    public static final String DOLPHIN_ACTIVITY_ACTION = "com.wellav.dolphin.activity";
    public static final String DOLPHIN_CAREMA_ACTIVITY_ACTION = "com.wellav.dolphin.camera.activity";
    public static final String ACTION_BR_MSG_NEW = "com.wellav.dolphin.cf.ACTION_BR_MSG_NEW";
    // broadcast about inspecting network sended
 	// 网络是否连接
 	public static final String ACTION_BR_NET_UNAVAILABLE = "com.wellav.dolphin.cf.ACTION_BR_NET_UNAVAILABLE";
 	public static final String ACTION_BR_NET_AVAILABLE = "com.wellav.dolphin.cf.ACTION_BR_NET_AVAILABLE";
 	// 网络是否可连服务器
 	public static final String ACTION_BR_NET_OUTABLE = "com.wellav.dolphin.cf.ACTION_BR_NET_OUTABLE";
 	public static final String ACTION_BR_NET_UNOUTABLE = "com.wellav.dolphin.cf.ACTION_BR_NET_UNOUTABLE";
 	public static final String ACTION_BR_NET_UNOUTABLE_LOGNIN_OK= "com.wellav.dolphin.cf.ACTION_BR_NET_UNOUTABLE_OK";
 	public static final String SCREEN_SAVER_TIMEOUT_MSG = "com.wellav.dolphin.launcher.broadcast.SCREEN_SAVER_TIMEOUT_MSG";
    
    public static final String UploadMessageBox = "UploadMessageBox";
    public static final String UploadPhotoUrl = "UploadPhoto";
    public static final String UploadPhotoRequest = "<UploadPhotoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">";
    public static final String UploadPhotoRequestEnd = "</UploadPhotoRequest>";
    public static final String IfExistMessage = "IfExistMessage";
    public static final String IfExistMessageRequest = "<IfExistMessageRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">";
    public static final String IfExistMessageRequestEnd = "</IfExistMessageRequest>";
    public static final String SimpleDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS Z";
    
    private String mPower = "100%";
    private String mNetState="";
    private boolean mLoginOK = false;
    private boolean mNewMsg = false;
    private boolean bIncoming = false;
    private boolean isCalling = false;
    private int mState = 0;
    private String mGroupName;
    private String mTeamId;
    private Team myTeam;
    
    /**
     * @author JingWen.Li
     * @return
     */
    public static final String ContactRecevicerAction = "com.wellav.dolphin.receiver.ContactRecevicer";
    public static final String PhotoRecevicerAction = "com.wellav.dolphin.receiver.PhotoRecevicer";
    public static final String ACTION_SHUTDOWNAction = "android.intent.action.ACTION_SHUTDOWN";
    public static final String SavePhotoRecevicerAction = "com.wellav.dolphin.receiver.SavePhotoRecevicer";
    
    public static SysConfig getInstance() {
        return LauncherApp.getInstance().getSysConfig();
    }
    
    
    public boolean isNewMsg() {
		return mNewMsg;
	}
	public void setNewMsg(boolean newMsg) {
		mNewMsg = newMsg;
	}
	public String getGroupName() {
		return mGroupName;
	}
	public void setGroupName(String groupId) {
		mGroupName = groupId;
	}
	public String getTeamId() {
		return this.myTeam.getId();
	}
//	public void setTeamId(String teamId) {
//		mTeamId = teamId;
//	}
	
	public void setMyTeam(Team team){
		this.myTeam = team;
	}
	
	public Team getMyTeam(){
		return this.myTeam;
	}
	
    public boolean isCalling() {
	    return isCalling;
	}
	public void setCalling(boolean isCalling) {
	    this.isCalling = isCalling;
	}
	public boolean isbIncoming() {
        return bIncoming;
    }
    public void setbIncoming(boolean incoming) {
        this.bIncoming = incoming;
    }
    public boolean ismLoginOK() {
        return mLoginOK;
    }
    public void setmLoginOK(boolean mLoginOK) {
        this.mLoginOK = mLoginOK;
    }
    public int getStatus() {
        return mState;
    } 
    public void setStatus(int state) {
    	Log.e("setStatus", state+"");
    	mState = state;
    } 
	public String getmPower() {
		return mPower;
	}


	public void setmPower(String mPower) {
		this.mPower = mPower;
	}
    
    /** 本地截图文件存放路径 */
    public String getCaptureFolder() {
        String path = Environment.getExternalStorageDirectory()+"/" + "wellav" + "/capture/"
        +uid+"/";
        return path;
    }
    /** 本地录制文件存放路径 */
    public String getRecordFolder() {
        return Environment.getExternalStorageDirectory()+"/" + "wellav"  + "/record/"
        		 +uid+"/";
    }
    /** 本地录制文件存放路径 */
    public String getAvatarFolder() {
        return Environment.getExternalStorageDirectory()+"/" + "wellav"  + "/avatar/";
    }
    /** 本地logcat存放路径*/
    public String getCrashFolder() {
        return Environment.getExternalStorageDirectory()+"/" + "wellav"  + "/crash/";
    }

    /** 本地logcat存放路径 */
    public String getCrashFolderTxt(String fileName) {
        return Environment.getExternalStorageDirectory()+"/" + "wellav"  + "/crash/"+fileName;
    }
	public String getmNetState() {
		return mNetState;
	}


	public void setmNetState(String mNetState) {
		this.mNetState = mNetState;
	}



}
