package com.blogspot.fwfaill.lunchbuddy;

import android.net.Uri;
import android.provider.BaseColumns;

public class LunchBuddy {

	public static final String AUTHORITY = "com.blogspot.fwfaill.provider.LunchBuddy";
	
	public static final class Courses implements BaseColumns {
		
		private Courses() {}
		
		public static final String TABLE_NAME = "courses";
		
		private static final String SCHEME = "content://";
		
		private static final String PATH_COURSES = "/courses";
		private static final String PATH_COURSE_ID = "/courses/";
		
		public static final int COURSE_ID_PATH_POSITION = 1;
		
		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_COURSES);
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
		
		public static final Uri BASE_URI_SALO = Uri.parse("http://www.sodexo.fi/ruokalistat/output/daily_json/459/");
		public static final Uri BASE_URI_ICT = Uri.parse("http://www.sodexo.fi/ruokalistat/output/daily_json/427/");
		public static final Uri BASE_URI_LEMPPARI = Uri.parse("http://www.sodexo.fi/ruokalistat/output/daily_json/442/");
		public static final String LANGUAGE_CODE = "fi";
		
		public static final String REF_TITLE_SALO = "Turun AMK, Aurinkolaiva";
		public static final String REF_TITLE_ICT = "ICT - talo";
		public static final String REF_TITLE_LEMPPARI = "Lemmink\u00e4isenkatu";
	}
}
