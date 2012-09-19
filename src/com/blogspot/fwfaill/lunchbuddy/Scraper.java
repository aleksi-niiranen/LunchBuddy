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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scraper {

	private static final int NUTRITIO_INDEX = 8;
	
	public static List<Course> scrape() throws IOException {
		Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki"), new Locale("Finnish", "Finland"));
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	long timestamp = cal.getTimeInMillis() / 1000;
    	
		Document docFi = Jsoup.connect("http://www.unica.fi/fi/").get();
		Document docEn = Jsoup.connect("http://www.unica.fi/en/").get();
		
		List<String> menuListEn = new ArrayList<String>();
		Element result = docEn.select("#menu-wrap ul").get(NUTRITIO_INDEX);
		Elements courses = result.select("li");
		for (Element e : courses) {
			String title = e.child(0).html();
			menuListEn.add(title);
		}
		
		List<Course> menuList = new ArrayList<Course>();
		result = docFi.select("#menu-wrap ul").get(NUTRITIO_INDEX);
		String refTitle = result.child(0).html();
		courses = result.select("li");
		for (Element e : courses) {
			String title = e.child(0).html();
			Elements limitations = e.child(1).select(".G, .L, .M, .VL, .VEG");
			StringBuilder properties = new StringBuilder();
			for (Element l : limitations) {
				properties.append(l.html()).append(" ");
			}
			String price = e.child(3).html().substring(7);
			Course c = new Course(timestamp, refTitle, title, price, properties.toString());
			menuList.add(c);
		}
		
		for (int i = 0; i < menuList.size(); i++) {
			menuList.get(i).setTitleEn(menuListEn.get(i));
		}
		
		return menuList;
	}
}
