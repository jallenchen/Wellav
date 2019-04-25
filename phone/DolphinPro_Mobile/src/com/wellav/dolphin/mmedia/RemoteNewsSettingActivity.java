package com.wellav.dolphin.mmedia;



import java.util.Calendar;
import java.util.TimeZone;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.commands.DolphinImMime;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.dialog.MyTimePicket;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.JsonUtil;

public class RemoteNewsSettingActivity extends BaseActivity implements OnClickListener{

	private SharedPreferences news_sharedPreference;
	private ToggleButton broadcast_toggle1;
	private ToggleButton broadcast_toggle2;
	private TextView broadcast_time1_setting;
	private TextView broadcast_time2_setting;
	private TextView mDeviceName;
	private CircleImageView mDeviceIcon;
	
	private MyTimePicket timePickerDialog1;
	private MyTimePicket timePickerDialog2;
	
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private Button mFinish;
	private String deviceID;
	private Editor editor;
	
	private Boolean isChecked_broadcast_toggle1;
	private Boolean isChecked_broadcast_toggle2;
	private Boolean isChecked_broadcast_toggle1_change;
	private Boolean isChecked_broadcast_toggle2_change;
	
	private Boolean ISBroadcastOne_flag = false;
	private Boolean BroadcastOneTime_flag = false;
	private Boolean ISBroadcastTwo_flag = false;
	private Boolean BroadcastTwoTime_flag = false;
	
