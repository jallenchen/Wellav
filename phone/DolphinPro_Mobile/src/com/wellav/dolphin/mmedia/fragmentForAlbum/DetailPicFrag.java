package com.wellav.dolphin.mmedia.fragmentForAlbum;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wellav.dolphin.application.DolphinApp;
import com.wellav.dolphin.imagelru.ImageCacheUtil;
import com.wellav.dolphin.imagelru.ImageLoaderManager;
import com.wellav.dolphin.imagelru.VolleyRequestQueueManager;
import com.wellav.dolphin.mmedia.GalleryActivity;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.Collect_CancleCollect;
import com.wellav.dolphin.mmedia.commands.Delete;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.ui.ImageViewTouch;
import com.wellav.dolphin.mmedia.ui.ImageViewTouch.OnImageViewTouchSingleTapListener;
import com.wellav.dolphin.mmedia.utils.SDCardScanner;

public class DetailPicFrag extends Fragment implements
		OnClickListener {
	private LinearLayout mFirstLine;
	private LinearLayout mLastLine;
	private ImageViewTouch mImage;
	private TextView mTxtDesc;
	private TextView mTxtTim;
	protected ProgressBar mProgressBar;
	Point point = new Point();
	private ImageButton mBtnBackwd;
	private ImageView mBtnDel;
	private ImageView mBtnCollect;
	private ImageView mBtnSave;

	PhotoInfo photoInfo;

	public DetailPicFrag(PhotoInfo pi) {
		// TODO Auto-generated constructor stub
		photoInfo = pi;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// photoInfo=(PhotoInfo) getArguments().getSerializable("PhotoInfo");
		getActivity().getWindowManager().getDefaultDisplay().getSize(point);
		View view = inflater.inflate(R.layout.frag_detailpic, null);
		mTxtDesc = (TextView) view.findViewById(R.id.detailpicFrag_txtDescId);
		mTxtDesc.setText(photoInfo.getmDesc());
		mTxtTim = (TextView) view.findViewById(R.id.detailpicFrag_txtTimeId);
		mTxtTim.setText(photoInfo.getmTime());
		mImage = (ImageViewTouch) view.findViewById(R.id.detaippicFrag_ImgId);
		mImage.setImageBitmap(ImageCacheUtil.getInstance().getCacheFromMemory(
				getCacheKey(photoInfo.getmThumbnail(), 0, 0,
						ScaleType.CENTER_INSIDE)));
		mFirstLine = (LinearLayout) view
				.findViewById(R.id.detailpicFrag_FirstLineId);
		mLastLine = (LinearLayout) view
				.findViewById(R.id.detailpicFrag_LastLineId);
		mImage.setSingleTapListener(new OnImageViewTouchSingleTapListener() {

			@Override
			public void onSingleTapConfirmed() {
				// TODO Auto-generated method stub
				mFirstLine
						.setVisibility(mFirstLine.getVisibility() == View.VISIBLE ? View.INVISIBLE
								: View.VISIBLE);
				mLastLine
						.setVisibility(mLastLine.getVisibility() == View.VISIBLE ? View.GONE
								: View.VISIBLE);
			}
		});
		mProgressBar = (ProgressBar) view.findViewById(R.id.detailpicFrag_pbId);
		mBtnBackwd = (ImageButton) view
				.findViewById(R.id.detailpicFrag_btnBackwardId);
		mBtnBackwd.setOnClickListener(this);
		mBtnCollect = (ImageView) view
				.findViewById(R.id.detailpicFrag_btnCollectId);
		mBtnCollect.setImageDrawable(getResources().getDrawable(
				photoInfo.ismIsFavor() ? R.drawable.ic_like_on_32dp
						: R.drawable.ic_like_off_32dp));
		mBtnCollect.setOnClickListener(this);
		mBtnDel = (ImageView) view.findViewById(R.id.detailpicFrag_btnDelId);
		mBtnDel.setOnClickListener(this);
		mBtnSave = (ImageView) view.findViewById(R.id.detailpicFrag_btnSaveId);
		mBtnSave.setOnClickListener(this);
		mImage.buildDrawingCache(true);
//		isViewCreated = true;
//		lazyLoad();
		ImageLoaderManager.loadImage(photoInfo.getmUrl(), mImage, mProgressBar,
				null, null, point.y, point.y);
		return view;
	}

	
	private static String getCacheKey(String url, int maxWidth, int maxHeight,
			ScaleType scaleType) {
		return new StringBuilder(url.length() + 12).append("#W")
				.append(maxWidth).append("#H").append(maxHeight).append("#S")
				.append(scaleType.ordinal()).append(url).toString();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		mImage.clear();
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String url;
		String msg;
		switch (v.getId()) {
		case R.id.detailpicFrag_btnBackwardId:
			getActivity().finish();
			break;
		case R.id.detailpicFrag_btnDelId:
			url = SysConfig.ServerUrl + "DeletePhoto";
			msg = "<DeletePhotoRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
					+ "<Token>"+SysConfig.dtoken+"</Token>"

					+ "<PhotoID>" + photoInfo.getID() + "</PhotoID>"

					+ "</DeletePhotoRequest>";
			List<PhotoInfo> delSrc=new LinkedList<PhotoInfo>();
			delSrc.add(photoInfo);
		
			
			VolleyRequestQueueManager.addRequest(Delete.newXMLRequest(url, msg, mBtnDel, mProgressBar, delSrc,
					(GalleryActivity) getActivity()),null);
			
			break;
		case R.id.detailpicFrag_btnCollectId:
			boolean isFavor = photoInfo.ismIsFavor();
			url = SysConfig.ServerUrl
					+ (isFavor ? "CancelPhotoFavourite" : "SetPhotoFavourite");
			msg = isFavor ? "<CancelPhotoFavouriteRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
					+ "<Token>"+SysConfig.dtoken+"</Token>"
					+ "<PhotoID>"
					+ photoInfo.getID()
					+ "</PhotoID>"
					+ "</CancelPhotoFavouriteRequest>"
					: "<SetPhotoFavouriteRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
							+ "<Token>"+SysConfig.dtoken+"</Token>"
							+ "<PhotoID>"
							+ photoInfo.getID()
							+ "</PhotoID>"
							+ "</SetPhotoFavouriteRequest>";

			VolleyRequestQueueManager.addRequest(Collect_CancleCollect
					.newXMLRequest(url, msg, mBtnCollect, mProgressBar,photoInfo), null);
			break;
		case R.id.detailpicFrag_btnSaveId:
			final String key = getCacheKey(photoInfo.getmUrl(), point.y,
					point.y, ScaleType.CENTER_INSIDE);
			String dirPath = SDCardScanner.mkdir(Environment
					.getExternalStorageDirectory().toString(),
					"WellavDolphinMedia", "")
					+ "/";
			final String strImgPath = dirPath + System.currentTimeMillis()
					+ ".jpg";
			Toast.makeText(getActivity(), "save to " + strImgPath, 1000).show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Bitmap bitmap = ImageCacheUtil.getInstance().getBitmap(key);
					if (SDCardScanner.saveMyBitmap(bitmap, strImgPath)) {
						ContentValues contentValues = new ContentValues();
						contentValues.put(Media.DISPLAY_NAME,
								System.currentTimeMillis());
						contentValues.put(Media.DESCRIPTION, "wellav");
						contentValues.put(Media.DATA, strImgPath);
						Uri uri = DolphinApp
								.getInstance()
								.getContentResolver()
								.insert(Media.EXTERNAL_CONTENT_URI,
										contentValues);
						DolphinApp.getInstance().getContentResolver()
								.update(uri, contentValues, null, null);

					}
				}
			}).start();

			break;
		}
	}

}
