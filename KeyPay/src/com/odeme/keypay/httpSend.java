package com.odeme.keypay;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import com.odeme.keypay.RestClient;

import android.util.Log;


public class httpSend {
	
//	public int iLanguage = 0;
//	TextView lbl;
//	Typeface arabicFont = null;
	int TIMEOUT_MILLISEC = 10000; // = 10 seconds
	
	/*public void SendPW1andTime(String number , String pw1, String bank) {
		try {
			JSONObject json = new JSONObject();
			//json.put("GivenVote", GivenVote);
			json.put("Number", number);
			json.put("PW1", pw1);
			json.put("Bank", bank);
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpClient client = new DefaultHttpClient(httpParams);
			//
			//String url = "http://10.0.2.2:8080/sample1/webservice2.php?json={\"UserName\":1,\"FullName\":2}";
			String url = "http://www.mustafatasdemir.com/hamdialtinok/servis/wsAddPW1_Time.php";

			HttpPost request = new HttpPost(url);
			request.setEntity(new ByteArrayEntity(json.toString().getBytes(
					"UTF8")));
			request.setHeader("json", json.toString());
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			// If the response does not enclose an entity, there is no need
			if (entity != null) {
				InputStream instream = entity.getContent();

				String result = RestClient.convertStreamToString(instream);
				Log.i("Read from server", result);
//				Toast.makeText(this,  result,Toast.LENGTH_LONG).show();
			}
		} catch (Throwable t) {
			//Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
			Log.i("hamdi server", t.getMessage());
		}
	}*/
	public String SendData(JSONObject json,String service) {
		String result="False";
		try {
			
			
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpClient client = new DefaultHttpClient(httpParams);
			//
			//String url = "http://10.0.2.2:8080/sample1/webservice2.php?json={\"UserName\":1,\"FullName\":2}";
			String url = "http://www.mustafatasdemir.com/hamdialtinok/servis/"+service+".php";

			HttpPost request = new HttpPost(url);
			request.setEntity(new ByteArrayEntity(json.toString().getBytes(
					"UTF8")));
			request.setHeader("json", json.toString());
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			// If the response does not enclose an entity, there is no need
			if (entity != null) {
				InputStream instream = entity.getContent();

				String res = RestClient.convertStreamToString(instream);
				JSONObject json1 = new JSONObject(res);
				JSONArray jArray = json1.getJSONArray("posts");

				result = jArray.getString(0);
				Log.i("Read from server", result);				
				
//				Toast.makeText(this,  result,Toast.LENGTH_LONG).show();
			}
		} catch (Throwable t) {
			//Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
			Log.i("hamdi server", t.getMessage());
		}
		return result;
	}
}