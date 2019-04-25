package com.wellav.dolphin.mmedia.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wellav.dolphin.mmedia.R;

public class DolphinSettingAdapter extends BaseAdapter {
	
	private Context mContext;
	private SharedPreferences DolphinSettingSharedPreferences;
	
	public DolphinSettingAdapter(Context ct,SharedPreferences sharedPreferences) {
		super();
		this.mContext = ct;
		this.DolphinSettingSharedPreferences = sharedPreferences;
	}

	@Override
	public int getCount() {
		return 4;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		int type = 0;
		if (position == 3) {
			type = 1;
		}
		return type;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		MyListener myListener = null;
		myListener = new MyListener(position);

		int type = getItemViewType(position);
		if (type == 0) {
			if (convertView != null) {
				viewHolder = (ViewHolder) convertView.getTag();
			} else {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.fragmentsetting_item, null);
				viewHolder.mSwitch = (Switch) convertView
						.findViewById(R.id.setting_switch);
				viewHolder.mTxtView = (TextView) convertView
						.findViewById(R.id.itemtxt);
				viewHolder.mLayout = (RelativeLayout) convertView
						.findViewById(R.id.setting_center_layout);
				convertView.setTag(viewHolder);
			}
			switch (position) {
			case 0:
				viewHolder.mTxtView.setText(R.string.watch_notification);
				viewHolder.mSwitch.setChecked(DolphinSettingSharedPreferences.getBoolean("watch_notification", true));
				break;
			case 1:
				viewHolder.mTxtView.setText(R.string.call_notification);
				viewHolder.mSwitch.setChecked(DolphinSettingSharedPreferences.getBoolean("call_notification", true));
				break;
			case 2:
				viewHolder.mTxtView.setText(R.string.checkPhoto_notification);
				viewHolder.mSwitch.setChecked(DolphinSettingSharedPreferences.getBoolean("checkPhoto_notification", true));
				break;

			default:
				break;
			}
			viewHolder.mSwitch.setOnCheckedChangeListener(myListener);

		} else {
			if (convertView != null) {
				viewHolder = (ViewHolder) convertView.getTag();
			} else {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.fragmentsetting_item0, null);
				viewHolder = new ViewHolder();

				convertView.setTag(viewHolder);
			}
		}
		return convertView;
	}

	private class MyListener implements OnCheckedChangeListener {
		int cPosition;
		public MyListener(int cPos) {
			cPosition = cPos;
		}
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			Editor editor = DolphinSettingSharedPreferences.edit();
			switch (cPosition) {
			case 0:
				editor.putBoolean("watch_notification", isChecked);
				editor.commit();
				break;
			case 1:
				editor.putBoolean("call_notification", isChecked);
				editor.commit();
				break;
			case 2:
				editor.putBoolean("checkPhoto_notification", isChecked);
				editor.commit();
				break;

			default:
				break;
			}
		}
	}

	private static class ViewHolder {
		TextView mTxtView;
		Switch mSwitch;
		RelativeLayout mLayout;
	}
}
