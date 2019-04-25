package com.wellav.dolphin.mmedia.ui;

import com.readystatesoftware.viewbadger.BadgeView;
import com.wellav.dolphin.mmedia.R;
import com.wellav.dolphin.mmedia.commands.SysConfig;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Copyright 2015 Wellav
 * 
 * All rights reserved.
 * 
 * @date2015年12月14日
 * @author Cheng
 */
public class MyTab extends LinearLayout implements
		android.view.View.OnClickListener {

	private String mBtnText[] = SysConfig.TABTITLE;// tab的title
	private Drawable mBtnDrawable_normal[];
	private Drawable mBtnDrawable_pressed[];
	private int mCurIndex = 0;
	private Button[] mButtons;
	private OnItemClickedListener mListener;
	private Context mContext;

	public interface OnItemClickedListener {
		/**
		 * @param clickItem
		 *            被点击的item的下标值
		 */
		public void onItemClicked(int clickItem);
	}

	public MyTab(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 设置item点击事件监听，并给每个item的text属性赋值
	 * 
	 * @param lis
	 * @param btnTxt
	 */
	public void init(Context context,OnItemClickedListener lis) {
		this.mContext = context;
		this.mListener = lis;
		int mItemCount = mBtnText.length;
		Resources res = getResources();
		mBtnDrawable_normal = new Drawable[mItemCount];
		mBtnDrawable_normal[0] = res.getDrawable(R.drawable.tab1_dolphin_nor);
		mBtnDrawable_normal[1] = res.getDrawable(R.drawable.tab2_contacts_nor);
		mBtnDrawable_normal[2] = res.getDrawable(R.drawable.tab3_find_nor);
		mBtnDrawable_normal[3] = res.getDrawable(R.drawable.tab4_mine_nor);

		mBtnDrawable_pressed = new Drawable[mItemCount];
		mBtnDrawable_pressed[0] = res.getDrawable(R.drawable.tab1_dolphin_foc);
		mBtnDrawable_pressed[1] = res.getDrawable(R.drawable.tab2_contacts_foc);
		mBtnDrawable_pressed[2] = res.getDrawable(R.drawable.tab3_find_foc);
		mBtnDrawable_pressed[3] = res.getDrawable(R.drawable.tab4_mine_foc);
		mButtons = new Button[mItemCount];
		this.setWeightSum(mItemCount);
		LinearLayout.LayoutParams param = new LayoutParams(0,
				LayoutParams.WRAP_CONTENT);
		param.weight = 1;
		for (int i = 0; i < mItemCount; i++) {
			mButtons[i] = new Button(getContext());
			mButtons[i].setLayoutParams(param);
			mButtons[i].setBackgroundColor(Color.TRANSPARENT);
			mButtons[i].setTag(i);
			mButtons[i].setOnClickListener(this);
			mButtons[i].setTextAppearance(getContext(), i!=mCurIndex?R.style.H8:R.style.H9);
			mButtons[i].setText(mBtnText[i]);
			setTopDrawable(mButtons[i], i!=mCurIndex?mBtnDrawable_normal[i]:mBtnDrawable_pressed[i]);
			this.addView(mButtons[i]);
		}
	}
	
//	public void redPoint(boolean is,int item){
//		BadgeView badge = new BadgeView(this.mContext, mButtons[item]);
//		badge.setHeight(this.mContext.getResources().getDimensionPixelSize(R.dimen.point_height));
//		badge.setWidth(this.mContext.getResources().getDimensionPixelSize(R.dimen.point_width));
//		badge.setBadgeMargin(0);
//		badge.setBackgroundResource(R.drawable.redpoint_10dp);
//		badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//		badge.show();
//	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int tmpCur = Integer.valueOf(v.getTag().toString());
		if (mCurIndex != tmpCur) {
			setTopDrawable(mButtons[mCurIndex],mBtnDrawable_normal[mCurIndex]);
			mButtons[mCurIndex].setTextAppearance(getContext(), R.style.H8);
			mCurIndex = tmpCur;
			setTopDrawable(mButtons[mCurIndex],mBtnDrawable_pressed[mCurIndex]);
			mButtons[mCurIndex].setTextAppearance(getContext(), R.style.H9);
			mListener.onItemClicked(mCurIndex);
		}
		
	}
	private void setTopDrawable(View view,Drawable img)
	{
		img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
		((Button) view).setCompoundDrawables(null, img, null, null); 
	}

}
