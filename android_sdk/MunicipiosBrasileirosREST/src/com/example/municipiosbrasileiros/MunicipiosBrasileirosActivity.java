package com.example.municipiosbrasileiros;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class MunicipiosBrasileirosActivity extends Activity {
	
	Spinner spinner;
	ListView listView;
	EditText editText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_municipios_brasileiros);
		
		listView = (ListView) findViewById(R.id.listView1);
		editText = (EditText) findViewById(R.id.editText1);
	}

	public void onRequest(View v) {

		final String baseUrl = "http://192.168.100.98:8080/MunicipiosBrasileirosREST/MunicipiosBrasileiros/uf/";
		String url = baseUrl + editText.getText();
		System.out.println("URL = " + url);
		
		new RequestMunicipiosTask().execute(url);
	}
	
	public void setListAdapter(ArrayAdapter<String> adapter) {
		listView.setAdapter(adapter);
	}
	
	/**
     * populate list in background while showing progress dialog.
     */
    private class RequestMunicipiosTask extends AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = 
        		new ProgressDialog(MunicipiosBrasileirosActivity.this);
    	private String[] municipios;
 
        @Override
        protected void onPreExecute() {
            // TODO i18n
            dialog.setMessage("Please wait..");
            dialog.show();
        }
         
        @Override
        protected Void doInBackground(String... urls) {
        	    		
            try {
        		String serviceResult = WebServiceUtil.requestWebService(urls[0]);
        		System.out.println(serviceResult);
        		municipios = serviceResult.split("\n");
            } catch (Throwable e) {
                // handle exceptions
        		System.err.println("ERRO REQUEST");
            }
    		
    		
            return null;
        }
         
        @Override
        protected void onPostExecute(Void unused) {
             
            // setListAdapter must not be called at doInBackground()
            // since it would be executed in separate Thread
        	if(municipios != null) {
	            setListAdapter(
	                new ArrayAdapter<String>(MunicipiosBrasileirosActivity.this, 
	                		android.R.layout.simple_list_item_1, municipios));
        	} else {
        		setListAdapter(null);
        	}
                     
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
	
	
}
