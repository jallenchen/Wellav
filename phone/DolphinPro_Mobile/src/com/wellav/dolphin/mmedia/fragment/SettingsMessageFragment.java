package com.wellav.dolphin.mmedia.fragment;


import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.dialog.CreateDialog;
import com.wellav.dolphin.mmedia.dialog.DolphinDialogLisenter;

public  class SettingsMessageFragment extends BaseFragment implements OnClickListener {
	
	private TextView mActionbarName;
	private TextView notifyName;
	private ImageView mActionbarPrev;
	private ToggleButton switch_notify;
	private ToggleButton switch_voice;
	private Button ring;
	private String[] ringtone;
	private AlertDialog mDiadlog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	View view = inflater.inflate(R.layout.fragment_settings_msgsetting, container, false);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		notifyName= (TextView) view.findViewById(R.id.notify_name);
		ring = (Button)view.findViewById(R.id.ring_tone);
		switch_notify=(ToggleButton)view.findViewById(R.id.switch_notify);
		switch_voice=(ToggleButton)view.findViewById(R.id.switch_voice);
		switch_notify.setChecked(PreferenceUtils.getInstance().getNotifySetting());
		switch_voice.setChecked(PreferenceUtils.getInstance().getVoiceSetting());
		switch_notify.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
				PreferenceUtils.getInstance().saveNotifySetting(isChecked);			
			}       
		});
		switch_voice.setOnCheckedChangeListener(new OnCheckedChangeListener() {		
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				PreferenceUtils.getInstance().saveVoiceSetting(isChecked);
			}
		});
		ring.setOnClickListener(this);
		mActionbarPrev.setOnClickListener(this);
		mActionbarName.setText(R.string.message_alert);
		initData();
		
    	return view;
    }
    
    private void initData(){
    	HBaseApp.post2WorkRunnable(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ringtone = getRing().toArray(new String[getRing().size()]); 
			}
		});
    }
       @Override
    public void onClick(View v) {
    	   switch (v.getId()) {
   		case R.id.actionbar_prev:
   			getActivity().getSupportFragmentManager().popBackStack(null, 0);
   			break;
   		case R.id.ring_tone:
   			HBaseApp.post2WorkRunnable(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					 
					 if(!handler.hasMessages(0)){
						 handler.sendEmptyMessage(0);
					 }
					 
				}
			});
   		
   	    
   		break;
   		default:
   			break;
   		}  	
    }
       Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(getActivity() == null){
				return;
			}
			 CreateDialog ringdialog=new CreateDialog(getActivity(), ringlisetener);
			 if(mDiadlog == null){
				 mDiadlog = ringdialog.singleChoiceDialog(ringtone, 0); 
				 mDiadlog.show();
			 }else if(mDiadlog.isShowing() == false){
				 mDiadlog.show();
				 
			 }
			 removeMessages(0);
			//super.handleMessage(msg);
		}
    	   
       };
       public List<String> getRing() {
   		RingtoneManager manager = new RingtoneManager(DolphinApp.getInstance());
   		manager.setType(RingtoneManager.TYPE_NOTIFICATION);
   		Cursor cursor = manager.getCursor();
   		int num = cursor.getCount();
   		ArrayList<String> ringtoneList = new ArrayList<String>();
   		
   		for (int i = 0; i < num; i++) {
   			Ringtone ringtone = manager.getRingtone(i);
   			
   			if(ringtone != null){
   				String title = ringtone.getTitle(DolphinApp.getInstance());
   				ringtoneList.add(title);
   			}

   		}
   		cursor.close();
   		return ringtoneList;
   	}
       DolphinDialogLisenter ringlisetener =new DolphinDialogLisenter() {
		
		@Override
		public void onDialogDismiss(int item_index) {
		     
			PreferenceUtils.getInstance().saveRingtoneSetting(getRingtoneUriPath(item_index));
		 //  Toast.makeText(getActivity(), getRingtoneUriPath(item_index), Toast.LENGTH_SHORT).show();
		   notifyName.setText(ringtone[item_index]);
		}
	};
	
	public String getRingtoneUriPath(int pos){ 
	    RingtoneManager manager = new RingtoneManager(getActivity());
	    manager.setType(RingtoneManager.TYPE_NOTIFICATION);
	    manager.getCursor();	    
	    Uri uri = manager.getRingtoneUri(pos);
		String ringtoneuri=uri.toString();	
		return ringtoneuri;
	} 
}
