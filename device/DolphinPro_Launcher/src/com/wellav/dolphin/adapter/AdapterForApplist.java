package com.wellav.dolphin.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.bean.AppInfo;
import com.wellav.dolphin.launcher.R;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * @date2016年4月11日
 * @author Cheng
 */
public class AdapterForApplist extends Adapter<AdapterForApplist.MyViewHolder>{
	private List<AppInfo> mDataSrc;
	private LayoutInflater mInflator;
	private OnItemClickListener mListener;
	public interface OnItemClickListener{
		public void onItemClick(AppInfo appInfo);
	}
	public AdapterForApplist(List<AppInfo> ds,Context ctx,OnItemClickListener lis) {
		mDataSrc=ds;
		mInflator=LayoutInflater.from(ctx);
		mListener=lis;
	}
	class MyViewHolder extends ViewHolder{
		ImageView mImg;
		TextView mTxt;
		public MyViewHolder(View itemView) {
			super(itemView);
			mImg=(ImageView)itemView.findViewById(R.id.item_applist_imgIconId);
			mTxt=(TextView)itemView.findViewById(R.id.item_applist_txtAppNamId);
		}
	}

	@Override
	public int getItemCount() {
		return mDataSrc.size();
	}

	@Override
	public void onBindViewHolder(MyViewHolder arg0, final int arg1) {
		final AppInfo appInfo=mDataSrc.get(arg1);
		arg0.mTxt.setText(appInfo.getAppName());
		arg0.mImg.setImageResource(appInfo.getAppIcon());
		arg0.mImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onItemClick(appInfo);
			}
		});
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		MyViewHolder holder=new MyViewHolder(mInflator.inflate(R.layout.item_applist, arg0, false));
		return holder;
	}
}
