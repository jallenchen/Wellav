package com.wellav.dolphin.mmedia;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.adapter.RankAdapter;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.MyStatistcsInfo;
import com.wellav.dolphin.mmedia.entity.Rank;
import com.wellav.dolphin.mmedia.net.LoadPhoneTimeRank;
import com.wellav.dolphin.mmedia.net.LoadPhoneTimeRank.phoneTimeDataCallBack;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal.userAvatarDataCallBack;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.ui.StatisticsView;
import com.wellav.dolphin.mmedia.utils.RequestString;

public class StatistcsFragmentActivity extends BaseActivity implements OnClickListener {
	private TextView mActionbarName;
	private ImageView mActionbarPrev; 
	private CircleImageView mIcon;
	private TextView mName;
	private StatisticsView mStatisticsView;
	private ListView ranklist;
	private Button numtimes;
	private LoadPhoneTimeRank  task;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.fragment_statistcs);
		mActionbarName = (TextView)findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView)findViewById(R.id.actionbar_prev);
		numtimes = (Button)findViewById(R.id.statistcs_round);
		ranklist=(ListView)findViewById(R.id.my_rank);
 	    mActionbarPrev.setOnClickListener(this);
		mActionbarName.setText(R.string.accompanytime);
		mName = (TextView)findViewById(R.id.statistcs_title);
		mIcon = (CircleImageView)findViewById(R.id.statistcs_home);
		mStatisticsView=(StatisticsView)findViewById(R.id.statisticsView);		
			
		
             // int tz=TimeZone.getDefault().getRawOffset()/36000;
        	 // String tzStr=tz.getDisplayName(false, TimeZone.SHORT).substring(4, 5);
        	  int timezone=TimeZone.getDefault().getRawOffset()/3600000;;
        	  String token=SysConfig.dtoken;
        	  Bundle bu=getIntent().getExtras();
        	  FamilyInfo info = (FamilyInfo) bu.getSerializable("FamilyInfo");
        	  
        	  LoadUserAvatarFromLocal avatar = new LoadUserAvatarFromLocal();
        	 Bitmap icon = avatar.loadImage(info.getDeviceID());
        	 if(icon != null){
        		 mIcon.setImageBitmap(icon);
        	 }else{
        		 avatar.getData(info.getDeviceUserID(), new userAvatarDataCallBack() {
        				
        				@Override
        				public void onDataCallBack(int code, Bitmap avatar) {
        					// TODO Auto-generated method stub
        					
        				}
        			});
        	 }
        	 mName.setText(info.getNickName());
        	 
        	 String StatPhoneRecord=RequestString.StatPhoneRecord(token, info.getFamilyID(), timezone);
        	  task = new LoadPhoneTimeRank(StatPhoneRecord);
              task.getPhoneTimeData(new phoneTimeDataCallBack() {
				
				@Override
				public void onDataCallBack(int code, MyStatistcsInfo data) {
					// TODO Auto-generated method stub
					if(code == 0){
						initData(data);	
					}
					
				}
			});
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}



	private void initData(MyStatistcsInfo data){
		 List<Rank> mranks=data.getRanks();
         RankAdapter rankAdapter=new RankAdapter(StatistcsFragmentActivity.this, mranks);
         ranklist.setAdapter(rankAdapter);
        
         ArrayList<String> mday=new ArrayList<String>(); 
         int num=data.getMyTime().size();
         int[] Ylabel= new int[num];
         int NumTime=0;
          for (int i = 0; i < num; i++) {
				int times=Integer.valueOf(data.getMyTime().get(i).getTimes());
				String date=data.getMyTime().get(i).getDay();	
				mday.add(date);
				Ylabel[i]=times;
				NumTime=NumTime+times;
			}
          numtimes.setText(String.valueOf(NumTime));
          String[]  Xlabel=mday.toArray(new String[mday.size()]);
          mStatisticsView.setXlabeldata(Xlabel);
			 mStatisticsView.setYlabeldata(Ylabel);
			 mStatisticsView.invalidate();
			 ranklist.invalidate();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.actionbar_prev:
			finish();
			break;

		default:
			break;
		}
	}	
	
}
