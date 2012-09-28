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

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class AccountAuthenticatorService extends Service {
	
	private static final String TAG = "AccountAuthenticatorService";
	private static AccountAuthenticatorImpl sAccountAuthenticator = null;
	
	public AccountAuthenticatorService() {
		super();
	}

	@Override
	public IBinder onBind(Intent intent) {
		IBinder ret = null;
		if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
			ret = getAuthenticator().getIBinder();
		return ret;
	}
	
	private AccountAuthenticatorImpl getAuthenticator() {
		if (sAccountAuthenticator == null)
			sAccountAuthenticator = new AccountAuthenticatorImpl(this);
		return sAccountAuthenticator;
	}

	private static class AccountAuthenticatorImpl extends AbstractAccountAuthenticator {
		
		private Context mContext;
		
		public AccountAuthenticatorImpl(Context context) {
			super(context);
			mContext = context;
		}

		@Override
		public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, 
				String[] requiredFeatures, Bundle options) throws NetworkErrorException {
			Bundle reply = new Bundle();
			Account account = new Account("LunchBuddy", "com.blogspot.fwfaill.lunchbuddy.account");
			AccountManager am = AccountManager.get(mContext);
			boolean accountCreated = am.addAccountExplicitly(account, null, null);
			if (accountCreated) {
				reply.putString(AccountManager.KEY_ACCOUNT_NAME, "LunchBuddy");
				reply.putString(AccountManager.KEY_ACCOUNT_TYPE, "com.blogspot.fwfaill.lunchbuddy.account");
			}
			return reply;
		}

		@Override
		public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, 
				Bundle options) throws NetworkErrorException {
			return null;
		}

		@Override
		public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
			return null;
		}

		@Override
		public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, 
				String authTokenType, Bundle options) throws NetworkErrorException {
			return null;
		}

		@Override
		public String getAuthTokenLabel(String authTokenType) {
			return null;
		}

		@Override
		public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, 
				String[] features) throws NetworkErrorException {
			return null;
		}

		@Override
		public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, 
				String authTokenType, Bundle options) throws NetworkErrorException {
			return null;
		}
	}
}
