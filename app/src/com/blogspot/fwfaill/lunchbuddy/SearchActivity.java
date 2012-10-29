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

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;

public class SearchActivity extends SherlockListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_view);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			doSearch(query);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent i = new Intent(this, LunchBuddyActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void doSearch(String query) {
		Cursor cursor = managedQuery(LunchBuddy.Restaurants.CONTENT_URI, null, null, new String[] {query}, null);
		
		if (cursor != null) {
			String[] from = new String[] { LunchBuddy.Restaurants.COLUMN_NAME_TITLE,
										   LunchBuddy.Restaurants.COLUMN_NAME_ADDRESS,
										   LunchBuddy.Restaurants.COLUMN_NAME_POSITION };
			
			int[] to = new int[] { R.id.restaurant_title,
								   R.id.restaurant_address };
			
			SimpleCursorAdapter restaurants = new SimpleCursorAdapter(this, R.layout.search_result, cursor, from, to);
			
			getListView().setAdapter(restaurants);
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(this, LunchBuddyActivity.class);
		Cursor cursor = (Cursor) l.getItemAtPosition(position);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		int navigationPosition = cursor.getInt(cursor.getColumnIndexOrThrow(LunchBuddy.Restaurants.COLUMN_NAME_POSITION));
		i.putExtra("navigationPosition", navigationPosition);
		startActivity(i);
	}
}
