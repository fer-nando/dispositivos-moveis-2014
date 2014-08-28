package com.example.chartview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ChartView extends View {
	
	
	private static final int background_color = 0xff000000;
	private static final int line_color = 0xffffff00;
	private static final int border_color = 0xffffffff;
	
	private int dataIndex = 0;
	private int dataLenght = 400; 
	private float[] data;

    RectF mBounds, mBorder;
    Paint pBackground, pLine, pBorder;
    float borderWidth = 2.0f;
    float amplitude = 5.0f;

	public ChartView(Context context) {
		super(context);
		init();
	}
	
	public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
	}
	
	public void init() {
		
		data = new float[dataLenght];		
		for(int i = 0; i < dataLenght; i++) {
			data[i] = 0;
		}
		
		pLine = new Paint(Paint.ANTI_ALIAS_FLAG);
		pLine.setColor(line_color);
		pLine.setStrokeWidth(2.0f);
		
		pBackground = new Paint();
		pBackground.setColor(background_color);
		pBackground.setStyle(Paint.Style.FILL);
		
		pBorder = new Paint();
		pBorder.setColor(border_color);
		pBorder.setStyle(Paint.Style.STROKE);
		pBorder.setStrokeWidth(2.0f);
		
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub

	}
	
	@Override
    protected void onDraw(Canvas canvas) {
		float scaleY = mBorder.height() / (float)(amplitude);
		float scaleX = mBorder.width() / (dataLenght);
		float offsetY = mBorder.centerY();
		float offsetX = mBorder.left;
		float ax,bx,ay,by;
		int counter = 0;
		
		canvas.drawRect(mBounds, pBackground);
		canvas.drawRect(mBounds, pBorder);
		
		//long start = System.currentTimeMillis();
		for(int i = 1; i < dataLenght; i++) {
			if(i != dataIndex) {
				bx = (float)(offsetX + scaleX * i);
				by = (float)(offsetY - scaleY * data[i]);
				ax = (float)(bx - scaleX);
				ay = (float)(offsetY - scaleY * data[i-1]);
				//System.out.println("line("+ax+", "+ay+", "+bx+", "+by+")");
				
				if(counter == 0) {
					int degradation;
					if(i < dataIndex)
						degradation = i - dataIndex + dataLenght;
					else
						degradation = i - dataIndex;
					float factor = (float)degradation / dataLenght;
					int r = (int)(Color.red(line_color) * factor);
					int g = (int)(Color.green(line_color) * factor);
					int b = (int)(Color.blue(line_color) * factor);
					pLine.setColor(Color.rgb(r, g, b));
					//canvas.drawText(String.valueOf(degradation), ax, mBorder.bottom, pBorder);
				}
				if(++counter == dataLenght/100)
					counter = 0;

				canvas.drawLine(ax, ay, bx, by, pLine);				

			} else {
				counter = 0;
			}
			
		}
		//long end = System.currentTimeMillis();
		//System.out.println("Draw time = " + (end-start) + " ms.");
	}
	
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBounds = new RectF(0, 0, w, h);
        mBorder = new RectF(borderWidth, borderWidth, w-2*borderWidth, h-2*borderWidth);
        System.out.println("size = (" + w + "," + h + ")");
    }

	public void setAmplitude(float value) {
		amplitude = value;
	}
	
	public void addValue(float value) {
		data[dataIndex++] = value;
		if(dataIndex >= dataLenght)
			dataIndex = 0;
	}
	
	public void addValues(float[] values) {
		for(float value : values)
			addValue(value);
	}
	

}
