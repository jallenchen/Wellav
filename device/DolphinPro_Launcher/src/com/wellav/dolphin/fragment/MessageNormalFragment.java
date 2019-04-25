package com.wellav.dolphin.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.wellav.dolphin.adapter.MessageAdapter;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.MessageEventType;
import com.wellav.dolphin.bean.MessageInform;
import com.wellav.dolphin.launcher.R;

/**
 * 
 * @author JingWen.Li
 *
 */
public class MessageNormalFragment extends BaseFragment {

	private ListView list_normal;
	private MessageAdapter messageAdapter = null;
	private List<MessageInform> messageInfos =new ArrayList<MessageInform>();

	@SuppressLint("InflateParams") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// 创建一个view
		View conternView = null;
		// 适配器
		conternView = inflater.inflate(R.layout.activity_message_normal, null);
		list_normal = (ListView) conternView
				.findViewById(R.id.list_message_normal);
		
		messageAdapter = new MessageAdapter(getActivity(), messageInfos);
		list_normal.setAdapter(messageAdapter);
		getMsgData();
		list_normal.setOnItemClickListener(new OnItemClickListener() { 
			@SuppressLint("ShowToast") @Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(messageInfos.get(position).getEventType().equals(MessageEventType.EVENTTYPE_CALL_YOU)){
					if(messageInfos.get(position).getBeenAnswered().equals(MessageEventType.NO_ANSWERED)){
						//Toast.makeText(getActivity(), "呼叫了您没答应" + position, 500).show();
					}
				}else if(messageInfos.get(position).getEventType().equals(MessageEventType.EVENTTYPE_INVITE_TALK)){
					if(!(messageInfos.get(position).getTalkingID().equals(""))){
						//Toast.makeText(getActivity(), "群聊没结束，可加入" + position, 500).show();
						String talkingID = messageInfos.get(position).getTalkingID();
						//LauncherApp.getInstance().JoinConf(talkingID);
					}else{
						Toast.makeText(getActivity(), R.string.group_chat_over + position, 500).show();
					}
				}else if(messageInfos.get(position).getEventType().equals(MessageEventType.EVENTTYPE_INVITE_MEETTING)){
					if(!(messageInfos.get(position).getMeettingID().equals(""))){
						String meettingID = messageInfos.get(position).getMeettingID();
						//Toast.makeText(getActivity(), "会议没结束，可加入" + position, 500).show();
						//LauncherApp.getInstance().JoinConf(meettingID);
					}else{
						Toast.makeText(getActivity(), R.string.group_meetting_over + position, 500).show();
					}
				}else if(messageInfos.get(position).getEventType().equals(MessageEventType.EVENTTYPE_POST_PIC)){
					//Toast.makeText(getActivity(), "欢迎查看我的照片" + position, 500).show();
				}
			}
		});
		return conternView;
	}
	
	public void getMsgData(){
		// 从数据库获取普通消息
		HBaseApp.post2WorkRunnable(new Runnable() {
			@Override
			public void run() {
				messageInfos = LauncherApp.getInstance().getDBHelper().getAllMessageInfo();
				mHandler.sendEmptyMessage(0);
			}
		});
	}
	
	@SuppressLint("HandlerLeak") 
	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			messageAdapter.refresh(messageInfos);
			super.handleMessage(msg);
		}
	};
}
