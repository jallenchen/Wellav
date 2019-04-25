package com.wellav.dolphin.mmedia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.wellav.dolphin.mmedia.utils.LocalActManager;

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
	 * 显示加载Dialog
	 * 
	 * @param showtext
	 */
	ProgressDialog loadingDialog;
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

	/**
	 * 网络检测
	 * 
	 * @return
	 */
//	public boolean checkNet() {
//		if (NetWorkUtil.isNetConnect(this) == false) {
//			CommFunc.PrintLog(5, LOGTAG, "checkNet()  isNetConnect net_cannot_use ismLoginOK==false");
//			return false;
//		}
//		if (!SysConfig.getInstance().ismLoginOK()) {
//			CommFunc.PrintLog(5, LOGTAG, "checkNet() unLogin ismLoginOK==false");
//			return false;
//		}
//		return true;
//	}

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
