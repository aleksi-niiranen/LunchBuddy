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

import android.net.Uri;
import android.provider.BaseColumns;

public class LunchBuddy {

	public static final String AUTHORITY = "com.blogspot.fwfaill.provider.LunchBuddy";
	public static final String ACCOUNT_TYPE = "com.blogspot.fwfaill.lunchbuddy.account";
	
	public static final class Courses implements BaseColumns {
		
		private Courses() {}
		
		public static final String TABLE_NAME = "courses";
		
		private static final String SCHEME = "content://";
		
		private static final String PATH_COURSES = "/courses";
		private static final String PATH_COURSE_ID = "/courses/";
		
		public static final int COURSE_ID_PATH_POSITION = 1;
		
		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_COURSES);
		public static final Uri SYNC_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_COURSES + "?syncadapter=true");
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_COURSE_ID);
		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + PATH_COURSE_ID + "/#");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fwfaill.course";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fwfaill.course";
		
		public static final String DEFAULT_SORT_ORDER = "_id asc";
		
		public static final String COLUMN_NAME_TITLE_FI = "title_fi";
		public static final String COLUMN_NAME_TITLE_EN = "title_en";
		public static final String COLUMN_NAME_PRICE = "price";
		public static final String COLUMN_NAME_PROPERTIES = "properties";
		public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
		public static final String COLUMN_NAME_REF_TITLE = "ref_title";
		
		public static final String BASE_UNICA_URI = "http://shielded-crag-5542.herokuapp.com/daily_json";
		public static final Uri BASE_URI_SALO = Uri.parse("http://www.sodexo.fi/ruokalistat/output/daily_json/459/");
		public static final Uri BASE_URI_ICT = Uri.parse("http://www.sodexo.fi/ruokalistat/output/daily_json/427/");
		public static final Uri BASE_URI_LEMPPARI = Uri.parse("http://www.sodexo.fi/ruokalistat/output/daily_json/442/");
		public static final Uri BASE_URI_NUTRITIO = Uri.parse("http://192.168.1.10:8080/LunchBuddyWebService/daily_json/Nutritio/");
		public static final Uri BASE_URI_ASSARI = Uri.parse("http://192.168.1.10:8080/LunchBuddyWebService/daily_json/Assarin%20ullakko/");
		public static final Uri BASE_URI_BRYGGE = Uri.parse("http://192.168.1.10:8080/LunchBuddyWebService/daily_json/Brygge/");
		public static final String LANGUAGE_CODE = "fi";
	}
	
	public static final class Restaurants implements BaseColumns {
		
		private Restaurants() { }
		
		public static final String TABLE_NAME = "restaurants";
		
		private static final String SCHEME = "content://";
		
		private static final String PATH_RESTAURANTS = "/restaurants";
		private static final String PATH_RESTAURANT_ID = "/restaurants/";
		
		public static final int RESTAURANT_ID_PATH_POSITION = 1;
		
		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_RESTAURANTS);
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_RESTAURANT_ID);
		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + PATH_RESTAURANT_ID + "/#");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fwfaill.restaurant";
		public static final String CONTENT_ITEM_TYPe = "vnd.android.cursor.item/vnd.fwfaill.restaurant";
		
		public static final String DEFAULT_SORT_ORDER = "_id asc";
		
		// TODO: column names
		
		public static final String[] REF_TITLES = {
			"Turun AMK, Aurinkolaiva",
			"ICT - talo",
			"Lemmink\u00e4isenkatu",
			"Nutritio",
			"Assarin ullakko",
			"Brygge",
			"Delica",
			"Deli Pharma",
			"Dental",
			"Macciavelli",
			"Mikro",
			"Parkkis",
			"Myssy \u0026 Silinteri"
		};
	}
}
