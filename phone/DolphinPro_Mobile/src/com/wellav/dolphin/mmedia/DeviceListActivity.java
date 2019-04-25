package com.wellav.dolphin.mmedia;

import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.wellav.dolphin.mmedia.adapter.AdapterForAlbumActivityDeviceFragment;
import com.wellav.dolphin.mmedia.adapter.AdapterForAlbumActivityDeviceFragment.OnDevItemClickedListener;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.db.SQLiteManager;
import com.wellav.dolphin.mmedia.entity.FamilyInfo;

public class DeviceListActivity extends BaseActivity implements  OnDevItemClickedListener {
	private List<FamilyInfo> mDataSrc = new LinkedList<>();
	private AdapterForAlbumActivityDeviceFragment mAdapter;
	private ListView mLv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_list);
		mLv = (ListView) findViewById(R.id.albumActivity_deviceFrag_lvId);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				mDataSrc.addAll(SQLiteManager.getInstance().getFamilyInfo());
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

			}
		}.execute(new Void[] {});

		mAdapter = new AdapterForAlbumActivityDeviceFragment(mDataSrc, this, this);
		mLv.setAdapter(mAdapter);
		findViewById(R.id.devicelistActivity_btnBackwardId).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}



	@Override
	public void onDevItemClicked(FamilyInfo familyInfo) {
		// TODO Auto-generated method stub
		SysConfig.familyIdForPic = familyInfo.getFamilyID();
		Intent intent = new Intent(DeviceListActivity.this, AlbumActivity.class);
		startActivity(intent);
	}

}
