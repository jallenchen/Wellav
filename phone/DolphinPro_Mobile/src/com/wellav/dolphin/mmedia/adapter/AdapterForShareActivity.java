package com.wellav.dolphin.mmedia.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.wellav.dolphin.imagelru.ImageCacheUtil;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.ShareActivity;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.utils.NativeImageLoader;
import com.wellav.dolphin.mmedia.utils.NativeImageLoader.NativeImageCallBack;

/**
 * Copyright 2015 Wellav
 * 
 * All rights reserved.
 * @date2015年12月21日
 * @author Cheng
 */
public class AdapterForShareActivity extends BaseAdapter{
	private GridView mGridView;
	private List<PhotoInfo> mGridSrc;
	private List<PhotoInfo> mChkedSrc;
	private LayoutInflater mInflator;
	private Point mPoint;
	private OnItemChkedListener mListener;
	public interface OnItemChkedListener{
		public void onItemChked(PhotoInfo pi);
		public void onCameraClicked();
	}
	public AdapterForShareActivity(List<PhotoInfo> ds,List<PhotoInfo> chkedSrc,Context ctx,GridView gridView,OnItemChkedListener lis) {
		// TODO Auto-generated constructor stub
		mListener=lis;
		mGridView=gridView;
		mChkedSrc=chkedSrc;
		mInflator=LayoutInflater.from(ctx);
		WindowManager wm = (WindowManager) ctx.getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mPoint=new Point(outMetrics.widthPixels/3,outMetrics.widthPixels/3);
		
		mGridSrc=ds;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mGridSrc.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mGridSrc.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder=null;
		if(convertView!=null)
			viewHolder=(ViewHolder) convertView.getTag();
		else{
			convertView=mInflator.inflate(R.layout.item_share_activity, parent,false);
			viewHolder = new ViewHolder();
			viewHolder.mImgView = (ImageView) convertView
					.findViewById(R.id.item_shareActivity_ImgId);	
			viewHolder.mChkBox=(ImageView)convertView.findViewById(R.id.item_shareActivity_chkId);
			convertView.setTag(viewHolder);	
		}
		final PhotoInfo pi=mGridSrc.get(position);
		String path =pi.getmUrl();
		
		if(path==null)
		{
			viewHolder.mImgView.setImageResource(R.drawable.camera);
			viewHolder.mChkBox.setVisibility(View.INVISIBLE);
			viewHolder.mImgView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mListener.onCameraClicked();
			       
				}
			});
		}
		else{
			viewHolder.mImgView.setImageBitmap(ImageCacheUtil.getInstance().getMemoryLruCache().get(path));
			viewHolder.mImgView.setTag(path);
			
			NativeImageLoader.getInstance().loadNativeImageThumbnail(mGridView,path, mPoint, null);
			
			viewHolder.mChkBox.setVisibility(View.VISIBLE);
			final boolean isChked=isChked(pi);
			viewHolder.mChkBox.setImageResource(isChked?R.drawable.checkbox_checked:R.drawable.checkbox_normal);
			viewHolder.mChkBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(isChked(pi))
					{
						mChkedSrc.remove(pi);
						((ImageView)v).setImageResource(R.drawable.checkbox_normal);
						mListener.onItemChked(pi);
					}
					else if(mChkedSrc.size()< (ShareActivity.MAX_COUNT - ShareActivity.mAddedCnt))
					{
						mChkedSrc.add(pi);
						Log.e("AdapterForShareActivity", pi.getmUrl());
						((ImageView)v).setImageResource(R.drawable.checkbox_checked);
						mListener.onItemChked(pi);
					}
				}
			});
			
			viewHolder.mImgView.setClickable(false);
			

		}
		
		return convertView;
	}
	public List<PhotoInfo> getChkedData(){
		return mChkedSrc;
	}
	private boolean isChked(PhotoInfo pi)
	{
		int size=mChkedSrc.size();
//		Log.e("", "bsize="+size);
		for(int i=0;i<size;i++)
		{
			if(pi.getmUrl().equals(mChkedSrc.get(i).getmUrl()))
				return true;
		}
		return false;
	}
	private static class ViewHolder{
		ImageView mImgView;
		ImageView mChkBox;
	}

}
