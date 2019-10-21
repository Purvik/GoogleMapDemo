package com.student.googlemapdemo.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.student.googlemapdemo.R;
import com.student.googlemapdemo.Restaurant;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {


    private Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_marker_window, null);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvPhone = view.findViewById(R.id.tvPhone);

        Restaurant restaurant = (Restaurant) marker.getTag();
        tvName.setText(restaurant.getName());
        tvPhone.setText(restaurant.getPhone());

        return view;
    }
}
