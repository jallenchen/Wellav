package com.wellav.dolphin.mmedia.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;

public class RemoteContactAdapter extends BaseAdapter {

	private Context mContext;
	private List<FamilyUserInfo> data;
	String contactID;
	HashMap<String, Boolean> states = new HashMap<String, Boolean>();
	public static int pos;
	LoadUserAvatarFromLocal task;
	int poscc;
	boolean res = false;
	private OnClickedListener mLis;

	// 监听toggleButton是否被按下的接口
	public interface OnClickedListener {
		public void onClicked(FamilyUserInfo info);
	}

	public void setmLis(OnClickedListener mLis) {
		this.mLis = mLis;
	}

	public RemoteContactAdapter(Context ct, List<FamilyUserInfo> userdata,
			String userid) {
		this.mContext = ct;
		this.data = userdata;
		this.contactID = userid;
		task = new LoadUserAvatarFromLocal();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		final FamilyUserInfo info = data.get(position);
		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_remote_senior_contact, null);

			viewHolder.memberIcon = (CircleImageView) convertView
					.findViewById(R.id.remotecontact_headicon);
			// viewHolder.memberBox = (RadioButton)
			// convertView.findViewById(R.id.radiobox);
			viewHolder.memberName = (TextView) convertView
					.findViewById(R.id.remote_name);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (info.getNoteName().equals("")) {
			viewHolder.memberName.setText(info.getNickName());
		} else {
			viewHolder.memberName.setText(info.getNoteName());
		}

		Bitmap bitmap = task.loadImage(info.getLoginName());
		if (bitmap != null) {
			viewHolder.memberIcon.setImageBitmap(bitmap);
		}

		final RadioButton radio = (RadioButton) convertView
				.findViewById(R.id.remote_radiobox);
		viewHolder.memberBox = radio;

		if (info.getUserID().equals(contactID)) {
			viewHolder.memberBox.setChecked(true);
		} else {
			viewHolder.memberBox.setChecked(false);
		}

		viewHolder.memberBox.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				// 重置，确保最多只有一项被选中
				for (String key : states.keySet()) {
					states.put(key, false);

				}
				mLis.onClicked(info);
				contactID = info.getUserID();
				// states.put(info.getUserID(), radio.isChecked());
				RemoteContactAdapter.this.notifyDataSetChanged();
			}
		});


		return convertView;
	}


	private class ViewHolder {
		private com.wellav.dolphin.mmedia.ui.CircleImageView memberIcon;
		private RadioButton memberBox;
		private TextView memberName;
		private String memberId;
	}
}
