/*
 * Copyright 2012 Aleksi Niiranen 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blogspot.fwfaill.lunchbuddy;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
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
