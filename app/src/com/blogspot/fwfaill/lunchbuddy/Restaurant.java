package com.blogspot.fwfaill.lunchbuddy;

public class Restaurant {

	private String title;
	private String address;
	private String location;

	public Restaurant(String title, String address, String location) {
		this.title = title;
		this.address = address;
		this.location = location;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
}
