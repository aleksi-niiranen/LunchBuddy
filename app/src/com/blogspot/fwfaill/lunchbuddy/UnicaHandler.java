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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;

public class UnicaHandler implements ResponseHandler {
	
	private LunchBuddyProvider mProvider;
	
	private String mResponseString;
	private String mResponseStringEn;
	
	public UnicaHandler(LunchBuddyProvider provider) {
		mProvider = provider;
	}

	@Override
	public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream is = entity.getContent();
			mResponseString = convertStreamToString(is);
			is.close();
		}
		if (mResponseString != null) {
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki"), 
					new Locale("Finnish", "Finland"));
	    	cal.set(Calendar.HOUR_OF_DAY, 0);
	    	cal.set(Calendar.MINUTE, 0);
	    	cal.set(Calendar.SECOND, 0);
	    	cal.set(Calendar.MILLISECOND, 0);
	    	long timestamp = cal.getTimeInMillis() / 1000;
	    	
	    	List<Course> courses = Scraper.scrapeFi(Jsoup.parse(mResponseString), 
	    			timestamp, Scraper.scrapeEn(Jsoup.parse(mResponseStringEn)));
	    	
	    	for (Course c : courses) {
				mProvider.insert(LunchBuddy.Courses.CONTENT_URI, c.getContentValues());
	    	}
		}
		return null;
	}
	
	public void requestEnTitles() {
		HttpGet get = new HttpGet("http://www.unica.fi/en/");
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				mResponseStringEn = convertStreamToString(is);
				is.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		}
		return null;
	}
}
