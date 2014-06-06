package com.odeme.keypay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class httpReceive {
	
	int TIMEOUT_MILLISEC = 10000; // = 10 seconds
	
	public String GetList(String pw1,String pw2,String remnant) {
		
		String result="";
		
		try {
			
			JSONObject json = new JSONObject();
			//json.put("GivenVote", GivenVote);
			json.put("PW1", pw1);
			json.put("PW2", pw2);
			json.put("Remnant", remnant);
			// http://androidarabia.net/quran4android/phpserver/connecttoserver.php

			// Log.i(getClass().getSimpleName(), "send  task - start");
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
			//
			HttpParams p = new BasicHttpParams();
			// p.setParameter("name", pvo.getName());
			p.setParameter("num", "1");
			//p.setDoubleParameter(lblGivenVote, Vote);

			// Instantiate an HttpClient
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://www.mustafatasdemir.com/hamdialtinok/servis/retrieve_data.php";
			HttpPost httppost = new HttpPost(url);

			// Instantiate a GET HTTP method
			try {
				Log.i(getClass().getSimpleName(), "send  task - start");
				//
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("Num", pw1));

				httppost.setEntity(new ByteArrayEntity(json.toString().getBytes(
						"UTF8")));

				// Execute HTTP Post Request

				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String response = httpclient.execute(httppost, responseHandler);

				// Parse
				JSONObject json1 = new JSONObject(response);
				JSONArray jArray = json1.getJSONArray("posts");
				
				JSONObject e = jArray.getJSONObject(0);
				result = e.getString("post");

//				Toast.makeText(this, responseBody, Toast.LENGTH_LONG).show();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Log.i(getClass().getSimpleName(), "send  task - end");

		} catch (Throwable t) {
//			Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
		}
		return result;
		

	}


}
