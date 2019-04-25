package com.wellav.dolphin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.launcher.R;

public class MoreListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private int[] ivId = new int[]{R.drawable.more_biand,R.drawable.more_newcontacts,R.drawable.more_groupchat,R.drawable.more_meeting};
	private String[] items ;
	
	public MoreListAdapter(Context context) {
		// TODO Auto-generated constructor stub
		inflater = LayoutInflater.from(context);
		items = context.getResources().getStringArray(R.array.moreListTxt);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 4;
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

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.more_item, null);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.more_icon);
			viewHolder.txt = (TextView) convertView.findViewById(R.id.more_txt);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.icon.setImageResource(ivId[position]);
		viewHolder.txt.setText(items[position]);
		return convertView;
	}
	
	private class ViewHolder {
		private TextView txt;
		private ImageView icon;
	}

}
