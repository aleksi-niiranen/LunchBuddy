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

import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.TextView;

public class CourseCursorAdapter extends ResourceCursorAdapter {
	
	private static final String TAG = "CourseCursorAdapter";
	
	private Locale mLocale = Locale.getDefault();
	private boolean mFi;
	
	private int mColumnIndexTitleFi;
	private int mColumnIndexTitleEn;
	private int mColumnIndexTitlePrice;
	private int mColumnIndexTitleProperties;
	
	public CourseCursorAdapter(int layout, Context context, Cursor c) {
		super(context, layout, c);
		mFi = mLocale.toString().equals("fi_FI");
		mColumnIndexTitleFi = c.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_TITLE_FI);
		mColumnIndexTitleEn = c.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_TITLE_EN);
		mColumnIndexTitlePrice = c.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_PRICE);
		mColumnIndexTitleProperties = c.getColumnIndexOrThrow(LunchBuddy.Courses.COLUMN_NAME_PROPERTIES);
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		ViewHolder holder = (ViewHolder) view.getTag();
		
		if (holder == null) {
			holder = new ViewHolder();
			holder.title = (TextView) view.findViewById(R.id.course_title);
			holder.price = (TextView) view.findViewById(R.id.course_price);
			holder.properties = (TextView) view.findViewById(R.id.course_properties);
			
			view.setTag(holder);
		}
		
		holder.title.setText(mFi 
				? c.getString(mColumnIndexTitleFi) 
				: c.getString(mColumnIndexTitleEn));
		holder.price.setText(c.getString(mColumnIndexTitlePrice) + " €");
		holder.properties.setText(c.getString(mColumnIndexTitleProperties));
	}

	static class ViewHolder {
		public TextView title;
		public TextView price;
		public TextView properties;
	}
}
