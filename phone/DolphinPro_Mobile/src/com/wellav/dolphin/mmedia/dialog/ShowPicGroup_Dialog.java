package com.wellav.dolphin.mmedia.dialog;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.adapter.AdapterForShowPicGrpDialog;
import com.wellav.dolphin.mmedia.adapter.AdapterForShowPicGrpDialog.OnItemClickedLis;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.entity.PicGrpInfo;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Copyright 2015 Wellav
 * 
 * All rights reserved.
 * 
 * @date2015年12月23日
 * @author Cheng
 */
public class ShowPicGroup_Dialog extends DialogFragment {
	private ListView mLv;
	private List<PicGrpInfo> mDataSrc = new LinkedList<PicGrpInfo>();
	private AdapterForShowPicGrpDialog mAdapter;
	private OnItemClickedLis mListener;
	private Point mPoint;
	
	public ShowPicGroup_Dialog(HashMap<String, List<PhotoInfo>> ds,
			String chkedPath, OnItemClickedLis lis) {
		// TODO Auto-generated constructor stub
		
		mListener = lis;
		Iterator<String> iterator = ds.keySet().iterator();
		PicGrpInfo picGrpInfo = null;
		String key;
		String firstChildPath;
		int totalCnt = 0;

		while (iterator.hasNext()) {
			key = iterator.next();

			picGrpInfo = new PicGrpInfo();
			int size = ds.get(key).size();
			totalCnt += size;
			picGrpInfo.setmChildCnt(size);
			firstChildPath = ds.get(key).get(0).getmUrl();
			picGrpInfo.setmFirstChildPath(firstChildPath);
			File file = new File(key);
			picGrpInfo.setmDirNam(file.getName());
			picGrpInfo.setmDirPath(key);
			
			picGrpInfo.setmIsChked(chkedPath != null && key.equals(chkedPath));
			mDataSrc.add(picGrpInfo);
		}

		if (picGrpInfo != null) {
			PicGrpInfo picGrpInfo_copyed = picGrpInfo.getCopy(picGrpInfo);
			picGrpInfo_copyed.setmChildCnt(totalCnt);
			picGrpInfo_copyed.setmDirNam(DolphinApp.getInstance().getString(R.string.all_picture));
			picGrpInfo_copyed.setmDirPath(null);
			picGrpInfo_copyed.setmIsChked(chkedPath == null);
			mDataSrc.add(0, picGrpInfo_copyed);
		}

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		AlertDialog.Builder buidler = new Builder(getActivity());
		LayoutInflater inflator = LayoutInflater.from(getActivity());
		View view = inflator.inflate(R.layout.dialog_show_pic_grp, null);
		mLv = (ListView) view.findViewById(R.id.showPicGrp_dialog_lvId);
		mAdapter = new AdapterForShowPicGrpDialog(mDataSrc, getActivity(), mLv);
		mLv.setAdapter(mAdapter);
		AlertDialog dialog = null;
		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				if (mListener != null)
					mListener.onItemClicked((PicGrpInfo) mAdapter
							.getItem(position));
			}
		});
		buidler.setView(view);
		dialog = buidler.create();
		return dialog;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		WindowManager wm = (WindowManager) getActivity().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mPoint=new Point(outMetrics.widthPixels,outMetrics.heightPixels);
		getDialog().getWindow().setLayout(mPoint.x-60, mPoint.y-150);
		super.onResume();
	}
}
