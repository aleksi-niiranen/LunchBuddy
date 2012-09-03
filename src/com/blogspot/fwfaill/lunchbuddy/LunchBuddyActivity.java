package com.blogspot.fwfaill.lunchbuddy;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
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
	
	public static final String TAG = "RuokalistaActivity";
	
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
	
	private LayoutInflater mInflater;
	private String mSelection = "Turun AMK, Aurinkolaiva";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SpinnerAdapter adapter = ArrayAdapter.createFromResource(
        		this, R.array.restaurants, R.layout.sherlock_spinner_dropdown_item);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(adapter, new ActionBar.OnNavigationListener() {
			
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				switch (itemPosition) {
				case SALO:
					mSelection = LunchBuddy.Courses.REF_TITLE_SALO;
					break;
				case ICT_TALO:
					mSelection = LunchBuddy.Courses.REF_TITLE_ICT;
					break;
				case LEMPPARI:
					mSelection = LunchBuddy.Courses.REF_TITLE_LEMPPARI;
					break;
				}
				query();
				return true;
			}
		});
        
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
    	String[] args = new String[] { mSelection };
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
    	case R.id.menu_settings:
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }
}