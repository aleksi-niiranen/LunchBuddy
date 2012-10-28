package com.blogspot.fwfaill.lunchbuddy;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class DiscoverFragment extends SherlockListFragment {
	
//	private final Restaurant[] mRestaurants = {
//			new Restaurant(R.drawable.amk_logo, "Aurinkolaiva"),
//			new Restaurant(R.drawable.amk_logo, "ICT-talo"),
//			new Restaurant(R.drawable.amk_logo, "Lemminkäisenkatu"),
//			new Restaurant(R.drawable.amk_logo, "Nutritio"),
//			new Restaurant(R.drawable.utu_logo, "Assarin ullakko"),
//			new Restaurant(R.drawable.utu_logo, "Brygge"),
//			new Restaurant(R.drawable.utu_logo, "Delica"),
//			new Restaurant(R.drawable.utu_logo, "Deli Pharma"),
//			new Restaurant(R.drawable.utu_logo, "Dental"),
//			new Restaurant(R.drawable.utu_logo, "Macciavelli"),
//			new Restaurant(R.drawable.utu_logo, "Mikro"),
//			new Restaurant(R.drawable.utu_logo, "Parkkis"),
//			new Restaurant(R.drawable.utu_logo, "Myssy & Silinteri")
//	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.discover_view, container, false);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		query();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		((LunchListFragment)getFragmentManager().findFragmentByTag("List")).setNavigationPosition(position);
		getSherlockActivity().getSupportActionBar().setSelectedNavigationItem(0);
	}
	
	private void query() {
		Cursor cursor = getSherlockActivity().getContentResolver().query(LunchBuddy.Restaurants.CONTENT_URI, 
				null, 
				null, 
				null, 
				LunchBuddy.Restaurants.DEFAULT_SORT_ORDER);
		
		RestaurantAdapter adapter = new RestaurantAdapter(R.layout.restaurant, getSherlockActivity(), cursor);
		
		getListView().setAdapter(adapter);
	}
}
