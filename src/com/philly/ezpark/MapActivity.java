package com.philly.ezpark;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity {
	
	GPSTracker gps;
	TextView tvAddress;
	Double latitude = 0.0;
	Double longitude = 0.0;
	private GoogleMap googleMap;
	private JSONArray results;
	private String[] dates = {"","Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		initMap();
		
		loadCurrentLocation();
		
		tvAddress = (TextView) findViewById(R.id.txtSearch);
		
		Button btn = (Button) findViewById(R.id.btnSearch);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (tvAddress.getText().toString().length() == 0) {
					loadCurrentLocation();
				} else {
					new AddressTask().execute();
				}
			}
		});
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        setList(results);
    }
	
	private void loadCurrentLocation() {
		gps = new GPSTracker(MapActivity.this);
		if(gps.canGetLocation()){
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
			Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
			new SyncData().execute();
		} else {
			gps.showSettingsAlert();
		}
	}
	
	private void initMap() {
		if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
		}
	}
	
	private void addMarker(double lat, double lon, String text, String snip) {
		LatLng current = new LatLng(lat, lon);
		
		googleMap.setMyLocationEnabled(true);
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 16));

		googleMap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking))
                .title(text)
                .snippet(snip)
                .position(current));
	}
	
	private void setList(JSONArray results) {
		initMap();
		
		if (latitude!=0 && longitude!=0) {
			addMarker(latitude, longitude, "Current location", "");
		}
		
		if (results!=null && results.length() > 0) {
			for (int i = 0; i < results.length(); i++) {
				JSONObject item = results.optJSONObject(i);
//				String hours = dates[item.optInt("fromDay")] + " - " + dates[item.optInt("toDay")];
				String hours = dates[1] + " - " + dates[7];
		    	hours += ", " + item.optInt("fromHour") + ":00 - " + item.optInt("toHour")+":00";
				addMarker(item.optDouble("lat"), item.optDouble("lon"), item.optString("address"), item.optDouble("lat")+ " "+item.optDouble("lon"));
			}
		}
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
			dialog = ProgressDialog.show(MapActivity.this, "", "Searching ...");
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
			results = result;
			setList(results);
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
