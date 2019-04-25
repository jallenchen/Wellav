package com.wellav.dolphin.mmedia.ui;



import com.wellav.dolphin.mmedia.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

public class StatisticsView extends View {

	private Context mContext;
	public Canvas mCanvas;
	
	//默认边距
	private int margin;      
    
	//原点坐标 
	private int Xpoint;
	private int Ypoint;
  
	//X轴的单位长度
	private int Xscale;
	
	private String[] Xlabel;
	//设置纵轴数据
	private int[] Ylabel;	
	
	//时间峰值
	private int MaxTime;
	
	//纵坐标的实际可控长度
	private int Ynum;
	
	public StatisticsView(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
		this.mContext = context;
	}	
	public StatisticsView(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
		this.mContext = context;
	}
	
	//设置纵坐标	
	  public void setYlabeldata(int[] ylabel){
		this.Ylabel=ylabel;
	    invalidate();
	}
		
	//设置横坐标
	public void setXlabeldata(String[] xlabel){
		this.Xlabel=xlabel;
		invalidate();
	}
	
	//初始化数据
	private void init(){
		margin=mContext.getResources().getDimensionPixelOffset(R.dimen.margin);
		Xpoint=mContext.getResources().getDimensionPixelOffset(R.dimen.point_x);
		Ypoint=mContext.getResources().getDimensionPixelOffset(R.dimen.point_y);
		Xscale=mContext.getResources().getDimensionPixelOffset(R.dimen.scale_x);
		Ynum = mContext.getResources().getDimensionPixelOffset(R.dimen.num_y);
		
	}
	//画横轴
	private void drawXLine(Canvas canvas) {	
		Paint paintx=new Paint();
		paintx.setStyle(Style.FILL);
		paintx.setAntiAlias(true);
		paintx.setColor(Color.WHITE);
		canvas.drawLine(margin, Ypoint, this.getWidth()-margin, Ypoint, paintx);		
	}
	//画纵轴
	private void drawYLine(Canvas canvas) {	
		Paint painty=new Paint();
		painty.setStyle(Style.FILL);
		painty.setAntiAlias(true);
		painty.setColor(Color.WHITE);
		canvas.drawLine(Xpoint, Xpoint/2, Xpoint, Ypoint+Ypoint/10, painty);		
	}
	
	//画横轴的陪伴日期
    private void drawXLabel(Canvas canvas){   	
    	Paint paintText=new Paint();
    	paintText.setAntiAlias(true);
    	paintText.setColor(Color.WHITE);
    	paintText.setTextAlign(Align.CENTER);
    	paintText.setTextSize(25);
    	int num=Xlabel.length;
    	for (int i = 0; i < num; i++) {
			canvas.drawText(this.Xlabel[i], Xpoint+Xscale*i, Ypoint+Ypoint/5, paintText);
		}
    }
    
    //画折线
    private void drawYLabel(Canvas canvas){
    	Paint paintyl=new Paint();
    	paintyl.setAntiAlias(true);
    	paintyl.setTextSize(26);
    	paintyl.setColor(Color.WHITE);
    	int mY[] ={Ypoint,Ypoint,Ypoint,Ypoint,Ypoint,Ypoint,Ypoint} ;
    	for (int i = 0; i < 7; i++) {
    		//时间纵坐标=实际总长-实际总长*（获取值/峰值)
    		float scale=(float) (this.Ylabel[i]*1.0/MaxTime);
    		//int Y1=(int) (Ypoint-scale*Ypoint*0.4f);
    		mY[i] =(int) (Ypoint-scale*Ypoint*0.4f); 
    		if (mY[i]>Ypoint) {mY[i]=Ypoint;}
    		if(MaxTime == 0){
    			//通话时间为0
    			canvas.drawCircle(Xpoint+Xscale*i, mY[i]+Ypoint, 5, paintyl);
    		}else{
    			canvas.drawCircle(Xpoint+Xscale*i, mY[i], 5, paintyl);
    		}
		}
    	for (int i = 0; i < 6; i++) {
    		canvas.drawLine(Xpoint+Xscale*i, mY[i], Xpoint+Xscale*(i+1), mY[i+1], paintyl);
    	}
    	//画峰值标识
    	   int m=(int)MaxTime;
    	   String maxtime=String.valueOf(m);
    	 //  canvas.drawText(maxtime, margin, Ypoint*0.4f, paintyl);
    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(getResources().getColor(R.color.color1));
		init();
		drawXLine(canvas);
		drawYLine(canvas);
		/*if (Ylabel==null) {
			Toast.makeText(getContext(), "获取数据失败，请重新尝试", Toast.LENGTH_SHORT);
			return ;
		}*/
		if (Ylabel==null||Xlabel==null) {
			Toast.makeText(getContext(), "获取数据失败，请重新尝试", Toast.LENGTH_SHORT);
			
		} else {
			MaxTime=getMax(Ylabel);
			drawXLabel(canvas);
			drawYLabel(canvas);
		}
		
	}
	
	//获取纵坐标数组中的峰值
	public static int getMax(int[] array) {
	    int Max = array[0];
	    for (int i = 1; i < array.length; i++) {
	        if (Max < array[i]) {
	            Max = array[i];
	        }
	    }
	    return Max;
	}	
}
