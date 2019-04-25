package com.wellav.dolphin.mmedia.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;

public class BaseFragment extends Fragment {
	
	//hide input
	public  void goneKeyboard() {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView()
					.getWindowToken(), 0);
		}
	}
}
