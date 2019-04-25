package com.wellav.dolphin.mmedia;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.ViewGroup;

import com.wellav.dolphin.mmedia.commands.Delete.OnDelSuccessLis;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.fragmentForAlbum.DetailPicFrag;
import com.wellav.dolphin.mmedia.utils.CommFunc;

public class GalleryActivity extends BaseActivity implements OnDelSuccessLis{
	public static List<PhotoInfo> mGridSrc;
	private ViewPager mViewPager;
	private FragmentStatePagerAdapter mPagerAdapter;
	private int mCurItem = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		getWindow().setBackgroundDrawable(null);
		if (mCurItem == 0) {
			Intent intent = getIntent();
			mCurItem = intent.getIntExtra("CurrentItem", 0);			
			
		}
		InitDatas();
		mViewPager = (ViewPager) findViewById(R.id.galleryActivity_vpId);
		mViewPager.setAdapter(mPagerAdapter);
		// mViewPager.setOffscreenPageLimit(0) ;
		mViewPager.setCurrentItem(mCurItem);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Intent intent=new Intent(getApplicationContext(),
			// MainActivity.class);
			// startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void InitDatas() {

		mPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mGridSrc.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				// TODO Auto-generated method stub
				return new DetailPicFrag(mGridSrc.get(arg0));
			}

			@Override
			public int getItemPosition(Object object) {
				// TODO Auto-generated method stub
				return POSITION_NONE;
//				return super.getItemPosition(object);
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				// TODO Auto-generated method stub
				
				super.destroyItem(container, position, object);
			}
		};
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onDelSuccess(List<PhotoInfo> delSrc) {
		// TODO Auto-generated method stub
		mGridSrc.removeAll(delSrc);
		CommFunc.PrintLog("onDelSuccess", "size:"+mGridSrc.size());
		if(mGridSrc.size() == 0){
			finish();
		}
		mPagerAdapter.notifyDataSetChanged();
	}
}
