package com.blogspot.fwfaill.lunchbuddy;

public class Course {

	private long timestamp;
	private String refTitle;
	private String titleFi;
	private String titleEn;
	private String price;
	private String properties;
	
	public Course() { }
	
	public Course(long timestamp, String refTitle, String titleFi, String price, String properties) {
		this.timestamp = timestamp;
		this.refTitle = refTitle;
		this.titleFi = titleFi;
		this.price = price;
		this.properties = properties;
	}

	public Course(long timestamp, String refTitle, String titleFi,
			String titleEn, String price, String properties) {
		this.timestamp = timestamp;
		this.refTitle = refTitle;
		this.titleFi = titleFi;
		this.titleEn = titleEn;
		this.price = price;
		this.properties = properties;
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
}
