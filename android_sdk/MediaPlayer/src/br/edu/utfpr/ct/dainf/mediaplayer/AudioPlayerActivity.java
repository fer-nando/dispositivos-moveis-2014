package br.edu.utfpr.ct.dainf.mediaplayer;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class AudioPlayerActivity extends Activity {
	

	private MediaPlayer mp;
	private TextView titleText;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_audio_player);
				
		titleText = (TextView) findViewById(R.id.textView1);
					
		startAudioPlayer();

	}	
	
	private void startAudioPlayer() {
		
		mp = new MediaPlayer();
		Uri uri = getIntent().getData();
		List<String> pathPieces = uri.getPathSegments();
		
		titleText.setText(pathPieces.get(pathPieces.size()-1));
		
		try {
			mp.setDataSource(this, uri);
			mp.prepare();
			mp.start();
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void onPlayClicked(View v) {
		mp.start();
	}
	
	public void onPauseClicked(View v) {
		mp.pause();
	}
	
	public void onStopClicked(View v) {
		mp.stop();
	}
	
	public void onStop() {
		super.onStop();
		mp.release();
		mp = null;
	}
	
}
