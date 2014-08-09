package com.example.chartview;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	Timer timer = new Timer();
	ChartView chart;
	
	final double amp = 5;
	final double f = 2;
	final double ts = f/30.0;
	double t = 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chart = (ChartView) findViewById(R.id.chartView1);
        chart.setAmplitude(amp);
    }

    
    Handler viewHandler = new Handler();
    Runnable updateView = new Runnable(){

		@Override
		public void run() {
			updateChart();
			chart.invalidate();
			viewHandler.postDelayed(updateView, (long)(1000*ts));
		}
    	
    };
    
    protected void onPostCreate(Bundle savedInstanceState) {
    	super.onPostCreate(savedInstanceState);
    	viewHandler.postDelayed(updateView, (long)(1000*ts));
    }
    
    private void updateChart() {
    	double newValue = amp * Math.sin(2*Math.PI*t/f) / 2;
    	t += ts;
		//System.out.println("value(" + t + ") = " + newValue);
    	chart.addValue(newValue);
    }
    
    class DataUpdateTask extends TimerTask {

		@Override
		public void run() {
			updateChart();
			chart.invalidate();
		}
    	
    }
    
    protected void onStop() {
    	super.onStop();
    	viewHandler.removeCallbacks(updateView);
    }
    
    
}
