package com.wellav.dolphin.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.config.MsgKey;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.ui.CircleImageView;
import com.wellav.dolphin.utils.Util;


/**
 *
 */
public class RequestContactAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<FamilyUserInfo> infos;
	private FamilyUserInfo info;
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;
	private LoadUserAvatarFromLocal mHead;
	  /** 
     * 根据拼音来排列ListView里面的数据类 
     */  

	public RequestContactAdapter(Context context, List<FamilyUserInfo> infos) {
		// TODO Auto-generated constructor stub
		inflater = LayoutInflater.from(context);
		mHead = new LoadUserAvatarFromLocal();
		this.infos = infos;
		listalpha();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.infos.size();
	}

	@Override
	public FamilyUserInfo getItem(int position) {
		// TODO Auto-generated method stub
		return infos.get(position);
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	public void refresh(List<FamilyUserInfo>  list) {
		this.infos = list;
		listalpha();
		notifyDataSetChanged();
	}
	

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		info = infos.get(position);
		
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.request_contact_item, null);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);

			viewHolder.manager = (ImageView) convertView
					.findViewById(R.id.manager);
			viewHolder.type = (ImageView) convertView.findViewById(R.id.type);
			viewHolder.title_layout = (LinearLayout) convertView
					.findViewById(R.id.title_layout);
			viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
			viewHolder.head = (CircleImageView) convertView
					.findViewById(R.id.headicon);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		
		if(info.getNoteName().equals("")){
			viewHolder.name.setText(info.getNickName());
		}else{
			viewHolder.name.setText(info.getNoteName());
		}
		Bitmap icon = mHead.loadImage(info.getLoginName());
		if (icon != null) {
			viewHolder.head.setImageBitmap(icon);
		} else {
			viewHolder.head
					.setImageResource(R.drawable.defaulthead_dolphin_48dp);
		}
		if (info.getDeviceType().equals(MsgKey.DEVICE)) {
			viewHolder.type.setImageResource(R.drawable.type_dolphin_blue);
		} else {
			viewHolder.type.setImageResource(R.drawable.type_phone_blue);
		}
		

		if (Util.getManger(info.getAuthority()) == true) {
			viewHolder.manager.setVisibility(View.VISIBLE);
		} else {
			viewHolder.manager.setVisibility(View.INVISIBLE);
		}
		if (position > 1) {
			String currentStr = "";
			currentStr = getAlpha(info.getFirstChar());
			String previewStr = "";

			if (position > 0) {
				FamilyUserInfo infoP = infos.get(position - 1);
				previewStr = getAlpha(infoP.getFirstChar());
			}
			if (!previewStr.equals(currentStr) && !currentStr.equals("#")
					|| position == 2) {
				viewHolder.title_layout.setVisibility(View.VISIBLE);
				viewHolder.title.setText(currentStr);
			} else {
				viewHolder.title_layout.setVisibility(View.GONE);
			}
		} else {
			viewHolder.title_layout.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	@SuppressLint("DefaultLocale") private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	private void listalpha() {
		alphaIndexer = new HashMap<String, Integer>();
		sections = new String[infos.size()];

		for (int i = 2; i < infos.size(); i++) {
			FamilyUserInfo info = infos.get(i);
			String currentStr = "";
			currentStr = getAlpha(info.getFirstChar());

			String previewStr = " ";
			if (i > 2) {
				FamilyUserInfo info1 = infos.get(i - 1);
				previewStr = getAlpha(info1.getFirstChar());
			}
			if (!previewStr.equals(currentStr)) {
				String name = currentStr;
				alphaIndexer.put(name, i);
				sections[i] = name;
			}
		}
	}
	
	/**
	 * Listview控件监听
	 * @author jallen
	 *
	 */

	private class ViewHolder {
		private TextView name;
		private TextView title;
		private ImageView type;
		private ImageView manager;
		private CircleImageView head;
		private LinearLayout title_layout;
	}

}
