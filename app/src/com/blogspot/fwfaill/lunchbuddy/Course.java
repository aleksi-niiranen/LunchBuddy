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

import android.content.ContentValues;
import android.database.Cursor;

public class Course {

	private long id;
	private long timestamp;
	private String refTitle;
	private String titleFi;
	private String titleEn;
	private String titleSe;
	private String price;
	private String properties;
	private boolean ratedGood;
	private boolean ratedBad;
	
	public static Course createFromCursor(Cursor cursor) {
		Course course = new Course();
		course.setId(cursor.getLong(cursor.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_ID)));
		course.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP)));
		course.setRefTitle(cursor.getString(cursor.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_REF_TITLE)));
		course.setTitleFi(cursor.getString(cursor.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_TITLE_FI)));
		course.setTitleEn(cursor.getString(cursor.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_TITLE_EN)));
		course.setTitleSe(cursor.getString(cursor.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_TITLE_SE)));
		course.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_PRICE)));
		course.setProperties(cursor.getString(cursor.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_PROPERTIES)));
		course.setRatedGood(cursor.getInt(cursor.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_RATED_GOOD)) == 1 ? true : false);
		course.setRatedBad(cursor.getInt(cursor.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_RATED_BAD)) == 1 ? true : false);
		return course;
	}
	
	public Course() { }

	public Course(long id, long timestamp, String refTitle, String titleFi,
			String titleEn, String titleSe, String price, String properties) {
		this.id = id;
		this.timestamp = timestamp;
		this.refTitle = refTitle;
		this.titleFi = titleFi;
		this.titleEn = titleEn;
		this.titleSe = titleSe;
		this.price = price;
		this.properties = properties;
		ratedGood = false;
		ratedBad = false;
	}
	
	/**
	 * Returns the id of the course on the server side database.
	 * @return id
	 */
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getRefTitle() {
		return refTitle;
	}

	public void setRefTitle(String refTitle) {
		this.refTitle = refTitle;
	}

	public String getTitleFi() {
		return titleFi;
	}

	public void setTitleFi(String titleFi) {
		this.titleFi = titleFi;
	}

	public String getTitleEn() {
		return titleEn;
	}

	public void setTitleEn(String titleEn) {
		this.titleEn = titleEn;
	}
	
	public String getTitleSe() {
		return titleSe;
	}

	public void setTitleSe(String titleSe) {
		this.titleSe = titleSe;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public boolean isRatedGood() {
		return ratedGood;
	}

	public void setRatedGood(boolean ratedGood) {
		this.ratedGood = ratedGood;
	}

	public boolean isRatedBad() {
		return ratedBad;
	}

	public void setRatedBad(boolean ratedBad) {
		this.ratedBad = ratedBad;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result
				+ ((properties == null) ? 0 : properties.hashCode());
		result = prime * result
				+ ((refTitle == null) ? 0 : refTitle.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + ((titleEn == null) ? 0 : titleEn.hashCode());
		result = prime * result + ((titleFi == null) ? 0 : titleFi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Course other = (Course) obj;
		if (refTitle == null) {
			if (other.refTitle != null)
				return false;
		} else if (!refTitle.equals(other.refTitle))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}
	
	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put(LunchBuddy.Courses.COLUMN_NAME_ID, id);
		values.put(LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP, timestamp);
		values.put(LunchBuddy.Courses.COLUMN_NAME_REF_TITLE, refTitle);
		values.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_FI, titleFi);
		values.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_EN, titleEn);
		values.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_SE, titleSe);
		values.put(LunchBuddy.Courses.COLUMN_NAME_PRICE, price);
		values.put(LunchBuddy.Courses.COLUMN_NAME_PROPERTIES, properties);
		values.put(LunchBuddy.Courses.COLUMN_NAME_RATED_GOOD, ratedGood ? 1 : 0);
		values.put(LunchBuddy.Courses.COLUMN_NAME_RATED_BAD, ratedBad ? 1 : 0);
		return values;
	}
}
