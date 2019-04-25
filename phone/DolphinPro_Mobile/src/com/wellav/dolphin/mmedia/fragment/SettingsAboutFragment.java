package com.wellav.dolphin.mmedia.fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;

public class SettingsAboutFragment extends BaseFragment implements OnClickListener {
	
	private Button feedback;
	private TextView mActionbarName;
	private TextView Version;
 	private ImageView mActionbarPrev;
      @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.fragment_settings_about, container, false);
  		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
  		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
  		Version = (TextView) view.findViewById(R.id.version);
  		feedback=(Button)view.findViewById(R.id.fast_feedback);
  		feedback.setOnClickListener(this);
  		mActionbarPrev.setOnClickListener(this);
  		mActionbarName.setText(R.string.about);
  		
  		String ver = DolphinUtil.getVersionName(getActivity());
  		Version.setText(getActivity().getResources().getString(R.string.dolphin)+ver);
      	return view;
    }
      @Override
    public void onClick(View v) {
    	  switch (v.getId()) {
  		case R.id.fast_feedback:
  			SettingsFeedbackFragment fbf=SettingsFeedbackFragment.newInstance(SettingsFeedbackFragment.From_Setting);
  			FragmentManager fm=getActivity().getSupportFragmentManager();
  	        FragmentTransaction ft=fm.beginTransaction();
  	        ft.add(R.id.container, fbf);
  	        ft.commit();
  			break;
  		case R.id.actionbar_prev:
  			getActivity().getSupportFragmentManager().popBackStack(null, 0);
  			break;

  		default:
  			break;
  		}
    	
    }
}
