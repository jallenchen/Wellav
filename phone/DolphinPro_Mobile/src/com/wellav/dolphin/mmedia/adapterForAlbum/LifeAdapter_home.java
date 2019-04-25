package com.wellav.dolphin.mmedia.adapterForAlbum;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.wellav.dolphin.imagelru.ImageLoaderManager;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.entity.PhotoInfo_grpByUsr;
import com.wellav.dolphin.imagelru.ImageCacheUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * @date2016年2月22日
 * @author Cheng
 */
public class LifeAdapter_home extends Adapter<LifeAdapter_home.MyViewHolder>{
	private List<PhotoInfo_grpByUsr> mDataSrc;
	private LayoutInflater mInflator;
	private OnItemClickListener mListener;
	private Context ctx;
	public interface OnItemClickListener{
		public void onItemClick(String usrNam);
	}
	public LifeAdapter_home(List<PhotoInfo_grpByUsr> ds,Context ctx,OnItemClickListener lis) {
		// TODO Auto-generated constructor stub
		mDataSrc=ds;
		mInflator=LayoutInflater.from(ctx);
		mListener=lis;
		this.ctx = ctx;
	}
	class MyViewHolder extends ViewHolder{
		ImageButton mImg;
		TextView mTxt;
		public MyViewHolder(View itemView) {
			super(itemView);
			// TODO Auto-generated constructor stub
			mImg=(ImageButton)itemView.findViewById(R.id.item_lifeFrag_home_ImgId);
			mTxt=(TextView)itemView.findViewById(R.id.item_lifeFrag_home_txtId);
		}
		
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mDataSrc.size();
	}

	@Override
	public void onBindViewHolder(MyViewHolder arg0, final int arg1) {
		// TODO Auto-generated method stub
		StringBuilder sb=new StringBuilder(mDataSrc.get(arg1).getmUsrNam()+ctx.getString(R.string.album));
		sb.append("[");
		sb.append(mDataSrc.get(arg1).getmCount());
		sb.append("]");
		arg0.mTxt.setText(sb);
		String thumbnail=mDataSrc.get(arg1).getmThumbUrl();
		arg0.mImg.setImageBitmap(ImageCacheUtil.getInstance().getCacheFromMemory(
				ImageCacheUtil.getCacheKey(thumbnail, 0, 0, ScaleType.CENTER_INSIDE)));
		ImageLoaderManager.loadImage(thumbnail, arg0.mImg, null,
				null, null);
		arg0.mImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mListener.onItemClick(mDataSrc.get(arg1).getmUsrId());
			}
		});
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		// TODO Auto-generated method stub
		MyViewHolder holder=new MyViewHolder(mInflator.inflate(R.layout.item_lifefrag_home, arg0, false));
		return holder;
	}

	

}
