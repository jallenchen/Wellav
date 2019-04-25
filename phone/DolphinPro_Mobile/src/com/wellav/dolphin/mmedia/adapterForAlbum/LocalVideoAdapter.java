package com.wellav.dolphin.mmedia.adapterForAlbum;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.imagelru.ImageCacheUtil;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.utils.NativeImageLoader;
import com.wellav.dolphin.mmedia.utils.NativeImageLoader.NativeImageCallBack;

public class LocalVideoAdapter extends MyBaseAdapter implements
OnScrollListener{
	private LayoutInflater mInflater;
	private GridView mGridView;
	private List<PhotoInfo> mChkedSrc;
	private List<PhotoInfo> mDataSrc;
	private Context mCtx;
	private Point mPoint=new Point(150, 150);//图片大小
	public LocalVideoAdapter(Context cxt, List<PhotoInfo> ds,List<PhotoInfo> cds,
			GridView gridView) {
		// TODO Auto-generated constructor stub
		mGridView = gridView;
		mGridView.setOnScrollListener(this);
		mCtx=cxt;
		mDataSrc=ds;
		mChkedSrc=cds;
		this.mInflater = LayoutInflater.from(cxt);
	}
	private OnItemChkedChangedListener mOnItemChkListener;
	public void setOnItemChkListener(OnItemChkedChangedListener mOnItemChkListener) {
		this.mOnItemChkListener = mOnItemChkListener;
	}



	public interface OnItemChkedChangedListener{
		public void onItemChkedChanged();
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
		
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.item_video, parent,
					false);

			viewHolder.mImgView = (ImageView) convertView
					.findViewById(R.id.item_lifeFrag_ImgId);

			
			viewHolder.mChkBox = (ImageView) convertView
					.findViewById(R.id.item_lifeFrag_chkId);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final PhotoInfo pi = mDataSrc.get(position);

		String thumbnail = pi.getmThumbnail();
		// 消除图片闪烁
		viewHolder.mImgView.setImageBitmap(ImageCacheUtil.getInstance().getCacheFromMemory(
				ImageCacheUtil.getCacheKey(thumbnail, mPoint.x, mPoint.y, ScaleType.CENTER_INSIDE)));

		viewHolder.mChkBox.setVisibility(mIsEditMode ? View.VISIBLE
				: View.INVISIBLE);

		
		if (mIsEditMode)
		{
			viewHolder.mChkBox
			.setBackgroundResource(mChkedSrc.contains(pi) ? R.drawable.checkbox_checked
					: R.drawable.checkbox_normal);
		
			viewHolder.mChkBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (!mChkedSrc.contains(pi)) {
						mChkedSrc.add(pi);
						((ImageView) v)
								.setBackgroundResource(R.drawable.checkbox_checked);
					} else {
						mChkedSrc.remove(pi);
						((ImageView) v)
								.setBackgroundResource(R.drawable.checkbox_normal);
					}
					mOnItemChkListener.onItemChkedChanged();
				}
			});
		}
		viewHolder.mImgView.setTag(thumbnail);
		if (!isScrolling) {
//			NativeImageLoader.getInstance().loadNativeVideo(thumbnail, mPoint, new NativeImageCallBack() {
//				
//				@Override
//				public void onImageLoader(Bitmap bitmap, String path) {
//					// TODO Auto-generated method stub
//					ImageView mImageView = (ImageView) mGridView
//							.findViewWithTag(path);
//					if (bitmap != null && mImageView != null) {
//						mImageView.setImageBitmap(bitmap);
//						
//					}
//				}
//			});
		}
		viewHolder.mImgView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 进入大图查看窗口
				if (mIsEditMode)
					return;
				Intent intent=new Intent(DolphinApp.getInstance(), com.wellav.dolphin.mmedia.SimpleVideoActivity.class);
				intent.putExtra("Url", pi.getmUrl());
				mCtx.startActivity(intent);
			}
		});
		viewHolder.mImgView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		return convertView;
	}

	

	private static class ViewHolder {
		ImageView mImgView;
		ImageView mChkBox;
	}

	private boolean isScrolling;

	
	
	private boolean isScrollToBottom;
	private boolean isLoading;

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public interface OnRefreshListener {

		// void onDownPullRefresh();

		void onLoadingMore();
	}

	private OnRefreshListener mOnRefershListener;

	/**
	 * 设置刷新监听事件
	 * 
	 * @param listener
	 */
	public void setOnRefreshListener(OnRefreshListener listener) {
		mOnRefershListener = listener;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		isScrollToBottom = view.getLastVisiblePosition() == (totalItemCount - 1);
		

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
		if (!isLoading
				&& (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING)
				&& view.canScrollVertically(-1)) {
			// 判断当前是否已经到了底部
			if (isScrollToBottom) {
				if (mOnRefershListener != null)
					mOnRefershListener.onLoadingMore();
			}
		}
		
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			isScrolling = false;
			this.notifyDataSetChanged();
		} else
			isScrolling = true;
		
	}
	
}
