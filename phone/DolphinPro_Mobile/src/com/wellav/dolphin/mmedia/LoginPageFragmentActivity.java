package com.wellav.dolphin.mmedia;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.AccountInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.UserInfo;
import com.wellav.dolphin.mmedia.fragment.LoginOneFragment;
import com.wellav.dolphin.mmedia.fragment.LoginOneFragment.MyLoginOneLisenter;
import com.wellav.dolphin.mmedia.fragment.LoginTwoFragment;
import com.wellav.dolphin.mmedia.fragment.LoginTwoFragment.MyLoginTwoLisenter;
import com.wellav.dolphin.mmedia.fragment.RegisterOneFragment;
import com.wellav.dolphin.mmedia.net.LoadAccountInfoFromServer;
import com.wellav.dolphin.mmedia.net.LoadAccountInfoFromServer.DataCallBack;
import com.wellav.dolphin.mmedia.net.LoadAllFamilyUsersFromServer;
import com.wellav.dolphin.mmedia.net.LoadAllFamilyUsersFromServer.allUserDataCallBack;
import com.wellav.dolphin.mmedia.net.LoadUsersFromServer;
import com.wellav.dolphin.mmedia.net.LoadUsersFromServer.userDataCallBack;
import com.wellav.dolphin.mmedia.utils.CommFunc;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class LoginPageFragmentActivity extends BaseActivity implements MyLoginOneLisenter,MyLoginTwoLisenter{
	private static String TAG = "LoginPageFragmentActivity";
	//private AccountInfo mAccountInfo = null;
	List<FamilyUserInfo> familyUserInfos = null;
	//List<FamilyInfo> familys = null;
	List<UserInfo> UserInfos = null;
	private String mToken;
	private boolean bRegreveiver = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		CommFunc.PrintLog(TAG, "onCreate()");
		if(getIntent().getBooleanExtra("autologin", false)){
			toLogin(SysConfig.uid,SysConfig.psw);
		}else{
			initView();
		}
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		CommFunc.PrintLog(TAG, "onDestroy()");
		if (bRegreveiver)
			unregisterReceiver(Msgreceiver);
		dismissLoadDialog();
		super.onDestroy();
	}

	public void onLogin(View view){
		initView();
	}
	public void onRegister(View view){
		RegisterOneFragment disFrag=new RegisterOneFragment();
    	Bundle bundle = new Bundle();  
		bundle.putBoolean("REGISTER", true);
		disFrag.setArguments(bundle);
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.add(R.id.container, disFrag);
        ft.commit(); 
	
	}

	private void initView(){
		PreferenceUtils.getInstance().getSharePreferencesAccount();
		
		 if(!"".equals(SysConfig.uid )){
			LoginTwoFragment tFrag=new LoginTwoFragment();
			Bundle bundle = new Bundle();  
			bundle.putString("LOGINNAME", SysConfig.uid );
			bundle.putString("NICKNAME", SysConfig.nickname);
			tFrag.setArguments(bundle);
	    	FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction tx = fm.beginTransaction();
			tx.replace(R.id.container, tFrag);
			tx.commit();
		}else{
	    	FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction tx = fm.beginTransaction();
			tx.replace(R.id.container, new LoginOneFragment());
			tx.commit();
		}
		
	}
	

	@Override
	public void OnLoginButtonOnClick(String loginName, String psw) {
		// TODO Auto-generated method stub
		SysConfig.uid = loginName;
		SysConfig.psw = DolphinUtil.md5(psw);
		
		Log.e(TAG, "OnLoginButtonOnClick"+loginName+"-"+psw);
		if (!"".equals(SysConfig.uid ) && !"".equals(SysConfig.psw)) {
			toLogin(loginName,SysConfig.psw);
		}else{
			CommFunc.DisplayToast(this, R.string.login_fail);
		}
		
		
	}
	
	private void toLogin(String name,String psw){
		
		if (bRegreveiver == false) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(SysConfig.BROADCAST_LOGIN_SERVICE);
			registerReceiver(Msgreceiver, intentFilter);
			bRegreveiver = true;
		}
		loadingDialog(getString(R.string.login_processing));
		String loginMsg = RequestString.getLogin(name, psw);
		LoadAccountInfoFromServer task = new LoadAccountInfoFromServer(loginMsg);
		task.getData(new DataCallBack() {
			
			@Override
			public void onDataCallBack(int code, AccountInfo data) {
				// TODO Auto-generated method stub
				if(data == null){
					CommFunc.DisplayToast(LoginPageFragmentActivity.this, R.string.login_ng);
					 dismissLoadDialog();
					return;
				}
				if(code == MsgKey.KEY_RESULT_SUCCESS){
					if(DolphinApp.getInstance().getFamilyInfos().size() != 0 ){
						SQLiteManager.getInstance().saveFamilyInfoList(DolphinApp.getInstance().getFamilyInfos(), true);
						getFamilyUserInfo();
					}else{
						getUsersInfo(data.getUserId());
					}
					
				}else{
					 if(code == SysConfig.loginFail_1100){
						 CommFunc.DisplayToast(LoginPageFragmentActivity.this, R.string.login_fail_1100);
					}else if(code == SysConfig.loginFail_1103){
						CommFunc.DisplayToast(LoginPageFragmentActivity.this, R.string.login_fail_1103);
					//	PreferenceUtils.getInstance().saveSharePreferences(true, false);
					//	initView();
					}else{
						CommFunc.DisplayToast(LoginPageFragmentActivity.this, getResources().getString(R.string.no_net));
					}
					 dismissLoadDialog();
			    }
			}
		});
		
		
	}
	private void getFamilyUserInfo(){
		mToken = SysConfig.dtoken;
		int familysize = DolphinApp.getInstance().getFamilyInfos().size();
		if(familysize !=0){
			ArrayList<String> mFamilyIDlist = new ArrayList<String>();
			for(int i=0;i<familysize;i++){
				mFamilyIDlist.add(DolphinApp.getInstance().getFamilyInfos().get(i).getFamilyID());
			}
		String FamilyUsersRequest = RequestString.getFamilyUsers(mToken,mFamilyIDlist);
		LoadAllFamilyUsersFromServer task = new LoadAllFamilyUsersFromServer(FamilyUsersRequest);
		task.getAllUserData(new allUserDataCallBack() {
			
			@Override
			public void onDataCallBack(int code, List<FamilyUserInfo> data) {
				// TODO Auto-generated method stub
				if(code == MsgKey.KEY_RESULT_SUCCESS){
					SQLiteManager.getInstance().saveFamilyUserInfoList(data, true);
					getUsersInfo(SysConfig.userid);
				}else{
					
				}
			}
		});
		}
	}
	
	
	
	private void getUsersInfo(String usid){
			String 	myuserInfo = RequestString.getUserInfo(DolphinApp.getInstance().getAccountInfo().getToken(),usid);
			LoadUsersFromServer task = new  LoadUsersFromServer(myuserInfo);
			task.getData(new userDataCallBack() {
				
				@Override
				public void onDataCallBack(int code, List<UserInfo> data) {
					// TODO Auto-generated method stub
					if(code == MsgKey.KEY_RESULT_SUCCESS){
						SQLiteManager.getInstance().saveMyUserInfo(data.get(0));
						mHandler.sendEmptyMessage(4);
					}else{
						
					}
				}
			});
	}
	
	

	@SuppressLint("HandlerLeak") Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case 4:
				//保存账户信息
				PreferenceUtils.getInstance().saveSharePreferences(SysConfig.key_loginpsw, SysConfig.psw);
				PreferenceUtils.getInstance().saveSharePreferences(SysConfig.key_neteasetoken,SysConfig.rtoken);
				PreferenceUtils.getInstance().saveSharePreferences(SysConfig.key_dolphintoken,SysConfig.dtoken);
				dismissLoadDialog();
				//DolphinApp.getInstance().initLoginRtc();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	
	private BroadcastReceiver Msgreceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(SysConfig.BROADCAST_LOGIN_SERVICE)) {
				switch (intent.getIntExtra("what", -1)) {
				case SysConfig.MSG_SIP_REGISTER: {
					dismissLoadDialog();
					OnLoginResult(intent);
					break;
				}
				case SysConfig.MSG_SDKInitOK: {
					//Toast.makeText(LoginPageFragmentActivity.this, "MSG_SDKInitOK", Toast.LENGTH_SHORT).show();
				//	DolphinApp.getInstance().initLoginRtc();
					break;
				}
				case SysConfig.MSG_SDKInitNG: {
					dismissLoadDialog();
					CommFunc.DisplayToast(LoginPageFragmentActivity.this, "SDK 初始化失败");
					PreferenceUtils.getInstance().clearSharePreferencesToken();
					//DolphinApp.getInstance().InitSdk();
					//toLogin(SysConfig.uid ,SysConfig.psw);
					DolphinApp.getInstance().beenLogOut();
					finish();
					
					break;
				}
				}
			}
		}
		
	};
	
	private void OnLoginResult(Intent intent) {
//		int result = intent.getIntExtra("arg1", -1);
//		String desc = intent.getStringExtra("arg2");
//
//		if (result == MsgKey.KEY_STATUS_200
//				|| result == MsgKey.KEY_RESULT_SUCCESS) {
//
//			SysConfig.getInstance().setmLoginOK(true);
//
//		} else {
//			SysConfig.getInstance().setmLoginOK(false);
//			CommFunc.PrintLog(5, TAG, getResources().getString(R.string.loading_fail_wrong) + result + "]desc:" + desc);
//			if(result == RtcConst.CallCode_Forbidden) {
//				PreferenceUtils.getInstance().saveSharePreferences(SysConfig.key_rtctoken, "");
//				CommFunc.DisplayToast(DolphinApp.getInstance(),getResources().getString(R.string.other_loading));
//			} else if(result == RtcConst.CallCode_NotFound) {
//				PreferenceUtils.getInstance().saveSharePreferences(SysConfig.key_rtctoken, "");
//				CommFunc.DisplayToast(DolphinApp.getInstance(),getResources().getString(R.string.no_register));
//			} else {
//				CommFunc.DisplayToast(DolphinApp.getInstance(), getResources().getString(R.string.load_fail) + result + getResources().getString(R.string.load_fail_reason) + desc);
//			}
//		}
//		
//		Intent inte = new Intent(LoginPageFragmentActivity.this,MainActivity.class);
//		startActivity(inte);
//		finish();
	}
	
	protected ProgressDialog loadingDialog;

	protected void loadingDialog(String showtext) {
		if (loadingDialog == null) {
			loadingDialog = new ProgressDialog(this);
			loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			if (showtext == null) {
				loadingDialog.setMessage(getString(R.string.login_processing));
			} else {
				loadingDialog.setMessage(showtext);
			}
			loadingDialog.setIndeterminate(false);
			loadingDialog.setCancelable(true);
			loadingDialog.setCanceledOnTouchOutside(false);
		}
		loadingDialog.show();
	}

	/**
	 * 销毁加载Dialog
	 */
	protected void dismissLoadDialog() {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
		}
	}
}
