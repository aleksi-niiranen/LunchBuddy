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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

public class CourseHandler implements ResponseHandler {
	
	private static final String TAG = "CourseHandler";

	public static final String TIMESTAMP = "requested_timestamp";
	public static final String REF_TITLE = "ref_title";
	public static final String COURSES = "courses";
	public static final String TITLE_FI = "title_fi";
	public static final String TITLE_EN = "title_en";
	public static final String PRICE = "price";
	public static final String PROPERTIES = "properties";
	
	private LunchBuddyProvider mProvider;
	
	private String mResponseString;
	
	public CourseHandler(LunchBuddyProvider provider) {
		mProvider = provider;
	}
	
	@Override
	public Object handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream is = entity.getContent();
			mResponseString = convertStreamToString(is);
			is.close();
		}
		
		if (mResponseString != null) {
			try {
				JSONObject jsonRoot = new JSONObject(mResponseString);
				JSONArray jsonCourses = jsonRoot.getJSONArray(COURSES);
				
				for (int i = 0; i < jsonCourses.length(); i++) {
					ContentValues values = new ContentValues();
					JSONObject jsonObject = jsonCourses.getJSONObject(i);
					values.put(LunchBuddy.Courses.COLUMN_NAME_TIMESTAMP, jsonRoot.getJSONObject("meta").getLong(TIMESTAMP));
					values.put(LunchBuddy.Courses.COLUMN_NAME_REF_TITLE, jsonRoot.getJSONObject("meta").getString(REF_TITLE));
					values.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_FI, jsonObject.getString(TITLE_FI));
					values.put(LunchBuddy.Courses.COLUMN_NAME_TITLE_EN, jsonObject.getString(TITLE_EN));
					values.put(LunchBuddy.Courses.COLUMN_NAME_PRICE, jsonObject.optString(PRICE) + " €");
					values.put(LunchBuddy.Courses.COLUMN_NAME_PROPERTIES, jsonObject.optString(PROPERTIES));
					
					mProvider.insert(LunchBuddy.Courses.CONTENT_URI, values);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch (UnsupportedEncodingException e) {
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}
}
