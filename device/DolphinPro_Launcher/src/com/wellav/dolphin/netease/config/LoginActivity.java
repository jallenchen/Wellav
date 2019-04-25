package com.wellav.dolphin.netease.config;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadAccountInfoFromServer;
import com.wellav.dolphin.net.LoadUsersFromServer;
import com.wellav.dolphin.net.LoadUsersFromServer.userDataCallBack;

/**
 * Created by hzxuwen on 2016/2/24.
 */
public class LoginActivity extends TActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String KICK_OUT = "KICK_OUT";

    private Button loginBtn;
    private Button registerBtn;
    private TextView switchModeBtn;  // 注册/登录切换按钮

    private ClearableEditTextWithIcon loginAccountEdit;
    private ClearableEditTextWithIcon loginPasswordEdit;
    private String mLoginName;
    private String mPsw;



    private AbortableFuture<LoginInfo> loginRequest;
    private boolean registerMode = false; // 注册模式

    public static void start(Context context) {
        start(context, false);
    }

    public static void start(Context context, boolean kickOut) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KICK_OUT, kickOut);
        context.startActivity(intent);
    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.login_activity);
//
//        parseIntent();
//        setupLoginPanel();
//    }
//
//    private void parseIntent() {
//        boolean isKickOut = getIntent().getBooleanExtra(KICK_OUT, false);
//        if (isKickOut) {
//            EasyAlertDialogHelper.showOneButtonDiolag(LoginActivity.this,
//                    getString(R.string.kickout_notify),
//                    getString(R.string.kickout_content),
//                    getString(R.string.ok), true, null);
//        }
//    }
//
//
//    /**
//     * 登录面板
//     */
//    private void setupLoginPanel() {
//        loginAccountEdit = (ClearableEditTextWithIcon) findViewById(R.id.edit_login_account);
//        loginPasswordEdit = (ClearableEditTextWithIcon) findViewById(R.id.edit_login_password);
//        switchModeBtn = (TextView) findViewById(R.id.register_login_tip);
//        loginBtn = (Button) findViewById(R.id.done);
//
//        loginAccountEdit.setIconResource(R.drawable.user_account_icon);
//        loginPasswordEdit.setIconResource(R.drawable.user_pwd_lock_icon);
//
//        loginAccountEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
//        loginPasswordEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
//        loginAccountEdit.addTextChangedListener(textWatcher);
//        loginPasswordEdit.addTextChangedListener(textWatcher);
//
//        loginAccountEdit.setText(SysConfig.uid);
//
//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // login();
//                onMyLogin();
//            }
//        });
//        switchModeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              //  switchMode();
//                onRegister();
//            }
//        });
//    }
//
//
//    private TextWatcher textWatcher = new TextWatcher() {
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            // 更新右上角按钮状态
//            if (!registerMode) {
//                // 登录模式
//                boolean isEnable = loginAccountEdit.getText().length() > 0
//                        && loginPasswordEdit.getText().length() > 0;
//                updateBtn(loginBtn, isEnable);
//            }
//        }
//    };
//
//    private void updateBtn(TextView loginBtn, boolean isEnable) {
//        loginBtn.setBackgroundResource(R.drawable.g_white_btn_selector);
//        loginBtn.setEnabled(isEnable);
//        loginBtn.setTextColor(getResources().getColor(R.color.color_blue_0888ff));
//    }
//
//    /**
//     * ***************************************** 登录 **************************************
//     */
//
//	@SuppressWarnings("unchecked")
//	private void onNeteaseLogin() {
//        // 登录
//        loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(mLoginName, mPsw));
//        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
//            @Override
//            public void onSuccess(LoginInfo param) {
//                Log.i(TAG, "login success");
//                Toast.makeText(LoginActivity.this, R.string.login_succ, Toast.LENGTH_SHORT).show();
//                onLoginDone();
//                DemoCache.setAccount(mLoginName);
//                saveLoginInfo();
//
//                // 初始化消息提醒
//                NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
//
//                // 初始化免打扰
//                NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());
//                // 进入主界面
//                startActivity(new Intent(LoginActivity.this, FlavorDependent.getInstance().getMainClass()));
//                finish();
//            }
//
//            @Override
//            public void onFailed(int code) {
//                onLoginDone();
//                if (code == 302 || code == 404) {
//                    Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(LoginActivity.this, "登录失败: " + code, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                onLoginDone();
//            }
//        });
//    }
//    
//    @SuppressWarnings("deprecation")
//	private void onMyLogin(){
//    	//TODO
//    	
//        DialogMaker.showProgressDialog(this, null, getString(R.string.logining), true, new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                if (loginRequest != null) {
//                    loginRequest.abort();
//                    onLoginDone();
//                }
//            }
//        }).setCanceledOnTouchOutside(false);
//        
//        mLoginName = loginAccountEdit.getEditableText().toString().toLowerCase();
//        //final String token = tokenFromPassword(loginPasswordEdit.getEditableText().toString());
//        mPsw = loginPasswordEdit.getEditableText().toString();//测试代码，密码不加密
//        
//    	String loginMsg = RequestString.getLogin(mLoginName, mPsw);
//		LoadAccountInfoFromServer task = new LoadAccountInfoFromServer(loginMsg);
//		task.getData(new DataCallBack() {
//			
//			@Override
//			public void onDataCallBack(int code, AccountInfo data) {
//				// TODO Auto-generated method stub
//				if(data == null){
//					CommFunc.DisplayToast(LoginActivity.this, R.string.login_ng);
//					onLoginDone();
//					return;
//				}
//				if(code == MsgKey.KEY_RESULT_SUCCESS){
//					SysConfig.dtoken = data.getToken();
//					SysConfig.userid = data.getUserId();
//					SysConfig.uid = mLoginName;
//					SysConfig.psw = mPsw;
//					if(DolphinApp.getInstance().getFamilyInfos().size() != 0 ){
//						SQLiteManager.getInstance().saveFamilyInfoList(DolphinApp.getInstance().getFamilyInfos(), true);
//						getFamilyUserInfo();
//					}else{
//						getUsersInfo(data.getUserId());
//					}
//					
//				}else{
//					 if(code == SysConfig.loginFail_1100){
//						 CommFunc.DisplayToast(LoginActivity.this, R.string.login_fail_1100);
//					}else if(code == SysConfig.loginFail_1103){
//						CommFunc.DisplayToast(LoginActivity.this, R.string.login_fail_1103);
//					}else{
//						CommFunc.DisplayToast(LoginActivity.this, getResources().getString(R.string.no_net));
//					}
//					 onLoginDone();
//			    }
//			}
//		});
//    }
//    
//    private void getFamilyUserInfo(){
//		int familysize = DolphinApp.getInstance().getFamilyInfos().size();
//		if(familysize !=0){
//			ArrayList<String> mFamilyIDlist = new ArrayList<String>();
//			for(int i=0;i<familysize;i++){
//				mFamilyIDlist.add(DolphinApp.getInstance().getFamilyInfos().get(i).getFamilyID());
//			}
//		String FamilyUsersRequest = RequestString.getFamilyUsers(SysConfig.dtoken,mFamilyIDlist);
//		LoadAllFamilyUsersFromServer task = new LoadAllFamilyUsersFromServer(FamilyUsersRequest);
//		task.getAllUserData(new allUserDataCallBack() {
//			
//			@Override
//			public void onDataCallBack(int code, List<FamilyUserInfo> data) {
//				// TODO Auto-generated method stub
//				if(code == MsgKey.KEY_RESULT_SUCCESS){
//					SQLiteManager.getInstance().saveFamilyUserInfoList(data, true);
//					getUsersInfo(SysConfig.userid);
//				}else{
//					
//				}
//			}
//		});
//		}
//	}
//	
//	
//	
//	private void getUsersInfo(String usid){
//			String 	myuserInfo = RequestString.getUserInfo(SysConfig.dtoken,usid);
//			LoadUsersFromServer task = new  LoadUsersFromServer(myuserInfo);
//			task.getData(new userDataCallBack() {
//				
//				@Override
//				public void onDataCallBack(int code, List<UserInfo> data) {
//					// TODO Auto-generated method stub
//					if(code == MsgKey.KEY_RESULT_SUCCESS){
//						SQLiteManager.getInstance().saveMyUserInfo(data.get(0));
//						onNeteaseLogin();
//					}else{
//						
//					}
//				}
//			});
//	}
//
//
//    private void onLoginDone() {
//        loginRequest = null;
//        DialogMaker.dismissProgressDialog();
//    }
//
//    private void saveLoginInfo() {
//    	PreferenceUtils.getInstance().saveSharePreferences(true,true);
//    }
//
//    //DEMO中使用 username 作为 NIM 的account ，md5(password) 作为 token
//    //开发者需要根据自己的实际情况配置自身用户系统和 NIM 用户系统的关系
//    private String tokenFromPassword(String password) {
//        return MD5.getStringMD5(password);
//    }
//    
//	public void onRegister() {
//		Intent inte = new Intent(LoginActivity.this,
//				RegisterActivity.class);
//		inte.putExtra("REGISTER", true);
//		startActivity(inte);
//	}
//
//   

}
