package com.wellav.dolphin.mmedia.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.ui.CircleImageView;

public class DolphinInfoFragment extends BaseFragment {
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	private CircleImageView mHeadIcon;
	private TextView mNotename;
	private TextView mNickname;
	private ImageView mSex;
	private ImageView mType;
	private TextView mModifyNotename;
	private TextView mCity;
	private Button mQuit;
	private TextView mContact;
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dmemberinfo, container, false);
		
		mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		mQuit = (Button) view.findViewById(R.id.info_turn_over);
		mHeadIcon = (CircleImageView) view.findViewById(R.id.info_icon);
		mSex = (ImageView) view.findViewById(R.id.info_sex);
		mType= (ImageView) view.findViewById(R.id.info_type);
		mNotename = (TextView) view.findViewById(R.id.info_notename);
		mNickname = (TextView) view.findViewById(R.id.info_nickname);
		mModifyNotename = (TextView) view.findViewById(R.id.info_note);
		mCity = (TextView) view.findViewById(R.id.info_city);
		mContact= (TextView) view.findViewById(R.id.info_contact_content);
		
		return view;
	}
	

}
