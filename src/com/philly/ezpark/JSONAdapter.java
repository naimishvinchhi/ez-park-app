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
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * The base JSON Adapter
 */
public class JSONAdapter extends BaseAdapter implements ListAdapter {

	protected Context mContext;
    protected final JSONArray jsonArray;
    public JSONAdapter(Context context, JSONArray jsonArray) {
        assert mContext != null;
        assert jsonArray != null;

        this.jsonArray = jsonArray;
        this.mContext = context;
    }

    @Override 
    public int getCount() {
        return jsonArray.length();
    }

    @Override 
    public JSONObject getItem(int position) {
        return jsonArray.optJSONObject(position);
    }

    @Override 
    public long getItemId(int position) {
        JSONObject jsonObject = getItem(position);
        return jsonObject.optLong("id");
    }

    @Override 
    public View getView(int position, View convertView, ViewGroup parent) {
    	return null;
    }
}
