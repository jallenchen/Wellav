package com.wellav.dolphin.mmedia;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.adapter.RemoteContactAdapter;
import com.wellav.dolphin.mmedia.adapter.RemoteContactAdapter.OnClickedListener;
import com.wellav.dolphin.mmedia.commands.DolphinImMime;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;
import com.wellav.dolphin.mmedia.utils.JsonUtil;

public class RemoteContactActivity extends BaseActivity implements OnClickListener,OnClickedListener {

	private String userid ;
	private TextView mDeviceName;
	private CircleImageView mDeviceIcon;
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private Button mFinish;
	private String familyID;
	private String deviceID;
	private ListView mList;
	private List<FamilyUserInfo> memberlist;
	private RemoteContactAdapter adapter;
	private FamilyInfo info;
	private FamilyUserInfo familyUserInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_setting_contact);
		
		Bundle bundle1=getIntent().getExtras();
		familyID=bundle1.getString("familyID");
		deviceID=bundle1.getString("deviceID");
		memberlist=SQLiteManager.getInstance().getFamilyUserInfoByFamilyIDNotDevice(familyID);
		mList = (ListView)findViewById(R.id.my_contacts);
		
		mActionbarName = (TextView)findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView)findViewById(R.id.actionbar_prev);
		mDeviceIcon = (CircleImageView) findViewById(R.id.device_icon);
		mDeviceName = (TextView) findViewById(R.id.device_name);
		mFinish = (Button)findViewById(R.id.finish);
		mFinish.setVisibility(View.VISIBLE);
		mFinish.setOnClickListener(this);
		mActionbarPrev.setOnClickListener(this);
		mActionbarName.setText(R.string.remote_contacts);
		
		info = SQLiteManager.getInstance().getFamilyInfoDeviceID(deviceID);
		LoadUserAvatarFromLocal task = new LoadUserAvatarFromLocal();
		Bitmap  head = task.loadImage(deviceID);
		if(head !=null){
			mDeviceIcon.setImageBitmap(head);
		}else{
			mDeviceIcon.setImageResource(R.drawable.defaulthead_home_40dp);
		}
		String name = TextUtils.isEmpty(info.getNote())?info.getNickName():info.getNote();
		mDeviceName.setText(name);
		
		//获取已经设置过的信息
		userid = SQLiteManager.getInstance().getAgentContactIDByDeviceUserID(info.getDeviceUserID());
		
		adapter=new RemoteContactAdapter(RemoteContactActivity.this, memberlist,userid);
		adapter.setmLis(this);
		mList.setAdapter(adapter);
		mList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
	
	private void setAgentContact(FamilyUserInfo familyUserInfo){
		if(familyUserInfo==null){
			Intent in= new Intent();
	     	in.putExtra("agaentChangedFlag", "agaent_no_changed");
	     	setResult(Activity.RESULT_OK, in);
			finish(); 
		}else{
			String name = TextUtils.isEmpty(familyUserInfo.getNoteName())?familyUserInfo.getNickName():familyUserInfo.getNoteName();
//			DolphinApp.getInstance().makeSendIm(deviceID, 
//				         DolphinImMime.DplMime_settingResult_json,
//				         JsonUtil.getMostcareJsonObject(name,familyUserInfo.getUserID()));
	     	SQLiteManager.getInstance().updateFamilyInfo(info.getDeviceUserID(), FamilyInfo._FAMILY_AGENTCONTACT_ID, familyUserInfo.getUserID(), false);
	     	Intent in= new Intent();
	     	in.putExtra("agaentChangedFlag", "agaent_changed");
	     	in.putExtra("name", name);
	     	setResult(Activity.RESULT_OK, in);
	     	finish(); 
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_prev:
			finish(); 			
			break;
		case R.id.finish:
	     	setAgentContact(familyUserInfo);
			break;
		default:
			break;
	   }
	}

	@Override
	public void onClicked(FamilyUserInfo info) {
		familyUserInfo = info;
	}
}
