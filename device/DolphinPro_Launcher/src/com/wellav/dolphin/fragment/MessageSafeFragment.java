package com.wellav.dolphin.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.wellav.dolphin.adapter.MessageSafeAdapter;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.MessageEventType;
import com.wellav.dolphin.bean.MessageInformSafe;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.launcher.R;

/**
 * @author JingWen.Li
 */
public class MessageSafeFragment extends BaseFragment {
	private ListView list_safe;
	MessageSafeAdapter messageAdapter = null;
	private List<MessageInformSafe> messageInfoSafes;
	@SuppressLint("InflateParams") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// 创建一个view 
		View view = null;
		// 适配器
		view = inflater.inflate(R.layout.activity_message_safe, null);
		list_safe = (ListView) view.findViewById(R.id.list_message_safe);
		
		// 从数据库获取安全消息
		messageInfoSafes = LauncherApp.getInstance().getDBHelper().getAllMessageInfoSafe();
		
	    messageAdapter = new MessageSafeAdapter(getActivity(), messageInfoSafes);
	    list_safe.setAdapter(messageAdapter);
		getMsgData();
	    list_safe.setOnItemClickListener(new OnItemClickListener() {
			@SuppressLint("ShowToast") public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(messageInfoSafes.get(position).getEventType().equals(MessageEventType.EVENTTYPE_WRONG_PIC)){
					//Toast.makeText(getActivity(), "异常照片查看" + position, 500).show();
					startPhotoActivity();
				}else {
					//Toast.makeText(getActivity(), "异常视频查看" + position, 500).show();
				}
			}
		});
	    
		// 删除所有记录
		return view;
	}
	private void startPhotoActivity(){
		ComponentName componet = new ComponentName(SysConfig.MEDIA_ACTIVITY_ACTION, SysConfig.MEDIA_MAIN_ACTIVITY_ACTION);
		Intent intent = new Intent(); 
		intent.putExtra("Tab", 2);
		intent.setComponent(componet);
		startActivity(intent);
	}
	public void getMsgData(){
		// 从数据库获取普通消息
		HBaseApp.post2WorkRunnable(new Runnable() {
			@Override
			public void run() {
				messageInfoSafes = LauncherApp.getInstance().getDBHelper().getAllMessageInfoSafe();
				mHandler.sendEmptyMessage(0);
				
			}
		});
	}
	
	@SuppressLint("HandlerLeak") Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			messageAdapter.refresh(messageInfoSafes);
			super.handleMessage(msg);
		}
	};
}
