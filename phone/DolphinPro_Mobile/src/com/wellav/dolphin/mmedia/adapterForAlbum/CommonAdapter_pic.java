package com.wellav.dolphin.mmedia.adapterForAlbum;

import java.util.List;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.imagelru.ImageCacheUtil;
import com.wellav.dolphin.imagelru.ImageLoaderManager;
import com.wellav.dolphin.mmedia.DelPhotoActivity;
import com.wellav.dolphin.mmedia.GalleryActivity;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.utils.DateUtils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class CommonAdapter_pic extends MyBaseAdapter implements OnScrollListener {
	private Context mContext;
	private LayoutInflater mInflater;
	private StickyGridHeadersGridView mGridView;
	private List<PhotoInfo> mGridSrc;

	public CommonAdapter_pic(Context cxt, List<PhotoInfo> list, StickyGridHeadersGridView gridView) {
		// TODO Auto-generated constructor stub
		mGridView = gridView;
		mGridView.setOnScrollListener(this);
		mContext = cxt;
		mGridSrc = list;
		this.mInflater = LayoutInflater.from(cxt);
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
	public View getView(int position1, View convertView, ViewGroup parent) {
		final int position = position1 >= mGridSrc.size() ? mGridSrc.size() - 1 : position1;

		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();

			convertView = mInflater.inflate(R.layout.item_lifefrag, parent, false);

			viewHolder.mImgView = (ImageView) convertView.findViewById(R.id.item_lifeFrag_ImgId);

			viewHolder.mImgNew = (ImageView) convertView.findViewById(R.id.item_lifeFrag_imgId);
			viewHolder.mChkBox = (ImageView) convertView.findViewById(R.id.item_lifeFrag_chkId);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final PhotoInfo pi = mGridSrc.get(position);
		if (mIsEditMode) {
			viewHolder.mChkBox.setVisibility(View.VISIBLE);
			viewHolder.mChkBox.setImageDrawable(mContext.getResources()
					.getDrawable(pi.ismIsChked() ? R.drawable.checkbox_checked : R.drawable.checkbox_normal));

			viewHolder.mChkBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pi.setmIsChked(!pi.ismIsChked());
					((ImageView) v).setImageDrawable(mContext.getResources()
							.getDrawable(pi.ismIsChked() ? R.drawable.checkbox_checked : R.drawable.checkbox_normal));

					if (mLis != null)
						mLis.onChkClicked(v, pi);
				}
			});
		}

		String thumbnail = pi.getmThumbnail();
		// 消除图片闪烁
		viewHolder.mImgView.setImageBitmap(ImageCacheUtil.getInstance()
				.getCacheFromMemory(ImageCacheUtil.getCacheKey(thumbnail, 0, 0, ScaleType.FIT_CENTER)));

		viewHolder.mImgNew.setVisibility(DateUtils.daysPast(pi.getmTime()) <= 2 ? View.VISIBLE : View.INVISIBLE);
		if (!isScrolling) {
			ImageLoaderManager.loadImage(thumbnail, viewHolder.mImgView, null, null, null, ScaleType.FIT_CENTER);
		}
		viewHolder.mImgView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 进入大图查看窗口
				if (mIsEditMode)
					return;
				GalleryActivity.mGridSrc = mGridSrc;
				Intent intent = new Intent(HBaseApp.getInstance(), GalleryActivity.class);
				intent.putExtra("CurrentItem", position);
				mContext.startActivity(intent);
			}
		});
		viewHolder.mImgView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				DelPhotoActivity.mGridSrc = mGridSrc;
				Intent intent = new Intent(HBaseApp.getInstance(), DelPhotoActivity.class);
				mContext.startActivity(intent);
				return true;
			}
		});
		return convertView;
	}

	
	public static class ViewHolder {
		public ImageView mImgView;
		public ImageView mImgNew;
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
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}
}
