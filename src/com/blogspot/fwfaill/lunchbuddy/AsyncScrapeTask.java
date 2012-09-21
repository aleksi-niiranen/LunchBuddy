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
import java.util.List;

import android.content.ContentValues;
import android.os.AsyncTask;

public class AsyncScrapeTask extends AsyncTask<Void, Void, Void> {
	
	private LunchBuddyProvider mProvider;
	
	public AsyncScrapeTask(LunchBuddyProvider provider) {
		mProvider = provider;
	}

	@Override
	protected Void doInBackground(Void... params) {
//		try {
//			List<Course> menu = Scraper.scrape();
//
//			for(Course c : menu) {
//				ContentValues values = new ContentValues();
//				values.put(LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP, c.getTimestamp());
//				values.put(LunchBuddy.Courses.COLUMN_NAME_REF_TITLE, c.getRefTitle());
//				values.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_FI, c.getTitleFi());
//				values.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_EN, c.getTitleEn());
//				values.put(LunchBuddy.Courses.COLUMN_NAME_PRICE, c.getPrice());
//				values.put(LunchBuddy.Courses.COLUMN_NAME_PROPERTIES, c.getProperties());
//
//				mProvider.insert(LunchBuddy.Courses.CONTENT_URI, values);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return null;
	}
}
