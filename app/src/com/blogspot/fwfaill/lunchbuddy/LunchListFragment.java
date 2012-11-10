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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.blogspot.fwfaill.lunchbuddy.LunchBuddy.Restaurants;

public class LunchListFragment extends SherlockListFragment {
	
	private static final String TAG = "LunchListFragment";
	
	private static final String[] PROJECTION = new String[] {
		LunchBuddy.Courses._ID,
		LunchBuddy.Courses.COLUMN_NAME_ID,
		LunchBuddy.Courses.COLUMN_NAME_TITLE_FI,
		LunchBuddy.Courses.COLUMN_NAME_TITLE_EN,
		LunchBuddy.Courses.COLUMN_NAME_TITLE_SE,
		LunchBuddy.Courses.COLUMN_NAME_PRICE,
		LunchBuddy.Courses.COLUMN_NAME_PROPERTIES,
		LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP,
		LunchBuddy.Courses.COLUMN_NAME_REF_TITLE,
		LunchBuddy.Courses.COLUMN_NAME_RATED_GOOD,
		LunchBuddy.Courses.COLUMN_NAME_RATED_BAD
	};
	
	private LayoutInflater mInflater;
	private int mNavigationPosition;
	
	private long mClickedId;
	private Course mClickedCourse;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LunchBuddyActivity activity = (LunchBuddyActivity) getSherlockActivity();
		if (activity.getNavigationPosition() == null) {
			mNavigationPosition = getSherlockActivity().getPreferences(Context.MODE_PRIVATE)
					.getInt(LunchBuddyActivity.FAVORITE_KEY, 0);
		} else {
			mNavigationPosition = activity.getNavigationPosition();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		View view = inflater.inflate(R.layout.list_view, container, false);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) getListView().getItemAtPosition(position);
				mClickedCourse = Course.createFromCursor(cursor);
				mClickedId = id;
				getSherlockActivity().startActionMode(mRateLunchActionModeCallback);
				return false;
			}
		});

		getSherlockActivity().getSupportActionBar().getTabAt(0).setText(Restaurants.REF_TITLES[mNavigationPosition]);
		
		getListView().addFooterView(mInflater.inflate(R.layout.footer_legend, null), null, false);
		
		query();
	}
	
	public void setNavigationPosition(int position) {
		mNavigationPosition = position;
	}
	
	public int getNavigationPosition() {
		return mNavigationPosition;
	}
	
	private void query() {
		Log.d(TAG, "navigation position: "  + mNavigationPosition);
		Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki"), new Locale("Finnish", "Finland"));
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	
    	long timestamp = cal.getTimeInMillis() / 1000;
    	
    	String where = LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP + "=" + timestamp
    			+ " and " + LunchBuddy.Courses.COLUMN_NAME_REF_TITLE + "= ?";
    	String[] args = new String[] { ""+mNavigationPosition };
    	Cursor cursor = getSherlockActivity().managedQuery(LunchBuddy.Courses.CONTENT_URI, PROJECTION, where, args, LunchBuddy.Courses.DEFAULT_SORT_ORDER);
    	
    	CourseCursorAdapter adapter = new CourseCursorAdapter(R.layout.course, getSherlockActivity(), cursor);
    	setListAdapter(adapter);
	}
	
	private ActionMode.Callback mRateLunchActionModeCallback = new ActionMode.Callback() {
		
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
//				mClickedCourse.setRatedGood(true);
//				getSherlockActivity().getContentResolver().update(
//						ContentUris.withAppendedId(LunchBuddy.Courses.CONTENT_ID_URI_BASE, mClickedId), 
//						mClickedCourse.getContentValues(), null, null);
				new RateTask().execute(""+mClickedCourse.getId(), "good");
				mode.finish();
				return true;
			case R.id.context_rate_bad:
//				mClickedCourse.setRatedBad(true);
//				getSherlockActivity().getContentResolver().update(
//						ContentUris.withAppendedId(LunchBuddy.Courses.CONTENT_ID_URI_BASE, mClickedId), 
//						mClickedCourse.getContentValues(), null, null);
				new RateTask().execute(""+mClickedCourse.getId(), "bad");
				mode.finish();
				return true;
			}
			return false;
		}
	};
}
