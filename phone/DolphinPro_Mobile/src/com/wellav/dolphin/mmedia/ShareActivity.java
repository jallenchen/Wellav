package com.wellav.dolphin.mmedia;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.wellav.dolphin.imagelru.ImageCacheUtil;
import com.wellav.dolphin.mmedia.adapter.AdapterForShareActivity;
import com.wellav.dolphin.mmedia.adapter.AdapterForShareActivity.OnItemChkedListener;
import com.wellav.dolphin.mmedia.adapter.AdapterForShowPicGrpDialog.OnItemClickedLis;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.dialog.ShowPicGroup_Dialog;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.entity.PicGrpInfo;
import com.wellav.dolphin.mmedia.utils.MediaScanner;
import com.wellav.dolphin.mmedia.utils.NativeImageLoader;
import com.wellav.dolphin.mmedia.utils.TimeUtils;

public class ShareActivity extends BaseActivity implements OnClickListener,
		OnItemChkedListener {
	public static final int MAX_COUNT = 9;// 一次最多传4张照片
	private GridView mGridView;
	private AdapterForShareActivity mAdapter;
	private HashMap<String, List<PhotoInfo>> mAllData = new HashMap<String, List<PhotoInfo>>();// 所有數據
	private List<PhotoInfo> mGridSrc = new LinkedList<PhotoInfo>();// 顯示的待選數據
	private List<PhotoInfo> mChkedSrc = new LinkedList<PhotoInfo>();// 選中的數據
	private String mChkedPath = null;// 表示選中的目錄，null表示所有
	private MediaScanner mScanner;
	private boolean mIsScanned = false;
	private Button mBtnFilter;
	private Button mBtnFinish;
	private ImageView mActionBack;
	private DialogFragment dialog;
	private boolean mAddMore = false;
	public static int mAddedCnt= 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		getWindow().setBackgroundDrawable(null);
		SysConfig.familyIdForPic = getIntent().getStringExtra("FamilyId");
		mAddMore = getIntent().getBooleanExtra("addmore", false);
		mAddedCnt = getIntent().getIntExtra("addedcnt", 0);
		mScanner = new MediaScanner(this,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		mGridView = (GridView) findViewById(R.id.shareActivity_gridId);
		mActionBack = (ImageView) findViewById(R.id.shareActivity_btnBackwdId);

		mScanner.scanMedia(new MediaScanner.ScanCompleteCallBack() {

			@Override
			public void scanComplete(Cursor cursor) {
				File file = null;
				String name = null;
				while (cursor.moveToNext()) {
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					long times = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
					PhotoInfo mGridItem = new PhotoInfo();
					mGridItem.setmUrl(path);
					mGridItem.setmTime(TimeUtils.paserTimeToYM(times));
					mGridItem.setmThumbnail(path);
					file = new File(path);
					name = file.getParentFile().getAbsolutePath();

					if (!mAllData.containsKey(name))
						mAllData.put(name, new LinkedList<PhotoInfo>());
					mAllData.get(name).add(mGridItem);

				}
				cursor.close();
				Iterator<String> iterator = mAllData.keySet().iterator();
				while (iterator.hasNext()) {
					mGridSrc.addAll(mAllData.get(iterator.next()));
				}
				PhotoInfo pi = new PhotoInfo();
				mGridSrc.add(0, pi);
				mAdapter = new AdapterForShareActivity(mGridSrc, mChkedSrc,
						ShareActivity.this, mGridView, ShareActivity.this);
				mGridView.setAdapter(mAdapter);
				mIsScanned = true;

			}
		});
		mBtnFilter = (Button) findViewById(R.id.shareActivity_btnFilterId);
		mBtnFilter.setOnClickListener(this);
		mBtnFinish = (Button) findViewById(R.id.shareActivity_btnOkId);
		String txt = getString(R.string.finish);
		mBtnFinish.setText(txt + mAddedCnt + "/" + MAX_COUNT);
		mBtnFinish.setOnClickListener(this);
		mActionBack.setOnClickListener(this);
	}

	@SuppressLint("HandlerLeak")
	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			dialog = null;
			mAdapter.notifyDataSetChanged();
		}
	}

	static List<PhotoInfo> chkedSrc;// 選中的數據

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (!mIsScanned)
			return;
		switch (v.getId()) {
		case R.id.shareActivity_btnBackwdId:
			ImageCacheUtil.getInstance().release();
			NativeImageLoader.getInstance().release();
			finish();
			break;
		case R.id.shareActivity_btnOkId:
			if (mAddMore == true) {

				chkedSrc = mAdapter.getChkedData();
				setResult(RESULT_OK);
			} else {
				Intent intent = new Intent(getApplicationContext(),
						UploadActivity.class);
				intent.putExtra("ChkedSrc", (Serializable) mChkedSrc);
				startActivity(intent);
			}
			finish();
			break;
		case R.id.shareActivity_btnFilterId:

			dialog = new ShowPicGroup_Dialog(mAllData, mChkedPath,
					new OnItemClickedLis() {

						@Override
						public void onItemClicked(PicGrpInfo pgi) {
							// TODO Auto-generated method stub
							String chkedName = pgi.getmDirNam();
							mGridSrc.clear();
							if (chkedName.equals(getResources().getString(
									R.string.all_picture))) {
								Iterator<String> iterator = mAllData.keySet()
										.iterator();
								while (iterator.hasNext()) {
									mGridSrc.addAll(mAllData.get(iterator
											.next()));
								}
								PhotoInfo pi = new PhotoInfo();
								mGridSrc.add(0, pi);
								mChkedPath = null;
								mBtnFilter.setText(getResources().getString(
										R.string.all_picture));

							} else {

								mGridSrc.addAll(mAllData.get(pgi.getmDirPath()));
								mBtnFilter.setText(pgi.getmDirNam());
								mChkedPath = pgi.getmDirPath();
							}

							new MyHandler().sendEmptyMessage(1);
						}
					});
			dialog.show(this.getSupportFragmentManager(), "ShowPicGroup_Dialog");

			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onItemChked(PhotoInfo pi) {
		// TODO Auto-generated method stub
		int size = mChkedSrc.size()+mAddedCnt;
		mBtnFinish.setText(getResources().getString(R.string.finish) + size
				+ "/" + MAX_COUNT);
		mBtnFinish.setEnabled(size == 0 ? false : true);
	}

	String strImgPath;
	Uri uri;

	@Override
	public void onCameraClicked() {
		// TODO Auto-generated method stub
		Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String strImgDirPath = SysConfig.getInstance().getCameraFolder();// 存放照片的文件夹
		String fileName = System.currentTimeMillis() + ".jpg";// 照片命名
		File out = new File(strImgDirPath);
		if (!out.exists()) {
			out.mkdirs();
		}
		out = new File(strImgDirPath, fileName);
		strImgPath = strImgDirPath + fileName;// 该照片的绝对路径
		uri = Uri.fromFile(out);

		imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(imageCaptureIntent, PHOTOHRAPH);
	}

	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	// 此处设置了调用的Data MINI类型
	public static final String IMAGE_UNSPECIFIED = "image*";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == NONE)
			return;
		// 拍照
		if (requestCode == PHOTOHRAPH) {
			// 设置文件保存路径这里放在跟目录下
			// 更新MediaStore中記錄的標題和描述

			ContentValues contentValues = new ContentValues();
			contentValues.put(Media.DISPLAY_NAME, System.currentTimeMillis());
			contentValues.put(Media.DESCRIPTION, "dolphin");
			contentValues.put(Media.DATA, strImgPath);
			contentValues.put(Media.ORIENTATION,
					NativeImageLoader.readPictureDegree(strImgPath));
			Uri uri1 = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI,
					contentValues);
			getContentResolver().update(uri1, contentValues, null, null);
			Intent intent = new Intent(getApplicationContext(),
					UploadActivity.class);
			mChkedSrc.clear();
			PhotoInfo pi = new PhotoInfo();
			pi.setmUrl(strImgPath);

			if (mAddMore == true) {
				mChkedSrc.add(pi);
				chkedSrc = mChkedSrc;
				// intent.putExtra("ChkedSrc", (Serializable) mChkedSrc);
				setResult(RESULT_OK);
			} else {
				mChkedSrc.add(pi);
				intent.putExtra("ChkedSrc", (Serializable) mChkedSrc);
				startActivity(intent);

			}

			finish();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ImageCacheUtil.getInstance().release();
			NativeImageLoader.getInstance().release();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
