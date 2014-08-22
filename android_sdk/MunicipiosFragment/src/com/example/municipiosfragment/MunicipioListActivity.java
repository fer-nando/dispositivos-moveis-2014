package com.example.municipiosfragment;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * An activity representing a list of Estado. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link MunicipioDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link MunicipioListFragment} and the item details (if present) is a
 * {@link MunicipioDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link MunicipioListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class MunicipioListActivity extends FragmentActivity implements
		MunicipioListFragment.Callbacks {
	

	public static final String MODE_TWOPANE = "two-pane";
	public static final String MODE_SINGLEPANE = "single-pane";

	public static FragmentManager fragmentManager;

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	
	MunicipioDetailFragment fragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		fragmentManager = getSupportFragmentManager();
		
		if(getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.activity_municipio_twopane);
		} else {
			setContentView(R.layout.activity_municipio_list);
		}

		if (findViewById(R.id.municipio_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;			
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link MunicipioListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id, Municipio mun) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			if(fragment == null) {
				Bundle arguments = new Bundle();
				arguments.putString(MunicipioDetailFragment.ARG_MODE, MODE_TWOPANE);
				arguments.putString(MunicipioDetailFragment.ARG_ITEM_ID, id);
				arguments.putDouble(MunicipioDetailFragment.ARG_ITEM_LAT, mun.getLatitude());
				arguments.putDouble(MunicipioDetailFragment.ARG_ITEM_LNG, mun.getLongitude());
				fragment = new MunicipioDetailFragment();
				fragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.municipio_detail_container, fragment)
						.commit();
			} else {
				fragment.changeCamera(id, mun.getLatitude(), mun.getLongitude());
			}

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					MunicipioDetailActivity.class);
			detailIntent.putExtra(MunicipioDetailFragment.ARG_MODE, MODE_SINGLEPANE);
			detailIntent.putExtra(MunicipioDetailFragment.ARG_ITEM_ID, id);
			detailIntent.putExtra(MunicipioDetailFragment.ARG_ITEM_LAT, mun.getLatitude());
			detailIntent.putExtra(MunicipioDetailFragment.ARG_ITEM_LNG, mun.getLongitude());
			startActivity(detailIntent);
		}
	}
}
