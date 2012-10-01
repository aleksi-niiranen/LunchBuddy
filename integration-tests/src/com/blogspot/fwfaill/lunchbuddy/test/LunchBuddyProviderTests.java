package com.blogspot.fwfaill.lunchbuddy.test;

import android.content.ContentUris;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.blogspot.fwfaill.lunchbuddy.Course;
import com.blogspot.fwfaill.lunchbuddy.LunchBuddy;
import com.blogspot.fwfaill.lunchbuddy.LunchBuddyProvider;

public class LunchBuddyProviderTests extends ProviderTestCase2<LunchBuddyProvider> {
	
	private static final Uri INVALID_URI = Uri.withAppendedPath(LunchBuddy.Courses.CONTENT_URI, "invalid");
	
	private MockContentResolver mMockResolver;
	
	private SQLiteDatabase mDb;
	
	private final Course[] TEST_COURSES = {
			new Course(1349038800, "testaurant1", "paahtoleipä", "toast", "1,30 / 2,70 / 4,85 €", "VL"),
			new Course(1349038800, "testaurant1", "keitto", "soup", "1,30 / 2,70 / 4,85 €", "G M"),
			new Course(1349038800, "testaurant2", "pata", "stew", "1,30 / 2,70 / 4,85 €", "G M"),
			new Course(1349038800, "testaurant2", "kana", "chicken", "1,30 / 2,70 / 4,85 €", "G L")
	};

	public LunchBuddyProviderTests() {
		super(LunchBuddyProvider.class, LunchBuddy.AUTHORITY);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mMockResolver = getMockContentResolver();
		mDb = getProvider().getOpenHelperForTest().getWritableDatabase();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	private void insertData() {
		for (int i = 0; i < TEST_COURSES.length; i++) {
			mDb.insertOrThrow(LunchBuddy.Courses.TABLE_NAME, null, TEST_COURSES[i].getContentValues());
		}
	}
	
	public void testUriAndGetType() {
		String mimeType = mMockResolver.getType(LunchBuddy.Courses.CONTENT_URI);
		assertEquals(LunchBuddy.Courses.CONTENT_TYPE, mimeType);
		
		Uri courseIdUri = ContentUris.withAppendedId(LunchBuddy.Courses.CONTENT_ID_URI_BASE, 1);
		mimeType = mMockResolver.getType(courseIdUri);
		assertEquals(LunchBuddy.Courses.CONTENT_ITEM_TYPE, mimeType);
		
		mimeType = mMockResolver.getType(INVALID_URI);
	}
	
	public void testQueriesOnCoursesUri() {
		final String[] PROJECTION = new String[] {
				LunchBuddy.Courses._ID,
				LunchBuddy.Courses.COLUMN_NAME_TITLE_FI,
				LunchBuddy.Courses.COLUMN_NAME_TITLE_EN,
				LunchBuddy.Courses.COLUMN_NAME_PRICE,
				LunchBuddy.Courses.COLUMN_NAME_PROPERTIES,
				LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP,
				LunchBuddy.Courses.COLUMN_NAME_REF_TITLE
		};
		final String SELECTION = LunchBuddy.Courses.COLUMN_NAME_REF_TITLE + " = " + "?" + " AND " 
				+ LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP + " = " + "?";
		final String[] SELECTION_ARGS = { "testaurant1", String.valueOf(1349038800) };
		final String SORT_ORDER = "_id asc";
		
		Cursor cursor = mMockResolver.query(
				LunchBuddy.Courses.CONTENT_URI, 
				PROJECTION, 
				SELECTION, 
				SELECTION_ARGS, 
				null);
		
		assertEquals(0, cursor.getCount());
		
		insertData();
		
		cursor = mMockResolver.query(
				LunchBuddy.Courses.CONTENT_URI, 
				PROJECTION, 
				SELECTION, 
				SELECTION_ARGS, 
				SORT_ORDER);
		
		assertEquals(2, cursor.getCount());
		assertEquals(PROJECTION.length, cursor.getColumnCount());
		
		assertEquals(PROJECTION[0], cursor.getColumnName(0));
		assertEquals(PROJECTION[1], cursor.getColumnName(1));
		assertEquals(PROJECTION[2], cursor.getColumnName(2));
		assertEquals(PROJECTION[3], cursor.getColumnName(3));
		assertEquals(PROJECTION[4], cursor.getColumnName(4));
		assertEquals(PROJECTION[5], cursor.getColumnName(5));
		assertEquals(PROJECTION[6], cursor.getColumnName(6));
		
		int index = 0;
		cursor.moveToFirst();
		do {
			assertEquals(TEST_COURSES[index].getTitleFi(), cursor.getString(1));
			assertEquals(TEST_COURSES[index].getTitleEn(), cursor.getString(2));
			index++;
		} while (cursor.moveToNext());
	}
}
