package com.example.sunshineairlines_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FlightAdapter extends ArrayAdapter<FlightData> {
    public FlightAdapter(Context context, ArrayList<FlightData> list) {
        super(context, R.layout.flight_item, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.flight_item,parent,false);
        FlightData data=getItem(position);
        ((TextView)view.findViewById(R.id.flightItem_airline)).setText(data.airlineName);
        ((TextView)view.findViewById(R.id.flightItem_tickets)).setText(String.format("%d available tickets",data.availableTickets));
        ((TextView)view.findViewById(R.id.flightItem_date)).setText(data.departureDate);
        ((TextView)view.findViewById(R.id.flightItem_detail)).setText(String.format("FlightNumber: %s\nPrice: $%.2f\nCabin Type: %s\nAircraft: %s\nTime: %s",data.flightNumber,data.price,data.cabinTypeName,data.aircraft,data.departureTime));
        return view;
    }
}
