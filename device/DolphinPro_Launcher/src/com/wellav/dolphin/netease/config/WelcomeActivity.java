package com.wellav.dolphin.netease.config;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.FamilyInfo;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.config.MsgKey;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.db.PreferenceUtils;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadAccountInfoFromServer;
import com.wellav.dolphin.net.LoadAccountInfoFromServer.DataCallBack;
import com.wellav.dolphin.net.LoadAllFamilyUserFromServer;
import com.wellav.dolphin.net.LoadAllFamilyUserFromServer.allUserDataCallBack;
import com.wellav.dolphin.net.XMLBody;

/**
 * 欢迎/导航页（app启动Activity）
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class WelcomeActivity extends TActivity {

    private static final String TAG = "WelcomeActivity";

    private boolean customSplash = false;
    private AbortableFuture<LoginInfo> loginRequest;
    private static boolean firstEnter = true; // 是否首次进入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        if (savedInstanceState != null) {
            setIntent(new Intent()); // 从堆栈恢复，不再重复解析之前的intent
        }
        if (!firstEnter) {
            onIntent();
        } else {
            showSplashView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (firstEnter) {
            firstEnter = false;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (canAutoLogin()) {
                        onIntent();
                    } else {
                       // LoginActivity.start(WelcomeActivity.this);
                        onMyLogin();
                       // finish();
                    }
                }
            };
            if (customSplash) {
                new Handler().postDelayed(runnable, 1000);
            } else {
                runnable.run();
            }
        }
    }
    

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        /**
         * 如果Activity在，不会走到onCreate，而是onNewIntent，这时候需要setIntent
         * 场景：点击通知栏跳转到此，会收到Intent
         */
        setIntent(intent);
        onIntent();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }

    // 处理收到的Intent
    private void onIntent() {
        LogUtil.i(TAG, "onIntent...");

        if (TextUtils.isEmpty(DemoCache.getAccount())) {
            // 判断当前app是否正在运行
            if (!SysInfoUtil.stackResumed(this)) {
               // LoginActivity.start(this);
            	onMyLogin();
            }
           // finish();
        } else {
            // 已经登录过了，处理过来的请求
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                    parseNotifyIntent(intent);
                    return;
                }
            }

            if (!firstEnter && intent == null) {
                finish();
            } else {
                showMainActivity();
            }
        }
    }

    /**
     * 已经登陆过，自动登陆
     */
    private boolean canAutoLogin() {
        Log.i(TAG, "get local sdk token =" + SysConfig.NeteaseToken);
        boolean isFirstEnter = PreferenceUtils.getInstance().getBooleanShare(MsgKey.Key_FirstEnter,true);
        if(isFirstEnter == true){
        	return false;
        }
        if(!TextUtils.isEmpty(SysConfig.uid) && !TextUtils.isEmpty(SysConfig.NeteaseToken) ) {
        	return true;
        }else{
        	return false;
        }
    }

    @SuppressWarnings("unchecked")
	private void parseNotifyIntent(Intent intent) {
        ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (messages == null || messages.size() > 1) {
            showMainActivity(null);
        } else {
            showMainActivity(new Intent().putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, messages.get(0)));
        }
    }

    /**
     * 首次进入，打开欢迎界面
     */
    private void showSplashView() {
        getWindow().setBackgroundDrawableResource(R.drawable.bg);
        customSplash = true;
    }

    private void showMainActivity() {
        showMainActivity(null);
    }

    private void showMainActivity(Intent intent) {
        startActivity(new Intent(WelcomeActivity.this, FlavorDependent.getInstance().getMainClass()));
        finish();
    }
    
    /**
   * ***************************************** 登录 **************************************
   */

	@SuppressWarnings("unchecked")
	private void onNeteaseLogin() {
      // 登录
      loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(SysConfig.uid, SysConfig.NeteaseToken));
      loginRequest.setCallback(new RequestCallback<LoginInfo>() {
          @Override
          public void onSuccess(LoginInfo param) {
              Log.e(TAG, "login success");
              Toast.makeText(WelcomeActivity.this, R.string.login_succ, Toast.LENGTH_SHORT).show();
            	if(true){
				 //TeamCreateHelper.createAdvancedTeam(WelcomeActivity.this, null);
			 }
              onLoginDone();
              DemoCache.setAccount(SysConfig.uid);
              saveLoginInfo();

              // 初始化消息提醒
              NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

              // 初始化免打扰
              NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());
              // 进入主界面
              startActivity(new Intent(WelcomeActivity.this, FlavorDependent.getInstance().getMainClass()));
              finish();
          }

          @Override
          public void onFailed(int code) {
              onLoginDone();
              if (code == 302 || code == 404) {
                  Toast.makeText(WelcomeActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
              } else {
                  Toast.makeText(WelcomeActivity.this, "登录失败: " + code, Toast.LENGTH_SHORT).show();
              }
          }

          @Override
          public void onException(Throwable exception) {
              onLoginDone();
          }
      });
  }
  
  @SuppressWarnings("deprecation")
	private void onMyLogin(){
  	//TODO
  	
      DialogMaker.showProgressDialog(this, null, getString(R.string.logining), true, new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface dialog) {
              if (loginRequest != null) {
                  loginRequest.abort();
                  onLoginDone();
              }
          }
      }).setCanceledOnTouchOutside(false);
      
      String loginBody = XMLBody.getLogin(SysConfig.uid, SysConfig.psw);
		LoadAccountInfoFromServer task = new LoadAccountInfoFromServer(
				loginBody);
		task.getData(new DataCallBack() {

			@Override
			public void onDataCallBack(int code, FamilyInfo data) {
				// TODO Auto-generated method stub
				if (code == MsgKey.KEY_RESULT_SUCCESS) {
					LauncherApp.getInstance().setFamily(data);
					getFamilyUsers();
					
				} else {
					 onLoginDone();
				}

			}

		});
  }
  
  public void getFamilyUsers() {
		String loginBody = XMLBody.getFamilyUsers(SysConfig.DolphinToken,
				LauncherApp.getInstance().getFamily().getFamilyID());
		LoadAllFamilyUserFromServer task = new LoadAllFamilyUserFromServer(
				loginBody);
		task.getAllUserData(new allUserDataCallBack() {

			@Override
			public void onDataCallBack(int code, final List<FamilyUserInfo> data) {
				// TODO Auto-generated method stub
				if (code == MsgKey.KEY_RESULT_SUCCESS) {
					onNeteaseLogin();
				} else {
					 onLoginDone();
				}
			}
		});
	}


  private void onLoginDone() {
      loginRequest = null;
      DialogMaker.dismissProgressDialog();
  }

  private void saveLoginInfo() {
  	PreferenceUtils.getInstance().saveAccountSharePreferences();
  	PreferenceUtils.getInstance().saveBooleanSharePreferences(MsgKey.Key_FirstEnter,false);
  }

  //DEMO中使用 username 作为 NIM 的account ，md5(password) 作为 token
  //开发者需要根据自己的实际情况配置自身用户系统和 NIM 用户系统的关系
  private String tokenFromPassword(String password) {
      return MD5.getStringMD5(password);
  }
}
