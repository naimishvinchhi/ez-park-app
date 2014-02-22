package com.philly.ezpark;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

public class ParkActivity extends Activity {
	
	ListView listParkings;
	ParkingAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_park);
		
		listParkings = (ListView) findViewById(R.id.lvParkings);
		new SyncData().execute();
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
//				JSONArray arrayForums = new JSONArray(FakeJsonProvider.FORUMS);
				MessageController test = new MessageController();
				arrayForums = test.getArray(null, null, Constants.REST_PARKING).getJSONArray("array");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
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

}
