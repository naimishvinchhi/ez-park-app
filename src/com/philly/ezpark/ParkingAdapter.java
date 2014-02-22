/*
 * 2013 (c) Drexel University
 *
 * Author: Jeffrey Segall <js572@drexel.edu>
 * License: Proprietary
 * 
 */
package com.philly.ezpark;

import org.json.JSONArray;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * The customized adapter for listview in entry menu
 */
public class ParkingAdapter extends JSONAdapter {

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
            holder.title = (TextView) convertView.findViewById(R.id.txtName);
           
            convertView.setTag(holder);
        } else {
        	holder = (ForumMenuHolder) convertView.getTag();
        }
    	
    	holder.title.setText(getItem(position).optString("address"));
    	
    	return convertView;
    }

}

/**
 * Layout holder for each menu item
 */
class ForumMenuHolder
{
    TextView title;
}