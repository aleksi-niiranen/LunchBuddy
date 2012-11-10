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
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;

import com.blogspot.fwfaill.lunchbuddy.LunchBuddy.Restaurants;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class LunchBuddyProvider extends ContentProvider {
	
	private static final String TAG = "LunchBuddyProvider";
	
	private static final String DATABASE_NAME = "lunchbuddy";
	
	private static final int DATABASE_VERSION = 6;
	
	private static HashMap<String, String> sCoursesProjectionMap;
	private static HashMap<String, String> sRestaurantsProjectionMap;
	
	private static final int COURSES = 1;
	private static final int COURSE_ID = 2;
	private static final int RESTAURANTS = 3;
	private static final int RESTAURANT_ID = 4;
	
	private static final UriMatcher sUriMatcher;
	
	private DatabaseHelper mOpenHelper;
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(LunchBuddy.AUTHORITY, "courses", COURSES);
		sUriMatcher.addURI(LunchBuddy.AUTHORITY, "courses/#", COURSE_ID);
		sUriMatcher.addURI(LunchBuddy.AUTHORITY, "restaurants", RESTAURANTS);
		sUriMatcher.addURI(LunchBuddy.AUTHORITY, "restaurants/#", RESTAURANT_ID);
		
		sCoursesProjectionMap = new HashMap<String, String>();
		sCoursesProjectionMap.put(LunchBuddy.Courses._ID, LunchBuddy.Courses._ID);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_ID, LunchBuddy.Courses.COLUMN_NAME_ID);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_FI, LunchBuddy.Courses.COLUMN_NAME_TITLE_FI);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_EN, LunchBuddy.Courses.COLUMN_NAME_TITLE_EN);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_SE, LunchBuddy.Courses.COLUMN_NAME_TITLE_SE);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_PRICE, LunchBuddy.Courses.COLUMN_NAME_PRICE);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_PROPERTIES, LunchBuddy.Courses.COLUMN_NAME_PROPERTIES);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP, LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_REF_TITLE, LunchBuddy.Courses.COLUMN_NAME_REF_TITLE);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_RATED_GOOD, LunchBuddy.Courses.COLUMN_NAME_RATED_GOOD);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_RATED_BAD, LunchBuddy.Courses.COLUMN_NAME_RATED_BAD);
		
		sRestaurantsProjectionMap = new HashMap<String, String>();
		sRestaurantsProjectionMap.put(LunchBuddy.Restaurants.COLUMN_NAME_TITLE, LunchBuddy.Restaurants.COLUMN_NAME_TITLE);
		sRestaurantsProjectionMap.put(LunchBuddy.Restaurants.COLUMN_NAME_ADDRESS, LunchBuddy.Restaurants.COLUMN_NAME_ADDRESS);
		sRestaurantsProjectionMap.put(LunchBuddy.Restaurants.COLUMN_NAME_LOCATION, LunchBuddy.Restaurants.COLUMN_NAME_LOCATION);
		sRestaurantsProjectionMap.put(LunchBuddy.Restaurants.COLUMN_NAME_POSITION, LunchBuddy.Restaurants.COLUMN_NAME_POSITION);
		sRestaurantsProjectionMap.put(LunchBuddy.Restaurants._ID, "rowid as " + LunchBuddy.Restaurants._ID);
	}
	
	private HashMap<String, UriRequestTask> mRequestsInProgress = new HashMap<String, UriRequestTask>();
	
	private UriRequestTask getRequestTask(String query) {
		return mRequestsInProgress.get(query);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String finalWhere;
		
		int count;
		
		switch (sUriMatcher.match(uri)) {
		case COURSES:
			count = db.delete(LunchBuddy.Courses.TABLE_NAME, where, whereArgs);
			break;
		case COURSE_ID:
			finalWhere = LunchBuddy.Courses._ID + "=" + uri.getPathSegments().get(LunchBuddy.Courses.COURSE_ID_PATH_POSITION);
			
			if (where != null) {
				finalWhere = finalWhere + " AND " + where;
			}
			
			count = db.delete(LunchBuddy.Courses.TABLE_NAME, finalWhere, whereArgs);
			break;
		case RESTAURANTS:
			count = db.delete(LunchBuddy.Restaurants.TABLE_NAME, where, whereArgs);
			break;
		case RESTAURANT_ID:
			finalWhere = LunchBuddy.Restaurants._ID + "=" + uri.getPathSegments().get(LunchBuddy.Restaurants.RESTAURANT_ID_PATH_POSITION);
			
			if (where != null) {
				finalWhere = finalWhere + " AND " + where;
			}
			
			count = db.delete(LunchBuddy.Restaurants.TABLE_NAME, finalWhere, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null, !callerIsSyncAdapter(uri));
		
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case COURSES:
			return LunchBuddy.Courses.CONTENT_TYPE;
		case COURSE_ID:
			return LunchBuddy.Courses.CONTENT_ITEM_TYPE;
		case RESTAURANTS:
			return LunchBuddy.Restaurants.CONTENT_TYPE;
		case RESTAURANT_ID:
			return LunchBuddy.Restaurants.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != COURSES) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		ContentValues values;
		
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		long rowId = db.insert(LunchBuddy.Courses.TABLE_NAME, null, values);
		
		if (rowId > 0) {
			Uri courseUri = ContentUris.withAppendedId(LunchBuddy.Courses.CONTENT_ID_URI_BASE, rowId);
			
			getContext().getContentResolver().notifyChange(courseUri, null, !callerIsSyncAdapter(uri));
			return courseUri;
		}
		
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		boolean courseQuery = false;
		int position = 0;
		
		switch (sUriMatcher.match(uri)) {
		case COURSES:
			courseQuery = true;
			qb.setTables(LunchBuddy.Courses.TABLE_NAME);
			qb.setProjectionMap(sCoursesProjectionMap);
			position = Integer.parseInt(selectionArgs[0]);
			break;
		case COURSE_ID:
			qb.setTables(LunchBuddy.Courses.TABLE_NAME);
			qb.setProjectionMap(sCoursesProjectionMap);
			qb.appendWhere(LunchBuddy.Courses._ID + "=" + uri.getPathSegments().get(LunchBuddy.Courses.COURSE_ID_PATH_POSITION));
			break;
		case RESTAURANTS:
			return selectionArgs != null ? search(selectionArgs[0]) : getAllRestaurants();
		case RESTAURANT_ID:
			qb.setTables(LunchBuddy.Restaurants.TABLE_NAME);
			qb.setProjectionMap(sRestaurantsProjectionMap);
			qb.appendWhere(LunchBuddy.Restaurants._ID + "=" + uri.getPathSegments().get(LunchBuddy.Restaurants.RESTAURANT_ID_PATH_POSITION));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = LunchBuddy.Courses.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
		
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		
		Cursor c;
		if (courseQuery) {
			String[] args = { LunchBuddy.Restaurants.REF_TITLES[position] };
			c = qb.query(db, projection, selection, args, null, null, orderBy);
			if (!c.moveToFirst())
				requestCourses(selectionArgs[0], position);
		} else {
			c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		}
		
		c.setNotificationUri(getContext().getContentResolver(), uri);
		
		return c;
	}
	
	private Cursor getAllRestaurants() {
		Log.d(TAG, "getAllRestaurants()");
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(LunchBuddy.Restaurants.TABLE_NAME);
		builder.setProjectionMap(sRestaurantsProjectionMap);
		
		Cursor cursor = builder.query(mOpenHelper.getReadableDatabase(), null, null, null, null, null, null);
		return cursor;
	}

	private Cursor search(String query) {
		String[] columns = new String[] {
				LunchBuddy.Restaurants._ID,
				LunchBuddy.Restaurants.COLUMN_NAME_TITLE,
				LunchBuddy.Restaurants.COLUMN_NAME_ADDRESS,
				LunchBuddy.Restaurants.COLUMN_NAME_POSITION
		};
		
		String selection = LunchBuddy.Restaurants.TABLE_NAME + " match ?";
		String[] selectionArgs = new String[] {"*"+query+"*"};
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(LunchBuddy.Restaurants.TABLE_NAME);
		builder.setProjectionMap(sRestaurantsProjectionMap);
		
		Cursor cursor = builder.query(mOpenHelper.getReadableDatabase(), columns, selection, selectionArgs, null, null, null);
		
		if (cursor == null)
			return null;
		else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;
	}

	private void requestCourses(String queryTag, int position) {
		Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki"), new Locale("Finnish", "Finland"));
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		String url = null;
		switch (position) {
		case 0:
			url = LunchBuddy.Courses.BASE_URI_SALO.toString() + year + "/" + (month + 1) + "/" + day;
			break;
		case 1:
			url = LunchBuddy.Courses.BASE_URI_ICT.toString() + year + "/" + (month + 1) + "/" + day;
			break;
		case 2:
			url = LunchBuddy.Courses.BASE_URI_LEMPPARI.toString() + year + "/" + (month + 1) + "/" + day;
			break;
		case 3:
			url = LunchBuddy.Courses.BASE_URI_NUTRITIO.toString() + year + "/" + (month + 1) + "/" + day;
			break;
		case 4:
			url = LunchBuddy.Courses.BASE_URI_ASSARI.toString() + year + "/" + (month + 1) + "/" + day;
			break;
		case 5:
			url = LunchBuddy.Courses.BASE_URI_BRYGGE.toString() + year + "/" + (month + 1) + "/" + day;
			break;
		case 6:
			url = LunchBuddy.Courses.BASE_URI_DELICA.toString() + year + "/" + (month + 1) + "/" + day;
			break;
		case 7:
			url = LunchBuddy.Courses.BASE_URI_DELI_PHARMA.toString() + year + "/" + (month + 1) + "/" + day;
			break;
		case 8:
			url = LunchBuddy.Courses.BASE_URI_DENTAL.toString() + year + "/" + (month + 1) + "/" + day;
			break;
		case 9:
			url = LunchBuddy.Courses.BASE_URI_MACCIAVELLI.toString() + year + "/" + (month + 1) + "/" + day;
			break;
		case 10:
			url = LunchBuddy.Courses.BASE_URI_MIKRO.toString() + year + "/" + (month + 1) + "/" + day;
			break;
		case 11:
			url = LunchBuddy.Courses.BASE_URI_PARKKIS.toString() + year + "/" + (month + 1) + "/" + day;
			break;
		case 12:
			url = LunchBuddy.Courses.BASE_URI_MYSSY.toString() + year + "/" + (month + 1) + "/" + day;
			break;
		}
		Log.d(TAG, url);
		if (url != null) asyncQueryRequest(queryTag, url);
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		String finalWhere;
		
		switch (sUriMatcher.match(uri)) {
		case COURSE_ID:
			String courseId = uri.getPathSegments().get(LunchBuddy.Courses.COURSE_ID_PATH_POSITION);
			
			finalWhere = LunchBuddy.Courses._ID + " = " + uri.getPathSegments().get(LunchBuddy.Courses.COURSE_ID_PATH_POSITION);
			
			if (where != null)
				finalWhere = finalWhere + " AND " + where;
			
			Log.d(TAG, "updating course " + courseId);
			count = db.update(LunchBuddy.Courses.TABLE_NAME, values, finalWhere, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}
	
	private boolean callerIsSyncAdapter(Uri uri) {
		final String isSyncAdapter = uri.getQueryParameter("syncadapter");
		return isSyncAdapter != null && isSyncAdapter.equals("true");
	}
	
	public void requestComplete(String requestTag) {
		synchronized (mRequestsInProgress) {
			mRequestsInProgress.remove(requestTag);
		}
	}
	
	private UriRequestTask newQueryTask(String requestTag, String url) {
		UriRequestTask requestTask;
		
		final HttpGet get = new HttpGet(url);
		ResponseHandler handler;
		handler = new CoursesJsonHandler(this);
		requestTask = new UriRequestTask(requestTag, this, get, handler, getContext());
		
		mRequestsInProgress.put(requestTag, requestTask);
		return requestTask;
	}
	
	public void asyncQueryRequest(String queryTag, String queryUri) {
		synchronized (mRequestsInProgress) {
			UriRequestTask requestTask = getRequestTask(queryTag);
			if (requestTask == null) {
				requestTask = newQueryTask(queryTag, queryUri);
				Thread t = new Thread(requestTask);
				t.start();
			}
		}
	}
	
	public DatabaseHelper getOpenHelperForTest() {
		return mOpenHelper;
	}

	public static class DatabaseHelper extends SQLiteOpenHelper {
		
		private String[] mInsertRestaurants;

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			mInsertRestaurants = context.getString(R.string.insert_restaurants).split("\n");
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "creating database tables");
			db.execSQL("create table " + LunchBuddy.Courses.TABLE_NAME + " ("
					+ LunchBuddy.Courses._ID + " integer primary key,"
					+ LunchBuddy.Courses.COLUMN_NAME_ID + " integer,"
					+ LunchBuddy.Courses.COLUMN_NAME_TITLE_FI + " text,"
					+ LunchBuddy.Courses.COLUMN_NAME_TITLE_EN + " text,"
					+ LunchBuddy.Courses.COLUMN_NAME_TITLE_SE + " text,"
					+ LunchBuddy.Courses.COLUMN_NAME_PRICE + " text,"
					+ LunchBuddy.Courses.COLUMN_NAME_PROPERTIES + " text,"
					+ LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP + " integer,"
					+ LunchBuddy.Courses.COLUMN_NAME_REF_TITLE + " text,"
					+ LunchBuddy.Courses.COLUMN_NAME_RATED_GOOD + " integer default 0," 
					+ LunchBuddy.Courses.COLUMN_NAME_RATED_BAD + " integer default 0);");
			
			db.execSQL("create virtual table " + Restaurants.TABLE_NAME + " using fts3 ("
					+ Restaurants.COLUMN_NAME_TITLE + ", "
					+ Restaurants.COLUMN_NAME_ADDRESS + ", "
					+ Restaurants.COLUMN_NAME_LOCATION + ", "
					+ Restaurants.COLUMN_NAME_POSITION + ");");
			
			db.beginTransaction();
			try {
				execMultipleSQL(db, mInsertRestaurants);
				db.setTransactionSuccessful();
			} catch (SQLException e) {
				Log.e(TAG, "Error populating tables", e);
			} finally {
				db.endTransaction();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data.");
			db.execSQL("drop table if exists " + LunchBuddy.Courses.TABLE_NAME);
			db.execSQL("drop table if exists " + Restaurants.TABLE_NAME);
			onCreate(db);
		}
		
		private void execMultipleSQL(SQLiteDatabase db, String[] sql) {
			for (String s : sql) {
				if (s.trim().length() > 0)
					db.execSQL(s);
			}
		}
	}
}
