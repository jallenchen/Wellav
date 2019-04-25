package com.wellav.dolphin.mmedia.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.mmedia.AlbumActivity;
import com.wellav.dolphin.mmedia.AssistanHouseFragmentActivity;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.SecondPswEnterFragmentActivity;
import com.wellav.dolphin.mmedia.StatistcsTimeActivity;
import com.wellav.dolphin.mmedia.adapter.ChooseDeviceAdapter;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class ChooseOneDeviceFragment extends BaseFragment implements OnItemClickListener,OnClickListener{
	public static final int To_STime = 5;
	public static final int To_SecPsw = 4;
	public static final int To_Photo = 7;
	public static final int To_Assis = 6;
	public static final int To_GChat = 0;
	public static final int To_LinkDev = 3;
	public static final int Get_FamilyPos = 1;
	private ListView mlist;
	private TextView actionbarName;
	private ImageView actionbarPrev;
	private ChooseDeviceAdapter adapter;
	private boolean isLinkDevice =false;
	private int mItem;
	List<FamilyInfo> info ;
	
	  public interface IfamilyEventListener {
	        public void familyEventListener(int selectID);
	    }
	    private IfamilyEventListener mFamilyEventListener;
	    
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_choose_device, container, false);
		mlist = (ListView) view.findViewById(R.id.device_list);
		actionbarName = (TextView) view.findViewById(R.id.actionbar_name);
		actionbarPrev = (ImageView) view.findViewById(R.id.actionbar_prev);
		actionbarName.setText(R.string.choose_device);
		mItem = getArguments().getInt("Item", 0);
		
		getData();
		
		adapter = new ChooseDeviceAdapter(getActivity(),info);
		mlist.setAdapter(adapter);
		
		actionbarPrev.setOnClickListener(this);
		mlist.setOnItemClickListener(this);
		return view;
	}
	
	private void getData(){
		switch(mItem){
			case 0:
			case To_STime:
			case To_Photo:
				info = DolphinApp.getInstance().getFamilyInfos();
				break;
			default:
				info = DolphinApp.getInstance().getMyFamilyInfos();
				break;
		}
	}
	 public static ChooseOneDeviceFragment newInstance(int index) {
		 ChooseOneDeviceFragment fragment = new ChooseOneDeviceFragment();
	        Bundle args = new Bundle();
	        args.putInt("Item", index);
	        fragment.setArguments(args);
	        return fragment;
	    }
	  public void setResultListener(IfamilyEventListener listener){
		  mFamilyEventListener = listener;
	    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if(mItem == Get_FamilyPos){
			mFamilyEventListener.familyEventListener(position);
			getFragmentManager().beginTransaction().remove(ChooseOneDeviceFragment.this).commit();
			return;
		}else if(mItem == To_GChat){
			DolphinApp.getInstance().getSysConfig().setbFamilyPos(position);
			InitiateChatFragment choosefra = new InitiateChatFragment();
		  	FragmentManager fm = getActivity().getSupportFragmentManager();
		  	Bundle bundle = new Bundle(); 
			choosefra.setArguments(bundle);
			FragmentTransaction tx = fm.beginTransaction();
			tx.add(R.id.container, choosefra,"InitiateChat");
			tx.commit();	
		}else if(mItem == To_LinkDev){
			DolphinApp.getInstance().getSysConfig().setbFamilyPos(position);
			SearchContactAddFragment SearchContactAdd = new SearchContactAddFragment();
		  	FragmentManager fm2 = getActivity().getSupportFragmentManager();
		  	Bundle bundle = new Bundle(); 
			bundle.putBoolean("OnlyDevice", true);
			bundle.putString("DeviceName", info.get(position).getNickName());
			SearchContactAdd.setArguments(bundle);
			FragmentTransaction tx2 = fm2.beginTransaction();
			tx2.add(R.id.container, SearchContactAdd);
			tx2.addToBackStack(null);
			tx2.commit();
		}else if(mItem == To_SecPsw){
			Intent st=new Intent(getActivity(),SecondPswEnterFragmentActivity.class);
			st.putExtra("myFamilyPos", position);
			startActivity(st);
		}else if(mItem == To_STime){
 			Intent st=new Intent(getActivity(),StatistcsTimeActivity.class);
 			st.putExtra("familyPos", position);
			startActivity(st);
		}else if(mItem == To_Assis){
			 if(DolphinApp.getInstance().getFamilyOnlineMap().containsKey(info.get(position).getDeviceID())){
				 if(DolphinApp.getInstance().getFamilyOnlineMap().get(info.get(position).getDeviceID()) == true){
					Intent st=new Intent(getActivity(),AssistanHouseFragmentActivity.class);
			 		st.putExtra("Pos", position);
					startActivity(st);
				}else{
					 CommFunc.DisplayToast(getActivity(), 
							 getActivity().getResources().getString(R.string.dolphin_outLine_remo));
					 return;
				}
			}else{
				CommFunc.DisplayToast(getActivity(), 
						 getActivity().getResources().getString(R.string.dolphin_outLine_remo));
				 return;
			}
		}else if(mItem == To_Photo){
			SysConfig.familyIdForPic = info.get(position).getFamilyID();
			Intent intent = new Intent(getActivity(), AlbumActivity.class);
			startActivity(intent);
		}
	     
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		getActivity().finish();
	}
	
}
