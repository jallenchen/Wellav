package com.wellav.dolphin.mmedia.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.BitmapUtils;


public class CheckedImgAdapter extends BaseAdapter {
	private Context ct;
	private List<FamilyUserInfo> data;
	private LoadUserAvatarFromLocal userAvatar;


	public CheckedImgAdapter(Context ct, List<FamilyUserInfo> data) {
		this.ct = ct;
		this.data = data;
		userAvatar = new LoadUserAvatarFromLocal();
	}



	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FamilyUserInfo info = data.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(ct).inflate(
					R.layout.checked_contact_item, null);

			viewHolder = new ViewHolder();
			viewHolder.headIv = (CircleImageView) convertView
					.findViewById(R.id.contactitem_head);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Bitmap bitmap = userAvatar.loadImage(info.getLoginName());
		if (bitmap != null){
			viewHolder.headIv.setImageBitmap(bitmap);
		}else{
			viewHolder.headIv.setImageResource(R.drawable.ic_defaulthead_home_40dp);
		}
		return convertView;
	}

	static class ViewHolder {
		String id;
		CircleImageView headIv;
	}
}