package com.wellav.dolphin.mmedia.adapterForAlbum;

import java.util.List;

import com.wellav.dolphin.imagelru.ImageCacheUtil;
import com.wellav.dolphin.imagelru.ImageLoaderManager;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView.ScaleType;
import android.widget.ImageView;

/**
 * Copyright 2016 Wellav
 * 
 * All rights reserved.
 * 
 * @date2016年3月11日
 * @author Cheng
 */
public class FavorAdapter extends MyBaseAdapter implements OnScrollListener {
	private List<PhotoInfo> mGridSrc;
	private List<PhotoInfo> mChkedSrc;
	private LayoutInflater mInflator;
	private GridView mGv;
	private Context mContext;

	public FavorAdapter(List<PhotoInfo> ds, List<PhotoInfo> chkedDs,
			Context cxt, GridView gv) {
		// TODO Auto-generated constructor stub
		mGridSrc = ds;
		mChkedSrc = chkedDs;
		mInflator = LayoutInflater.from(cxt);
		mGv = gv;
		mGv.setOnScrollListener(this);
		mContext = cxt;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflator.inflate(R.layout.item_favor, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.mChkBox = (ImageView) convertView
					.findViewById(R.id.item_favor_imgFavorId);
			viewHolder.mImgView = (ImageView) convertView
					.findViewById(R.id.item_favor_ImgId);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		final PhotoInfo pi = mGridSrc.get(position);
		String thumbnail = pi.getmThumbnail();
		viewHolder.mChkBox.setImageDrawable(mContext.getResources()
				.getDrawable(
						mChkedSrc.contains(pi) ? R.drawable.album_like_off
								: R.drawable.album_like_on));
		viewHolder.mChkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mChkedSrc.contains(pi))
					mChkedSrc.remove(pi);
				else
					mChkedSrc.add(pi);
				((ImageView) v).setImageDrawable(mContext
						.getResources()
						.getDrawable(
								mChkedSrc.contains(pi) ? R.drawable.album_like_off
										: R.drawable.album_like_on));
				if (mLis != null)
					mLis.onChkClicked(v, pi);
			}
		});
		// 消除图片闪烁
		viewHolder.mImgView.setImageBitmap(ImageCacheUtil.getInstance()
				.getCacheFromMemory(
						ImageCacheUtil.getCacheKey(thumbnail, 0, 0,
								ScaleType.FIT_CENTER)));


		if (!isScrolling) {
			ImageLoaderManager.loadImage(thumbnail, viewHolder.mImgView, null,
					null, null, ScaleType.FIT_CENTER);
		}
		return convertView;
	}

	private static class ViewHolder {
		public ImageView mImgView;
		public ImageView mChkBox;
	}

	private boolean isScrolling;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			isScrolling = false;
			this.notifyDataSetChanged();
		} else
			isScrolling = true;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}
}
