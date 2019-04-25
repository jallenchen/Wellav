package com.wellav.dolphin.mmedia.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.MsgFragmentActivity;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.WatchFamilyAvtivity;
import com.wellav.dolphin.mmedia.adapter.DolphinAdapter;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class DolphinFragment extends BaseFragment implements OnItemClickListener{
	private static final int To_Msg = 0;
	private ListView list;
	private List<FamilyInfo> familys = new ArrayList<FamilyInfo>();
	public DolphinAdapter adapter;
    private boolean hidden;
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_dolphin2, container, false);
		list = (ListView) view.findViewById(R.id.list);
		familys = DolphinApp.getInstance().getFamilyInfos();
		adapter = new DolphinAdapter(getActivity(), familys);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		return view;
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		CommFunc.PrintLog("DolphinFragment2", "onResume()");
		  if (!hidden) {
	           refresh();
	      }
		super.onResume();
	}

	public void refresh(){
		adapter.refresh(DolphinApp.getInstance().getFamilyInfos());
		list.setAdapter(adapter);
	}
	
	

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		  this.hidden = hidden;
	        if (!hidden) {
	            refresh();
	        }
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch(position){
		case To_Msg:
			intent = new Intent(getActivity(),MsgFragmentActivity.class);
			startActivity(intent);
			break;
		default :
			intent = new Intent(getActivity(),WatchFamilyAvtivity.class);
			intent.putExtra("FamilyPos",position-1);
			startActivity(intent);
			break;
		}
		
	}

}
