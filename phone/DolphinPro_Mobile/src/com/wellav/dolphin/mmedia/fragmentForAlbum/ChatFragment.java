package com.wellav.dolphin.mmedia.fragmentForAlbum;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.ui.AlbumTab;
import com.wellav.dolphin.mmedia.ui.AlbumTab.OnItemClickedListener;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * @date2016年3月18日
 * @author Cheng
 */
public class ChatFragment extends BaseFragment implements OnItemClickedListener{
	private AlbumTab mTaB;
	private String[] mBtnTxt;
	private static BaseFragment[] mFragments;
	public static BaseFragment[] getmFragments() {
		return mFragments;
	}
	public ChatFragment() {
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.frag_chat, null);
		mBtnTxt = getResources().getStringArray(R.array.pictureAndVedio);
		mTaB = (AlbumTab) view.findViewById(R.id.fragChat_tabId);
		mTaB.init(this, mBtnTxt);
		mFragments=new BaseFragment[2];
		CommonFragment_pic frag=new CommonFragment_pic();
		frag.setmPhotoTyp(2);
		mFragments[0]=frag;
		FragmentManager fm =getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.chatFrag_frameId, frag);
		ft.commit();
		return view;
	}
	@Override
	public void onItemClicked(int clickItem) {
		// TODO Auto-generated method stub
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.hide(mFragments[clickItem==1?0:1]);
		if(mFragments[clickItem]==null)
		{
			mFragments[clickItem]=new CommonFragment_video();
			
			ft.add(R.id.chatFrag_frameId, mFragments[clickItem]);
		}
		else
			ft.show(mFragments[clickItem]);
		ft.commit();	
	}
	@Override
	public void onDestroy() {
		mFragments=null;
		super.onDestroy();
	}
}
