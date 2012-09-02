package com.blogspot.fwfaill.lunchbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RestClient {

	public static final String TAG = "RuokalistaRestClient";
	public static final int POST = 0;
	public static final int GET = 1;
	public static final int JSON = 2;
	public static final int XML = 3;
	
	private String mUrl;
	private String mResponsePhrase;
	private String mResponse;
	private JSONObject mJSONObj;
	private int mResponseCode;
	private HashMap<String, String> mParams;
	private HashMap<String, String> mHeaders;
	
	public RestClient(String restUrl) {
		this.mUrl = restUrl;
		this.mParams = new HashMap<String, String>();
		this.mHeaders = new HashMap<String, String>();
	}
	
	public JSONObject getJSONObj() {
		return mJSONObj;
	}
	
	public String getResponse() {
		return mResponse;
	}
	
	public String getErrorMessage() {
		return mResponsePhrase;
	}
	
	public int getResponseCode() {
		return mResponseCode;
	}
	
	public void addParam(String key, String value) {
		mParams.put(key, value);
	}
	
	public void addHeader(String key, String value) {
		mHeaders.put(key, value);
	}
	
	public String buildParams() throws UnsupportedEncodingException {
		Iterator<Entry<String, String>> it = mParams.entrySet().iterator();
		String res = "?";
		while (it.hasNext()) {
			Entry<String, String> pair = it.next();
			String add = pair.getKey() + "=" + URLEncoder.encode(pair.getValue(), "UTF-8");
			if (mParams.size() > 1) {
				res += "&amp;" + add;
			} else {
				res += add;
			}
		}
		return res;
	}
	
	public String convertStreamToString(InputStream is) throws IOException {
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
	
	public void callWebService(int method) throws IllegalStateException, ClientProtocolException, IOException {
		switch (method) {
		case GET:
		{
			HttpGet request = new HttpGet(mUrl + buildParams());
			
			Iterator<Entry<String, String>> it = mHeaders.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> header = it.next();
				request.addHeader(header.getKey(), header.getValue());
			}
			
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			
			response = client.execute(request);
			mResponseCode = response.getStatusLine().getStatusCode();
			mResponsePhrase = response.getStatusLine().getReasonPhrase();
			Log.i(TAG, "response code: " + mResponseCode);
			Log.i(TAG, "response phrase: " + mResponsePhrase);
			
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				InputStream is = entity.getContent();
				mResponse = convertStreamToString(is);
				mResponse = mResponse.substring(1, mResponse.length() - 1);
				mResponse = "{" + mResponse + "}";
				Log.i(TAG, "response: " + mResponse);
				is.close();
			}
		}
			break;
		case POST:
		{
			HttpPost request = new HttpPost(mUrl + buildParams());
			
			Iterator<Entry<String, String>> it = mHeaders.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> header = it.next();
				request.addHeader(header.getKey(), header.getValue());
			}
			
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			
			response = client.execute(request);
			mResponseCode = response.getStatusLine().getStatusCode();
			mResponsePhrase = response.getStatusLine().getReasonPhrase();
			Log.i(TAG, "response code: " + mResponseCode);
			Log.i(TAG, "response phrase: " + mResponsePhrase);
			
			break;
		}
		}
	}
	
	public void createObjectJson() throws JSONException {
		mJSONObj = new JSONObject(mResponse);
		Log.i(TAG, "json object" + mJSONObj.toString() + "/json object");
	}
}
