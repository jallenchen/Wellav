package com.wellav.dolphin.mmedia.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.Rank;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;

public class RankAdapter extends BaseAdapter {

	private Context mContext;
	private List<Rank> ranks;
	LoadUserAvatarFromLocal task;
	
	public RankAdapter(Context context ,List<Rank> rank){
		this.mContext=context;
		this.ranks=rank;
		task = new LoadUserAvatarFromLocal();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ranks.size();
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.rank_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.headIcon = (CircleImageView) convertView.findViewById(R.id.rank_headicon);
            viewHolder.username=(TextView)convertView.findViewById(R.id.rank_user);
            viewHolder.rankMinute=(TextView)convertView.findViewById(R.id.rank_time);
            convertView.setTag(viewHolder);
			
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Bitmap bitmap = task.loadImage(SQLiteManager.getInstance().getLoginNameByUserId(ranks.get(position).getUserID()));
		if (bitmap!=null) {
			viewHolder.headIcon.setImageBitmap(bitmap);
		} else {
			viewHolder.headIcon.setImageResource(R.drawable.login_head);
		}		
		viewHolder.username.setText(ranks.get(position).getUserName());
		viewHolder.rankMinute.setText(ranks.get(position).getTimes()+mContext.getString(R.string.minute));
		return convertView;
	}
	 class ViewHolder {
		TextView username;
		CircleImageView headIcon;
		TextView rankMinute;
	}
}
