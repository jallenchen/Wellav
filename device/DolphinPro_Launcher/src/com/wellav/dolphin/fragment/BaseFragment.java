package com.wellav.dolphin.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * @date2016年4月14日
 * @author Cheng
 */
public class BaseFragment extends Fragment{
	
	/**
	 * 隐藏软键盘
	 */
	protected void goneKeyboard() {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(getActivity().getWindow()
					.getDecorView().getWindowToken(), 0);
		}
	}

}
