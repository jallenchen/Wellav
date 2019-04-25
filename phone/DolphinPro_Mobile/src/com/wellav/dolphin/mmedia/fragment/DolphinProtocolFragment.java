package com.wellav.dolphin.mmedia.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;

public class DolphinProtocolFragment extends BaseFragment implements OnClickListener{
	private TextView mClose;
	private ImageView mActionbarPrev;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragmentdolphinagree, container, false);
		mClose = (TextView) view.findViewById(R.id.close_button);
		mClose.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.close_button:
			getActivity().getSupportFragmentManager().beginTransaction().remove(DolphinProtocolFragment.this).commit();
			break;
		
		}
	}
	
	

}
