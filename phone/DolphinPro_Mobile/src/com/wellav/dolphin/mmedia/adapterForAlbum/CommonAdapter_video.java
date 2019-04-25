package com.wellav.dolphin.mmedia.adapterForAlbum;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.DelVideoActivity;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.SimpleVideoActivity;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.utils.NativeImageLoader;

public class CommonAdapter_video extends MyBaseAdapter {
	private List<PhotoInfo> mItemList;
	private Context mContext;
	private LayoutInflater mInflater;
	private Point mPoint;
	private GridView mGridView;
	public CommonAdapter_video(Context cxt, List<PhotoInfo> list,GridView gridView) {
		// TODO Auto-generated constructor stub
		this.mItemList=list;
		mGridView=gridView;
		mContext=cxt;
		this.mInflater=LayoutInflater.from(cxt);
		float density=cxt.getResources().getDisplayMetrics().density;
		mPoint=new Point(Math.round(density*80),Math.round(density*60));
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItemList.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mItemList.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(final int position1, View convertView, final ViewGroup parent) {
		// TODO Auto-generated method stub
		final int position = position1 >= mItemList.size() ? mItemList.size() - 1
				: position1;
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_video, parent, false);
			viewHolder.mImgView = (ImageView) convertView
					.findViewById(R.id.item_video_ImgId);
			viewHolder.mImgNew=(ImageView)convertView.findViewById(R.id.item_video_ImgId);
			viewHolder.mChkBox=(ImageView)convertView.findViewById(R.id.item_video_chkId);
			convertView.setTag(viewHolder);						  
 
			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mChkBox.setVisibility(mIsEditMode?View.VISIBLE:View.INVISIBLE);
		final PhotoInfo pi = mItemList.get(position);
		String thumbnail = pi.getmThumbnail();
		if (mIsEditMode) {
			viewHolder.mChkBox.setVisibility(View.VISIBLE);
			viewHolder.mChkBox.setImageDrawable(mContext.getResources()
					.getDrawable(
							pi.ismIsChked() ? R.drawable.checkbox_checked
									: R.drawable.checkbox_normal));
			
			viewHolder.mChkBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pi.setmIsChked(!pi.ismIsChked());
					((ImageView)v).setImageDrawable(mContext.getResources()
							.getDrawable(
									pi.ismIsChked() ? R.drawable.checkbox_checked
											: R.drawable.checkbox_normal));
					
					if(mLis!=null)
						mLis.onChkClicked(v, pi);
				}
			});
		}
		viewHolder.mImgView.setImageBitmap(NativeImageLoader.getInstance().getCacheFromMemory(thumbnail));
		viewHolder.mImgView.setTag(thumbnail);
		NativeImageLoader.getInstance().loadNativeVideoThumbnail(mGridView,thumbnail, mPoint, null);
		viewHolder.mImgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 进入大图查看窗口
				if(mIsEditMode)
					return;
				Intent intent=new Intent(HBaseApp.getInstance(), SimpleVideoActivity.class);
				intent.putExtra("Url", mItemList.get(position).getmUrl());
				mContext.startActivity(intent);
			}
		});
		viewHolder.mImgView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				DelVideoActivity.mGridSrc = mItemList;
				Intent intent = new Intent(HBaseApp.getInstance(), DelVideoActivity.class);
				mContext.startActivity(intent);
				return true;
			}
		});
		return convertView;
	}
	

	
	private static class ViewHolder{
		public ImageView mImgView;
		public ImageView mImgNew;
		public ImageView mChkBox;
	}
	
}
