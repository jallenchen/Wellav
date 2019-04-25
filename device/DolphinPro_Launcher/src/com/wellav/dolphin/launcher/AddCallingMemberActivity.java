package com.wellav.dolphin.launcher;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.wellav.dolphin.adapter.CheckedContactAdapter;
import com.wellav.dolphin.adapter.RequestContactAdapter;
import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.application.LauncherApp;
import com.wellav.dolphin.bean.FamilyUserInfo;
import com.wellav.dolphin.calling.CallingActivity;
import com.wellav.dolphin.calling.CallingGroupChatActivity;
import com.wellav.dolphin.config.SysConfig;
import com.wellav.dolphin.utils.NameUtils;
import com.wellav.dolphin.utils.Util;

public class AddCallingMemberActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {
	private ListView contactsLv, checkedContackLv;
	private Button cancelBtn, confirmBtn;
	private List<FamilyUserInfo> list = new ArrayList<FamilyUserInfo>();
	private List<FamilyUserInfo> checkedList = new ArrayList<FamilyUserInfo>();
	private ArrayList<String> checkedName = new ArrayList<String>();
	private int MSG_UPDATELIST = 100;
	private RequestContactAdapter adapter;
	private CheckedContactAdapter checkedAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_addmember);
		initView();
		initData();
	}

	private void initView() {
		contactsLv = (ListView) this
				.findViewById(R.id.groupchat_request_contact_lv);
		checkedContackLv = (ListView) this
				.findViewById(R.id.groupchat_request_prompt_lv);
		cancelBtn = (Button) findViewById(R.id.groupchat_request_cancel_btn);
		confirmBtn = (Button) findViewById(R.id.groupchat_request_confirm_btn);

		adapter = new RequestContactAdapter(this, list);
		contactsLv.setAdapter(adapter);
		contactsLv.setOnItemClickListener(this);

		initCheckList();
		checkedAdapter = new CheckedContactAdapter(this, checkedList);
		checkedContackLv.setAdapter(checkedAdapter);

		checkedContackLv.setOnItemClickListener(this);
		cancelBtn.setOnClickListener(this);
		confirmBtn.setOnClickListener(this);
	}

	private void initCheckList() {
		if (SysConfig.getInstance().getStatus() == SysConfig.Free) {
			return;
		}
		checkedList = NameUtils.loginName2Users(this,
				NameUtils.getRemoteNames());
	}

	private void initData() {
		HBaseApp.post2WorkRunnable(new Runnable() {
			@Override
			public void run() {
				list = LauncherApp.getInstance().getDBHelper()
						.getAllFamilyUsers(AddCallingMemberActivity.this);
				// list.add(meber);
				Message msg = new Message();
				msg.what = MSG_UPDATELIST;
				handler.sendEmptyMessage(msg.what);
			}

		});
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == MSG_UPDATELIST) {
				adapter.refresh(list);
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.groupchat_request_cancel_btn:

			break;
		case R.id.groupchat_request_confirm_btn:
			
			if (SysConfig.getInstance().getStatus() == SysConfig.MutilAccept || SysConfig.getInstance().getStatus() == SysConfig.MutilCall ) {

				Intent data = new Intent();
				data.putStringArrayListExtra("invitename",checkedName);
				setResult(RESULT_OK, data);
			} else {
				CallingGroupChatActivity.start(AddCallingMemberActivity.this, checkedName);
			}
			break;
		}
		finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.equals(contactsLv)) {

			FamilyUserInfo addUserInfo = list.get(position);
			if (NameUtils.getRemoteName().contains(addUserInfo.getLoginName())
					|| checkedList.contains(addUserInfo)) {
				Util.DisplayToast(this, this.getString(R.string.been_add));
				return;
			}
			if (SysConfig.uid.contains(addUserInfo.getLoginName())) {
				Util.DisplayToast(this, this.getString(R.string.cannot_invite_self));
				return;
			}

			checkedList.add(addUserInfo);
			checkedName.add(addUserInfo.getLoginName());

		} else {
			if (position < NameUtils.remoteNamesCount()) {
				Util.DisplayToast(this, this.getString(R.string.cannot_move));
				return;
			}
			FamilyUserInfo checkUserInfo = checkedList.get(position);
			checkedList.remove(checkUserInfo);
			checkedName.remove(checkUserInfo.getLoginName());
		}
		checkedAdapter.refresh(checkedList);
	}

}
