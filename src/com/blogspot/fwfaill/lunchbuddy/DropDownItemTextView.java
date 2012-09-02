package com.blogspot.fwfaill.lunchbuddy;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class DropDownItemTextView extends TextView {
	
	public DropDownItemTextView(Context context) {
		super(context);
	}
	
	public DropDownItemTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DropDownItemTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Log.i("dropdownitemtextview", "action down");
			setBackgroundResource(R.color.bottom);
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			Log.i("dropdownitemtextview", "action up");
			setBackgroundResource(0);
		}
		return super.onTouchEvent(event);
	}
}
