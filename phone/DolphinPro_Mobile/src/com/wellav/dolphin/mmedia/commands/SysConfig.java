package com.wellav.dolphin.mmedia.commands;

import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

import com.netease.nimlib.sdk.team.model.Team;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;

public class SysConfig {
	// RTC
	public static String APP_ID = "70292";//
	public static String APP_KEY = "dc6c1a07-7215-4a28-85c5-d74d33e0241d";//
	//public static String APP_ID = "70038";//
	//public static String APP_KEY = "MTQxMDkyMzU1NTI4Ng==";//
	//public static String APP_ID = "70063";//
   // public static String APP_KEY = "MTQzMTQyNTM2MzA4NQ==";//
	public static String VOLUME="volume";
	public static String BRIGHTNESS="brightness";
	// MSG
//	public static String serverIP = "sandboxapp.cloopen.com";
//	public static String serverPort = "8883";
//	public static String accountSid = "8a48b551525cdd3301525de7287b0423";
//	public static String accountToken = "80dd9093cadd4c8289ee4267a6afed18";
//	public static String appId = "aaf98f8952624f610152688481360e01";
	//短信验证码
	//https://app.cloopen.com:8883
	public static String serverIP = "app.cloopen.com";//测试地址
	//public static String serverIP = "sandboxapp.cloopen.com";//测试地址
	public static String serverPort = "8883";
	public static String accountSid = "aaf98f894ef91b17014efcb879c80480";
	public static String accountToken = "91671e8c20934c339179d9f3eb022f4d";
	public static String appId = "aaf98f8953acd61f0153ad310d0b00b7";
	//微信分享
	public static String downloadLink="http://www.wellav.com/";
	public static final String WX_APP_ID="wxbfa2dc86b99f8b99";
	
	// Location
	// String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php";
	public static String locationUrl = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json";
	//自理的IP 10.200.0.94:9090
	//本地服务器地址：10.200.0.94   替换为阿里云服务器地址：112.74.143.22 或 120.76.220.183
	public static String ServerUrl= "http://120.76.220.183:9090/services/hiapi/";
	public static String userid = "";
	public static String uid = "";
	public static String psw = "";
	public static String nickname = "nickname";
	public static String rtoken = "";
	public static String dtoken = "";
	public static String familyIdForPic;
	public static int loginFail_1103 = -1103;
	public static int loginFail_1100 = -1100;
	public static String MSG_CONFIG="msg_config";
	public static String IS_NOTIFY="is_notify";
	public static String HAS_VOICE="has_voice";
	public static String WatchingNotify="WatchingNotify";
	public static String CallingNotify="CallingNotify";
	public static String LookingPhotoNotify="LookingPhotoNotify";
	public static String choseRingtone="chose_Ringtone";
	public static String secondPassword="second_Password";
	public static String SHARE_NAME = "dolphin_info";
	public static String key_loginpsw = "login_psw";
	public static String key_neteasetoken = "rtc_token";
	public static String key_dolphintoken = "dolphin_token";
	public static String key_privacymode = "privacy_mode";
	public static String BROADCAST_LOGIN_SERVICE = "com.wellav.service.broadcast";
	public static final String PackageName = "com.wellav.dolphin.mmedia";
	public static final int MSG_SIP_REGISTER = 1001;
	public static final int MSG_SDKInitOK = 1002;// by cpl
	public static final int MSG_SDKInitNG = 1003;
	
    //通话类型
	public static final int Free=0;
    public static final int ChatCall=1;
    public static final int ChatAccept=2;
    public static final int MutilCall=3;
    public static final int MutilAccept=4;
    public static final int MeetingCall=5;
    public static final int MeetingAccept=6;
    public static final int MonitorAccept=7;
    public static final int MutilJoin=8;
    public static final int MeetingJoin=9;
    
