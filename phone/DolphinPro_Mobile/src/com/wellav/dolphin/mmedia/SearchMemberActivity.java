package com.wellav.dolphin.mmedia;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.wellav.dolphin.mmedia.fragment.SearchContactAddFragment;

public class SearchMemberActivity extends BaseFragmentActivity{

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		Bundle bundle = getIntent().getBundleExtra("bundle");
		return SearchContactAddFragment.newIstanceSearch(bundle);
	}

}
