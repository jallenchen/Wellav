package com.wellav.dolphin.mmedia.fragmentForAlbum;

import java.util.Observable;
import java.util.Observer;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment implements Observer {
	protected int mPhotoTyp = 1;// 1生活2聊天3安全4猫眼5收藏
	public void setmPhotoTyp(int mPhotoTyp) {
		this.mPhotoTyp = mPhotoTyp;
	}
	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub

	}
}
