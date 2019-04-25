package com.wellav.dolphin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.launcher.R;

public class MsgBoxFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view =inflater.inflate(R.layout.activity_main, container, false);
		
		SysConfig.getInstance().setNewMsg(false);
		return view;
	}

}
