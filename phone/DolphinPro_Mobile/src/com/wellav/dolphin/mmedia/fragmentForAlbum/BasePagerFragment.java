package com.wellav.dolphin.mmedia.fragmentForAlbum;

import java.util.Observable;
import java.util.Observer;

import com.wellav.dolphin.mmedia.adapterForAlbum.MyBaseAdapter;

import android.support.v4.app.Fragment;

public abstract class BasePagerFragment extends Fragment implements Observer {
	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub

	}

	public abstract MyBaseAdapter getmBaseAdapter();

	protected boolean isViewCreated;
	protected boolean isVisible;

	/*
	 * adapter中的每个fragment切换的时候都会被调用，如果是切换到当前页，那么isVisibleToUser==true，否则为false
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}
	}

	/**
	 * 可见
	 */
	protected void onVisible() {
		lazyLoad();
	}

	/**
	 * 不可见
	 */
	protected void onInvisible() {

		release();
	}

	/**
	 * 延迟加载 子类必须重写此方法
	 */
	protected abstract void lazyLoad();

	/**
	 * 
	 */
	protected abstract void release();
}
