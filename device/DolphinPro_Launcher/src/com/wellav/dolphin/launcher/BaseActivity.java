package com.wellav.dolphin.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.utils.LocalActManager;
import com.wellav.dolphin.utils.NetWorkUtil;
import com.wellav.dolphin.utils.Util;

/**
 * 
 * @author Administrator
 * 
 */
public class BaseActivity extends FragmentActivity {

	//public String lOGTAG = getClass().getName();
	public String LOGTAG = getClass().getName().substring(getClass().getName().lastIndexOf(".") + 1);
	
//	protected ProgressDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LocalActManager.getInstance().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalActManager.getInstance().removeActivity(this);
	}

	

	/**
	 * 网络检测
	 * 
	 * @return
	 */
	public boolean checkNet() {
		return true;
	}

	/**
	 * 隐藏软键盘
	 */
	public void goneKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(getWindow().getDecorView()
					.getWindowToken(), 0);
		}
	}
	
	  /**
     * 返回
     * 
     * @param view
     */
    public void back(View view) {
        finish();
    }
}
