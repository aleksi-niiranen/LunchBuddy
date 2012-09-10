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

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import android.content.ContentValues;
import android.os.AsyncTask;

public class AsyncScrapeTask extends AsyncTask<Void, Void, Void> {
	
	private LunchBuddyProvider mProvider;
	
	public AsyncScrapeTask(LunchBuddyProvider provider) {
		mProvider = provider;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			Map menu = Scraper.scrape();
			
			String restaurant = (String) menu.get("restaurant");
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki"), new Locale("Finnish", "Finland"));
	    	cal.set(Calendar.HOUR_OF_DAY, 0);
	    	cal.set(Calendar.MINUTE, 0);
	    	cal.set(Calendar.SECOND, 0);
	    	cal.set(Calendar.MILLISECOND, 0);
	    	
	    	for(int i = 0; i < 5; i++) {
	    		List<HashMap<String, String>> courses = (List<HashMap<String, String>>) menu.get(Scraper.WEEKDAYS[i]);
	    		for(Map<String, String> m : courses) {
	    			ContentValues values = new ContentValues();
		    		values.put(LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP, cal.getTimeInMillis() / 1000);
		    		values.put(LunchBuddy.Courses.COLUMN_NAME_REF_TITLE, restaurant);
		    		values.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_FI, m.get("titleFi"));
					values.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_EN, m.get("titleEn"));
					values.put(LunchBuddy.Courses.COLUMN_NAME_PRICE, m.get("price"));
					values.put(LunchBuddy.Courses.COLUMN_NAME_PROPERTIES, m.get("properties"));
					
					mProvider.insert(LunchBuddy.Courses.CONTENT_URI, values);
	    		}
	    		
	    		cal.add(Calendar.DAY_OF_MONTH, 1);
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
