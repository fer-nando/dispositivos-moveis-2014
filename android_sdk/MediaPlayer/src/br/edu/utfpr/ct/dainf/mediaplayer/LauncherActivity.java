package br.edu.utfpr.ct.dainf.mediaplayer;

import br.edu.utfpr.ct.dainf.mediaplayer.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LauncherActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
	}
	
	public void onAudioSelected(View v) {
		launchActivity("audio");
	}
	
	public void onVideoSelected(View v) {
		launchActivity("video");
	}

	public void launchActivity(String type) {
		Intent intent = new Intent(this, SelectionActivity.class);
	    intent.putExtra("TYPE", type);
	    startActivity(intent);
	}
}
