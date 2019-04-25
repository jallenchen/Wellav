package com.wellav.dolphin.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import java.util.TimeZone;

import com.wellav.dolphin.launcher.R;

public class AnalogClock extends View {
	
	public AnalogClock(Context context) {
		super(context);
	}

	private Time mCalendar;

	private Drawable mHourHand;
	private Drawable mMinuteHand;
	private Drawable mDial;
	
	private int mDialWidth;
	private int mDialHeight;

	private boolean mAttached;
	
	private final Handler mHandler = new Handler();
	private float mMinutes;
	private float mHour;
	private boolean mChanged;

	Context mContext;
	public AnalogClock(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AnalogClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		
		Resources r = context.getResources();
		mDial = r.getDrawable(R.drawable.clock_round);
		mHourHand = r.getDrawable(R.drawable.clock_needlehour);
		mMinuteHand = r.getDrawable(R.drawable.clock_needlemin);

		mCalendar = new Time();
		mDialWidth = mDial.getIntrinsicWidth();
		mDialHeight = mDial.getIntrinsicHeight();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (!mAttached) {
			mAttached = true;
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

			getContext().registerReceiver(mIntentReceiver, filter, null, mHandler);
		}
		mCalendar = new Time();
		//调用时间改变动画方法
		onTimeChanged();
		//开始计时
		counter.start();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mAttached) {
			counter.cancel();
			getContext().unregisterReceiver(mIntentReceiver);
			mAttached = false;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		float wScale = 1.0f;
		float hScale = 1.0f;
		if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
			wScale = (float) widthSize / (float) mDialWidth;
		}
		if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
			hScale = (float) heightSize / (float) mDialHeight;
		}

		float scale = Math.min(wScale, hScale);

		setMeasuredDimension(resolveSize((int) (mDialWidth * scale), widthMeasureSpec),
				resolveSize((int) (mDialHeight * scale), heightMeasureSpec));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mChanged = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		boolean changed = mChanged;
		if (changed) {
			mChanged = false;
		}
		boolean seconds = mSeconds;
		if (seconds) {
			mSeconds = false;
		}
		int availableWidth = 340;
		int availableHeight = 340;

		int x = availableWidth / 2;
		int y = availableHeight / 2;

		final Drawable dial = mDial;
		int w = dial.getIntrinsicWidth();
		int h = dial.getIntrinsicHeight();

		boolean scaled = false;
		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float)availableWidth/(float)w, (float)availableHeight/(float)h);
			canvas.save();
			canvas.scale(scale, scale, x, y);
		}
		if (changed) {
			dial.setBounds(x - (w/2), y - (h/2), x + (w/2), y + (h/2));
		}
		dial.draw(canvas);

		
		if(mHour>12){
			mHour = mHour - 12;
		}
		canvas.save();
		canvas.rotate(mHour / 12.0f * 360.0f - 90, x, y);
		final Drawable hourHand = mHourHand;
		if (changed) {
			w = hourHand.getIntrinsicWidth();
			h = hourHand.getIntrinsicHeight();
			hourHand.setBounds(x-20, y-26, x+w-20, y+h-26);
		}
		hourHand.draw(canvas);
		canvas.restore();

		canvas.save();
		canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);
		final Drawable minuteHand = mMinuteHand;
		if (changed) {
			w = minuteHand.getIntrinsicWidth();
			h = minuteHand.getIntrinsicHeight();
			minuteHand.setBounds(x-w+20, y-h+26, x+20, y+26);
		}
		minuteHand.draw(canvas);
		if (scaled) {
			canvas.restore();
		}
	}

	MyCount counter = new MyCount(10000, 1000);
	public class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}
		@Override
		public void onFinish() {
			counter.start();
		}
		@Override
		public void onTick(long millisUntilFinished) {
			mCalendar.setToNow();

			int hour = mCalendar.hour;
            int minute = mCalendar.minute;
            int second = mCalendar.second;

            mSecond = 6.0f * second;
            mSeconds = true;
            AnalogClock.this.invalidate();
		}
	}

	// 时间广播
	boolean mSeconds = false;
	float mSecond = 0;
	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				String tz = intent.getStringExtra("time-zone");
				mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
			}
			//调用时间改变动画方法
			onTimeChanged();
			invalidate();
		}
	};
	
	// 时间改变动画
	private void onTimeChanged() {
		mCalendar.setToNow();

		int hour = mCalendar.hour;
		int minute = mCalendar.minute;
		int second = mCalendar.second;

		mMinutes = minute + second / 60.0f;
		mHour = hour + mMinutes / 60.0f;
		mChanged = true;
	}

}