package com.wellav.dolphin.mmedia.fragmentForAlbum;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.adapterForAlbum.CommonAdapter_video;
import com.wellav.dolphin.mmedia.adapterForAlbum.MyBaseAdapter;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.entity.PhotoInfo;
import com.wellav.dolphin.mmedia.fragmentForAlbum.BaseFragment;
import com.wellav.dolphin.mmedia.utils.MediaScanner;

public class CommonFragment_video extends BaseFragment {
	private StickyGridHeadersGridView mGridView;
	private MyBaseAdapter mAdapter;
	private List<PhotoInfo> mGridSrc ;
	private int section = 1;
	private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
	private ProgressBar mPb;
	private MediaScanner mScanner;
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if(mAdapter!=null)
			mAdapter.notifyDataSetChanged();
		super.onResume();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mGridSrc = new ArrayList<PhotoInfo>();
		View view = inflater.inflate(R.layout.frag_life, null);
		mGridView = (StickyGridHeadersGridView) view
				.findViewById(R.id.lifeFrag_gridId);
		mPb=(ProgressBar)view.findViewById(R.id.lifeFrag_pbId);
		
		mScanner = new MediaScanner(getActivity(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
		mScanner.scanMedia(new MediaScanner.ScanCompleteCallBack() {
			{
				mPb.setVisibility(View.VISIBLE);
			}

			@Override
			public void scanComplete(Cursor cursor) {

				mPb.setVisibility(View.INVISIBLE);

				while (cursor.moveToNext()) {
					
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Video.Media.DATA));
					CharSequence cs = SysConfig.getInstance().getRecordFolder();
					if(path.contains(cs)){
					long times = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
					PhotoInfo mGridItem = new PhotoInfo();
					
					mGridItem.setmUrl(path);
					mGridItem.setmTime(paserTimeToYM(times));
					mGridItem.setmThumbnail(path);
					mGridItem.setID(String.valueOf(mGridSrc.size()));
					mGridSrc.add(mGridItem);
					}
				}
				cursor.close();
				Collections.sort(mGridSrc, new Comparator<PhotoInfo>() {
					@Override
					public int compare(PhotoInfo o1, PhotoInfo o2) {
						return o1.getmTime().compareTo(o2.getmTime());
					}

				});

				for (ListIterator<PhotoInfo> it = mGridSrc.listIterator(); it
						.hasNext();) {
					PhotoInfo mGridItem = it.next();
					String ym = mGridItem.getmTime();
					if (!sectionMap.containsKey(ym)) {
						mGridItem.setmSection(section);
						sectionMap.put(ym, section);
						section++;
					} else {
						mGridItem.setmSection(sectionMap.get(ym));
					}
				}
				mAdapter=new CommonAdapter_video(getActivity(), mGridSrc,mGridView);
				mGridView.setAdapter(mAdapter);
			}
		});
		return view;
	}

	public static String paserTimeToYM(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(time * 1000L));
	}
}
