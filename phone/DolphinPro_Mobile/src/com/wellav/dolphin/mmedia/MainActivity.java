package com.wellav.dolphin.mmedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.imagelru.VolleyRequestQueueManager;
import com.wellav.dolphin.mmedia.db.PreferenceUtils;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.dialog.PopMenu;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;
import com.wellav.dolphin.mmedia.entity.FamilyUserInfo;
import com.wellav.dolphin.mmedia.entity.UserInfo;
import com.wellav.dolphin.mmedia.fragment.ContactFragment;
import com.wellav.dolphin.mmedia.fragment.DiscoveryFragment;
import com.wellav.dolphin.mmedia.fragment.DolphinFragment;
import com.wellav.dolphin.mmedia.fragment.MeFragment;
import com.wellav.dolphin.mmedia.netease.LogUtil;
import com.wellav.dolphin.mmedia.netease.LogoutHelper;
import com.wellav.dolphin.mmedia.ui.MyTab;
import com.wellav.dolphin.mmedia.ui.MyTab.OnItemClickedListener;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class MainActivity extends BaseActivity implements
		OnItemClickedListener, OnClickListener, Observer {
	private static final int UPDATE_FAMILY = 0;
	private static final int UPDATE_FAMILYUSER = 1;
	private static final int UPDATE_FAMILY_AND_USER = 2;
	private static final int UPDATE_INVITE_MSG = 3;
	private static final int UPDATE_BOX_MSG = 4;
	private static final int UPDATE_ONLINE = 5;
	private static final String TAG = "MainActivity";
	private MyTab mTab;
	private Fragment[] mFragments;
	private ContactFragment mContactFragment;
	private DolphinFragment mDolpFragment;
	private DiscoveryFragment mDisFragment;
	private MeFragment mMeFragment;
	private int mCurIndex = 0;
	private ImageView mActionbarAdd;
	public RelativeLayout errorItem;
	public TextView errorText;
	public static boolean isCheckUpdate = false;
	private PopMenu popMenu;
	private List<FamilyInfo> familys = new ArrayList<FamilyInfo>();
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, List<FamilyUserInfo>> map = new HashMap<Integer, List<FamilyUserInfo>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		registerObservers(true);
		registerSystemMessageObserver(true);
		SQLiteManager.getInstance().addObserver(this);
		getWindow().setBackgroundDrawableResource(R.color.color106);
		mActionbarAdd = (ImageView) findViewById(R.id.actionbar_add);
		mTab = (MyTab) findViewById(R.id.mainActivity_tabId);
		errorItem = (RelativeLayout) findViewById(R.id.rl_error_item);
		errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
		mTab.init(this, this);
		initView();
		addPopMenu();

		update(null, UPDATE_FAMILY_AND_USER);
		update(null, UPDATE_BOX_MSG);
		DolphinApp.getInstance().setMyUserInfo(
				SQLiteManager.getInstance().getMyUserInfo());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		// super.onSaveInstanceState(outState);//将这一行注释掉，阻止activity保存fragment的状态
	}

	private void registerObservers(boolean register) {
		NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
				userStatusObserver, register);
	}

	private void initView() {
		mDolpFragment = new DolphinFragment();
		mContactFragment = new ContactFragment();
		mDisFragment = new DiscoveryFragment();
		mMeFragment = new MeFragment();

		mFragments = new Fragment[] { mDolpFragment, mContactFragment,
				mDisFragment, mMeFragment };

		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction()
				.add(R.id.mainActivity_contentId, mDolpFragment)
				.add(R.id.mainActivity_contentId, mContactFragment)
				.add(R.id.mainActivity_contentId, mDisFragment)
				.add(R.id.mainActivity_contentId, mMeFragment)
				.hide(mContactFragment).hide(mDisFragment).hide(mMeFragment)
				.show(mDolpFragment).commit();
	}

	@SuppressWarnings("serial")
	com.netease.nimlib.sdk.Observer<StatusCode> userStatusObserver = new com.netease.nimlib.sdk.Observer<StatusCode>() {

		@Override
		public void onEvent(StatusCode code) {
			LogUtil.e(TAG, "StatusCode:" + code);
			if (code.wontAutoLogin()) {
				kickOut(code);
			} else {
				if (code == StatusCode.NET_BROKEN) {
					errorItem.setVisibility(View.VISIBLE);
				} else if (code == StatusCode.UNLOGIN) {
					errorItem.setVisibility(View.VISIBLE);
				} else if (code == StatusCode.CONNECTING) {
					errorItem.setVisibility(View.VISIBLE);
				} else if (code == StatusCode.LOGINING) {
					errorItem.setVisibility(View.VISIBLE);
				} else if (code == StatusCode.LOGINED) {
					errorItem.setVisibility(View.GONE);
				} else {

				}
			}
		}
	};

	private void kickOut(StatusCode code) {
		if (code == StatusCode.PWD_ERROR) {
			LogUtil.e("Auth", "user password error");
			Toast.makeText(MainActivity.this, R.string.login_failed,
					Toast.LENGTH_SHORT).show();
		} else {
			LogUtil.i("Auth", "Kicked!");
		}
		LogoutHelper.logout(MainActivity.this, true);
	}

	// add by jallen
	private void addPopMenu() {
		popMenu = new PopMenu(MainActivity.this);
		popMenu.addItems(getResources().getStringArray(R.array.pop_menu));
		popMenu.setOnItemClickListener(popmenuItemClickListener);
		mActionbarAdd.setOnClickListener(this);
	}

	OnItemClickListener popmenuItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			System.out.println("position" + position);
			Intent intent = new Intent(MainActivity.this,
					DolphinAddFragmentActivity.class);
			intent.putExtra("ADDITEM", position);
			startActivity(intent);
			popMenu.dismiss();
		}
	};

	@Override
	public void onItemClicked(int clickItem) {

		if (mCurIndex != clickItem) {
			FragmentTransaction trx = getSupportFragmentManager()
					.beginTransaction();
			trx.hide(mFragments[mCurIndex]);
			if (!mFragments[clickItem].isAdded()) {
				trx.add(R.id.mainActivity_contentId, mFragments[clickItem]);
			}
			trx.show(mFragments[clickItem]).commit();
		}
		mCurIndex = clickItem;

	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.quit_click_again),
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				// DolphinApp.getInstance().exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		VolleyRequestQueueManager.release(null);
		registerObservers(false);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_add:
			popMenu.showAsDropDown(v);
			break;
		}
	}

	private void getFamilyIdUsers(final List<String> ids) {
		map.clear();
		if (ids.size() == 0) {
			DolphinApp.getInstance().setFamilyUserMap(map);
			return;
		}
		for (int i = 0; i < ids.size(); i++) {
			List<FamilyUserInfo> infos = SQLiteManager.getInstance()
					.geFamilyUserInfoFamilyId(ids.get(i));
			map.put(i, infos);
		}
		DolphinApp.getInstance().setFamilyUserMap(map);
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			}
			super.handleMessage(msg);
		}
	};

	private void updateFamilyInfo() {
		familys = SQLiteManager.getInstance().getFamilyInfo();
		String myUserId = PreferenceUtils.getInstance()
				.getStringSharePreferences(UserInfo._USER_ID);
		DolphinApp.getInstance().setFamilyInfos(familys);
		DolphinApp.getInstance().setMyFamilyInfos(
				SQLiteManager.getInstance().getFamilyInfoByMangerID(myUserId));
		if (mCurIndex == 0) {
			// mDolpFragment.refresh();
		}
	}

	private void updateFamilyUsers() {
		getFamilyIdUsers(SQLiteManager.getInstance().getFamilyIds());
		if (mCurIndex == 1) {
			mContactFragment.refresh();
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		Integer value = (Integer) data;
		CommFunc.PrintLog("MainActivity", "update:" + value);
		switch (value.intValue()) {
		case UPDATE_FAMILY:
			updateFamilyInfo();
			break;
		case UPDATE_FAMILYUSER:
			updateFamilyUsers();
			break;
		case UPDATE_FAMILY_AND_USER:
			updateFamilyInfo();
			updateFamilyUsers();
			break;
		case UPDATE_INVITE_MSG:
		case UPDATE_BOX_MSG:
			DolphinApp.getInstance().getMessageBox();
			break;
		case UPDATE_ONLINE:
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("serial")
	private void registerSystemMessageObserver(boolean register) {
		NIMClient.getService(SystemMessageObserver.class)
				.observeReceiveSystemMsg(
						new com.netease.nimlib.sdk.Observer<SystemMessage>() {
							@Override
							public void onEvent(SystemMessage message) {
								if (message.getType() == SystemMessageType.TeamInvite
										|| message.getType() == SystemMessageType.ApplyJoinTeam) {

								}
								Log.e(TAG, message.getType() + "");
							}
						}, register);
	}
}
