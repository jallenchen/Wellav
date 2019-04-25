package com.wellav.dolphin.mmedia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wellav.dolphin.mmedia.db.DBHelper;
import com.wellav.dolphin.mmedia.entity.City;
import com.wellav.dolphin.mmedia.interfaces.LocateIn;
import com.wellav.dolphin.mmedia.ui.MyLetterListView;
import com.wellav.dolphin.mmedia.ui.MyLetterListView.OnTouchingLetterChangedListener;
import com.wellav.dolphin.mmedia.utils.DolphinUtil;

public class SelectCityActivity extends BaseActivity {
	private BaseAdapter adapter;
	private ListView personList;
	private TextView overlay; //
	private MyLetterListView letterListView; // A-Z listview
	private HashMap<String, Integer> alphaIndexer;//
	private String[] sections;// 
	private Handler handler;
	private OverlayThread overlayThread; // 
	private ArrayList<City> allCity_lists; // 
	private ArrayList<City> city_lists;// 
	ListAdapter.TopViewHolder topViewHolder;
	private String lngCityName = "";
	private String mMycity;
	private int selectedPosition = -1;// 
	private TextView mAcionBarName;
	private ImageView mActionBarPrev;
	private WindowManager windowManager;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);
		personList = (ListView) findViewById(R.id.list_view);
		mAcionBarName = (TextView) findViewById(R.id.actionbar_name);
		mActionBarPrev = (ImageView) findViewById(R.id.actionbar_prev);
		allCity_lists = new ArrayList<City>();
		letterListView = (MyLetterListView) findViewById(R.id.MyLetterListView01);
		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		boolean IsRegister = getIntent().getBooleanExtra("isRegister", false);
		if(IsRegister == false){
			thread.start();
		}
		DolphinUtil.setLocateIn(new GetCityName());
		
		overlayThread = new OverlayThread();
		personList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == 0) {
					returnActivity(0);
				}else{
					mMycity = allCity_lists.get(arg2).getName();
					returnActivity(1);
				}
