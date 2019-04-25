package com.wellav.dolphin.mmedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.iflytek.autoupdate.IFlytekUpdate;
import com.iflytek.autoupdate.IFlytekUpdateListener;
import com.iflytek.autoupdate.UpdateConstants;
import com.iflytek.autoupdate.UpdateErrorCode;
import com.iflytek.autoupdate.UpdateInfo;
import com.iflytek.autoupdate.UpdateType;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.activity.RegisterActivity;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.fragment.WelcomeFragment;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class FirstPageFragmentActivity extends BaseActivity {
	WelcomeFragment mWelcome;
	private IFlytekUpdate updManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		//checkUpdate();
		initView();
		
		int isStart = getIntent().getIntExtra("start", 0);
		if(isStart == 1){
			Toast.makeText(this, getResources().getString(R.string.restart_ok), Toast.LENGTH_LONG).show();
		}else if(isStart == 2){
			Toast.makeText(this, getResources().getString(R.string.other_phone_loading), Toast.LENGTH_LONG).show();
		}
		CommFunc.PrintLog("FirstPageFragmentActivity", "onCreate()");
	}
	
	private void checkUpdate(){
		// 初始化 自动更新对象
		updManager = IFlytekUpdate.getInstance(FirstPageFragmentActivity.this);
		// 开启 调试 模式 ，默 认不开启
		updManager.setDebugMode(true);
		// 开启 wifiwifiwifiwifi环境下检测更新 ，仅对 自动 更新有效 ，强制则生更新有效 ，强制则生更新有效
		updManager.setParameter(UpdateConstants.EXTRA_WIFIONLY, "false");
		// 设置 通知栏 使用 应用 icon ，详情请见 示例
		updManager.setParameter(UpdateConstants.EXTRA_NOTI_ICON, "true");
		// 设置更新 提示类型 ，默认为通知栏提示 默认为通知栏提示
		updManager.setParameter(UpdateConstants.EXTRA_STYLE,UpdateConstants.UPDATE_UI_DIALOG);
		// 启动 自动更新
		updManager.autoUpdate(FirstPageFragmentActivity.this, updateListener);
		// 自动 更新回调方法，详情参考 demo
		
	}
	IFlytekUpdateListener updateListener = new IFlytekUpdateListener() {
		@Override
		public void onResult(int errorcode, UpdateInfo result) {
			//CommFunc.PrintLog("FirstPageFragmentActivity", "updateListener()"+errorcode+"-"+result.getUpdateVersion());
			//CommFunc.PrintLog("FirstPageFragmentActivity", "getDownloadUrl()"+"-"+result.getDownloadUrl());
			if(errorcode == UpdateErrorCode.OK && result!= null) {
				if(result.getUpdateType() == UpdateType.NoNeed) {
					//showTip("已经是最新版本！");
					initView();
					return;
				}
				updManager.showUpdateInfo(FirstPageFragmentActivity.this, result);
				newInstance(false);
			}
			else
			{
				//showTip("请求更新失败！\n更新错误码：" + errorcode);
				initView();
			}
		}
		};
		
		private void showTip(final String str) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					CommFunc.DisplayToast(getApplicationContext(), str);
				}
			});
		}

	private void newInstance(boolean is) {
		mWelcome = (WelcomeFragment) WelcomeFragment.newInstance(is);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		tx.add(R.id.container, mWelcome);
		tx.commit();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		updManager = null;
		updateListener = null;
		mWelcome = null;
		super.onDestroy();
	}

	public void onLogin(View view) {
		Intent inte = new Intent(FirstPageFragmentActivity.this,
				LoginPageFragmentActivity.class);
		startActivity(inte);
	}

	public void onRegister(View view) {
		Intent inte = new Intent(FirstPageFragmentActivity.this,
				RegisterActivity.class);
		inte.putExtra("REGISTER", true);
		startActivity(inte);
	}
	
	
	

	@Override
	protected void onStart() {
		super.onStart();
		
		
		
	}

	private void initView() {

		PreferenceUtils.getInstance().getSharePreferencesAccount();
		if (!"".equals(SysConfig.uid) && !"".equals(SysConfig.psw)) {
			newInstance(true);
			HBaseApp.post2WorkDelayed(new Runnable() {

				@Override
				public void run() {
					Intent inte = new Intent(FirstPageFragmentActivity.this,
							LoginPageFragmentActivity.class);
					inte.putExtra("autologin", true);
					startActivity(inte);
					//DolphinApp.getInstance().initLoginRtc();
					finish();
				}
			}, 2000);
		} else {
			newInstance(false);
			//mWelcome.setBtnVisible(View.VISIBLE);
		}
	}

}
