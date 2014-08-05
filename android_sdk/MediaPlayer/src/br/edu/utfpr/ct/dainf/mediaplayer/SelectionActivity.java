package br.edu.utfpr.ct.dainf.mediaplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectionActivity extends Activity {
    
	ArrayList<String> audioExtensions = new ArrayList<String>(
			Arrays.asList("mp3", "mp4", "aac", "flac", "mid", "ogg", "wav"));
	ArrayList<String> videoExtensions = new ArrayList<String>(
			Arrays.asList("mp4", "3gp", "mkv", "webm"));
	
	AlertDialog alert;
    ArrayAdapter<String> dirAdapter;
	ArrayList<File> files = new ArrayList<File>();
	ArrayList<String> filesNames = new ArrayList<String>();
	ListView listView;
	String currPath;
	String type;
	TextView header;
	boolean isAudio = false,
			isVideo = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selection);
		
		listView = (ListView) findViewById(R.id.listView);		
		
		String state = Environment.getExternalStorageState();
		
		if(Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			
			createListView();				
		}
	}
	
	public String appendPath(String root, String dir) {
		return root + "/" + dir;
	}
	
	public void listAllByType(String path, String type) {
		File fileList[] = new File(path).listFiles();
		for (File dir : fileList) {
			if(dir.isDirectory()) {
				listAllByType(appendPath(path, dir.getName()), type);
			} else {
				String filename = dir.getName();
				int dotpos = filename.lastIndexOf('.') + 1;
				String extension = filename.substring(dotpos);
				
				if(type.equals("audio") && audioExtensions.contains(extension)
						|| type.equals("video") && videoExtensions.contains(extension)) {
					
					filesNames.add(filename);
					files.add(dir);
				}
			}
		}	
	}
	
	public void createListView() {
				
		if (getIntent().hasExtra("TYPE")) {
			
			type = getIntent().getStringExtra("TYPE");
			System.out.println("TYPE=" + type);
			
			currPath = Environment.getExternalStorageDirectory().getPath();
			
			String externPath = "/storage/sdcard1";
			File externDir = new File(externPath);
			
			
			if (type != null) {				
				if(type.equals("audio")) {					
					isAudio = true;
					appendPath(currPath, Environment.DIRECTORY_MUSIC);
					if(externDir.exists() && externDir.canRead()) {
						appendPath(externPath, Environment.DIRECTORY_MUSIC);
					}
					setTitle("Selecione um audio");
					
				} else if(type.equals("video")) {					
					isVideo = true;
					appendPath(currPath, Environment.DIRECTORY_MOVIES);
					if(externDir.exists() && externDir.canRead()) {
						appendPath(externPath, Environment.DIRECTORY_MOVIES);
					}
					setTitle("Selecione um video");			
				}
				
				listAllByType(currPath, type);
				if(externDir.exists() && externDir.canRead()) {
					listAllByType(externPath, type);
				}
			}
		}		
		
		
		dirAdapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, filesNames);
		listView.setAdapter(dirAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, 
		    		long id) {
		    	
		    	File file = files.get(position);
	    		Uri uri = Uri.fromFile(file);
	    		Intent intent = new Intent();
		    		    		
		    	if(isAudio) {
		    		intent.setClass(parent.getContext(), 
	    				AudioPlayerActivity.class);
		    	} else if(isVideo) {
		    		intent.setClass(parent.getContext(), 
		    				VideoPlayerActivity.class);
		    	}		    	
		    	
	    	    intent.putExtra("TYPE", type);
	    		intent.setData(uri);
	    			    		
	    		try {
	    			startActivity(intent);
	    		} catch(ActivityNotFoundException ex) {
	    			Toast.makeText(getApplicationContext(), uri.toString() + 
	    	    		  ": Activity not found", Toast.LENGTH_LONG).show();
	    		}	    		
		    }
		});	
	}
}
