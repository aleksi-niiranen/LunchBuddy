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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class Scraper {

	public static final String[] WEEKDAYS = new String[] {
		"Monday",
		"Tuesday",
		"Wednesday",
		"Thursday",
		"Friday"
	};
	
	public static Map scrape() throws IOException {
		Document docFi = Jsoup.connect("http://www.unica.fi/fi/ravintolat/nutritio/").get();
		Document docEn = Jsoup.connect("http://www.unica.fi/en/restaurants/nutritio/").get();
		
		String weekday = null;
		Map<String, List<String>> menuListEn = new HashMap<String, List<String>>();
		Elements resultEn = docEn.select(".menu-list div");
		for(Element e : resultEn) {
			List<String> courses = new ArrayList<String>();
			weekday = WEEKDAYS[Integer.parseInt(e.child(0).attr("data-dayofweek"))];
			Elements tableRows = e.select("table tr");
			for(Element row : tableRows) {
				courses.add(row.child(0).html());
			}
			menuListEn.put(weekday, courses);
		}
		
		Map menuListFi = new HashMap();
		String restaurant = docFi.select(".head h1").html();
		menuListFi.put("restaurant", restaurant);
		Elements resultFi = docFi.select(".menu-list div");
		for(Element e : resultFi) {
			List<HashMap<String, String>> courses = new ArrayList<HashMap<String, String>>();
			weekday = WEEKDAYS[Integer.parseInt(e.child(0).attr("data-dayofweek"))];
			Elements tableRows = e.select("table tr");
			tableRows.remove(0);
			for(Element row : tableRows) {
				HashMap<String, String> course = new HashMap<String, String>();
				course.put("titleFi", row.child(0).html());
				StringBuilder properties = new StringBuilder();
				for(Element p : row.child(1).select("span")) {
					properties
						.append(p.html())
						.append(" ");
				}
				course.put("properties", properties.toString());
				course.put("price", row.child(2).html().substring(7));
				courses.add(course);
			}
			menuListFi.put(weekday, courses);
		}
		
		for(int i = 0; i < 5; i++) {
			String day = WEEKDAYS[i];
			for(int j = 0; j < ((List) menuListFi.get(day)).size(); j++) {
				((Map)((List) menuListFi.get(day)).get(j)).put("titleEn", menuListEn.get(day).get(j));
			}
		}
		
		return menuListFi;
	}
}
