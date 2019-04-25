package com.wellav.dolphin.mmedia.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wellav.dolphin.mmedia.R;

public class FeedBackFragment extends BaseFragment implements OnClickListener{
	private TextView mActionbarName;
	private ImageView mActionbarPrev;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 View view = inflater.inflate(R.layout.fragmentfeedback, container, false);
			mActionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
			mActionbarName = (TextView) view.findViewById(R.id.actionbar_name);
			
			mActionbarName.setText(R.string.feed_back);
			mActionbarPrev.setOnClickListener(this);
			return view;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.actionbar_prev:
			getActivity().finish();
			break;
		
		}
	}

}
