package com.wellav.dolphin.mmedia.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wellav.dolphin.mmedia.R;

public class AdvanceAdapter extends BaseAdapter{
	
	private ArrayList<HashMap<String, Object>> ls;
	Context mContext;
	LinearLayout linearLayout = null;
	LayoutInflater inflater;
	
	private final int TYPE_TEXT = 0;
	private final int TYPE_TOGGLE = 1;
	private final int VIEW_TYPE =4;
	
	Boolean is_face_trace = false;
	Boolean is_photo_info = false;
	Boolean is_watching_remind = false;
	
	public AdvanceAdapter(Context context,ArrayList<HashMap<String, Object>> list ){
		ls = list;
		mContext = context;
		inflater = LayoutInflater.from(mContext);
		
	}
	@Override
	public int getCount() {		
		return ls.size();
	}
	@Override
	public Object getItem(int position) {
		return ls.get(position);
	}
	@Override
	public long getItemId(int position) {

		return position;
	}
	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE;
	}
	@Override
	public int getItemViewType(int position) {
		int type=0;
		switch(position){
			case 0:			
			case 1:
			case 2:
			case 3:
			case 6:
			case 5:
		       type = TYPE_TEXT;
		       break;
			case 4:
				type = TYPE_TOGGLE;
				break;
		}
		return type;
	}
	
	// 监听toggleButton是否被按下的接口
	public interface OnToggleClickedListener{
		public void onToggleClicked(boolean ischk,int msg);
	}
	private OnToggleClickedListener mLis;
	
	/**
	 * 真正的给每一层的view配上视图
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	
		ViewHolder_text viewHolder_text = null;
	    ViewHolder_toggle viewHolder_toggle = null;
		int type = getItemViewType(position);
		
		if (convertView == null) {
			switch(type){
				case TYPE_TEXT:
					convertView = inflater.inflate(R.layout.item_remote_senior,parent, false);
					viewHolder_text = new ViewHolder_text();
					viewHolder_text.title = (TextView) convertView.findViewById(R.id.item_string3);
					viewHolder_text.content = (TextView) convertView.findViewById(R.id.content);
					convertView.setTag(viewHolder_text);
			    break;
				case TYPE_TOGGLE:
					convertView = inflater.inflate(R.layout.item_remote_senior_toggle,parent, false);
					viewHolder_toggle = new ViewHolder_toggle();
					viewHolder_toggle.title = (TextView) convertView.findViewById(R.id.item_av_tg_tx1);
					
					
					ToggleButton tb = (ToggleButton) convertView.findViewById(R.id.togglebutton_av);
					viewHolder_toggle.togglebutton = tb;
					 
					convertView.setTag(viewHolder_toggle);
					break;
				default:
					break;
			}
		}else{
			switch(type){
			case TYPE_TEXT :
				viewHolder_text = (ViewHolder_text) convertView.getTag();
				break;
			case TYPE_TOGGLE:
				viewHolder_toggle = (ViewHolder_toggle) convertView.getTag();
				break;
			}
		}
		switch(type){
			case TYPE_TEXT :
				switch(position){
				case 0:
					viewHolder_text.title.setText((String)ls.get(position).get("title"));
					viewHolder_text.content.setText ((String) ls.get(position).get("content"));
					break;
				case 1:
					viewHolder_text.title.setText((String)ls.get(position).get("title"));
					viewHolder_text.content.setText((String)ls.get(position).get("voice_reminder_lang"));
					break;
				case 5:
					viewHolder_text.title.setText((String)ls.get(position).get("title"));
					viewHolder_text.content.setText((String)ls.get(position).get("content"));
					break;
				case 2:
					viewHolder_text.title.setText((String)ls.get(position).get("title"));
					viewHolder_text.content.setText((String)ls.get(position).get(""));
					break;
				case 3:
					viewHolder_text.title.setText((String)ls.get(position).get("title"));
					break;
				case 6:
					viewHolder_text.title.setText(R.string.data_time);
					viewHolder_text.content.setText(R.string.auto);
					break;
				}
				break;
			case TYPE_TOGGLE:
				viewHolder_toggle.title.setText((String)ls.get(position).get("title"));
				viewHolder_toggle.togglebutton.setChecked((boolean) ls.get(position).get("isCheck"));
				viewHolder_toggle.togglebutton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ToggleButton view = (ToggleButton) v;
						if(mLis!=null){
							mLis.onToggleClicked(view.isChecked(),position);
						}
					}
				});
				
				break;
		}
		return convertView;
	}

	public class ViewHolder_text {
		TextView title;
		TextView content;
		
	}
	
	public class ViewHolder_toggle {
		TextView title;
		TextView hint;
		ToggleButton togglebutton;
    }

	public void setmLis(OnToggleClickedListener mLis) {
		this.mLis = mLis;
	}
	
}