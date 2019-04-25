package com.wellav.dolphin.mmedia.fragmentForAlbum;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView.OnRefreshListener;
import com.wellav.dolphin.imagelru.VolleyRequestQueueManager;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.adapterForAlbum.CommonAdapter_pic;
import com.wellav.dolphin.mmedia.adapterForAlbum.MyBaseAdapter;
import com.wellav.dolphin.mmedia.commands.GetPhoto;
import com.wellav.dolphin.mmedia.commands.GetPhoto.OnGetSuccessLis;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.net.XMLRequest;

/**
 * @author cf
 * 
 */
public class CommonFragment_pic extends BaseFragment implements
		OnRefreshListener, OnGetSuccessLis {
	private StickyGridHeadersGridView mGridView;
	private List<PhotoInfo> mGridSrc;// 加载的数据
	private MyBaseAdapter mAdapter;
	private ProgressBar mPb;
	private String mUrl;
	private String mMsgBody;
	private String mUsrId = null;
	private int mGetCount = 60;
	private int mLoadmoreCount = 30;
	private boolean mNoMorePhoto = false;
	TextView emptyView;

	@Override
	public void update(Observable observable, Object data) {
		if (data == null){
			return;
		}
		List<PhotoInfo> mCanceledLike = (List<PhotoInfo>) data;
		int index;
		for (int i = 0; i < mCanceledLike.size(); i++) {
			if ((index = mGridSrc.indexOf(mCanceledLike.get(i))) != -1)
				mGridSrc.get(index).setmIsFavor(false);
		}
	}

	public CommonFragment_pic() {

	}

	public CommonFragment_pic(String UsrId) {
		mUsrId = UsrId;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mGridSrc = new LinkedList<PhotoInfo>();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (mUsrId == null) {
			mUrl = SysConfig.ServerUrl + "GetNewestPhotoList";
			mMsgBody = "<GetNewestPhotoListRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
					+ "<Token>"
					+ SysConfig.dtoken
					+ "</Token>"
					+ "<PhotoType>"
					+ mPhotoTyp
					+ "</PhotoType>"
					+ "<Count>"
					+ mGetCount
					+ "</Count>"
					+ "<FamilyID>"
					+ SysConfig.familyIdForPic
					+ "</FamilyID>"
					+ "</GetNewestPhotoListRequest>";
		} else {
			mUrl = SysConfig.ServerUrl + "GetNewestUserPhotoList";
			mMsgBody = "<GetNewestUserPhotoListRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
					+ "<Token>"
					+ SysConfig.dtoken
					+ "</Token>"
					+ "<UploadUserID>"
					+ mUsrId
					+ "</UploadUserID>"
					+ "<Count>"
					+ mGetCount
					+ "</Count>"
					+ "<PhotoType>"
					+ mPhotoTyp
					+ "</PhotoType>"
					+ "<FamilyID>"
					+ SysConfig.familyIdForPic
					+ "</FamilyID>" + "</GetNewestUserPhotoListRequest>";
		}

		View view = inflater.inflate(R.layout.frag_life, null);

		mGridView = (StickyGridHeadersGridView) view
				.findViewById(R.id.lifeFrag_gridId);
		mGridView.setOnRefreshListener(this);
		mPb = (ProgressBar) view.findViewById(R.id.lifeFrag_pbId);

		mPb.setVisibility(View.VISIBLE);
		mAdapter = new CommonAdapter_pic(getActivity(), mGridSrc, mGridView);

		mGridView.setAdapter(mAdapter);
		emptyView = new TextView(getActivity());
		LayoutParams p = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		emptyView.setLayoutParams(p);
		emptyView.setGravity(Gravity.CENTER);
		emptyView.setText(getResources().getString(R.string.load_data));
		emptyView.setVisibility(View.GONE);
		emptyView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				emptyView.setText(getResources().getString(R.string.load_data));
				mPb.setVisibility(View.VISIBLE);
				XMLRequest xmlRequest = GetPhoto.newXMLRequest(mUrl, mMsgBody,
						CommonFragment_pic.this, null);
				VolleyRequestQueueManager.addRequest(xmlRequest, null);
			}
		});
		((ViewGroup) mGridView.getParent()).addView(emptyView, 0);
		mGridView.setEmptyView(emptyView);
		XMLRequest xmlRequest = GetPhoto.newXMLRequest(mUrl, mMsgBody, this,
				null);
		VolleyRequestQueueManager.addRequest(xmlRequest, null);
		return view;
	}

	@Override
	public void onLoadingMore() {
		if (mNoMorePhoto) {
			return;
		}
		mPb.setVisibility(View.VISIBLE);
		mGridView.setLoading(true);
		mPb.setVisibility(View.VISIBLE);
		if (mUsrId == null) {
			mUrl = SysConfig.ServerUrl + "GetOlderPhotoList";
			mMsgBody = "<GetOlderPhotoListRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
					+ "<Token>"
					+ SysConfig.dtoken
					+ "</Token>"
					+ "<PhotoType>"
					+ mPhotoTyp
					+ "</PhotoType>"
					+ "<Count>"
					+ mLoadmoreCount
					+ "</Count>"
					+ "<MinID>"
					+ mGridSrc.get(mGridSrc.size() - 1).getID()
					+ "</MinID>"
					+ "<FamilyID>"
					+ SysConfig.familyIdForPic
					+ "</FamilyID>"
					+ "</GetOlderPhotoListRequest>";

		} else {
			mUrl = SysConfig.ServerUrl + "GetOlderUserPhotoList";
			mMsgBody = "<GetOlderUserPhotoListRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
					+ "<Token>"
					+ SysConfig.dtoken
					+ "</Token>"
					+ "<UploadUserID>"
					+ mUsrId
					+ "</UploadUserID>"
					+ "<Count>"
					+ mLoadmoreCount
					+ "</Count>"
					+ "<PhotoType>"
					+ mPhotoTyp
					+ "</PhotoType>"
					+ "<MinID>"
					+ mGridSrc.get(mGridSrc.size() - 1).getID()
					+ "</MinID>"
					+ "<FamilyID>"
					+ SysConfig.familyIdForPic
					+ "</FamilyID>"
					+ "</GetOlderUserPhotoListRequest>";
		}

		XMLRequest xmlRequest = GetPhoto.newXMLRequest(mUrl, mMsgBody, this,
				null);
		VolleyRequestQueueManager.addRequest(xmlRequest, null);
	}

	@Override
	public void onGetSuccess(List<PhotoInfo> getSrc) {
		emptyView.setText(R.string.no_data);
		mGridView.setEmptyView(emptyView);
		if (mPb == null)
			return;
		mPb.setVisibility(View.INVISIBLE);
		int size = mGridSrc.size();
		if (mGridSrc != null && size != 0) {
			mGridView.setLoading(false);
			if (getSrc == null || getSrc.size() == 0)
				mNoMorePhoto = true;
		}
		if (mGridSrc != null && getSrc != null) {
			mGridSrc.addAll(getSrc);
			mAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onResume() {
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		mGridSrc.clear();
		mGridSrc = null;
		super.onDestroy();
	}
}