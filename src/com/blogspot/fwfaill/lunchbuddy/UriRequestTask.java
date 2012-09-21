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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;

public class UriRequestTask implements Runnable {
	
	private HttpUriRequest mRequest;
	private ResponseHandler mHandler;
	
	protected Context mContext;
	
	private LunchBuddyProvider mProvider;
	private String mRequestTag;
	
	public UriRequestTask(HttpUriRequest request, ResponseHandler handler, Context context) {
		this(null, null, request, handler, context);
	}
	
	public UriRequestTask(String requestTag, LunchBuddyProvider provider, HttpUriRequest request, ResponseHandler handler, Context context) {
		mRequestTag = requestTag;
		mProvider = provider;
		mRequest = request;
		mHandler = handler;
		mContext = context;
	}

	@Override
	public void run() {
		HttpResponse response;
		
		try {
			response = execute(mRequest);
			if (mHandler.getClass() == UnicaHandler.class)
				((UnicaHandler) mHandler).requestEnTitles();
			mHandler.handleResponse(response);
		} catch (IOException e) {
			Log.w("UriRequestTask", "exception processing asynch request", e);
		} finally {
			if (mProvider != null) {
				mProvider.requestComplete(mRequestTag);
			}
		}
	}

	private HttpResponse execute(HttpUriRequest request) throws IOException {
		HttpClient client = new DefaultHttpClient();
		return client.execute(request);
	}

}
