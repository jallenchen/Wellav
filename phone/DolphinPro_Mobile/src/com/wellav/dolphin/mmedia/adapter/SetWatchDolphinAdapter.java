package com.wellav.dolphin.mmedia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;

public class SetWatchDolphinAdapter extends BaseAdapter {
	private Context mContext;

	public SetWatchDolphinAdapter(Context ct) {
		// TODO Auto-generated constructor stub
		this.mContext = ct;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		return convertView;
	}
	
	
	static class ViewHolder {
		TextView item;
		CheckBox checkbox;
	}

}