	private int hourOfDay1;
	private int hourOfDay2;
	private int minute1;
	private int minute2;
	private String time2Txt;
	private String time1Txt;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_setting_news);
		
		Bundle bundle=getIntent().getExtras();
		deviceID=bundle.getString("deviceID");
		
		//获取已经设置过的信息
		news_sharedPreference = this.getSharedPreferences(SysConfig.userid+"_"+deviceID, MODE_PRIVATE);
		
		broadcast_toggle1 = (ToggleButton) findViewById(R.id.time1_setting);
		broadcast_toggle2 = (ToggleButton) findViewById(R.id.time2_setting);
		broadcast_time1_setting = (TextView) findViewById(R.id.broadcast1);
	    broadcast_time2_setting = (TextView) findViewById(R.id.broadcast2);
	    
	    broadcast_time1_setting.setOnClickListener(this);
	    broadcast_time2_setting.setOnClickListener(this);
	    
	    //titlebar2
	  	mActionbarName = (TextView)findViewById(R.id.actionbar_name);
	  	mActionbarPrev = (ImageView)findViewById(R.id.actionbar_prev);
	  	mDeviceIcon = (CircleImageView) findViewById(R.id.device_icon);
		mDeviceName = (TextView) findViewById(R.id.device_name);
	  	mFinish = (Button)findViewById(R.id.finish);
	  	mFinish.setVisibility(View.VISIBLE);
	  	mFinish.setOnClickListener(this);
	  	mActionbarPrev.setOnClickListener(this);
	  	mActionbarName.setText(R.string.msgsetting);
	  	
	  	FamilyInfo info = SQLiteManager.getInstance().getFamilyInfoDeviceID(deviceID);
		LoadUserAvatarFromLocal task = new LoadUserAvatarFromLocal();
		Bitmap  head = task.loadImage(deviceID);
		if(head !=null){
			mDeviceIcon.setImageBitmap(head);
		}else{
			mDeviceIcon.setImageResource(R.drawable.defaulthead_home_40dp);
		}
		String name = TextUtils.isEmpty(info.getNote())?info.getNickName():info.getNote();
		mDeviceName.setText(name);
	  	
	    //设置控件的初始状态
		operate_toggleButton();
		init_broadcastTime();
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_prev:
			finish(); 			
			break;
			
		case R.id.broadcast1:
			if(timePickerDialog1==null){
				init_timePickerDialog1();
			}
		    timePickerDialog1.show();
			break;
        
		case R.id.broadcast2:
        	if(timePickerDialog2==null){
			    init_timePickerDialog2();
			}
			timePickerDialog2.show();
			break;
		case R.id.finish:
			while(ISBroadcastOne_flag){
				editor = news_sharedPreference.edit();
	        	editor.putBoolean("broadcast_toggle1", isChecked_broadcast_toggle1_change);
	        	editor.commit();
//				DolphinApp.getInstance().makeSendIm(deviceID, 
//				         DolphinImMime.DplMime_settingResult_json,
//				         JsonUtil.getSettingTime1JsonObject("toggle", 0, 0, isChecked_broadcast_toggle1_change)); 
				ISBroadcastOne_flag = false;
			}
			while(BroadcastOneTime_flag){
				
				editor = news_sharedPreference.edit();
                // 存入sharedpreferences
                editor.putString("broadcast_time1", time1Txt);
                editor.commit();
//				DolphinApp.getInstance().makeSendIm(deviceID, 
//				         DolphinImMime.DplMime_settingResult_json,
//				         JsonUtil.getSettingTime1JsonObject("time", hourOfDay1, minute1, true));
				BroadcastOneTime_flag = false;
			}
			while(ISBroadcastTwo_flag){
				// 存入数据库
	        	editor = news_sharedPreference.edit();
	        	editor.putBoolean("broadcast_toggle2", isChecked_broadcast_toggle2_change);
	        	editor.commit();
//				DolphinApp.getInstance().makeSendIm(deviceID, 
//				         DolphinImMime.DplMime_settingResult_json,
//				         JsonUtil.getSettingTime2JsonObject("toggle", 0, 0, isChecked_broadcast_toggle2_change));
				ISBroadcastTwo_flag = false;
			}
			while(BroadcastTwoTime_flag){
				// 存入sharedpreferences
                editor = news_sharedPreference.edit();
                editor.putString("broadcast_time2", time2Txt);
                editor.commit();
//				 DolphinApp.getInstance().makeSendIm(deviceID, 
//				         DolphinImMime.DplMime_settingResult_json,
//				         JsonUtil.getSettingTime2JsonObject("time", hourOfDay2, minute2, true));
				BroadcastTwoTime_flag = false;
			}
			finish();
			break;
		default:
			break;
		}
	}
	
	private void init_broadcastTime() {
		   broadcast_time1_setting.setText(news_sharedPreference.getString("broadcast_time1", "09:00"));
		   broadcast_time2_setting.setText(news_sharedPreference.getString("broadcast_time2", "19:00"));
	}
	
	 /*
	 * 设置时间的对话框
	 */
	private void init_timePickerDialog1(){  
        TimePickerDialog.OnTimeSetListener otsl=new TimePickerDialog.OnTimeSetListener(){  
            @Override
        	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {  
                 // tv.setText("您设置了时间："+hourOfDay+"时"+minute+"分");  
                 timePickerDialog1.dismiss(); 
                 hourOfDay1 = hourOfDay;
                 minute1 = minute;
                 // Toast.makeText(NewsSetting.this, "您设置了时间："+hourOfDay+"时"+minute+"分", Toast.LENGTH_LONG).show();
                 time1Txt =  String.format("%02d:%02d", hourOfDay, minute);
                 broadcast_time1_setting.setText(time1Txt);
                 if(news_sharedPreference.getString("broadcast_time1", "09:00") != (time1Txt)){
                	 BroadcastOneTime_flag = true;
                 }
                 
            }
        };  
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());  
        hourOfDay1 = calendar.get(Calendar.HOUR_OF_DAY);  
        minute1 = calendar.get(Calendar.MINUTE);  
        timePickerDialog1 = new MyTimePicket(this,otsl,hourOfDay1,minute1,true);
    }  
	
	private void init_timePickerDialog2(){  
        TimePickerDialog.OnTimeSetListener otsl = new TimePickerDialog.OnTimeSetListener(){  
          @Override
        	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {  
        	  
                 // tv.setText("您设置了时间："+hourOfDay+"时"+minute+"分");  
                 timePickerDialog2.dismiss(); 
                 hourOfDay2 = hourOfDay;
                 minute2 = minute;
                 // Toast.makeText(NewsSetting.this, "您设置了时间："+hourOfDay+"时"+minute+"分", Toast.LENGTH_LONG).show();
                 time2Txt =  String.format("%02d:%02d", hourOfDay, minute);
                 broadcast_time2_setting.setText(time2Txt);
              
                 if(news_sharedPreference.getString("broadcast_time2", "09:00")!= (time2Txt)){
                	 BroadcastTwoTime_flag = true;
                 }
                 
            }
        };  
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());  
        hourOfDay2 = calendar.get(Calendar.HOUR_OF_DAY);  
        minute2 = calendar.get(Calendar.MINUTE);  
        timePickerDialog2=new MyTimePicket(this,otsl,hourOfDay2,minute2,true);
    }  
	
	/*
	 * 初始化togglebutton，设置监听
	 */
	private void operate_toggleButton(){
		
		//读出数据，设置初始状态
		isChecked_broadcast_toggle1 = news_sharedPreference.getBoolean("broadcast_toggle1", true);
		broadcast_toggle1.setChecked( isChecked_broadcast_toggle1);
		
		broadcast_toggle1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				ISBroadcastOne_flag = true;
				
				isChecked_broadcast_toggle1_change = isChecked;
			}
		});
		
		isChecked_broadcast_toggle2 = news_sharedPreference.getBoolean("broadcast_toggle2", true);
		broadcast_toggle2.setChecked(isChecked_broadcast_toggle2);
		
		broadcast_toggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				
				ISBroadcastTwo_flag = true;
				
				isChecked_broadcast_toggle2_change = isChecked;
				
			}
		});
	}
}
