package com.wellav.dolphin.mmedia.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.utils.DataUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StandbyAdapter extends BaseAdapter {
	
	private ArrayList<HashMap<String, Object>> ls;
	Context mContext;
	LinearLayout linearLayout = null;
	LayoutInflater inflater;
	int which;
	HashMap<String, Integer> map;

	/*
	 * @param:
	 * which:标识是哪一个listview调用的
	 */
	public StandbyAdapter(Context context,ArrayList<HashMap<String, Object>> list,
			HashMap<String, Integer> map,int which){
		ls = list;
		mContext = context;
		inflater = LayoutInflater.from(mContext);
		this.which = which;
		this.map = map;
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
		return 1;
	}
			
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
	
	    ViewHolder holder=null;
		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.item_remote_senior,parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.item_string3);
			holder.content = (TextView) convertView.findViewById(R.id.content);		
		    convertView.setTag(holder);
		}else{		
			holder = (ViewHolder) convertView.getTag();			
		}
		holder.title.setText((String)ls.get(position).get("title"));
		
	    switch(which){
		    case 1:
		    	String content1=null;
		    	if(position==0){
		    		int data = map.get("standby_time_sp");
		    		content1=DataUtil.time_str[data];
		    	}else{
		    		int index =map.get("mode_sp");
		    		content1=DataUtil.mode[index];
		    	}
		    	holder.content.setText(content1);
		    	break;
		    case 2:
		    	String content2=null;
		    	if(position==0){
		    		int data = map.get("interval_sp");
		    		content2=DataUtil.playing_interval_str[data];
		    	}else if(position==1){
		    		int data=map.get("playing_how_long_sp");
		    		content2=DataUtil.playing_how_long_str[data];
		    	}else{
		    		int data = map.get("which_photo_sp");
		    		content2=DataUtil.photo_whichToPlay_str[data];
		    	}
		    	  holder.content.setText(content2);
		    	  break;
		    case 3:
		    	
		    	int data =map.get("duration_sp");
		    	String  content3 =DataUtil.duration_str[data]; 
		    	holder.content.setText(content3);
		        break;
	     }
		return convertView;
	}
	class ViewHolder {
		TextView title;
		TextView content;
	}
}