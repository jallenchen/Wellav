package com.wellav.dolphin.mmedia;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView.OnRefreshListener;
import com.wellav.dolphin.imagelru.VolleyRequestQueueManager;
import com.wellav.dolphin.mmedia.adapterForAlbum.FavorAdapter;
import com.wellav.dolphin.mmedia.commands.CancelCollect;
import com.wellav.dolphin.mmedia.commands.CancelCollect.OnCancelCollectSuccessLis;
import com.wellav.dolphin.mmedia.commands.GetPhoto;
import com.wellav.dolphin.mmedia.commands.GetPhoto.OnGetSuccessLis;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.fragmentForAlbum.BaseFragment;
import com.wellav.dolphin.mmedia.fragmentForAlbum.ChatFragment;
import com.wellav.dolphin.mmedia.fragmentForAlbum.SecurityFragment;
import com.wellav.dolphin.mmedia.net.XMLRequest;
import com.wellav.dolphin.mmedia.ui.AlertButton;
import com.wellav.dolphin.mmedia.ui.AlertButton.OnPositiveButtonClickenListener;

public class FavorActivity extends BaseActivity implements OnGetSuccessLis, OnRefreshListener,OnPositiveButtonClickenListener, OnCancelCollectSuccessLis{
	private  Subject mSub;
	private StickyGridHeadersGridView mGridView;
	private FavorAdapter mAdapter;
	private ProgressBar mPb;
	private  List<PhotoInfo> mGridSrc;
	private List<PhotoInfo> mChkedSrc;
	private AlertButton mBtnDel;
	private ImageView mBtnCancel;
	private TextView mTxtChkedCount;
	private String mUrl;
	private String mMsgBody;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favor);
		mSub=new Subject();
		BaseFragment frag=AlbumActivity.getmCurFrag();
		if(frag!=null)
			mSub.addObserver(frag);
		BaseFragment mFrags[]=AlbumActivity.getmFragments();
		for(int i=0;i<mFrags.length;i++){
			frag=mFrags[i];
			if(frag!=null)
				mSub.addObserver(frag);
		}
		mFrags=ChatFragment.getmFragments();
		for(int i=0;mFrags!=null&&i<mFrags.length;i++){
			frag=mFrags[i];
			if(frag!=null)
				mSub.addObserver(frag);
		}
		mFrags=SecurityFragment.getmFragments();
		for(int i=0;mFrags!=null&&i<mFrags.length;i++){
			frag=mFrags[i];
			if(frag!=null)
				mSub.addObserver(frag);
		}
		mGridSrc=new LinkedList<PhotoInfo>();
		mChkedSrc=new LinkedList<PhotoInfo>();
		mBtnCancel=(ImageView)findViewById(R.id.favorActivity_backwdBtnId);
		mBtnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mBtnDel=(AlertButton)findViewById(R.id.favorActivity_DelBtnId);
		mBtnDel.setmLis(this);
		mGridView=(StickyGridHeadersGridView)findViewById(R.id.favorActivity_gvId);
		mGridView.setOnRefreshListener(this);
		mPb=(ProgressBar)findViewById(R.id.favorActivity_pbId);
		mAdapter=new FavorAdapter(mGridSrc, mChkedSrc, this, mGridView);
		mGridView.setAdapter(mAdapter);
		mUrl = SysConfig.ServerUrl + "GetNewestFavouritePhotoList";
		mMsgBody = "<GetNewestFavouritePhotoListRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"+SysConfig.dtoken+"</Token>"
				+ "<Count>60</Count>"
				+ "<FamilyID>"+SysConfig.familyIdForPic+"</FamilyID>"
				+ "</GetNewestFavouritePhotoListRequest>";
		XMLRequest xmlRequest = GetPhoto.newXMLRequest(mUrl, mMsgBody, this,null);
		VolleyRequestQueueManager.addRequest(xmlRequest, null);
	}
	class Subject extends Observable{
		public void noteAll(Object delSrc)
		{
			setChanged();
			notifyObservers(delSrc);
		}
	}
	@Override
	public void onGetSuccess(List<PhotoInfo> getSrc) {
		// TODO Auto-generated method stub
		if(mPb==null)
			return;
		mPb.setVisibility(View.INVISIBLE);
		if (mGridSrc.size() != 0){
			mGridView.setLoading(false);
		}
		if (getSrc != null) {
			mGridSrc.addAll(getSrc);
			mAdapter.notifyDataSetChanged();
		}
		if(mGridSrc.size() == 0){
			mBtnDel.setEnabled(false);
		}
	}
	@Override
	public void onLoadingMore() {
		// TODO Auto-generated method stub
		mPb.setVisibility(View.VISIBLE);
		mGridView.setLoading(true);
		mPb.setVisibility(View.VISIBLE);
		mUrl = SysConfig.ServerUrl + "GetOlderFavouritePhotoList";
		mMsgBody = "<GetOlderFavouritePhotoListRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+ "<Token>"+SysConfig.dtoken+"</Token>"
			
				+ "<Count>30</Count>"
				+ "<MinID>"
				+ mGridSrc.get(mGridSrc.size() - 1).getID()
				+ "</MinID>"
				+ "<FamilyID>"+SysConfig.familyIdForPic+"</FamilyID>" 
				+ "</GetOlderFavouritePhotoListRequest>";
		XMLRequest xmlRequest = GetPhoto.newXMLRequest(mUrl, mMsgBody, this,null);
		VolleyRequestQueueManager.addRequest(xmlRequest, null);
	}
	@Override
	public void onPositiveClicked() {
		// TODO Auto-generated method stub
		int size=mChkedSrc.size();
		if(size==0){
			return;
		}
		mPb.setVisibility(View.VISIBLE);
		String url = SysConfig.ServerUrl
				+ "CancelPhotoFavourite" ;
		StringBuilder sb=new StringBuilder("<CancelPhotoFavouriteRequest xmlns=\"http://ws.wellav.com/hiapi/20150925\">"
				+  "<Token>"+SysConfig.dtoken+"</Token>");
		for(int i=0;i<size;i++)
		{
			sb.append("<PhotoID>"+ mChkedSrc.get(i).getID()+ "</PhotoID>");
		}
		sb.append("</CancelPhotoFavouriteRequest>");
		XMLRequest xmlRequest = CancelCollect.newXMLRequest(url, sb.toString(), mBtnDel, mPb, mChkedSrc, this);
		VolleyRequestQueueManager.addRequest(xmlRequest, null);
	}
	@Override
	public void onCancelCollectSuccess(final List<PhotoInfo> delSrc) {
		// TODO Auto-generated method stub
		mPb.setVisibility(View.VISIBLE);
		new AsyncTask<Void,Void,Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				mSub.noteAll(delSrc);
				mGridSrc.removeAll(mChkedSrc);
				mChkedSrc.clear();
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				mPb.setVisibility(View.INVISIBLE);
				if(mChkedSrc.size() == 0){
					mBtnDel.setEnabled(false);
				}
				mAdapter.notifyDataSetChanged();
			}
		}.execute(new Void[]{});
		
		
		
	}
	
}
