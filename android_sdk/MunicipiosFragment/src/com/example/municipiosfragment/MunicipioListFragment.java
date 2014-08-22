package com.example.municipiosfragment;

import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;


/**
 * A list fragment representing a list of Estado. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link MunicipioDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class MunicipioListFragment extends Fragment {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private static final String STATE_SCROLLY_POSITION = "scrolly_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = Spinner.INVALID_POSITION;
	private int mScrollYPosition = ListView.SCROLLBAR_POSITION_DEFAULT;
	

	private DatabaseCopyHelper helper;
	private SQLiteDatabase db;
	private Spinner spEstados;
	private ListView lvMunicipios;
	private ArrayAdapter<String> adapEstados, adapMunicipios;
	private String [] estados, municipios;
	private Cursor cursor;
	
	

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id, Municipio municipio);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id, Municipio municipio) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MunicipioListFragment() {
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View view = inflater.inflate(R.layout.fragment_municipio_list, container, false);

		

		helper = new DatabaseCopyHelper(getActivity());

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
		
		spEstados = (Spinner) view.findViewById(R.id.spinner1);	
		adapEstados = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_spinner_dropdown_item, estados);		
		adapEstados.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);		
		spEstados.setAdapter(adapEstados);		
		spEstados.setOnItemSelectedListener(new UFListener(getActivity()));

		lvMunicipios = (ListView) view.findViewById(R.id.listView1);
		lvMunicipios.setOnItemClickListener(new MunicipioListener(getActivity()));
		lvMunicipios.setEnabled(false);
		
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null) {
			if(savedInstanceState.containsKey(STATE_ACTIVATED_POSITION))
				spEstados.setSelection(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
			if(savedInstanceState.containsKey(STATE_SCROLLY_POSITION))
				lvMunicipios.setScrollY(savedInstanceState.getInt(STATE_SCROLLY_POSITION));
		}
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != Spinner.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
		if (mScrollYPosition != ListView.SCROLLBAR_POSITION_DEFAULT) {
			outState.putInt(STATE_SCROLLY_POSITION, mScrollYPosition);
		}
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
				
				if (cursor.moveToFirst()) {
					do {
						municipios[cursor.getPosition()] = cursor.getString(0);
					} while(cursor.moveToNext());
				}
				
				cursor.close();
				lvMunicipios.setEnabled(true);
			} else {
				municipios = new String[] { "" };
				lvMunicipios.setEnabled(false);
			}
			
			adapMunicipios = new ArrayAdapter<String>(context, 
					android.R.layout.simple_list_item_1, municipios);
			lvMunicipios.setAdapter(adapMunicipios);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}		
	}
	
	class MunicipioListener implements OnItemClickListener {
		Context context;
		
		public MunicipioListener(Context c) { context = c; }

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
						
			String nomeMunicipio = (String) parent.getItemAtPosition(position);
			nomeMunicipio = nomeMunicipio.replace("'", "''");
			String query = "SELECT * FROM `municipios` WHERE `NOME`='" 
							+ nomeMunicipio + "'";
			System.out.println(query);
			cursor = db.rawQuery(query, null);
						
			Municipio municipio = new Municipio();			
			
			if(cursor.moveToFirst()) {				
				municipio.setNm_municipio(cursor.getString(1));
				municipio.setId(cursor.getInt(2));
				municipio.setCd_geocodmu(cursor.getInt(4));
				municipio.setLongitude(cursor.getDouble(5));
				municipio.setLatitude(cursor.getDouble(6));
				municipio.setAltitude(cursor.getDouble(7));
				mCallbacks.onItemSelected(nomeMunicipio, municipio);
			}
			
			mScrollYPosition = lvMunicipios.getScrollY();
			cursor.close();			
		}
	}

}
