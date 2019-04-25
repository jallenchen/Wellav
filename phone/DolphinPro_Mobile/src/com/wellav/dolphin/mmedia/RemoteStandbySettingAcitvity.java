package com.wellav.dolphin.mmedia;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.adapter.StandbyAdapter;
import com.wellav.dolphin.mmedia.commands.DolphinImMime;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.DataUtil;
import com.wellav.dolphin.mmedia.utils.JsonUtil;

public class RemoteStandbySettingAcitvity extends BaseActivity implements OnClickListener {

	SharedPreferences standby_sharedPreference;
	private String[] titles;
	
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private Button mFinish;
	
	private int[] imgIds = new int[]{R.drawable.arraw_right};
	private StandbyAdapter adapter,adapter2, adapter3;
	private HashMap<String, Object> map1,map2,map3,map4,map5,map6;
	ListView lv_child1;
	ListView lv_child2;
    protected int duration_selected;
	protected int which_photo_selected;
	protected int playing_how_long_int_sp;
	protected int mode_int_sp;
	protected int interval_int_sp;
	protected int standby_time_int_sp;
	//private int durationIndex_lastTime;
	private int photoIndex_lastTime;
	protected int playing_how_long_selected;
	//private int playing_howlong_index;
	protected int playing_interval_selected;
	//private int playing_intervalIndex_lastTime;
	//private int standby_time_lastTime;
	protected int standby_time_selected;
	protected int mode_selected;
	private String deviceID;
	private TextView mDeviceName;
	private CircleImageView mDeviceIcon;
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	
	Boolean standByTime_flag = false;
	Boolean startTime_flag = false;
	Boolean intervalTime_flag = false;
	Boolean continueTime_flag = false;
	Boolean playPhotoType_flag = false;
	Boolean waitMode_flag = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.standby);
		
		titles = getResources().getStringArray(R.array.picture);
		
		mActionbarName = (TextView)findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView)findViewById(R.id.actionbar_prev);
		mFinish = (Button)findViewById(R.id.finish);
		mDeviceIcon = (CircleImageView) findViewById(R.id.device_icon);
		mDeviceName = (TextView) findViewById(R.id.device_name);
		mFinish.setVisibility(View.VISIBLE);
		mFinish.setOnClickListener(this);
		mActionbarPrev.setOnClickListener(this);
		mActionbarName.setText(R.string.standbymodelsetting);
		Bundle bundle=getIntent().getExtras();
		deviceID=bundle.getString("deviceID");
		
		standby_sharedPreference = this.getSharedPreferences(SysConfig.userid+"_"+deviceID, MODE_PRIVATE);
		initdata();
		inti_lv_child1();
		inti_lv_child2();
		inti_lv_parent();
		
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
		
    }
	
	private void initdata(){
		duration_selected=standby_sharedPreference.getInt("duration_sp", 0);//获得SharePreferences的数据，存的是数字，单位是秒
		which_photo_selected = standby_sharedPreference.getInt("which_photo_sp", 0);//0表示“我收藏的”
		playing_how_long_selected  =standby_sharedPreference.getInt("playing_how_long_sp", 0);
		playing_interval_selected =standby_sharedPreference.getInt("interval_sp", 0);
	    mode_selected = standby_sharedPreference.getInt("mode_sp", 0);
	    standby_time_selected = standby_sharedPreference.getInt("standby_time_sp",0);
	 
	    map.put("duration_sp", duration_selected);
	    map.put("which_photo_sp", which_photo_selected);
	    map.put("playing_how_long_sp", playing_how_long_selected);
	    map.put("interval_sp", playing_interval_selected);
	    map.put("mode_sp", mode_selected);
	    map.put("standby_time_sp", standby_time_selected);
	}
	
	private void inti_lv_child2() {
		lv_child2 = (ListView) findViewById(R.id.list_stangby3);
		ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		
	    map6 = new HashMap<String, Object>();      
	    map6.put("title",titles[5]); 
	    
	    list.add(map6);    
	    adapter3 = new StandbyAdapter(this,list,map,3);
	    lv_child2.setAdapter(adapter3); 
	    lv_child2.setOnItemClickListener (new OnItemClickListener(){
			// 第position项被单击时激发该方法。
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
				int position, long id){					
				Dialog_clock();					
			}					
		});
	}
	
	protected void Dialog_clock() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setIcon(android.R.drawable.ic_dialog_info);
	    builder.setTitle(getResources().getString(R.string.select_continue));  
	   
	    /**
	     * 第一个参数指定我们要显示的一组下拉单选框的数据集合
	     * 第二个参数代表索引，指定默认哪一个单选框被勾选上，0表示默认'普通话' 会被勾选上
	     * 第三个参数给每一个单选项绑定一个监听器
	     */
	     builder.setSingleChoiceItems(DataUtil.duration_str, duration_selected, new DialogInterface.OnClickListener(){
	        @Override
	        public void onClick(DialogInterface dialog, int which){
	        	
	        	if(duration_selected!=which){
	        		startTime_flag = true;
	        	}
	            
	        	map6.put("content", DataUtil.duration_str[which]);
	        	duration_selected = which;
	         }
	     });
	     builder.setPositiveButton(getResources().getString(R.string.conform), new DialogInterface.OnClickListener(){
	        @Override
	        public void onClick(DialogInterface dialog, int which){
	            String duration = (String)map6.get("content");
	            map.put("duration_sp", duration_selected);
	            
	        	//更新显示
	        	ChangeItem change=new ChangeItem(adapter3,map6);
			    change.execute("content",duration);
	        }
	    });
	    builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
	        @Override
	        public void onClick(DialogInterface dialog, int which){              
	        }
	    });
	    builder.show();
		
	}
	private void inti_lv_child1() {
		lv_child1 = (ListView) findViewById(R.id.list_stangby2);
		ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		
		  //第一行 
	      map3 = new HashMap<String, Object>();      
	      map3.put("title",titles[2]); 
	      map3.put("content", null);
	      list.add(map3);    
	      //第二行 
	      map4 = new HashMap<String, Object>();      
	      map4.put("title",titles[3]); 
	      map4.put("content", null);
	      list.add(map4);    
	      //第二行 
	      map5 = new HashMap<String, Object>();      
	      map5.put("title",titles[4]); 
	      map5.put("content", null);
	      list.add(map5);    
	      adapter2 = new StandbyAdapter(this,list,map,2);
	      lv_child1.setAdapter(adapter2);  

	      lv_child1.setOnItemClickListener (new OnItemClickListener(){
				// 第position项被单击时激发该方法。
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id){
					switch(position){
					case 0:
						//弹出对播放间隔话框
						Dialog_interval();
						break;
					case 1:
						//弹出轮播时间对话框
						Dialog_how_long();
						break;
					case 2:
						//弹出轮播照片类型对话框
						Dialog_which();
				}
			}					
		});
	}
	protected void Dialog_which() {
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setIcon(android.R.drawable.ic_dialog_info);
	       builder.setTitle(getResources().getString(R.string.select_continue_picture));
	    
	      photoIndex_lastTime= DataUtil.getIndex(DataUtil.photo_whichToPlay_int, which_photo_selected);
	      /**
	        * 设置一个单项选择下拉框
	        * 第一个参数指定我们要显示的一组下拉单选框的数据集合
	        * 第二个参数代表索引，指定默认哪一个单选框被勾选上
	        * 第三个参数给每一个单选项绑定一个监听器
	        */
	       builder.setSingleChoiceItems(DataUtil.photo_whichToPlay_str,which_photo_selected, new DialogInterface.OnClickListener(){
	           @Override
	           public void onClick(DialogInterface dialog, int which){
	               if(photoIndex_lastTime!= which){
	            	   playPhotoType_flag = true;
	               }
	        	   map5.put("content", DataUtil.photo_whichToPlay_str[which]);
	        	   which_photo_selected = which;
	        		
	           }
	       });
	       builder.setPositiveButton(getResources().getString(R.string.conform), new DialogInterface.OnClickListener(){
	           @Override
	           public void onClick(DialogInterface dialog, int which){
	        	   String which_photo = (String)map5.get("content");
	        	    map.put("which_photo_sp", which_photo_selected);
	        	   //更新显示
	        	   ChangeItem change=new ChangeItem(adapter2,map5);
			       change.execute("content",which_photo);
	           }
	       });
	       builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
	           @Override
	           public void onClick(DialogInterface dialog, int which){              
	           }
	       });
	       builder.show();
		
	}
	//轮播持续时间的dialog
	protected void Dialog_how_long() {
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setIcon(android.R.drawable.ic_dialog_info);
	       builder.setTitle(getResources().getString(R.string.select_continue_picture1));
	   
	       // 设置一个单项选择下拉框
	       /**
	        * 第一个参数指定我们要显示的一组下拉单选框的数据集合
	        * 第二个参数代表索引，指定默认哪一个单选框被勾选上
	        * 第三个参数给每一个单选项绑定一个监听器
	        */
	       builder.setSingleChoiceItems(DataUtil.playing_how_long_str, playing_how_long_selected, new DialogInterface.OnClickListener(){
			   @Override
	           public void onClick(DialogInterface dialog, int which){
				   if(playing_how_long_selected!= which){
					   continueTime_flag = true;
				   }
	               //
	        	   map4.put("content", DataUtil.playing_how_long_str[which]);
	        	   playing_how_long_selected = which;
	           }
	       });
	       builder.setPositiveButton(getResources().getString(R.string.conform), new DialogInterface.OnClickListener(){
	           @Override
	           public void onClick(DialogInterface dialog, int which){
	        	   String playing_how_long = (String)map4.get("content");
	        	   map.put("playing_how_long_sp", playing_how_long_selected);
	        	   
	        	   //更新显示
	        	   ChangeItem change=new ChangeItem(adapter2,map4);
			       change.execute("content",playing_how_long);
	           }
	       });
	       builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
	           @Override
	           public void onClick(DialogInterface dialog, int which){              
	           }
	       });
	       builder.show();
		
	}
	protected void Dialog_interval() {
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setIcon(android.R.drawable.ic_dialog_info);
	       builder.setTitle(getResources().getString(R.string.select_interval_time));
	      
	       // 把语言的类型转为int     
	       // 设置一个单项选择下拉框
	       /**
	        * 第一个参数指定我们要显示的一组下拉单选框的数据集合
	        * 第二个参数代表索引，指定默认哪一个单选框被勾选上，0表示默认'普通话' 会被勾选上
	        * 第三个参数给每一个单选项绑定一个监听器
	        */
	       builder.setSingleChoiceItems(DataUtil.playing_interval_str, playing_interval_selected, 
	    		   new DialogInterface.OnClickListener(){
	           @Override
	           public void onClick(DialogInterface dialog, int which){
	               
	        	   if(playing_interval_selected!=which){
	        		   intervalTime_flag = true;
	        	   }
	        	   map3.put("content", DataUtil.playing_interval_str[which]);
	        	   playing_interval_selected= which;
	           }
	       });
	       builder.setPositiveButton(getResources().getString(R.string.conform), new DialogInterface.OnClickListener(){
	           @Override
	           public void onClick(DialogInterface dialog, int which){
	        	   String interval = (String)map3.get("content");
	        	   map.put("interval_sp", playing_interval_selected);
	        	   
	        	   //更新显示
	        	   ChangeItem change=new ChangeItem(adapter2,map3);
			       change.execute("content",interval);
	           }
	       });
	       builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
	           @Override
	           public void onClick(DialogInterface dialog, int which){              
	           }
	       });
	       builder.show();
		
	}
	private void inti_lv_parent(){
		ListView lv_parent = (ListView) findViewById(R.id.list_stangby1);
		ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
			
			  //第一行 
		      map1 = new HashMap<String, Object>();      
		      map1.put("title",titles[0]); 
		      map1.put("img",imgIds[0]);
		      map1.put("content", null);
		      list.add(map1);    
		      //第二行 
		      map2 = new HashMap<String, Object>();      
		      map2.put("title",titles[1]); 
		      map2.put("img",imgIds[0]);
		      map2.put("content", null);
		      list.add(map2);    
		      adapter = new StandbyAdapter(this,list,map,1);
		      lv_parent.setAdapter(adapter);  

		      //设置两个子listview的初始可见状态
		      mode_int_sp = standby_sharedPreference.getInt("mode_sp", 0);
		      setChildListViewVisible(mode_int_sp);
		      
		      lv_parent.setOnItemClickListener (new OnItemClickListener(){
					// 第position项被单击时激发该方法。
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id){
						switch(position){
						case 0:
							//弹出对话框
							Dialog_time();
							break;
						case 1:
							//语音提醒，弹出对话框
							Dialog_mode();
							break;
						}
					}					
				});
	}
	protected void Dialog_mode() {
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setIcon(android.R.drawable.ic_dialog_info);
         builder.setTitle(getResources().getString(R.string.select_model));
         // final String[] mode = {"时钟", "轮播相册", "直接休屏"};
      
         // String mode_lastTime = Data.mode[index];
         // 设置一个单项选择下拉框
         /**
          * 第一个参数指定我们要显示的一组下拉单选框的数据集合
          * 第二个参数代表索引，指定默认哪一个单选框被勾选上
          * 第三个参数给每一个单选项绑定一个监听器
          */
         builder.setSingleChoiceItems(DataUtil.mode, mode_selected, new DialogInterface.OnClickListener(){
             @Override
             public void onClick(DialogInterface dialog, int which){
                 if(mode_selected != which){
                	 waitMode_flag = true;
                 }
        	     map2.put("content", DataUtil.mode[which]);
        	     mode_selected= which;
             }
         });
         builder.setPositiveButton(getResources().getString(R.string.conform), new DialogInterface.OnClickListener(){
             @Override
             public void onClick(DialogInterface dialog, int which){
        	     String mode = (String)map2.get("content");
        		   map.put("mode_sp", mode_selected);
        	     
        	     //设置两个子listview哪个可见
        	     setChildListViewVisible(mode_selected);
        	      //更新显示
        	     ChangeItem change=new ChangeItem(adapter,map2);
		         change.execute("content",mode);
             }
         });
         builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
             @Override
             public void onClick(DialogInterface dialog, int which){              
             }
         });
         builder.show();
	}
	protected void setChildListViewVisible(int which) {
		   switch(which){
    	   case 0:
    		   //时钟
    		   lv_child1.setVisibility(View.GONE);
    		   lv_child2.setVisibility(View.VISIBLE);
    		   break;
    	   case 1:
    		   lv_child1.setVisibility(View.VISIBLE);
    		   lv_child2.setVisibility(View.GONE);	        		  
    		   break;
    	   case 2:
    		   lv_child1.setVisibility(View.GONE);
    		   lv_child2.setVisibility(View.GONE);
    		   break;
    	   }	        	  
	}
	//进入待机的dialog
	private void Dialog_time() {
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  builder.setIcon(android.R.drawable.ic_dialog_info);
	     builder.setTitle(getResources().getString(R.string.select_how_long));
	     // final String[] time = {"30秒", "1分钟", "2分钟","5分钟", "10分钟", "30分钟","1小时"};
	   
	     /**
	     * 第一个参数指定我们要显示的一组下拉单选框的数据集合
	     * 第二个参数代表索引，指定默认哪一个单选框被勾选上
	     * 第三个参数给每一个单选项绑定一个监听器
	     */
	     builder.setSingleChoiceItems(DataUtil.time_str, standby_time_selected, new DialogInterface.OnClickListener(){
	         @Override
	         public void onClick(DialogInterface dialog, int which){
	        	 
	        	 if(standby_time_selected!=which){
	        		 standByTime_flag = true;
	        	 }
	        	 map1.put("content", DataUtil.time_str[which]);
	        	 standby_time_selected=which;
	         }
	     });
	     builder.setPositiveButton(getResources().getString(R.string.conform), new DialogInterface.OnClickListener(){
	         @Override
	         public void onClick(DialogInterface dialog, int which){
	        	 String time = (String)map1.get("content");
	        	 map.put("standby_time_sp", standby_time_selected);
	        	 // 更新显示
	        	 ChangeItem change=new ChangeItem(adapter,map1);
			     change.execute("content",time);
	         }
	     });
	     builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
	         @Override
	         public void onClick(DialogInterface dialog, int which){              
	         }
	     });
	     builder.show();
	 }
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
		// TODO
		case R.id.finish:
			while (standByTime_flag) {

				// 存入数据库
				Editor editor = standby_sharedPreference.edit();
				editor.putInt("standby_time_sp",standby_time_selected);
				editor.commit();
//				DolphinApp
//						.getInstance()
//						.makeSendIm(
//								deviceID,
//								DolphinImMime.DplMime_settingResult_json,
//								JsonUtil.getStandbyTimeJsonObject(standby_time_selected));
				standByTime_flag = false;
			}
			while (waitMode_flag) {
				// 存入数据库
				Editor editor = standby_sharedPreference.edit();
				editor.putInt("mode_sp", mode_selected);// 存入选择的脚标
				editor.commit();
//				DolphinApp.getInstance().makeSendIm(deviceID,
//						DolphinImMime.DplMime_settingResult_json,
//						JsonUtil.getModeseclectJsonObject(mode_selected));
				waitMode_flag = false;
			}
			while (startTime_flag) {
				// 存入数据库
				Editor editor = standby_sharedPreference.edit();
				// editor.putString("duration_sp", duration);
				editor.putInt("duration_sp",duration_selected);
				editor.commit();

//				DolphinApp
//						.getInstance()
//						.makeSendIm(
//								deviceID,
//								DolphinImMime.DplMime_settingResult_json,
//								JsonUtil.getModefirstJsonObject(duration_selected));
				startTime_flag = false;
			}
			while (intervalTime_flag) {
				// 存入数据库
				Editor editor = standby_sharedPreference.edit();
				editor.putInt(
						"interval_sp",playing_interval_selected);
				editor.commit();

//				DolphinApp
//						.getInstance()
//						.makeSendIm(
//								deviceID,
//								DolphinImMime.DplMime_settingResult_json,
//								JsonUtil.getModesecondJsonObject(
//										"intervalsTime",
//										playing_interval_selected));

				intervalTime_flag = false;
			}
			while (continueTime_flag) {
				// 存入数据库
				Editor editor = standby_sharedPreference.edit();
				editor.putInt(
						"playing_how_long_sp",playing_how_long_selected);
				editor.commit();
//				DolphinApp
//						.getInstance()
//						.makeSendIm(
//								deviceID,
//								DolphinImMime.DplMime_settingResult_json,
//								JsonUtil.getModesecondJsonObject(
//										"carouselTime",
//										playing_how_long_selected));

				continueTime_flag = false;
			}
			while (playPhotoType_flag) {

				// 存入数据库
				Editor editor = standby_sharedPreference.edit();
				editor.putInt("which_photo_sp",which_photo_selected);
				editor.commit();

//				DolphinApp
//						.getInstance()
//						.makeSendIm(
//								deviceID,
//								DolphinImMime.DplMime_settingResult_json,
//								JsonUtil.getModesecondJsonObject(
//										"which",
//										which_photo_selected));

				playPhotoType_flag = false;
			}
			finish();
			break;
		default:
			break;
		}

	}
}
