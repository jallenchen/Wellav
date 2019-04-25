package com.wellav.dolphin.mmedia;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.wellav.dolphin.mmedia.adapterForAlbum.CommonAdapter_video;
import com.wellav.dolphin.mmedia.adapterForAlbum.MyBaseAdapter.OnItemClickedListener;
import com.wellav.dolphin.mmedia.entity.BasePhotoInfo;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.ui.AlertButton;
import com.wellav.dolphin.mmedia.ui.AlertButton.OnPositiveButtonClickenListener;

public class DelVideoActivity extends BaseActivity implements OnItemClickedListener, OnClickListener, OnPositiveButtonClickenListener {
	private StickyGridHeadersGridView mGridView;
	private CommonAdapter_video mAdapter;
	private ProgressBar mPb;
	public static List<PhotoInfo> mGridSrc;
	private List<PhotoInfo> mChkedSrc;
	private AlertButton mBtnDel;
	private Button mBtnCancel;
	private TextView mTxtChkedCount;
	private ToggleButton mChkObj;
	private boolean flag_ChkedChange=true;//该标志标志ToggleButton的 OnCheckedChange事件的触发方式，true表示正常点击触发
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_del_photo_or_video);
		mGridView = (StickyGridHeadersGridView) findViewById(R.id.delPhotoActivity_gridId);
		mAdapter=new CommonAdapter_video(this, mGridSrc,mGridView);
		mAdapter.setmLis(this);
		mAdapter.setmIsEditMode(true);
		mChkedSrc = new LinkedList<PhotoInfo>();
		mGridView.setAdapter(mAdapter);
		mBtnCancel = (Button) findViewById(R.id.delPhotoActivity_btnCancelId);
		mBtnDel = (AlertButton) findViewById(R.id.delPhotoActivity_btnDelId);
		mBtnCancel.setOnClickListener(this);
		mBtnDel.setmLis(this);
		mPb = (ProgressBar) findViewById(R.id.delPhotoActivity_pbId);
		mTxtChkedCount = (TextView) findViewById(R.id.delPhotoActivity_txtChkedCountId);
		mChkObj = (ToggleButton) findViewById(R.id.delPhotoActivity_ChkId);
		mChkObj.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!flag_ChkedChange) {
					flag_ChkedChange = true;
					return;
				}
				if (!isChecked) {
					mChkedSrc.clear();
					setDataSrc(false);
				} else {
					mChkedSrc.clear();
					mChkedSrc.addAll(mGridSrc);
					setDataSrc(true);
				}
				mTxtChkedCount.setText(getResources().getString(R.string.selected) + mChkedSrc.size());
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.delPhotoActivity_btnCancelId:
			setDataSrc(false);
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onPositiveClicked() {
		// TODO Auto-generated method stub
		if(mChkedSrc.size()==0)
			return;
		mPb.setVisibility(View.VISIBLE);
		
		new AsyncTask<Void,Void,Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				File file;
				for(int i=0;i<mChkedSrc.size();i++)
				{
					file=new File(mChkedSrc.get(i).getmThumbnail());
					if(file.exists())
					{
						file.delete();					
					}
					
		            getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
		            		"_data=" + "'"+file+"'", null);
		            mGridSrc.remove(mChkedSrc.get(i));
					mChkedSrc.remove(i);
				}
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				
				mAdapter.notifyDataSetChanged();
				flag_ChkedChange = false;
				mChkObj.setChecked(false);
				mPb.setVisibility(View.INVISIBLE);
			}
		}.execute(new Void[] {});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setDataSrc(false);
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setDataSrc(boolean arg) {
		for (int i = 0; i < mGridSrc.size(); i++) {
			mGridSrc.get(i).setmIsChked(arg);
		}
	}

	@Override
	public void onChkClicked(View v, BasePhotoInfo pi0) {
		// TODO Auto-generated method stub
		PhotoInfo pi=(PhotoInfo) pi0;
		if (pi.ismIsChked())
			mChkedSrc.add(pi);
		else
			mChkedSrc.remove(pi);
		boolean chked_last = mChkObj.isChecked();
		flag_ChkedChange = chked_last == (mChkedSrc.size() == mGridSrc.size());
		mChkObj.setChecked(mChkedSrc.size() == mGridSrc.size());

		mTxtChkedCount.setText(getResources().getString(R.string.selected) + mChkedSrc.size());
	}

	@Override
	public void onImgClicked(View v, BasePhotoInfo pi) {
		// TODO Auto-generated method stub

	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mGridSrc=null;
		super.onDestroy();
	}
}
