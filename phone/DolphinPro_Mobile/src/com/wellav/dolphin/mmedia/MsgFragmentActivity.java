package com.wellav.dolphin.mmedia;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.adapter.MsgAdapter;
import com.wellav.dolphin.mmedia.commands.MsgKey;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.MessageInform;

public class MsgFragmentActivity extends BaseActivity implements OnClickListener,Observer, OnItemLongClickListener{
	private ListView mList;
	private MsgAdapter adapter;
	private TextView mActionbarName;
	private TextView myEmpty;
	private ImageView mActionbarPrev;
	 List<MessageInform> msgList = new ArrayList<MessageInform> ();
	// List<FamilyUserInfo> mInfos = new ArrayList<FamilyUserInfo> ();
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.fragmentmsg_layout);
		 SQLiteManager.getInstance().addObserver(this);
    	mActionbarName = (TextView) findViewById(R.id.actionbar_name);
    	myEmpty = (TextView) findViewById(R.id.myempty);
    	mActionbarPrev = (ImageView) findViewById(R.id.actionbar_prev);
		mList = (ListView) findViewById(R.id.msg_list);
		mList.setOnItemLongClickListener(this); 
		setAdapter();
		getInviteMsg();
		PreferenceUtils.getInstance().saveBooleanSharePreferences(MsgKey.newBoxMsg, false);
		mActionbarName.setText(R.string.msg);
		mActionbarPrev.setOnClickListener(this);
		//registerReceiver(receiver, new IntentFilter(CallingActivity.BROADCAST_CALLING_ACTION));
	}
	
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//unregisterReceiver(receiver);
		super.onDestroy();
	}



	private void getInviteMsg(){
		HBaseApp.post2WorkRunnable(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				msgList = SQLiteManager.getInstance().getAllBoxMessageInfo();
					handler.sendEmptyMessage(0);
			}
		});
		
	}
	
	/**
     * 
     */
//	private BroadcastReceiver receiver = new BroadcastReceiver() {
//		@SuppressLint("NewApi")
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent.getAction().equals(CallingActivity.BROADCAST_CALLING_ACTION)) {
//
//                Bundle bundle = intent.getExtras();
//                if (bundle != null) {
//                    Message msg = new Message();
//                    msg.what = bundle.getInt("what", 0);
//                    int msgtype = bundle.getInt("arg2",
//                            MsgKey.grpv_listener_onResponse); //
//                    
//                    if (msgtype == MsgKey.grpv_listener_onRequest) {
//                    } else if (msgtype == MsgKey.grpv_listener_onResponse) {
//                       BroadMsg_OnResponse(msg.what, bundle.getString("arg1"));
//                    } else if (msgtype == MsgKey.broadmsg_sip) {
//                    } else {
//                    }
//
//                }
//			}
//		}
//	};
//	  private void BroadMsg_OnResponse(int action,String params) {
//		  switch(action) {
//		    case RtcConst.groupcall_opt_join:
//                onResponse_grpJoin(params);
//                break;
//		  }
//	    }
//	     private void onResponse_grpJoin(String parameters) {
//	         try {
//	             Utils.PrintLog(5, LOGTAG, "onResponse_grpJoin:"+parameters);
//	             if(parameters == null || parameters.equals(""))
//	                 return;
//	             JSONObject jsonrsp = new JSONObject(parameters);
//	             if(jsonrsp.isNull("code")==false) {
//	                 String code = jsonrsp.getString(RtcConst.kcode);
//	                 String reason = jsonrsp.getString(RtcConst.kreason);
//	                 Utils.PrintLog(5, LOGTAG, "onResponse_grpJoin code:"+code+" reason:"+reason);
//	                 if(code.equals("0")) {
//	                	 Utils.PrintLog(5, LOGTAG, "onResponse_grpJoin:success");
//	                 }
//	                 else {
//	                	 Utils.PrintLog(5, LOGTAG, "onResponse_grpJoin:fail");
//	                	 Utils.DisplayToast(getApplication(), getResources().getString(R.string.been_end));
//	                 }
//	             }
//	         } catch (JSONException e) {
//	             // TODO Auto-generated catch block
//	             e.printStackTrace();
//	         }
//	     }
	
	@SuppressLint("HandlerLeak") Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			adapter.refresh(msgList);
			super.handleMessage(msg);
		}
		
	};
	
	private void setAdapter(){
		adapter = new MsgAdapter(MsgFragmentActivity.this,msgList);
			mList.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.actionbar_prev:
			finish();
			break;
		
		}
	}


	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		getInviteMsg();
		adapter.refresh(msgList);
	}



	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder=new Builder(MsgFragmentActivity.this);
		builder.setMessage(SysConfig.DOTHIS);
		builder.setPositiveButton(SysConfig.YES, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				SQLiteManager.getInstance().deleteBoxMessageById(msgList.get(position).getID(),false);
				msgList.remove(position);
				handler.sendEmptyMessage(0);
			}
		});
		builder.setNegativeButton(SysConfig.NO, null);
		builder.create().show();
		
		
		
		return true;
	}

}
