package com.wellav.dolphin.mmedia.adapter;

import java.util.List;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.UsrInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Copyright 2015 Wellav
 * 
 * All rights reserved.
 * @date2015年12月15日
 * @author Cheng
 */
public class AdapterForAlbumActivityDeviceFragment extends BaseAdapter{
	public interface OnDevItemClickedListener{
		public void onDevItemClicked(FamilyInfo familyInfo);
	}
	private OnDevItemClickedListener mLisener;
	private List< FamilyInfo> mDataSrc;
	private LayoutInflater mInflator;
	public AdapterForAlbumActivityDeviceFragment(List<FamilyInfo> mDataSrc2,Context ctx,OnDevItemClickedListener listener) {
		// TODO Auto-generated constructor stub
		mDataSrc=mDataSrc2;
		mInflator=LayoutInflater.from(ctx);
		mLisener=listener;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataSrc.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDataSrc.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if(convertView!=null)
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}else{
			viewHolder=new ViewHolder();
			convertView=mInflator.inflate(R.layout.item_lstview_album_activity, parent,false);
			viewHolder.mImgView=(CircleImageView)convertView.findViewById(R.id.item_lstview_albumActivity_ImgId);
			viewHolder.mTxtView=(TextView)convertView.findViewById(R.id.item_lstview_albumActivity_TxtId);
			convertView.setTag(viewHolder);
		}
		viewHolder.mTxtView.setText(this.mDataSrc.get(position).getNickName());
//		if(this.mDataSrc.get(position).getNote().equals("")){
//			viewHolder.mTxtView.setText(this.mDataSrc.get(position).getNickName());
//		}else{
//			viewHolder.mTxtView.setText(this.mDataSrc.get(position).getNote());
//		}
		LoadUserAvatarFromLocal task = new LoadUserAvatarFromLocal();
		Bitmap bitmap = task.loadImage(this.mDataSrc.get(position).getDeviceID());
		if(bitmap != null){
			viewHolder.mImgView.setImageBitmap(bitmap);
		}
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mLisener!=null)
					mLisener.onDevItemClicked(mDataSrc.get(position));
			}
		});
		return convertView;
	}
	private static class ViewHolder{
		CircleImageView mImgView;
		TextView mTxtView;
	}
}
