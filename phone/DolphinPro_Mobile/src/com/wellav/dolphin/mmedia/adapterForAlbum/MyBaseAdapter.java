package com.wellav.dolphin.mmedia.adapterForAlbum;

import com.wellav.dolphin.mmedia.entity.BasePhotoInfo;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyBaseAdapter extends BaseAdapter{
	protected boolean mIsEditMode=false;
	public boolean ismIsEditMode() {
		return mIsEditMode;
	}

	public void setmIsEditMode(boolean mIsEditMode) {
		this.mIsEditMode = mIsEditMode;
	}
	protected OnItemClickedListener mLis;
	public interface OnItemClickedListener{
		public void onChkClicked(View v,BasePhotoInfo pi);
		public void onImgClicked(View v,BasePhotoInfo pi);
	}

	public void setmLis(OnItemClickedListener mLis) {
		this.mLis = mLis;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
