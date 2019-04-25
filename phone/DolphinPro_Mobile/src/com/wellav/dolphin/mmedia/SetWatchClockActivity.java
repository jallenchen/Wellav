package com.wellav.dolphin.mmedia;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wellav.dolphin.application.HBaseApp;
import com.wellav.dolphin.mmedia.commands.SysConfig;
import com.wellav.dolphin.mmedia.dialog.DatepickerFragment;
import com.wellav.dolphin.mmedia.dialog.WeekdayPickerFragment;
import com.wellav.dolphin.mmedia.dialog.WeekdayPickerFragment.OnPositiveButtonClickedListener;
import com.wellav.dolphin.mmedia.entity.ClockInfo;
import com.wellav.dolphin.mmedia.utils.DateUtils;

public class SetWatchClockActivity extends BaseActivity implements OnClickListener,
		OnPositiveButtonClickedListener {
	
	private Editor mEditor;
	private ClockInfo mClockInfo;
	private TextView txtWeekday;
	private TextView txtsTime;
	private TextView txteTime;
	private ImageView mActionbarPrev;
	private TextView mActionbarName;
	private Button mActionbarFinish;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		
		mActionbarPrev = (ImageView) findViewById(R.id.actionbar_prev);
		mActionbarName = (TextView) findViewById(R.id.actionbar_name);
		mActionbarFinish = (Button) findViewById(R.id.finish);
		txteTime = (TextView) findViewById(R.id.setActivity_eTimId);
		txtsTime = (TextView) findViewById(R.id.setActivity_sTimId);
		txtWeekday = (TextView) findViewById(R.id.setActivity_eWeekdaysId);

		mClockInfo = (ClockInfo) getIntent().getSerializableExtra("clockInfo");
		txtWeekday.setText( DateUtils.getWeekdays(mClockInfo.getmDays()));
		txtsTime.setText( mClockInfo.getmStartTime());
		txteTime.setText(mClockInfo.getmEndTime());
		mActionbarName.setText(R.string.look_date_set);
		mActionbarFinish.setVisibility(View.VISIBLE);
		txtsTime.setOnClickListener(this);
		txteTime.setOnClickListener(this);
		txtWeekday.setOnClickListener(this);
		mActionbarFinish.setOnClickListener(this);
		mActionbarPrev.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.actionbar_prev:
			finish();
			break;
		case R.id.setActivity_sTimId:

			DialogFragment df = new DatepickerFragment((TextView) v);
			FragmentManager fm = getSupportFragmentManager();
			df.show(fm, "DatePickerFragment");
			break;
		case R.id.setActivity_eTimId:
			DialogFragment df3 = new DatepickerFragment((TextView) v);
			FragmentManager fm3 = getSupportFragmentManager();
			df3.show(fm3, "DatePickerFragment");
			break;
		case R.id.setActivity_eWeekdaysId:
			DialogFragment df4 = new WeekdayPickerFragment(
					mClockInfo.getmDays(), SetWatchClockActivity.this);
			FragmentManager fm4 = getSupportFragmentManager();
			df4.show(fm4, "WeekdayPickerFragment");
			break;
		case R.id.finish:

			String st = txtsTime.getText().toString();
			String et = txteTime.getText().toString();
			if ((DateUtils.getHour(et) * 60 + DateUtils.getMin(et))
					- (DateUtils.getHour(st) * 60 + DateUtils.getMin(st)) < 5) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.intervalTime), 1000)
						.show();
				return;
			}
			
			mClockInfo.setmStartTime(st);
			mClockInfo.setmEndTime(et);
			
			HBaseApp.post2WorkDelayed(new Runnable() {

				@Override
				public void run() {
					sendBroadcast(new Intent(SysConfig.Broadcast_detect_time_changed));
				}
			}, 500);

			Intent intent = new Intent(SetWatchClockActivity.this, MainActivity.class);
			
			

			intent.putExtra("ConfigChanged", true);
			intent.putExtra("ClockInfo", mClockInfo);
			setResult(0, intent);
			finish();
			break;
		}
	}

	@Override
	public void onPositiveButtonClicked(int weekdays) {
		mClockInfo.setmDays(weekdays);
		txtWeekday.setText(DateUtils.getWeekdays(weekdays));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.ACTION_DOWN) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
