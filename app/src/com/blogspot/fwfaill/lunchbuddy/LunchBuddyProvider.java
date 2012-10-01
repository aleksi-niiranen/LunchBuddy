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

public class LunchBuddyProvider extends ContentProvider {
	
	private static final String TAG = "LunchBuddyProvider";
	
	private static final String DATABASE_NAME = "lunchbuddy";
	
	private static final int DATABASE_VERSION = 2;
	
	private static HashMap<String, String> sCoursesProjectionMap;
	
	private static final int COURSES = 1;
	private static final int COURSE_ID = 2;
	
	private static final UriMatcher sUriMatcher;
	
	private DatabaseHelper mOpenHelper;
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(LunchBuddy.AUTHORITY, "courses", COURSES);
		sUriMatcher.addURI(LunchBuddy.AUTHORITY, "courses/#", COURSE_ID);
		
		sCoursesProjectionMap = new HashMap<String, String>();
		sCoursesProjectionMap.put(LunchBuddy.Courses._ID, LunchBuddy.Courses._ID);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_FI, LunchBuddy.Courses.COLUMN_NAME_TITLE_FI);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_EN, LunchBuddy.Courses.COLUMN_NAME_TITLE_EN);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_PRICE, LunchBuddy.Courses.COLUMN_NAME_PRICE);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_PROPERTIES, LunchBuddy.Courses.COLUMN_NAME_PROPERTIES);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP, LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP);
		sCoursesProjectionMap.put(LunchBuddy.Courses.COLUMN_NAME_REF_TITLE, LunchBuddy.Courses.COLUMN_NAME_REF_TITLE);
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
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case COURSES:
			return LunchBuddy.Courses.CONTENT_TYPE;
		case COURSE_ID:
			return LunchBuddy.Courses.CONTENT_ITEM_TYPE;
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
		qb.setTables(LunchBuddy.Courses.TABLE_NAME);
		
		switch (sUriMatcher.match(uri)) {
		case COURSES:
			qb.setProjectionMap(sCoursesProjectionMap);
			break;
		case COURSE_ID:
			qb.setProjectionMap(sCoursesProjectionMap);
			qb.appendWhere(LunchBuddy.Courses._ID + "=" + uri.getPathSegments().get(LunchBuddy.Courses.COURSE_ID_PATH_POSITION));
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
		
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		
		if (!c.moveToFirst()) {
			Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki"), new Locale("Finnish", "Finland"));
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DATE);
			String url = null;
			if (selectionArgs[0].equals(LunchBuddy.Courses.REF_TITLE_SALO)) {
				url = LunchBuddy.Courses.BASE_URI_SALO.toString() 
						+ year + "/" + (month + 1) + "/" + day + "/" + LunchBuddy.Courses.LANGUAGE_CODE;
			} else if (selectionArgs[0].equals(LunchBuddy.Courses.REF_TITLE_ICT)) {
				url = LunchBuddy.Courses.BASE_URI_ICT.toString() 
						+ year + "/" + (month + 1) + "/" + day + "/" + LunchBuddy.Courses.LANGUAGE_CODE;
			} else if (selectionArgs[0].equals(LunchBuddy.Courses.REF_TITLE_LEMPPARI)) {
				url = LunchBuddy.Courses.BASE_URI_LEMPPARI.toString() 
						+ year + "/" + (month + 1) + "/" + day + "/" + LunchBuddy.Courses.LANGUAGE_CODE;
			} else if (selectionArgs[0].equals(LunchBuddy.Courses.REF_TITLE_NUTRITIO)) {
				url = "http://www.unica.fi/fi/";
			}
			if (url != null) asyncQueryRequest(selectionArgs[0], url);
		}
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// Records are never updated
		return 0;
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
	
	private ResponseHandler newResponseHandler() {
		return new CourseHandler(this);
	}
	
	private UriRequestTask newQueryTask(String requestTag, String url) {
		UriRequestTask requestTask;
		
		final HttpGet get = new HttpGet(url);
		ResponseHandler handler;
		if (requestTag.equals(LunchBuddy.Courses.REF_TITLE_NUTRITIO))
			handler = new UnicaHandler(this);
		else
			handler = newResponseHandler();
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

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table " + LunchBuddy.Courses.TABLE_NAME + " ("
					+ LunchBuddy.Courses._ID + " integer primary key,"
					+ LunchBuddy.Courses.COLUMN_NAME_TITLE_FI + " text,"
					+ LunchBuddy.Courses.COLUMN_NAME_TITLE_EN + " text,"
					+ LunchBuddy.Courses.COLUMN_NAME_PRICE + " text,"
					+ LunchBuddy.Courses.COLUMN_NAME_PROPERTIES + " text,"
					+ LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP + " integer,"
					+ LunchBuddy.Courses.COLUMN_NAME_REF_TITLE + " text);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists " + LunchBuddy.Courses.TABLE_NAME);
			onCreate(db);
		}
	}
}