//				((ListAdapter) adapter).setSelectedPosition(arg2);
//				adapter.notifyDataSetInvalidated();
//				mMycity = allCity_lists.get(arg2).getName();
				//Toast.makeText(SelectCityActivity.this, mMycity, Toast.LENGTH_SHORT).show();
			}
		});
		mActionBarPrev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//returnActivity(1);
				windowManager.removeView(overlay);
				overlay = null;
				windowManager = null;
				finish();
			}
		});
		lngCityName = getString(R.string.city_gps);
		personList.setAdapter(adapter);
		mAcionBarName.setText(R.string.change_city);
		initOverlay();
		hotCityInit();
		setAdapter(allCity_lists);
	}
	
	private void returnActivity(int pos){
		if(pos == 0){
			if(lngCityName.length() >5){
				Toast.makeText(SelectCityActivity.this, R.string.is_getcity_state, Toast.LENGTH_SHORT).show();
				return ;
			}
			mMycity = lngCityName;
		}
		
		Intent intent = new Intent();
		intent.putExtra("MYCITY", mMycity);
		setResult(RESULT_OK, intent);
		//Toast.makeText(SelectCityActivity.this, mMycity, Toast.LENGTH_SHORT).show();
		windowManager.removeView(overlay);
		overlay = null;
		windowManager = null;
		finish();
	}
	
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		windowManager.removeView(overlay);
		overlay = null;
		windowManager = null;
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		super.onDestroy();
	}



	public void hotCityInit() {
		
		CharSequence[] items = this.getResources().getStringArray(R.array.hotcity);
		City city ;
		for(int i=0;i<items.length;i++){
			if(i==0){
				 city = new City(items[i].toString(), "-");
			}else{
				 city = new City(items[i].toString(), "");	
			}
			
			allCity_lists.add(city);
		}

		city_lists = getCityList();
		allCity_lists.addAll(city_lists);
	}

	@SuppressWarnings("unchecked")
	private ArrayList<City> getCityList() {
		DBHelper dbHelper = new DBHelper(this);
		ArrayList<City> list = new ArrayList<City>();
		try {
			dbHelper.createDataBase();
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery("select * from city", null);
			City city;
			while (cursor.moveToNext()) {
				city = new City(cursor.getString(1), cursor.getString(2));
				list.add(city);
			}
			cursor.close();
			db.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Collections.sort(list, comparator);
		return list;
	}

	/**
	 * a-z����
	 */
	@SuppressWarnings("rawtypes")
	Comparator comparator = new Comparator<City>() {
		@Override
		public int compare(City lhs, City rhs) {
			String a = lhs.getPinyi().substring(0, 1);
			String b = rhs.getPinyi().substring(0, 1);
			int flag = a.compareTo(b);
			if (flag == 0) {
				return a.compareTo(b);
			} else {
				return flag;
			}

		}
	};

	private void setAdapter(List<City> list) {
		adapter = new ListAdapter(this, list);
		personList.setAdapter(adapter);
	}

	@SuppressLint("InflateParams") public class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<City> list;
		final int VIEW_TYPE = 2;

		public ListAdapter(Context context, List<City> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				String currentStr = DolphinUtil.getAlpha(list.get(i).getPinyi());
				String previewStr = (i - 1) >= 0 ? DolphinUtil.getAlpha(list.get(i - 1)
						.getPinyi()) : " ";
				if (!previewStr.equals(currentStr)) {
					String name = DolphinUtil.getAlpha(list.get(i).getPinyi());
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			int type = 0;
			if (position == 0) {
				type = 1;
			}
			return type;
		}

		@Override
		public int getViewTypeCount() {
			return VIEW_TYPE;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		@SuppressLint("InflateParams") @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			int viewType = getItemViewType(position);
			if (viewType == 1) {
				if (convertView == null) {
					topViewHolder = new TopViewHolder();
					convertView = inflater.inflate(R.layout.frist_list_item,
							null);
					topViewHolder.alpha = (TextView) convertView
							.findViewById(R.id.alpha);
					topViewHolder.name = (TextView) convertView
							.findViewById(R.id.lng_city);
					convertView.setTag(topViewHolder);
				} else {
					topViewHolder = (TopViewHolder) convertView.getTag();
				}

				topViewHolder.name.setText(lngCityName);
				topViewHolder.alpha.setVisibility(View.VISIBLE);
				topViewHolder.alpha.setText(R.string.gps_city);

			} else {
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.city_list_item, null);
					holder = new ViewHolder();
					holder.cityLayout = (LinearLayout) convertView
							.findViewById(R.id.city_layout);

					holder.alpha = (TextView) convertView
							.findViewById(R.id.alpha);
					holder.name = (TextView) convertView
							.findViewById(R.id.name);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				if (position >= 1) {
					holder.name.setText(list.get(position).getName());
					String currentStr = DolphinUtil.getAlpha(list.get(position).getPinyi());
					String previewStr = (position - 1) >= 0 ? DolphinUtil.getAlpha(list
							.get(position - 1).getPinyi()) : " ";
					if (!previewStr.equals(currentStr)) {
						holder.alpha.setVisibility(View.VISIBLE);
						if (currentStr.equals("#")) {
							currentStr = getString(R.string.hot_city);
						}
						holder.alpha.setText(currentStr);
					} else {
						holder.alpha.setVisibility(View.GONE);
					}
				}

				if (selectedPosition == position) {
					holder.name.setSelected(true);
					holder.name.setPressed(true);
					holder.cityLayout.setBackgroundColor(0xff6699ff);
				} else {
					holder.name.setSelected(false);
					holder.name.setPressed(false);
					holder.cityLayout.setBackgroundColor(Color.TRANSPARENT);

				}
			}
			return convertView;
		}

		private class ViewHolder {
			TextView alpha; // 
			TextView name; // 
			LinearLayout cityLayout;
		}

		private class TopViewHolder {
			TextView alpha; //
			TextView name; //
		}
	}

	 private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
	   windowManager = (WindowManager) SelectCityActivity.this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				personList.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				handler.postDelayed(overlayThread, 1500);
			}
		}

	}

	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			if(overlay != null){
				overlay.setVisibility(View.GONE);
			}
			
		}

	}


	private class GetCityName implements LocateIn {
		@Override
		public void getCityName(String name) {
			System.out.println(name);
			if (topViewHolder.name != null) {
				lngCityName = name;
				adapter.notifyDataSetChanged();
			}
		}
	}

	
	Thread thread = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String mCity = DolphinUtil.getLocation();
			Message msg = new Message();
			msg.what = 0;
			msg.obj = mCity;
			mHandler.sendMessageDelayed(msg, 1000);

		}
	});
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				String city = (String) msg.obj;
				if (city != null) {
					DolphinUtil.getLocateIn().getCityName(city);
					
				} else {
				}
				break;}
			}
		};

}