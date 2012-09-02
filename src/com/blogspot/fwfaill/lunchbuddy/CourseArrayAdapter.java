package com.blogspot.fwfaill.lunchbuddy;

import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CourseArrayAdapter extends ArrayAdapter<Course> {
	private LayoutInflater mInflater;

	private Course[] mCourses;
	
	private Locale locale = Locale.getDefault();

	public CourseArrayAdapter(Context context, int textViewResourceId,
			Course[] courses, LayoutInflater inflater) {
		super(context, textViewResourceId, courses);
		mCourses = courses;
		mInflater = inflater;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.course, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.course_title);
			holder.price = (TextView) convertView.findViewById(R.id.course_price);
			holder.properties = (TextView) convertView.findViewById(R.id.course_properties);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(locale.toString().equals("fi_FI") ? mCourses[position].getTitle_fi() : mCourses[position].getTitle_en());
		holder.price.setText((mCourses[position].getPrice()));
		holder.properties.setText((mCourses[position].getProperties()));
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView title;
		TextView price;
		TextView properties;
	}
}