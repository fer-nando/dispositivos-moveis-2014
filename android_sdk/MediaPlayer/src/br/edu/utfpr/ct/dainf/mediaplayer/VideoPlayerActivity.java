package br.edu.utfpr.ct.dainf.mediaplayer;

import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;


public class VideoPlayerActivity extends Activity {
	

	private VideoView vv;
	private MediaController mc;
	private Uri uri;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_video_player);
				
		vv = (VideoView) findViewById(R.id.videoView1);
				
		uri = getIntent().getData();
		List<String> pathPieces = uri.getPathSegments();
		
		setTitle(pathPieces.get(pathPieces.size()-1));
		
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		startVideoView();
		
	}
	
	private void startVideoView() {
		mc = new MediaController(this);	
		vv.setMediaController(mc);
		mc.addOnAttachStateChangeListener(new VideoTouchListener(this));
		
		Uri uri = getIntent().getData();
		
		vv.setVideoURI(uri);
		vv.requestFocus();		
		vv.start();
	}
	
	public void onStop() {
		super.onStop();
		vv.stopPlayback();
	}
	
	
	public class VideoTouchListener implements OnAttachStateChangeListener {		
	    Activity activity;
		
	    public VideoTouchListener(Activity a) {
	    	activity = a;
	    }

		@Override
		public void onViewAttachedToWindow(View arg0) {
			activity.getActionBar().show();
		}

		@Override
		public void onViewDetachedFromWindow(View arg0) {
        	activity.getActionBar().hide();
		}
		
	}
	
}
