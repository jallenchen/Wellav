package com.wellav.dolphin.mmedia.adapter;

import java.util.List;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.adapter.CheckedImgAdapter.ViewHolder;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.BitmapUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChooseDeviceAdapter extends BaseAdapter {
	private Context mContext;
	private List<FamilyInfo> info;
	private LoadUserAvatarFromLocal userAvatar;

	public ChooseDeviceAdapter(Context ct,List<FamilyInfo> info) {
		this.mContext = ct;
		this.info = info;
		userAvatar = new LoadUserAvatarFromLocal();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return info.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.fragment_choose_item, null);

			viewHolder = new ViewHolder();
			viewHolder.headIv = (CircleImageView) convertView
					.findViewById(R.id.device_icon);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.device_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Bitmap bitmap = userAvatar.loadImage(info.get(position).getDeviceID());
		if(bitmap!=null){
			viewHolder.headIv.setImageBitmap(bitmap);
		}else{
			viewHolder.headIv.setImageResource(R.drawable.ic_defaulthead_home_40dp);
		}
		viewHolder.name.setText(info.get(position).getNickName());
//		if(info.get(position).getNote().equals("")){
//			viewHolder.name.setText(info.get(position).getNickName());
//		}else{
//			viewHolder.name.setText(info.get(position).getNote());
//		}
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView name;
		CircleImageView headIv;
	}
}
