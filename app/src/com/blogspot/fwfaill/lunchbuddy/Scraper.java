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
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scraper {

	private static final int NUTRITIO_INDEX = 8;
	
	public static List<Course> scrapeFi(Document doc, long timestamp, List<String> menuListEn) throws IOException {
		List<Course> menuList = new ArrayList<Course>();
		try {
			Element result = doc.select("#menu-wrap ul").get(NUTRITIO_INDEX);
			String refTitle = result.child(0).html();
			Elements courses = result.select("li");
			for (Element e : courses) {
				try {
					String title = e.child(0).html();
					Elements limitations = e.child(1).select(".G, .L, .M, .VL, .VEG");
					StringBuilder properties = new StringBuilder();
					for (Element l : limitations) {
						properties.append(l.html()).append(" ");
					}
					String price = e.child(3).html().substring(7);
					Course c = new Course(timestamp, refTitle, title, price, properties.toString());
					menuList.add(c);
				} catch (Exception ex) {
					// Catches exceptions that are thrown if menu contains entries that are not courses.
				}
			}
		} catch (IndexOutOfBoundsException e) {
			// consume exception. caused by no courses being served this day.
		}
		
		
		for (int i = 0; i < menuList.size(); i++) {
			menuList.get(i).setTitleEn(menuListEn.get(i));
		}
		
		return menuList;
	}

	public static List<String> scrapeEn(Document doc) {
		List<String> menuListEn = new ArrayList<String>();
		try {
			Element result = doc.select("#menu-wrap ul").get(NUTRITIO_INDEX);
			Elements courses = result.select("li");
			for (Element e : courses) {
				String title = e.child(0).html();
				menuListEn.add(title);
			}
		} catch (IndexOutOfBoundsException e) {
			// consume exception. caused by no courses being served this day.
		}
		
		return menuListEn;
	}
}
