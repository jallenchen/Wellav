package com.wellav.dolphin.mmedia.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.mmedia.ui.CircleImageView;

public class RemoteSettingAboutFragment extends BaseFragment implements OnClickListener {
	
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private TextView mDeviceName;
	private CircleImageView mDeviceIcon;
	private Button mFinish;
	private String familyID;
	private TextView SoftwareVersion;
	private TextView HardwareVersion;
	private TextView SerialNumber;
	SharedPreferences remoteshareprefences;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.remote_setting_about, container, false);
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mFinish = (Button) view.findViewById(R.id.finish);
		mDeviceIcon = (CircleImageView) view.findViewById(R.id.device_icon);
		mDeviceName = (TextView) view.findViewById(R.id.device_name);
		
		SoftwareVersion=(TextView)view.findViewById(R.id.software_version);
		HardwareVersion=(TextView)view.findViewById(R.id.hardware_version);
		SerialNumber=(TextView)view.findViewById(R.id.serial_number);
		
		mFinish.setVisibility(View.GONE);
		mActionbarPrev.setOnClickListener(this);
		mActionbarName.setText(R.string.about_dolphin);
		
		Bundle bundle=getArguments();
		familyID=bundle.getString("familyID");
		
		FamilyInfo info = SQLiteManager.getInstance().getFamilyInfoFamilyID(familyID);
		LoadUserAvatarFromLocal task = new LoadUserAvatarFromLocal();
		Bitmap  head = task.loadImage(info.getDeviceID());
		if(head !=null){
			mDeviceIcon.setImageBitmap(head);
		}else{
			mDeviceIcon.setImageResource(R.drawable.defaulthead_home_40dp);
		}
		String name = TextUtils.isEmpty(info.getNote())?info.getNickName():info.getNote();
		mDeviceName.setText(name);
		
		remoteshareprefences = getActivity().getSharedPreferences(SysConfig.userid+"_"+info.getDeviceID(), Context.MODE_PRIVATE);
		 SoftwareVersion.setText(remoteshareprefences.getString("softVersion", "V1.0.0"));
		 HardwareVersion.setText(remoteshareprefences.getString("hardVersion", "V1.0.0"));
		 SerialNumber.setText(remoteshareprefences.getString("seriaNumber", "12345678"));
		
		return view;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.actionbar_prev:
			getActivity().getSupportFragmentManager().popBackStack(null, 0); 			
			break;

		default:
			break;
	   }
		
	}
}
