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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.SearchView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class LunchBuddyActivity extends SherlockFragmentActivity {
	
	private static final String TAG = "LunchBuddyActivity";
	public static final String FAVORITE_KEY = "Favorite";
	
	private static final long SYNC_FREQUENCY = 43200; // 12 hours in seconds
	
	private Integer mNavigationPosition = null;
	
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
        
        setContentView(R.layout.main_view);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        Tab listTab = actionBar.newTab()
        		.setText("List")
        		.setTabListener(new TabListener<LunchListFragment>(this, "List", LunchListFragment.class));
        
        actionBar.addTab(listTab);
        
        Tab discoverTab = actionBar.newTab()
        		.setText("All restaurants")
        		.setTabListener(new TabListener<DiscoverFragment>(this, "Discover", DiscoverFragment.class));
        
        actionBar.addTab(discoverTab);
        
        Intent intent = getIntent();
        if (intent.hasExtra("navigationPosition")) {
        	mNavigationPosition = intent.getIntExtra("navigationPosition", 0);
			actionBar.setSelectedNavigationItem(0);
        }
    }

	@SuppressLint("NewApi") @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getSupportMenuInflater().inflate(R.menu.main, menu);
    	
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
    		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        	SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        	searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        	searchView.setIconifiedByDefault(true);
    	}
    	
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.menu_setAsDefault:
    		SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
    		int navigationPosition = ((LunchListFragment) getSupportFragmentManager()
    				.findFragmentByTag("List")).getNavigationPosition();
    		editor.putInt(FAVORITE_KEY, navigationPosition);
    		editor.commit();
    		break;
    	case R.id.menu_search:
    		onSearchRequested();
    		break;
//    	case R.id.menu_settings:
//    		// TODO: start settings activity
//    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    public Integer getNavigationPosition() {
    	return mNavigationPosition;
    }
    
    private static class TabListener<T extends Fragment> implements ActionBar.TabListener {
    	
    	private Fragment mFragment;
    	private final Activity mActivity;
    	private final String mTag;
    	private final Class<T> mClass;
    	
    	public TabListener(Activity activity, String tag, Class<T> clz) {
    		mActivity = activity;
    		mTag = tag;
    		mClass = clz;
    	}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(R.id.content, mFragment, mTag);
			} else {
				ft.attach(mFragment);
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				ft.detach(mFragment);
			}
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
    	
    }
}