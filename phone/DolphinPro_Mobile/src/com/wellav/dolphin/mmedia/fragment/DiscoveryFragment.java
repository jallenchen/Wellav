package com.wellav.dolphin.mmedia.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.AlbumActivity;
import com.wellav.dolphin.mmedia.AssistanHouseFragmentActivity;
import com.wellav.dolphin.mmedia.ChooseOneDeviceActivity;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.utils.CommFunc;

/**
 * Copyright 2015 Wellav
 * 
 * All rights reserved.
 * 
 * @date2015年12月14日
 * @author Chen
 */
public class DiscoveryFragment extends BaseFragment implements OnClickListener {
	private Button mBtnShare;
	private Button mBtnAlbum;
	private Button mBtnAssis;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_discovery, null);
		mBtnAlbum = (Button) view.findViewById(R.id.discoveryFrag_btnAlbumId);
		mBtnAlbum.setOnClickListener(this);
		mBtnShare = (Button) view.findViewById(R.id.discoveryFrag_btnShareId);
		mBtnShare.setOnClickListener(this);
		mBtnAssis = (Button) view.findViewById(R.id.discoveryFrag_btn_assistan);
		mBtnAssis.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.discoveryFrag_btnAlbumId:
			int decivecnt = DolphinApp.getInstance().getFamilyInfos().size();
			if (decivecnt == 0) {
				CommFunc.DisplayToast(getActivity(), R.string.nodevice_album);
				return;
			} else if (decivecnt == 1) {
				SysConfig.familyIdForPic = DolphinApp.getInstance().getFamilyInfos().get(0).getFamilyID();
				intent = new Intent(getActivity(), AlbumActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(getActivity(), ChooseOneDeviceActivity.class);
				intent.putExtra("Item", ChooseOneDeviceFragment.To_Photo);
				startActivity(intent);
			}
			break;
		case R.id.discoveryFrag_btnShareId:
			// intent=new Intent(getActivity(), ShareActivity.class);
			// startActivity(intent);
			break;
		case R.id.discoveryFrag_btn_assistan:
			int myfamilyinfos = DolphinApp.getInstance().getMyFamilyInfos().size();
			
			if (myfamilyinfos == 0) {
				CommFunc.DisplayToast(getActivity(), 
						DiscoveryFragment.this.getResources().getString(R.string.you_not_manager));
				return;
			} else if (myfamilyinfos == 1) {
				if(DolphinApp.getInstance().getFamilyOnlineMap().containsKey(DolphinApp.getInstance().getMyFamilyInfos().get(0).getDeviceID())){
					 if(DolphinApp.getInstance().getFamilyOnlineMap().get(DolphinApp.getInstance().getMyFamilyInfos().get(0).getDeviceID()) == true){
						Intent st=new Intent(getActivity(),AssistanHouseFragmentActivity.class);
						startActivity(st);
					}else{
						 CommFunc.DisplayToast(getActivity(), 
								 DiscoveryFragment.this.getResources().getString(R.string.dolphin_outLine_remo));
						 return;
					}
				}else{
					CommFunc.DisplayToast(getActivity(), 
							 getActivity().getResources().getString(R.string.dolphin_outLine_remo));
					 return;
					 }
			} else {
				intent = new Intent(getActivity(), ChooseOneDeviceActivity.class);
				intent.putExtra("Item", ChooseOneDeviceFragment.To_Assis);
				startActivity(intent);
			}
			break;
		}
	}
}
