package com.wellav.dolphin.mmedia.ui;

import com.wellav.dolphin.mmedia.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Copyright 2015 Wellav
 * 
 * All rights reserved.
 * @date2015年12月11日
 * @author Cheng
 */
public class AlbumTab extends LinearLayout implements android.view.View.OnClickListener{
	
	private int mItemCount;//tab中item的个数
	private String[] mBtnText;
	private int mCurIndex=0;
	private Button[] mButtons;
	private Drawable mDrawable;
	private Drawable mChkDrawable;
	private OnItemClickedListener mListener;
	public interface OnItemClickedListener{
		/**
		 * @param clickItem 被点击的item的下标值
		 */
		public void onItemClicked(int clickItem);
	}
	
	/** 
	 * 设置item点击事件监听，并给每个item的text属性赋值
	 * @param lis
	 * @param btnTxt
	 */
	public void init(OnItemClickedListener lis,String[] btnTxt) {
		this.mListener=lis;
		this.mBtnText=btnTxt;
		mButtons=new Button[mItemCount];
		this.setWeightSum(mItemCount);
		LinearLayout.LayoutParams param=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		param.weight=1;
		for(int i=0;i<mItemCount;i++)
		{
			mButtons[i]=new Button(getContext());
			mButtons[i].setLayoutParams(param);			
			mButtons[i].setText(mBtnText[i]);
			mButtons[i].setTag(i);
			mButtons[i].setBackgroundDrawable(mDrawable);
			mButtons[i].setOnClickListener(this);
			mButtons[i].setTextAppearance(getContext(), R.style.H3);
			mButtons[i].setPadding(0, 0, 0, 0);
			this.addView(mButtons[i]);
		}
		mButtons[mCurIndex].setTextAppearance(getContext(), R.style.H1);
		mButtons[mCurIndex].setBackgroundDrawable(mChkDrawable);
	}
	public AlbumTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray tArr=context.obtainStyledAttributes(attrs, R.styleable.AlbumTab);
		mItemCount=tArr.getInt(R.styleable.AlbumTab_itemCount, 0);
		mDrawable=tArr.getDrawable(R.styleable.AlbumTab_itemBckgrdSrc);
		mChkDrawable=tArr.getDrawable(R.styleable.AlbumTab_itemChkBckgrdSrc);
		tArr.recycle();	
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		int tmpCur=Integer.valueOf(v.getTag().toString());
		
		if(mCurIndex!=tmpCur)
		{
			mButtons[mCurIndex].setBackgroundDrawable(mDrawable);	
			mButtons[mCurIndex].setTextAppearance(getContext(), R.style.H3);
			mCurIndex=	tmpCur;
			mButtons[mCurIndex].setBackgroundDrawable(mChkDrawable);
			mButtons[mCurIndex].setTextAppearance(getContext(), R.style.H1);
			if(mListener!=null)
				mListener.onItemClicked(mCurIndex);
		}
		
	}

}
