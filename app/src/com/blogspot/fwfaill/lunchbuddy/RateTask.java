package com.blogspot.fwfaill.lunchbuddy;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class RateTask extends AsyncTask<String, Void, Void> {
	
	private static final String BASE_URL = "http://lunchbuddy.cloudfoundry.com/course/rate?id=";

	@Override
	protected Void doInBackground(String... params) {
		long id = Long.parseLong(params[0]);
		String rating = params[1];
		HttpClient client = new DefaultHttpClient();
		HttpPut req = new HttpPut(
				BASE_URL + id + "&rating=" + rating);
		try {
			client.execute(req);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
