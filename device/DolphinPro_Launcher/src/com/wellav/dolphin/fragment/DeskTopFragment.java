package com.wellav.dolphin.fragment;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.readystatesoftware.viewbadger.BadgeView;
import com.wellav.dolphin.adapter.AdapterForHomeContacts;
import com.wellav.dolphin.adapter.AdapterForHomeContacts.OnItemClickListener;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.FamilyInfo;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.calling.CallingActivity;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.db.ObserverHandler;
import com.wellav.dolphin.db.ObserverHandler.onDataChangeListener;
import com.wellav.dolphin.interfaces.IProviderContactMetaData.ContactTableMetaData;
import com.wellav.dolphin.launcher.MainActivity;
import com.wellav.dolphin.launcher.MessageActivity;
import com.wellav.dolphin.launcher.R;
import com.wellav.dolphin.launcher.VoipCallActivity;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal;
import com.wellav.dolphin.net.LoadUserAvatarFromLocal.userAvatarDataCallBack;
import com.wellav.dolphin.netease.avchat.AVChatAction;
import com.wellav.dolphin.ui.CircleImageView;
import com.wellav.dolphin.ui.GridSpacingItemDecoration;
import com.wellav.dolphin.utils.Util;

public class DeskTopFragment extends BaseFragment implements OnClickListener,
		OnItemClickListener, onDataChangeListener {

	private static final String TAG = "DeskTopFragment";
	private CircleImageView mMyHead;
	private TextView mMyName;
	private TextView mNote;
	private ImageView MsgBox;
	private ImageView AudioAss;
	private ImageView MoreFunc;
	private BadgeView msgBadge;
	private RecyclerView recyclerView;
	private AdapterForHomeContacts adapter;
	private ObserverHandler mObserverHandler;
	private List<FamilyUserInfo> mUsers;
	private static FamilyUserInfo myHome;
	private LoadUserAvatarFromLocal task;
	private String mDailName;
	private DeskTopFragment ctx;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		this.ctx = this;
		View view = inflater.inflate(R.layout.activity_launcher, container,
				false);
		mMyHead = (CircleImageView) view.findViewById(R.id.home_myhead_iv);
		mMyName = (TextView) view.findViewById(R.id.home_myname_tv);
		mNote = (TextView) view.findViewById(R.id.note);
		MsgBox = (ImageView) view.findViewById(R.id.home_mymsg_iv);
		AudioAss = (ImageView) view.findViewById(R.id.home_myvoice_iv);
		MoreFunc = (ImageView) view.findViewById(R.id.home_more_iv);
		recyclerView = (RecyclerView) view.findViewById(R.id.home_recyclerview);
		int spacingInPixels = getResources().getDimensionPixelSize(
				R.dimen.spacing);
		recyclerView.addItemDecoration(new GridSpacingItemDecoration(4,
				spacingInPixels, false));
		BadgeView badge = new BadgeView(getActivity(), mMyHead);
		badge.setHeight(getActivity().getResources().getDimensionPixelSize(
				R.dimen.ass_height));
		badge.setWidth(getActivity().getResources().getDimensionPixelSize(
				R.dimen.ass_width));
		badge.setBadgeMargin(0);
		badge.setBackgroundResource(R.drawable.assistantlable);
		badge.setBadgePosition(BadgeView.POSITION_BOTTOM_LEFT);
		badge.show();

		msgBadge = new BadgeView(getActivity(), MsgBox);
		msgBadge.setHeight(getActivity().getResources().getDimensionPixelSize(
				R.dimen.msgRed_height));
		msgBadge.setWidth(getActivity().getResources().getDimensionPixelSize(
				R.dimen.msgRed_width));
		msgBadge.setBadgeMargin(1);
		msgBadge.setBackgroundResource(R.drawable.remind_redpoint_5dp);
		msgBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);

		mObserverHandler = new ObserverHandler(
				ContactTableMetaData.CONTENT_URI, this);
		mObserverHandler.registerObserver();//DB监听
		mUsers = LauncherApp.getInstance().getDBHelper()
				.getAllFamilyUsersOderByType(getActivity());
		adapter = new AdapterForHomeContacts(getActivity(), mUsers, this);
		recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4,
				GridLayoutManager.VERTICAL, false));
		recyclerView.setAdapter(adapter);

		mMyHead.setOnClickListener(this);
		MsgBox.setOnClickListener(this);
		AudioAss.setOnClickListener(this);
		MoreFunc.setOnClickListener(this);

		if (SysConfig.getInstance().ismLoginOK() == false) {
		}
		FamilyInfo family = LauncherApp.getInstance().getDBHelper()
				.getFamilyInfo();
		LauncherApp.getInstance().setFamily(family);
		setMyView(family);
		Util.PrintLog(TAG, "onCreateView:");
		return view;
	}

	private void newMsgView() {
		boolean isNew = SysConfig.getInstance().isNewMsg();
		if (isNew) {
			msgBadge.show();
		} else {
			msgBadge.hide();
		}
		Util.PrintLog(TAG, "isShown:" + msgBadge.isShown());
	}

	private void netWorkUnable() {
		if (MainActivity.ismIsConnectted() == false) {
			mNote.setVisibility(View.VISIBLE);
			mNote.setText(R.string.no_network);
		} else {
			mNote.setVisibility(View.INVISIBLE);
		}
		if (SysConfig.getInstance().ismLoginOK() == false) {
			mNote.setVisibility(View.VISIBLE);
			mNote.setText(R.string.no_RTCnetwork);
		} else {
			mNote.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		LocalBroadcastManager broadcastManager = LocalBroadcastManager
				.getInstance(getActivity());
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SysConfig.ACTION_BR_MSG_NEW);
		intentFilter.addAction(SysConfig.ACTION_BR_NET_UNOUTABLE);
		intentFilter.addAction(SysConfig.ACTION_BR_NET_UNOUTABLE_LOGNIN_OK);
		
		BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Util.PrintLog(TAG, intent.getAction());
				if (intent.getAction().equals(SysConfig.ACTION_BR_MSG_NEW)) {
					LauncherApp.getInstance().getBoxMessage(null);
					newMsgView();
				} else {
					if (intent.getAction().equals(SysConfig.ACTION_BR_NET_UNOUTABLE_LOGNIN_OK)) {	
					}
					netWorkUnable();
					
				}
			}
		};
		broadcastManager.registerReceiver(mItemViewListClickReceiver,
				intentFilter);
		super.onActivityCreated(savedInstanceState);
	}

	private void setMyView(FamilyInfo info) {
		if (info != null) {
			task = new LoadUserAvatarFromLocal();
			Bitmap head = task.loadImage(info.getDeviceID());
			if (head != null) {
				mMyHead.setImageBitmap(head);
			} else {
				task.getData(info.getDeviceUserID(),
						new userAvatarDataCallBack() {
							@Override
							public void onDataCallBack(int code, Bitmap avatar) {
								mMyHead.setImageBitmap(avatar);
							}
						});
			}

			mMyName.setText(info.getNickName());
		} else {
			mMyHead.setBackgroundResource(R.drawable.defaulthead_dolphin_48dp);
			// mMyName.setText(R.string.my_home);
		}
		if(Util.isPrivacy(info.getAuthority())){
			mNote.setVisibility(View.VISIBLE);
			mNote.setText(R.string.no_watch);
		} else {
			mNote.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onResume() {
		newMsgView();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		mObserverHandler.unRegisterObserver();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_myhead_iv:
			AppMenuFragment();
			break;
		case R.id.home_mymsg_iv:

			// 跳转到消息盒页面
			Intent messageintent = new Intent(getActivity(),
					MessageActivity.class);
			startActivity(messageintent);
			SysConfig.getInstance().setNewMsg(false);
			break;
		case R.id.home_myvoice_iv:
			// 当有人观看时，把这一消息上传到服务器
			ComponentName componet = new ComponentName(
					SysConfig.MSC_ACTIVITY_ACTION,
					SysConfig.MSC_MAIN_ACTIVITY_ACTION);
			Intent intent = new Intent();
			intent.setComponent(componet);
			startActivity(intent);

			
			break;
		case R.id.home_more_iv:
			MoreFunction();
			break;

		}
	}

	private void AppMenuFragment() {
		AppsFragment Apps = new AppsFragment();
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.container, Apps);
		ft.addToBackStack(null);
		ft.commit();
	}

	private void MoreFunction() {
		MoreFunctionFragment MoreFunction = new MoreFunctionFragment();
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.container, MoreFunction);
		ft.addToBackStack(null);
		ft.commit();
	}

	@Override
	public void onItemClick(String userName) {
		Util.PrintLog(TAG, userName);
		mDailName = userName;
		new AVChatAction(getActivity(), mDailName, AVChatType.VIDEO).onClick();
	}

	@Override
	public void onChange(Uri uri) {
		Util.PrintLog(TAG, uri.toString());
		FamilyInfo family = LauncherApp.getInstance().getDBHelper()
				.getFamilyInfo();
		LauncherApp.getInstance().setFamily(family);
		setMyView(family);
		mUsers = LauncherApp.getInstance().getDBHelper()
				.getAllFamilyUsersOderByType(getActivity());
		adapter.refresh(mUsers);
	}


	// wwyue
	public void freshConStatus(int status, String params) {
		Util.PrintLog(TAG, "freshConStatus:" + params);
		String state = "0";
		String name = "";
		try {
			JSONObject object = new JSONObject(params);
			JSONArray array = object.getJSONArray("userStatusList");
			name = ((JSONObject) array.get(0)).getString("appAccountId");
			state = ((JSONObject) array.get(0)).getString("status");
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if (state.equals("1")) {
				Intent in = new Intent(getActivity(), CallingActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("CallName", mDailName);
				bundle.putInt("CallType", SysConfig.ChatCall);
				in.putExtras(bundle);
				startActivity(in);
			} else {
				
				FamilyUserInfo user = LauncherApp.getInstance().getDBHelper().getFamilyUser(getActivity(), mDailName);
				if(user.getDeviceType().equals("1")){
					Util.DisplayToast(getActivity(),
							ctx.getString(R.string.no_online));
				}else{
					Intent in = new Intent(getActivity(), VoipCallActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("UserInfo", user);
					in.putExtras(bundle);
					startActivity(in);
				}
			}
		}
	}


	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return getActivity();
	}

}
