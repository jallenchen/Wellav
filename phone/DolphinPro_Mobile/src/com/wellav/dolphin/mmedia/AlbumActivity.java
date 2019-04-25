package com.wellav.dolphin.mmedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.wellav.dolphin.imagelru.ImageCacheUtil;
import com.wellav.dolphin.imagelru.ImageLoaderManager;
import com.wellav.dolphin.imagelru.VolleyRequestQueueManager;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.fragment.FragmentFactory;
import com.wellav.dolphin.mmedia.fragmentForAlbum.BaseFragment;
import com.wellav.dolphin.mmedia.fragmentForAlbum.CommonFragment_pic;
import com.wellav.dolphin.mmedia.ui.AlbumTab;
import com.wellav.dolphin.mmedia.ui.AlbumTab.OnItemClickedListener;
import com.wellav.dolphin.mmedia.utils.NativeImageLoader;

public class AlbumActivity extends BaseActivity implements OnItemClickedListener {
	private AlbumTab mTaB;
	private String[] mBtnTxt;
	private String[] mFrag = { "CommonFragment_pic", "ChatFragment", "SecurityFragment" };
	private static BaseFragment[] mFragments;

	public static BaseFragment[] getmFragments() {
		return mFragments;
	}

	private int mCurIndex = 0;
	private static BaseFragment mCurFrag;

	public static BaseFragment getmCurFrag() {
		return mCurFrag;
	}

	private ImageView mBtnBack;
	private Button mBtnFavor;
	private boolean isSecondLevel = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setBackgroundDrawable(null);
		super.onCreate(savedInstanceState);
		
		mBtnTxt = getResources().getStringArray(R.array.albumType);
		
		setContentView(R.layout.activity_album);
		mTaB = (AlbumTab) findViewById(R.id.albumActivity_tabId);
		mTaB.init(this, mBtnTxt);
		mFragments = new BaseFragment[3];
		CommonFragment_pic lifeFrag = new CommonFragment_pic();
		lifeFrag.setmPhotoTyp(1);
		mFragments[0] = lifeFrag;
		mCurFrag = lifeFrag;
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.albumActivity_contentId, lifeFrag);
		ft.commit();
		mBtnBack = (ImageView) findViewById(R.id.albumActivity_btnBackwardId);
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isSecondLevel) {
					finish();
					return;
				}
				switchFragment(mFragments[mCurIndex], false);
			}
		});
		mBtnFavor = (Button) findViewById(R.id.albumActivity_FavorBtnId);
		mBtnFavor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 Intent intent = new Intent(AlbumActivity.this,
				 FavorActivity.class);
				 startActivity(intent);
			}
		});
	}

	@Override
	public void onItemClicked(int clickItem) {
		boolean isNew = mFragments[clickItem] == null;
		if (isNew) {
			BaseFragment frag = (BaseFragment) FragmentFactory
					.createFragment(SysConfig.fragmentForAlbumAction, mFrag[clickItem]);
			frag.setmPhotoTyp(clickItem + 1);
			mFragments[clickItem] = frag;
		}
		switchFragment(mFragments[clickItem], isNew);
		mCurIndex = clickItem;
	}

	public void setIsSencondLevel(boolean arg) {
		isSecondLevel = arg;
	}

	public void switchFragment(BaseFragment FragDes, boolean fragDes_isNew) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (isSecondLevel) {
			ft.remove(mCurFrag);
			isSecondLevel = false;
		} else
			ft.hide(mCurFrag);
		if (fragDes_isNew)
			ft.add(R.id.albumActivity_contentId, FragDes);
		else
			ft.show(FragDes);
		ft.commit();
		mCurFrag = FragDes;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		VolleyRequestQueueManager.release(null);
		ImageCacheUtil.getInstance().release();
		NativeImageLoader.getInstance().release();
		ImageLoaderManager.mImageLoader = null;
		// NativeImageLoader.getInstance().release();
		mFragments = null;
		mCurFrag = null;
		super.onDestroy();
	}

}
