package com.wellav.dolphin.mmedia;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.adapter.AdvanceAdapter;
import com.wellav.dolphin.mmedia.adapter.AdvanceAdapter.OnToggleClickedListener;
import com.wellav.dolphin.mmedia.commands.DolphinImMime;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.DataUtil;
import com.wellav.dolphin.mmedia.utils.JsonUtil;

public class RemoteAdvanceActivity extends BaseActivity implements OnClickListener,OnToggleClickedListener {

	private static final int To_VoiceLanage = 1;
	private static final int To_Stanby = 2;
	private static final int To_News = 3;
	private static final int To_AgentContact = 5;
	private ListView listview;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private CircleImageView mDeviceIcon;
	private TextView mDeviceName;
	private String deviceID;
	private String familyID;
	private String name;
	private HashMap<String, Object> map_voice;
	private HashMap<String, Object> map_language;
	private	AdvanceAdapter advanceAdapter = null;
   	private FamilyInfo mDeviceInfo;
   	private	SharedPreferences setting_sharedPreference;   	
   	private	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
   	private	String lang_default;
   	private LoadUserAvatarFromLocal task;
   	
	protected int voice_reminder_lang_sp;
	
	private boolean isPhotoMsg = true;
	
	Boolean voiceLanguage_flag = false;
	Boolean is_photo_info_flag = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_advance);
		
		listview=(ListView) findViewById(R.id.list_advance);		
		
		final Bundle bundle2=getIntent().getExtras();
		deviceID=bundle2.getString("deviceID");
		familyID=bundle2.getString("familyID");
		mDeviceInfo  = SQLiteManager.getInstance().getFamilyInfoFamilyID(familyID);
		
		//titlebar2
		mActionbarName = (TextView)findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView)findViewById(R.id.actionbar_prev);
		mDeviceIcon = (CircleImageView) findViewById(R.id.device_icon);
		mDeviceName = (TextView)findViewById(R.id.device_name);
		mActionbarPrev.setOnClickListener(this);
		mActionbarName.setText(R.string.senior_setting);
		
		task = new LoadUserAvatarFromLocal();
		Bitmap  head = task.loadImage(deviceID);
		if(head != null){
			mDeviceIcon.setImageBitmap(head);
		}else{
			mDeviceIcon.setImageResource(R.drawable.ic_defaulthead_home_48dp);
		}
		String dname  = TextUtils.isEmpty(mDeviceInfo.getNote())? mDeviceInfo.getNickName() :mDeviceInfo.getNote();
		mDeviceName.setText(dname);
		setting_sharedPreference = getSharedPreferences(SysConfig.userid+"_"+deviceID, MODE_PRIVATE);
		
		//紧急联系人
		String contactUid = SQLiteManager.getInstance().getAgentContactIDByDeviceUserID(mDeviceInfo.getDeviceUserID());
		if(contactUid.equals("0")){
			name = getString(R.string.no_setting);
		}else{
			FamilyUserInfo info = SQLiteManager.getInstance().geFamilyUserInfoUserId(contactUid);
			if(info.getNoteName().equals("")){
				name = info.getNickName();
			}else{
				name = info.getNoteName();
			}
		}
		isPhotoMsg = setting_sharedPreference.getBoolean("is_photo_info",true);
		// 语音提醒 
		voice_reminder_lang_sp = setting_sharedPreference.getInt("voice_reminder_lang_sp",0);
		lang_default = DataUtil.language_str[voice_reminder_lang_sp];
		
		initData();
        
        advanceAdapter = new AdvanceAdapter(this,list);     
	    advanceAdapter.setmLis(this);
        listview.setAdapter(advanceAdapter);
	    listview.setOnItemClickListener (new OnItemClickListener(){
			// 第position项被单击时激发该方法。
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id){
				Intent intent = null;
				switch(position){
				case To_VoiceLanage:
					//语音提醒，弹出对话框
					showDialog();
					break;
				case To_Stanby:	
					//待机设置，弹出新Activity
					intent = new Intent(RemoteAdvanceActivity.this,RemoteStandbySettingAcitvity.class);
					intent.putExtras(bundle2);
					startActivity(intent);
					break;
				case To_News:
					//资讯设置，新Activity
					intent= new Intent(RemoteAdvanceActivity.this,RemoteNewsSettingActivity.class);
					intent.putExtras(bundle2);
					startActivity(intent);
					break;
				case To_AgentContact:
					//紧急联系人，新Activity
					intent= new Intent(RemoteAdvanceActivity.this,RemoteContactActivity.class);
					intent.putExtras(bundle2);
					startActivityForResult(intent, 5);
					break;
				}
			}
		});
	}
	
	private void initData(){
		   HashMap<String, Object> map;
		   list.clear();
			//第一行 sd卡存储
		    map = new HashMap<String, Object>();      
		    map.put("title",getResources().getText(R.string.sd_data)); 
		    map.put("content", getResources().getString(R.string.SD_unused) +getSDAvailableSize()+ "  " + getResources().getString(R.string.SD_all)+getSDTotalSize());
		    list.add(map);    
		    
		    
		    //第2行 语音提醒
		    map_voice = new HashMap<String, Object>();    
		    map_voice.put("title",getResources().getText(R.string.voice_remind)); 
		    map_voice.put("voice_reminder_lang", lang_default);
		    list.add(map_voice);
		    
		      
		    //第3行 待机模式设置
		    map = new HashMap<String, Object>();   
		    map.put("title",getResources().getText(R.string.standby_setting));
		    list.add(map);
		   
		    //第4行 资讯设置
		    map = new HashMap<String, Object>();    
		    map.put("title",getResources().getText(R.string.info_setting));
		    list.add(map);
		      
		    //第5行 相册信息栏
		    map = new HashMap<String, Object>();
		    map.put("title",getResources().getText(R.string.photo_display));
		    map.put("isCheck",isPhotoMsg);
		    
		    list.add(map);
		    
		    //第6行  紧急联系人
		    map = new HashMap<String, Object>();      
		    map.put("title",getResources().getText(R.string.most_care)); 
		    map.put("content", name);
		    list.add(map);    
		    
		    //第7行 时间与日期
		    map = new HashMap<String, Object>();	    
		    map.put("title",getResources().getText(R.string.date_time));
		    map.put("content",getTime()+ getResources().getString(R.string.auto_update));
	        list.add(map);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int arg1, Intent data) {
		if(data != null){
			switch(requestCode){
			case 5:
				String agaentChangedFlag = data.getStringExtra("agaentChangedFlag");
				if(agaentChangedFlag.equals("agaent_no_changed")){
					return;
				}else{
					name = data.getStringExtra("name");
					initData();
					advanceAdapter.notifyDataSetChanged();
				}
				break;
			}
		}
		super.onActivityResult(requestCode, arg1, data);
	}



	/**
	 * 获得SD卡总大小
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private String getSDTotalSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(this, blockSize * totalBlocks);
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private String getSDAvailableSize() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(this, blockSize * availableBlocks);
	}
	
    private String getTime(){	
	    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");     
	    String date = sDateFormat.format(new java.util.Date(System.currentTimeMillis()));  	   
	    return date;
    }
    
	/*
	 * 选择语音提醒的语言对话框
	 */
    private void showDialog(){
    	
	   AlertDialog.Builder builder = new AlertDialog.Builder(RemoteAdvanceActivity.this);
       builder.setIcon(R.drawable.ic_launcher);
       builder.setTitle(getResources().getString(R.string.select_language));
       builder.setSingleChoiceItems(DataUtil.language_str, voice_reminder_lang_sp, new DialogInterface.OnClickListener(){
           @Override
           public void onClick(DialogInterface dialog, int which){
            	   
        	   if(voice_reminder_lang_sp != which){
        		   voiceLanguage_flag = true;
        	   }
               //放入map，用来listview的显示
        	   map_voice.put("voice_reminder_lang", DataUtil.language_str[which]);
        	   voice_reminder_lang_sp = which;
           }
       });
       builder.setPositiveButton(getResources().getString(R.string.conform), new DialogInterface.OnClickListener(){
           @Override
           public void onClick(DialogInterface dialog, int which){
        	   setting_sharedPreference.edit().putInt("voice_reminder_lang_sp", voice_reminder_lang_sp).commit();
        	   
        	// 发送语音提醒
//		      	  DolphinApp.getInstance().makeSendIm(deviceID, 
//		      			   DolphinImMime.DplMime_settingResult_json,
//		      			   JsonUtil.getVoiceJsonObject(voice_reminder_lang_sp));
		      	  
        	   String lang = (String)map_voice.get("voice_reminder_lang");
        	   // 调用ChangeItem方法，更新显示
        	   ChangeItem change = new ChangeItem(advanceAdapter,map_voice);
		       change.execute("language",lang);
           }
       });
       builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
           @Override
           public void onClick(DialogInterface dialog, int which){              
           }
       });
       builder.show();
   }
    
   /**
    * ChangeItem 方法
    * @author Administrator
    */
   class ChangeItem extends AsyncTask<String,Integer,String>{
	   	private BaseAdapter adpter;
	   	private HashMap<String,Object> map;
	   	protected ChangeItem(BaseAdapter adpter,HashMap<String,Object> map){
	   		this.adpter=adpter;
	   		this.map = map;
	   	}
         @Override
         protected String doInBackground(String... params) {
        	map.put(params[0], params[1]);   	     
            return null;    
         }
        @Override
         protected void onPostExecute(String result) {
            super.onPostExecute(result);
            adpter.notifyDataSetChanged();
            //执行完毕，更新UI
        }
    }
   
   @Override
   public void onClick(View v) {
	   switch (v.getId()) {
		case R.id.actionbar_prev:
			finish(); 			
			break;
		default:
			break;
		}
   }

	@Override
	public void onToggleClicked(boolean isChk,int msg) {
		setting_sharedPreference.edit().putBoolean("is_photo_info", isChk).commit();
		
//		DolphinApp.getInstance().makeSendIm(deviceID, 
//		         DolphinImMime.DplMime_settingResult_json,
//		         JsonUtil.getAlbumInfoJsonObject(isChk));
	}
}

