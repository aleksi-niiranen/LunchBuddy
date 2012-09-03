package com.blogspot.fwfaill.lunchbuddy;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
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

	private Menu mOptionsMenu;

	private View mRefreshIndeterminateProgressView = null;

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
        
        ListView lv = getListView();
        lv.addFooterView(mInflater.inflate(R.layout.footer_legend, null), null, false);

        query();
    }
    
    private void query() {
    	Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki"), new Locale("Finnish", "Finland"));
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	String where = LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP + "=" + (cal.getTimeInMillis() / 1000)
    			+ " and " + LunchBuddy.Courses.COLUMN_NAME_REF_TITLE + "= ?";
    	String[] args = new String[] { mSelection };
    	Cursor cursor = managedQuery(LunchBuddy.Courses.CONTENT_URI, PROJECTION, where, args, LunchBuddy.Courses.DEFAULT_SORT_ORDER);
    	
    	CourseCursorAdapter adapter = new CourseCursorAdapter(R.layout.course, this, cursor);
    	
    	setListAdapter(adapter);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	mOptionsMenu = menu;
    	MenuInflater inflater = getSupportMenuInflater();
    	inflater.inflate(R.menu.main, menu);
    	
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.menu_refresh:
    		Toast.makeText(this, "Fake refreshing", Toast.LENGTH_SHORT).show();
//    		setRefreshActionItemState(true);
//    		getWindow().getDecorView().postDelayed(new Runnable() {
//    			@Override
//    			public void run() {
//    				setRefreshActionItemState(false);
//    			}
//    		}, 1000);
    		break;
    	}
    	return super.onOptionsItemSelected(item);
    }

	private void setRefreshActionItemState(boolean refreshing) {
		final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
		if (refreshing) {
			if (mRefreshIndeterminateProgressView  == null) {
				mRefreshIndeterminateProgressView = mInflater.inflate(R.layout.actionbar_indeterminate_progress, null);
			}
			refreshItem.setActionView(mRefreshIndeterminateProgressView);
		} else {
			refreshItem.setActionView(null);
		}
	}
}