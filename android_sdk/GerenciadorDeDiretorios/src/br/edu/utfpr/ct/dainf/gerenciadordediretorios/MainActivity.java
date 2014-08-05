package br.edu.utfpr.ct.dainf.gerenciadordediretorios;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    
	AlertDialog alert;
    ArrayAdapter<String> dirAdapter;
	ArrayList<String> dirNames = new ArrayList<String>();;
	File fileList[];
	ListView listView;
	String currPath;
	TextView header;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listView = (ListView) findViewById(R.id.listView);		
		
		String state = Environment.getExternalStorageState();
		
		if(Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			
				createListView();				
		}
	}
	
	public void createListView() {
		
		if (getIntent().hasExtra("DIR_PATH")) {
			currPath = getIntent().getStringExtra("DIR_PATH");
			fileList = new File(currPath).listFiles();			
			for (File dir : fileList) {
				dirNames.add(dir.getName());
			}			
		} else { 
			currPath = Environment.getExternalStorageDirectory().getPath();
			dirNames.add(Environment.DIRECTORY_ALARMS);
			dirNames.add(Environment.DIRECTORY_DCIM);
			dirNames.add(Environment.DIRECTORY_DOWNLOADS);
			dirNames.add(Environment.DIRECTORY_MOVIES);
			dirNames.add(Environment.DIRECTORY_MUSIC);
			dirNames.add(Environment.DIRECTORY_NOTIFICATIONS);
			dirNames.add(Environment.DIRECTORY_PICTURES);
			dirNames.add(Environment.DIRECTORY_PODCASTS);
			dirNames.add(Environment.DIRECTORY_RINGTONES);
			
		}		
		
		setTitle(currPath);
		
		dirAdapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, dirNames);
		listView.setAdapter(dirAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, 
		    		long id) {
		    	
		    	String filename = parent.getItemAtPosition(position).toString();
		    	String newPath = currPath + "/" + filename;
		    	File file = new File(newPath);
		    	
		    	if (file.isDirectory()) {
		    		if (file.canRead()) {
			    		Intent intent = new Intent(getApplicationContext(), 
			    				MainActivity.class);
			    		intent.putExtra("DIR_PATH", newPath);
			    		startActivity(intent);
		    		} else {
		    			Toast.makeText(getApplicationContext(), 
		    					"Cannot open directory " +
		    					newPath + ": Permission denied", 
		    					Toast.LENGTH_LONG).show();
		    		}
		    	} else {		
		    		Uri uri = Uri.fromFile(file);
		    		Intent intent = new Intent(Intent.ACTION_VIEW);
		    		intent.setDataAndType(uri, getMimeType(newPath));
		    		
		    		try {
		    			startActivity(intent);
		    		} catch(ActivityNotFoundException ex) {
		    			Toast.makeText(getApplicationContext(), uri.toString() + 
		    	    		  ": Activity not found", Toast.LENGTH_LONG).show();
		    		}
		        }		    		
		    }
		});	
	}
	
	public static String getMimeType(String url)
	{
	    String type = null;
	    String extension = MimeTypeMap.getFileExtensionFromUrl(url);
	    if (extension != null) {
	        MimeTypeMap mime = MimeTypeMap.getSingleton();
	        type = mime.getMimeTypeFromExtension(extension);
	    }
	    return type;
	}
}
