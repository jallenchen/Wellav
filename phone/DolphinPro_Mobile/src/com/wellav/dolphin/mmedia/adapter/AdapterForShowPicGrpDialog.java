package com.wellav.dolphin.mmedia.adapter;

import java.util.List;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.entity.PicGrpInfo;
import com.wellav.dolphin.mmedia.utils.NativeImageLoader;
import com.wellav.dolphin.mmedia.utils.NativeImageLoader.NativeImageCallBack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Copyright 2015 Wellav
 * 
 * All rights reserved.
 * @date2015年12月23日
 * @author Cheng
 */
public class AdapterForShowPicGrpDialog extends BaseAdapter{
	private List<PicGrpInfo> mDataSrc;
	private LayoutInflater mInflator;
	private ListView mLv;
	private Point mPoint;
	public interface OnItemClickedLis{
		public void onItemClicked(PicGrpInfo picGrpInfo);
	}
	public AdapterForShowPicGrpDialog(List<PicGrpInfo> ds,Context ctx,ListView lv) {
		// TODO Auto-generated constructor stub
		mDataSrc=ds;
		mInflator=LayoutInflater.from(ctx);
		mLv=lv;
		WindowManager wm = (WindowManager) ctx.getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mPoint=new Point(outMetrics.widthPixels/3,outMetrics.widthPixels/3);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder=null;
		if(convertView!=null)
		{
			viewHolder=(ViewHolder) convertView.getTag();
		}else {
			convertView=mInflator.inflate(R.layout.item_dialog_show_pic_grp, parent, false);
			viewHolder=new ViewHolder();
			viewHolder.mChkBox=(CheckBox)convertView.findViewById(R.id.showPicGrp_dialog_item_chkId);
			viewHolder.mImgView=(ImageView)convertView.findViewById(R.id.showPicGrp_dialog_item_imgId);
			viewHolder.mTxtView=(TextView)convertView.findViewById(R.id.showPicGrp_dialog_item_txtId);
			convertView.setTag(viewHolder);
		}
		final PicGrpInfo picGrpInfo=mDataSrc.get(position);
		String firstChildPath=picGrpInfo.getmFirstChildPath();
		viewHolder.mImgView.setTag(firstChildPath);
//		Bitmap bitmap =NativeImageLoader.getInstance().loadNativeImage2(firstChildPath, mPoint, new NativeImageCallBack() {
//			
//			@Override
//			public void onImageLoader(Bitmap bitmap, String path) {
//				ImageView mImageView = (ImageView) mLv
//						.findViewWithTag(path);
//				if (bitmap != null && mImageView != null) {
//					mImageView.setImageBitmap(bitmap);
//				}
//			}
//		});
//		if (bitmap != null) {
//			viewHolder.mImgView.setImageBitmap(bitmap);
//		} 
		viewHolder.mTxtView.setText(picGrpInfo.getmDirNam()+"/"+picGrpInfo.getmChildCnt());
		viewHolder.mChkBox.setVisibility(picGrpInfo.ismIsChked()?View.VISIBLE:View.INVISIBLE);

		return convertView;
	}
	private static class ViewHolder{
		ImageView mImgView;
		TextView mTxtView;
		CheckBox mChkBox;
	}

}
