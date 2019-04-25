package com.wellav.dolphin.fragment;

import java.util.LinkedList;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.wellav.dolphin.adapter.AdapterForApplist;
import com.wellav.dolphin.adapter.AdapterForApplist.OnItemClickListener;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.AppInfo;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.launcher.R;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * 
 * @date2016年4月14日
 * @author Chen
 */
public class AppsFragment extends BaseFragment implements OnItemClickListener,
		OnClickListener {
	private RecyclerView mRv;
	private AdapterForApplist mAdapter;
	private LinkedList<AppInfo> mDataSrc;
	private ImageButton mActionBarPre;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frag_apps, null);
		
		mActionBarPre = (ImageButton) view.findViewById(R.id.actionbar_prev);
		mRv = (RecyclerView) view.findViewById(R.id.appsFrag_rvId);
		initAppInfo();
		mAdapter = new AdapterForApplist(mDataSrc, getActivity(), this);
		mRv.setLayoutManager(new StaggeredGridLayoutManager(1,
				StaggeredGridLayoutManager.HORIZONTAL));
		mRv.setAdapter(mAdapter);
		mActionBarPre.setOnClickListener(this);
		return view;
	}

	private void initAppInfo() {
		mDataSrc = new LinkedList<AppInfo>();
		AppInfo appInfo = new AppInfo(R.string.app_album,
				SysConfig.MEDIA_ACTIVITY_ACTION, "MainActivity",
				R.drawable.menu_album);
		mDataSrc.add(appInfo);
		appInfo = new AppInfo(R.string.app_message,
				SysConfig.MSC_ACTIVITY_ACTION, "WeatherFragmentActivity",
				R.drawable.menu_message);
		mDataSrc.add(appInfo);
		// add by jallen 20160418
		appInfo = new AppInfo(R.string.app_contact,
				SysConfig.DOLPHIN_CONTACT_ACTIVITY_ACTION, "ContactViewActivity",
				R.drawable.menu_contacts);
		mDataSrc.add(appInfo);
		appInfo = new AppInfo(R.string.app_setting,
				SysConfig.DOLPHIN_ACTIVITY_ACTION, "MainSettingActivity",
				R.drawable.menu_setting);
		mDataSrc.add(appInfo);
		appInfo = new AppInfo(R.string.app_sa,
				SysConfig.DOLPHIN_CAREMA_ACTIVITY_ACTION, "MainActivity",
				R.drawable.menu_securityassistant);
		mDataSrc.add(appInfo);
	}

	@Override
	public void onItemClick(AppInfo info) {
		// 该应用的包名
		String pkg = info.getPackageName();
		// 应用的主activity类
		StringBuilder sb = new StringBuilder(info.getPackageName());
		sb.append(".");
		sb.append(info.getClsName());
		ComponentName componet = new ComponentName(pkg, sb.toString());
		Intent intent = new Intent();
		intent.setComponent(componet);
		
		Bundle bundle = new Bundle();
		bundle.putString("city", LauncherApp.getInstance().getFamily().getCity());
		intent.putExtras(bundle); 
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		getFragmentManager().beginTransaction().remove(AppsFragment.this)
				.commit();
	}
	
}
