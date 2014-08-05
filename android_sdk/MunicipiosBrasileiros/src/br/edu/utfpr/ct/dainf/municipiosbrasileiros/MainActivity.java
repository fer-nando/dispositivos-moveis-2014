package br.edu.utfpr.ct.dainf.municipiosbrasileiros;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	final Locale myLocale = new Locale("pt", "BR");

	private DatabaseCopyHelper helper;
	private SQLiteDatabase db;
	
	private TextView tvMun, tvLong, tvLat, tvAlt, tvIdMun, tvCod;
	private Spinner spEstados, spMunicipios;
	private ArrayAdapter<String> adapEstados, adapMunicipios;
	private String [] estados, municipios;
	private Cursor cursor;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tvMun = (TextView) findViewById(R.id.tvMunicipio);
		tvLong = (TextView) findViewById(R.id.tvLong);
		tvLat = (TextView) findViewById(R.id.tvLat);
		tvAlt = (TextView) findViewById(R.id.tvAlt);
		tvIdMun = (TextView) findViewById(R.id.tvIDMun);
		tvCod = (TextView) findViewById(R.id.tvCodMun);

		helper = new DatabaseCopyHelper(this);

		try {
			helper.criaBaseDados();
		} catch (IOException e) {
			e.printStackTrace();
		}

		db = helper.getReadableDatabase();

		cursor = db.rawQuery("SELECT `ESTADO_ID`,`NOME` FROM `estados`"
				+ " ORDER BY `NOME` ASC;", null);
		estados = new String[cursor.getCount()+1];
		
		estados[0] = "";
		if (cursor.moveToFirst()) {
			do {
				estados[cursor.getPosition()+1] = cursor.getString(1);
			} while(cursor.moveToNext());
		}
		
		cursor.close();
		
		spEstados = (Spinner) findViewById(R.id.spUF);	
		adapEstados = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_dropdown_item, estados);		
		adapEstados.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);		
		spEstados.setAdapter(adapEstados);
		
		spEstados.setOnItemSelectedListener(new UFListener(this));

		spMunicipios = (Spinner) findViewById(R.id.spMunicipio);	
		spMunicipios.setOnItemSelectedListener(new MunicipioListener(this));
		spMunicipios.setEnabled(false);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}
	
	class UFListener implements OnItemSelectedListener {
		Context context;
		
		public UFListener(Context c) { context = c; }
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {

			if(position > 0) {	
				
				String estado = (String) parent.getSelectedItem();
				String query = "SELECT `NOME` FROM `municipios` WHERE "
						+ "`ESTADO_ID`=(SELECT `ESTADO_ID` FROM `estados`"
						+ "WHERE `NOME`='" + estado + "') ORDER BY `NOME` ASC";
				System.out.println(query);
				cursor = db.rawQuery(query, null);
				municipios = new String[cursor.getCount()+1];
				
				municipios[0] = "";
				if (cursor.moveToFirst()) {
					do {
						municipios[cursor.getPosition()+1] = cursor.getString(0);
					} while(cursor.moveToNext());
				}
				
				cursor.close();
				spMunicipios.setEnabled(true);
			} else {
				municipios = new String[] { "" };
				spMunicipios.setEnabled(false);
			}
			
			adapMunicipios = new ArrayAdapter<String>(context, 
					android.R.layout.simple_spinner_dropdown_item, municipios);
			adapMunicipios.setDropDownViewResource(
					android.R.layout.simple_spinner_dropdown_item);
			spMunicipios.setAdapter(adapMunicipios);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}		
	}
	
	class MunicipioListener implements OnItemSelectedListener {
		Context context;
		
		public MunicipioListener(Context c) { context = c; }

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
						
			String municipio = (String) parent.getSelectedItem();
			municipio = municipio.replace("'", "''");
			String query = "SELECT * FROM `municipios` WHERE `NOME`='" 
							+ municipio + "'";
			System.out.println(query);
			cursor = db.rawQuery(query, null);
						
			if(position > 0 && cursor.moveToFirst()) {
				tvMun.setText(cursor.getString(1));
				tvIdMun.setText(String.valueOf(cursor.getInt(2)));
				tvCod.setText(String.valueOf(cursor.getInt(4)));
				tvLong.setText(String.valueOf(cursor.getDouble(5)));
				tvLat.setText(String.valueOf(cursor.getDouble(6)));
				tvAlt.setText(String.valueOf(cursor.getDouble(7)));
			} else {
				tvMun.setText("<Nome do Município>");
				tvIdMun.setText("");
				tvCod.setText("");
				tvLong.setText("");
				tvLat.setText("");
				tvAlt.setText("");
			}
			
			cursor.close();			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}
	}
}
