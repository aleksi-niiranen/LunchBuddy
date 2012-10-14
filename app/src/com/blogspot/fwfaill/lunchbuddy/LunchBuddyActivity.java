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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class LunchBuddyActivity extends SherlockListActivity {
	
	private static final String TAG = "LunchBuddyActivity";
	private static final String FAVORITE_KEY = "Favorite";
	
	private static final long SYNC_FREQUENCY = 43200; // 12 hours in seconds
	
	private static final String[] PROJECTION = new String[] {
		LunchBuddy.Courses._ID,
		LunchBuddy.Courses.COLUMN_NAME_TITLE_FI,
		LunchBuddy.Courses.COLUMN_NAME_TITLE_EN,
		LunchBuddy.Courses.COLUMN_NAME_PRICE,
		LunchBuddy.Courses.COLUMN_NAME_PROPERTIES,
		LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP,
		LunchBuddy.Courses.COLUMN_NAME_REF_TITLE
	};
	
	private static final int SALO = 0;
	private static final int ICT_TALO = 1;
	private static final int LEMPPARI = 2;
	private static final int NUTRITIO = 3;
	
	private LayoutInflater mInflater;
	private SharedPreferences mPreferences;
	private int mNavigationPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccountsByType(LunchBuddy.ACCOUNT_TYPE);
        if (accounts.length < 1) {
        	Account account = new Account("LunchBuddy", LunchBuddy.ACCOUNT_TYPE);
			am.addAccountExplicitly(account, null, null);
			ContentResolver.setIsSyncable(account, LunchBuddy.AUTHORITY, 1);
			ContentResolver.setSyncAutomatically(account, LunchBuddy.AUTHORITY, true);
			ContentResolver.addPeriodicSync(account, LunchBuddy.AUTHORITY, new Bundle(), SYNC_FREQUENCY);
        }
        
        setContentView(R.layout.main);
        
        mPreferences = getPreferences(Context.MODE_PRIVATE);
        mNavigationPosition = mPreferences.getInt(FAVORITE_KEY, SALO);
        
        SpinnerAdapter adapter = ArrayAdapter.createFromResource(
        		this, R.array.restaurants, R.layout.sherlock_spinner_dropdown_item);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {
			
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				switch (itemPosition) {
				case SALO:
					mNavigationPosition = SALO;
					break;
				case ICT_TALO:
					mNavigationPosition = ICT_TALO;
					break;
				case LEMPPARI:
					mNavigationPosition = LEMPPARI;
					break;
				case NUTRITIO:
					mNavigationPosition = NUTRITIO;
					break;
				}
				query();
				return true;
			}
		});
        actionBar.setSelectedNavigationItem(mNavigationPosition);
        
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getListView().addFooterView(mInflater.inflate(R.layout.footer_legend, null), null, false);
        
        query();
    }
    
    private void removeOld(long timestamp) {
    	String where = LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP + " < " + timestamp;
    	getContentResolver().delete(LunchBuddy.Courses.CONTENT_URI, where, null);
    }
    
    private void query() {
    	Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki"), new Locale("Finnish", "Finland"));
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	
    	long timestamp = cal.getTimeInMillis() / 1000;
    	if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
    		removeOld(timestamp);
    	
    	String where = LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP + "=" + timestamp
    			+ " and " + LunchBuddy.Courses.COLUMN_NAME_REF_TITLE + "= ?";
    	String[] args = new String[] { LunchBuddy.Courses.REF_TITLES[mNavigationPosition] };
    	Cursor cursor = managedQuery(LunchBuddy.Courses.CONTENT_URI, PROJECTION, where, args, LunchBuddy.Courses.DEFAULT_SORT_ORDER);
    	
    	CourseCursorAdapter adapter = new CourseCursorAdapter(R.layout.course, this, cursor);
    	setListAdapter(adapter);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getSupportMenuInflater().inflate(R.menu.main, menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.menu_setAsDefault:
    		SharedPreferences.Editor editor = mPreferences.edit();
    		editor.putInt(FAVORITE_KEY, mNavigationPosition);
    		editor.commit();
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
}