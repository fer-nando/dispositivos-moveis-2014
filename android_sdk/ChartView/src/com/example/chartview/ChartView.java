package com.example.chartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;

public class ChartView extends View {
	
	public static final int DATA_LENGHT = 200; 
	
	private int dataIndex = 0, lastIndex = 0;
	private double[] data = new double[DATA_LENGHT];

    RectF mBounds;
    Paint pBackground, pLine;
    double amplitude = 1;

	public ChartView(Context context) {
		super(context);
		init();
	}
	
	public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
	}
	
	public void init() {
		for(int i = 0; i < DATA_LENGHT; i++)
			data[i] = 0;
		
		pLine = new Paint(Paint.ANTI_ALIAS_FLAG);
		pLine.setColor(0xff0000ff);
		pLine.setStrokeWidth(2.0f);
		
		pBackground = new Paint();
		pBackground.setColor(0xff000000);
		pBackground.setStyle(Paint.Style.FILL);
		
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub

	}
	
	@Override
    protected void onDraw(Canvas canvas) {
		float scaleY = 0.9f * mBounds.height() / (float)(amplitude) - getPaddingBottom() - getPaddingTop();
		float scaleX = mBounds.width() / (DATA_LENGHT) - getPaddingLeft() - getPaddingRight();
		float ax,bx,ay,by;
		
		canvas.drawRect(mBounds, pBackground);
		
		for(int i = 1; i < DATA_LENGHT; i++) {
			if(i != dataIndex) {
				bx = (float)(scaleX * i + getPaddingLeft());
				by = (float)(mBounds.centerY() - scaleY * data[i]);
				ax = (float)(bx - scaleX);
				ay = (float)(mBounds.centerY() - scaleY * data[i-1]);
				//System.out.println("line("+ax+", "+ay+", "+bx+", "+by+")");
				int colorDegradation = dataIndex - i;
				colorDegradation += (colorDegradation < 0) ? DATA_LENGHT : 0;
				colorDegradation = 0xff * colorDegradation / DATA_LENGHT;
				pLine.setColor(0xff0000ff - colorDegradation);
				canvas.drawLine(ax, ay, bx, by, pLine);
			}
		}
	}
	
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBounds = new RectF(0, 0, w, h);
        System.out.println("size = (" + w + "," + h + ")");
    }

	public void setAmplitude(double value) {
		amplitude = value;
	}
	
	public void addValue(double value) {
		lastIndex = dataIndex;
		data[dataIndex++] = value;
		if(dataIndex >= DATA_LENGHT)
			dataIndex = 0;
	}

}
