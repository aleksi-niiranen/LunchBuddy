package com.blogspot.fwfaill.lunchbuddy;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RestaurantAdapter extends ResourceCursorAdapter {

	public RestaurantAdapter(int layout, Context context, Cursor c) {
		super(context, layout, c);
	}
	
	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		ViewHolder holder = (ViewHolder) arg0.getTag();
		
		if (holder == null) {
			holder = new ViewHolder();
			holder.title = (TextView) arg0.findViewById(R.id.d_restaurant_title);
			holder.address = (TextView) arg0.findViewById(R.id.d_restaurant_address);
			holder.location = (TextView) arg0.findViewById(R.id.d_restaurant_location);
			
			arg0.setTag(holder);
		}
		
		holder.title.setText(arg2.getString(arg2.getColumnIndexOrThrow(LunchBuddy.Restaurants.COLUMN_NAME_TITLE)));
		holder.address.setText(arg2.getString(arg2.getColumnIndexOrThrow(LunchBuddy.Restaurants.COLUMN_NAME_ADDRESS)));
		holder.location.setText(arg2.getString(arg2.getColumnIndexOrThrow(LunchBuddy.Restaurants.COLUMN_NAME_LOCATION)));
	}
	
	static class ViewHolder {
		TextView title;
		TextView address;
		TextView location;
	}
}
