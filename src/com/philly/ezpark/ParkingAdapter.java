/*
 * 2013 (c) Drexel University
 *
 * Author: Jeffrey Segall <js572@drexel.edu>
 * License: Proprietary
 * 
 */
package com.philly.ezpark;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * The customized adapter for listview in entry menu
 */
public class ParkingAdapter extends JSONAdapter {
	
	private String[] dates = {"","Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

	public ParkingAdapter(Context context, JSONArray jsonArray) {
		super(context, jsonArray);
	}
	
	@Override 
    public View getView(int position, View convertView, ViewGroup parent) {
    	ForumMenuHolder holder = null;
    	if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_parking, parent, false);
            
            holder = new ForumMenuHolder();
            holder.address = (TextView) convertView.findViewById(R.id.txtAddress);
            holder.remaining = (TextView) convertView.findViewById(R.id.txtRemaining);
            holder.hours = (TextView) convertView.findViewById(R.id.txtHours);
            holder.rate = (TextView) convertView.findViewById(R.id.txtRate);
           
            convertView.setTag(holder);
        } else {
        	holder = (ForumMenuHolder) convertView.getTag();
        }
    	
    	JSONObject item = getItem(position);
    	
    	holder.address.setText(item.optString("address"));
    	holder.remaining.setText("Slots: "+(item.optInt("limit")-item.optInt("count")));
    	String hours = dates[item.optInt("fromDay")] + " - " + dates[item.optInt("toDay")];
    	hours += ", " + item.optInt("fromHour") + ":00 - " + item.optInt("toHour")+":00";
    	holder.hours.setText(hours);
    	holder.rate.setText("$"+item.optDouble("rate"));
    	
    	return convertView;
    }

}

/**
 * Layout holder for each menu item
 */
class ForumMenuHolder
{
    TextView address;
    TextView remaining;
    TextView hours;
    TextView rate;
}