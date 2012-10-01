package com.blogspot.fwfaill.lunchbuddy.test;

import android.test.ActivityInstrumentationTestCase2;
import com.blogspot.fwfaill.lunchbuddy.LunchBuddyActivity;

public class LunchBuddyActivityTests extends ActivityInstrumentationTestCase2<LunchBuddyActivity> {

    public LunchBuddyActivityTests() {
        super(LunchBuddyActivity.class);
    }

    public void testActivityExists() {
        assertNotNull(getActivity());
    }
}
