package com.example.municipiosfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A fragment representing a single Municipio detail screen. This fragment is
 * either contained in a {@link MunicipioListActivity} in two-pane mode (on
 * tablets) or a {@link MunicipioDetailActivity} on handsets.
 */
public class MunicipioDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "municipio";
	public static final String ARG_ITEM_LAT = "latitude";
	public static final String ARG_ITEM_LNG = "longitude";
	public static final String ARG_MODE = "mode";

	
    private GoogleMap mMap;
    private String name;
    private double lat;
    private double lng;
    private boolean twoPane;
    private FragmentManager fragmentManager;
    

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MunicipioDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();

		if (args != null && getArguments().containsKey(ARG_MODE)) {
			twoPane = args.getString(ARG_MODE).equals(MunicipioListActivity.MODE_TWOPANE);
		}
		if (args != null && getArguments().containsKey(ARG_ITEM_ID)) {
			name = args.getString(ARG_ITEM_ID);
			lat = args.getDouble(ARG_ITEM_LAT);
			lng = args.getDouble(ARG_ITEM_LNG);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_municipio_detail,
				container, false);
		
		if(twoPane)
			fragmentManager = MunicipioListActivity.fragmentManager;
		else
			fragmentManager = MunicipioDetailActivity.fragmentManager;	

		setUpMapIfNeeded();
		
		if (mMap != null && name != null) {
			changeCamera();
		}

		return rootView;
	}
	
	private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
        	
            mMap = ((SupportMapFragment) fragmentManager.findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
	
	public void changeCamera(String namev, double latv, double lngv) {
		name = namev;
		lat = latv;
		lng = lngv;
		changeCamera();
	}
	
	private void changeCamera() {
		LatLng position = new LatLng(lat, lng);
    	MarkerOptions marker = new MarkerOptions();
    	marker.position(position);
    	marker.title(name);
        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 11));
	}

    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-14.2400732, -53.1805017), 4));
    }
    
    /*public void onDestroyView() {
	   Fragment fragment = fragmentManager.findFragmentById(R.id.map);   
	   FragmentTransaction ft = fragmentManager.beginTransaction();
	   ft.remove(fragment);
	   ft.commit();

	   super.onDestroyView(); 
	}*/
}