    /**
     * add
     */
    public static final String Broadcast_Action_MuteAudio="Broadcast_action_muteaudio";
    public static final String Broadcast_Action_Roomname="Broadcast_action_roomname";
    public static final String Broadcast_detect_time_changed="Broadcast_detect_time_changed";
    public static String RecevierNetPowerAction = "com.wellav.activities.DolphinSettingFragmentActivity";
    public static String fragmentForAlbumAction = "com.wellav.dolphin.mmedia.fragmentForAlbum.";
    public static String mmediafragmentAction = "com.wellav.dolphin.mmedia.fragment.";
    public static String LOADOTddHERDEVICE = "Loading On The Other Device";
    public static String DOTHIS = DolphinApp.getInstance().getResources().getString(R.string.do_this);
    public static String YES = DolphinApp.getInstance().getResources().getString(R.string.conform);
    public static String NO = DolphinApp.getInstance().getResources().getString(R.string.cancel);
    public static String TABTITLE[] = DolphinApp.getInstance().getResources().getStringArray(R.array.dolphin_);
    public static String RELOADING = DolphinApp.getInstance().getResources().getString(R.string.sorry_reload);
    
	private boolean mLoginOK = false;
	private int mStatus = 0;
	private String mGroupname;
	private String mCreator;
	 private String mFamilyId;
	private int mCallType = 0;
	private int mFamilyPos = 0;
	private boolean isPrivacy = false;
    private boolean bIncoming = false;
    private boolean isCalling = false;
    private boolean newBoxMsg = false;
    private boolean newinviteMsg = false;
    private List<Team> mTeams = new ArrayList<Team>();
    
	public void setTeams(List<Team>  teams){
		this.mTeams = teams;
	}
	
	public List<Team>  getTeams(){
		return this.mTeams;
	}
	
	public List<String> getMyTeamID(){
		List<String> mMyTeamIds = new ArrayList<String>();
		for(int i=0; i<mTeams.size();i++){
			if(mTeams.get(i).isMyTeam()){
				mMyTeamIds.add(mTeams.get(i).getId());
			}
		}
		return mMyTeamIds;
	}
	
	public String getCreator() {
		return mCreator;
	}

	public void setCreator(String creator) {
		this.mCreator = creator;
	}
	public String getGroupName() {
		return mGroupname;
	}
	public void setGroupName(String groupId) {
		mGroupname = groupId;
	}
   public int getCallType() {
        return mCallType;
    }

    public void setCallType(int mCallType) {
        this.mCallType = mCallType;
    }
	public static SysConfig getInstance() {
		return DolphinApp.getInstance().getSysConfig();
	}

	public boolean ismLoginOK() {
		return mLoginOK;
	}

	public void setmLoginOK(boolean mLoginOK) {
		this.mLoginOK = mLoginOK;
	}

	public void setStatus(int status) {
		this.mStatus = status;
	}

	public int getStatus() {
		return mStatus;
	}
	
    public String getFamilyId() {
        return mFamilyId;
    }

    public void setbFamilyId(String familyId) {
        this.mFamilyId = familyId;
    }
    public int getFamilyPos() {
        return mFamilyPos;
    }

    public void setbFamilyPos(int familyId) {
        this.mFamilyPos = familyId;
    }
    
    public void setPrivacyMode(boolean is){
    	this.isPrivacy = is;
    }
    public boolean isPrivacyMode(){
    	return this.isPrivacy;
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
    
    
    /** 本地录制文件存放路径 */
    public String getRecordFolder() {
        return Environment.getExternalStorageDirectory()+"/" + DolphinApp.getInstance().getFileFolder() + "/record/"
                +DolphinApp.getInstance().getAppAccountID();
    }
    public String getCaptureFolder() {
        String path = Environment.getExternalStorageDirectory()+"/" + DolphinApp.getInstance().getFileFolder() + "/capture/"
        +DolphinApp.getInstance().getAppAccountID()+"/";
        return path;
    }
    
    /** 本地录制文件存放路径 */
    public String getAvatarFolder() {
        return Environment.getExternalStorageDirectory()+"/" + "wellav"  + "/avatar/";
    }
    /** 本地录制文件存放路径 */
    public String getCrashFolder() {
        return Environment.getExternalStorageDirectory()+"/" + "wellav"  + "/crash/";
    }
    /** 本地录制文件存放路径 */
    public String getCrashFolderTxt(String name) {
        return Environment.getExternalStorageDirectory()+"/" + "wellav"  + "/crash/"+name;
    }
    /** 本地拍照存放路径 */
    public String getCameraFolder() {
        return Environment.getExternalStorageDirectory()+"/" + "wellav"  + "/camera/";
    }

}
