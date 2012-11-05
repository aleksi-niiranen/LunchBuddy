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

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class DiscoverFragment extends SherlockListFragment {
	
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
	
	private ActionMode.Callback mRateRestaurantActionModeCallback = new ActionMode.Callback() {
		
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
		@Override
		public void onDestroyActionMode(ActionMode mode) {
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.rate_menu, menu);
			return true;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.context_rate_good:
				// TODO: finalize restaurant rating
				mode.finish();
				return true;
			case R.id.context_rate_bad:
				// TODO: finalize restaurant rating
				mode.finish();
				return true;
			}
			return false;
		}
	};
}
