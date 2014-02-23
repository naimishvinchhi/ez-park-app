package com.philly.ezpark;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ParkActivity extends Activity {
	
	ListView listParkings;
	ParkingAdapter adapter;
	GPSTracker gps;
	TextView tvAddress;
	Double latitude = 0.0;
	Double longitude = 0.0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_park);
		
		listParkings = (ListView) findViewById(R.id.lvParkings);
		new SyncData().execute();
		
		tvAddress = (TextView) findViewById(R.id.txtSearch);
		
		Button btn = (Button) findViewById(R.id.btnSearch);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (tvAddress.getText().toString().length() == 0) {
					gps = new GPSTracker(ParkActivity.this);
					if(gps.canGetLocation()){
						latitude = gps.getLatitude();
						longitude = gps.getLongitude();
						Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
						new SyncData().execute();
					} else {
						gps.showSettingsAlert();
					}
				} else {
					new AddressTask().execute();
				}
			}
		});
		
		Button btnMap = (Button) findViewById(R.id.btnMap);
		btnMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ParkActivity.this, MapActivity.class));
			}
		});
	}
	
	private void setList(JSONArray arrays) {
		adapter = new ParkingAdapter(this, arrays);
		listParkings.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.park, menu);
		return true;
	}
	
	private class SyncData extends AsyncTask<Object, Void, JSONArray> {
		
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(ParkActivity.this, "", "Searching ...");
			dialog.setCancelable(false);
		}
		@Override
		protected JSONArray doInBackground(Object... params) {
			JSONArray arrayForums = null;
			try {
				MessageController test = new MessageController();
				arrayForums = test.getArray(null, null, String.format(Constants.REST_PARKING, latitude, longitude)).getJSONArray("array");
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			return arrayForums;
		}
		
		@Override
		protected void onPostExecute(JSONArray result) {
			setList(result);
			dialog.dismiss(); 
		}
		
	}
	
	private class AddressTask extends AsyncTask<Object, Void, JSONObject> {
		
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
		}
		@Override
		protected JSONObject doInBackground(Object... params) {
			JSONObject address = null;
			MessageController test = new MessageController();
			try {
				address = test.post(null, null, "http://maps.google.com/maps/api/geocode/json?address=" + URLEncoder.encode(tvAddress.getText().toString(),"UTF-8") + "&sensor=false");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return address;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				longitude = ((JSONArray)result.get("results")).getJSONObject(0)
				        .getJSONObject("geometry").getJSONObject("location")
				        .getDouble("lng");
				latitude = ((JSONArray)result.get("results")).getJSONObject(0)
						.getJSONObject("geometry").getJSONObject("location")
						.getDouble("lat");
				
				Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
				new SyncData().execute();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
