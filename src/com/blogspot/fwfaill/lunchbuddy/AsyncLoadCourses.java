package com.blogspot.fwfaill.lunchbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncLoadCourses extends AsyncTask<HttpGet, Void, String> {
	
	private static final String TAG = "AsyncLoadCourses";
	
	private String mResponseString = null;
	
	private String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, e.getMessage());
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	@Override
	protected String doInBackground(HttpGet... params) {
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = client.execute(params[0]);
			
			int statusCode = response.getStatusLine().getStatusCode();
			String reasonPhrase = response.getStatusLine().getReasonPhrase();
			Log.d(TAG, "status code: " + statusCode);
			Log.d(TAG, reasonPhrase);
			
//			HttpEntity entity = response.getEntity();
//			if (entity != null) {
//				InputStream is = entity.getContent();
//				mResponseString = convertStreamToString(is);
//				is.close();
//			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mResponseString;
	}

	@Override
	protected void onPostExecute(String param) {
		if (param != null) {
			
		}
	}
}
