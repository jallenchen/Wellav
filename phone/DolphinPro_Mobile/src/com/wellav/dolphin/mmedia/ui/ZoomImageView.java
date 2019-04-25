package com.wellav.dolphin.mmedia.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * @author cf
 *
 */
@SuppressLint("FloatMath") public class ZoomImageView extends ImageView {
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;
	private static final float MAX_SCALE = 3;
	private static float mStartScale;
	// 第一个按下的手指的点
	private PointF startPoint = new PointF();
	private int mWidth;
	private int mHeight;
	// 初始的两个手指按下的触摸点的距离
	private float oriDis = 1f;
	private boolean isZoomed=false;
	public ZoomImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// mGestureDetector=new GestureDetector(context, new GestureListener());
		
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		mWidth=getWidth();
		mHeight=getHeight();
	}
	private boolean mPrepared = false;//
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (!mPrepared) {
			float[] values = new float[9];
			getImageMatrix().getValues(values);
			mStartScale = values[Matrix.MSCALE_X];
			mPrepared=true;
		}

	}
	
	/**
	 * @return 双击后应该放大的scale值
	 */
	private float getScacleAfterDoubleClick() {
		float[] values = new float[9];
		getImageMatrix().getValues(values);

		float scale = values[Matrix.MSCALE_X];
		if (scale != mStartScale)
		{
			isZoomed=false;
			return mStartScale / scale;
		}
		else
		{
			isZoomed=true;
			return 2.0f;
		}
	}


	private long startTime = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isZoomed)
			getParent().requestDisallowInterceptTouchEvent(true);
		// 进行与操作是为了判断多点触摸
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			// 第一个手指按下事件
			matrix.set(savedMatrix);
			long interval = event.getEventTime() - startTime;
//			Log.e("", "now-st=" + interval + "st=" + startTime);

			if (interval < 300&&50<interval) {
				//双击事件处理
//				Log.e("", "double click");
				matrix.set(getImageMatrix());

				float scale = getScacleAfterDoubleClick();
//				Log.e("", "scale=" + scale);
				matrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
				setImageMatrix(matrix);
				if(scale<2)
				{
					matrix.setTranslate(0, 0);
					setImageMatrix(matrix);
				}

				break;
			}

			startTime = event.getEventTime();
			matrix.set(getImageMatrix());
			savedMatrix.set(matrix);
			startPoint.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			// 第二个手指按下事件
			
//			Log.e("", "double down");
			oriDis = distance(event);
			if (oriDis > 10f) {
				savedMatrix.set(matrix);
//				midPoint = middle(event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
			// 手指放开事件
			mode = NONE;
			
			break;
		case MotionEvent.ACTION_POINTER_UP:
			// 手指放开事件

			mode = NONE;
			
			break;
		case MotionEvent.ACTION_MOVE:
			setScaleType(ScaleType.MATRIX);
			// 手指滑动事件
			if (mode == DRAG) {
				// 是一个手指拖动				
				
				float[] values = new float[9];
				matrix.getValues(values);
				float dx=event.getX() - startPoint.x;
				float dy=event.getY()- startPoint.y;
				checkBound(dx, dy, values);

				
			} else if (mode == ZOOM) {
				// 两个手指滑动
				float newDist = distance(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oriDis;
					float[] values = new float[9];
					matrix.getValues(values);
					scale = checkMaxScale(scale, values);
					values = new float[9];
					matrix.getValues(values);
					checkBound(0, 0, values);
				}
			}
			break;
		}

		// 设置ImageView的Matrix
		this.setImageMatrix(matrix);
		return true;
	}
	/**
	 * 移动不能超出控件边界
	 * @param dx横向移动距离
	 * @param dy纵向移动距离
	 * @param values
	 */
	private void checkBound(float dx,float dy,float[] values)
	{
		float wlen=mWidth*values[Matrix.MSCALE_X]-mWidth;
		float minTX=-wlen;
		float hlen=mHeight*values[Matrix.MSCALE_Y]-mHeight;
		float minTY=-hlen;
		float tx=0;
		if((values[Matrix.MTRANS_X]+dx)>=minTX&&(values[Matrix.MTRANS_X]+dx)<=0)
		{
			tx=dx;
		}
		else if((values[Matrix.MTRANS_X]+dx)>0)
		{
			tx=0-values[Matrix.MTRANS_X];
		}
		else if(values[Matrix.MTRANS_X]+dx<minTX)
		{
			tx=minTX-values[Matrix.MTRANS_X];
		}
		float ty=0;
		if(values[Matrix.MTRANS_Y]+dy>=minTY&&values[Matrix.MTRANS_Y]+dy<=0)
			ty=dy;
		else if(values[Matrix.MTRANS_Y]+dy>0)
			ty=0-values[Matrix.MTRANS_Y];
		else if(values[Matrix.MTRANS_Y]+dy<minTY)
			ty=minTY-values[Matrix.MTRANS_Y];
		matrix.postTranslate(tx, ty);
	}
	/**
	 * 检验scale，使图像缩放后不会超出最大倍数，不会小于最小倍数
	 * 
	 * @param scale 根据手指动作计算出的放大系数
	 * @param values
	 * @return 返回实际放大系数
	 */
	private float checkMaxScale(float scale, float[] values) {

		if (scale * values[Matrix.MSCALE_X] >= MAX_SCALE)
		{
			isZoomed=true;
			scale = MAX_SCALE / values[Matrix.MSCALE_X];
		}
		else if (scale * values[Matrix.MSCALE_X] <=mStartScale)
		{
			isZoomed=false;
			scale = mStartScale / values[Matrix.MSCALE_X];
		}
		else
			isZoomed=true;
		matrix.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
		return scale;
	}

	/**
	 * 计算两个触摸点之间的距离
	 * @param event
	 * @return
	 */
	private float distance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 计算两个触摸点的中点
	 * @param event
	 * @return
	 */
//	private PointF middle(MotionEvent event) {
//		float x = event.getX(0) + event.getX(1);
//		float y = event.getY(0) + event.getY(1);
//		return new PointF(x / 2, y / 2);
//	}
	
	/**
	 * 调用该方法恢复初始状态
	 */
	private void restoreScale(){
		matrix.setTranslate(0, 0);
		setImageMatrix(matrix);
		isZoomed=false;
	}
	/**
	 * 调用该方法图片旋转90°
	 */
	public void rotateBy90()
	{
		Drawable d = getDrawable(); // xxx根据自己的情况获取drawable
		if(d==null)
			return;
		BitmapDrawable bd = (BitmapDrawable) d;
		Bitmap bitmap = bd.getBitmap();
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		setImageBitmap(bm);
		restoreScale();
	}
	
}
