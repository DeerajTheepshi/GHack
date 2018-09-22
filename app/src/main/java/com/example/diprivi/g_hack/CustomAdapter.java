package com.example.diprivi.g_hack;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<ItemList> {
    Context context;
    List<ItemList> items;

    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<ItemList> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.items = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.activity_listing,parent,false);
        }

        final ItemList currentResult = items.get(position);
        TextView Lat_lan = (TextView) view.findViewById(R.id.LatLon);
        Lat_lan.setText(currentResult.getLat() + " and " + currentResult.getLon());

        TextView decibel = (TextView) view.findViewById(R.id.deciOnList);
        decibel.setText(currentResult.getDecibel()+"");

        return view;

    }
}