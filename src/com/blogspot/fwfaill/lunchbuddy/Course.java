package com.blogspot.fwfaill.lunchbuddy;

public class Course {

	private String title_en;
	private String title_fi;
	private String price;
	private String properties;
	
	public Course(String title_en, String title_fi, String price, String properties) {
		this.title_en = title_en;
		this.title_fi = title_fi;
		this.price = price;
		this.properties = properties;
	}

	public String getTitle_en() {
		return title_en;
	}

	public void setTitle_en(String title_en) {
		this.title_en = title_en;
	}

	public String getTitle_fi() {
		return title_fi;
	}

	public void setTitle_fi(String title_fi) {
		this.title_fi = title_fi;
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
}
