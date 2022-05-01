package com.example.sunshineairlines_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MileagePointsAdapter extends ArrayAdapter<MileagePointsData> {
    public MileagePointsAdapter(Context context, ArrayList<MileagePointsData> list){
        super(context,R.layout.mileage_points_item,list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view= LayoutInflater.from(getContext()).inflate(R.layout.mileage_points_item,parent,false);
        MileagePointsData data=getItem(position);
        ((TextView)view.findViewById(R.id.mileagePointsItem_points)).setText(data.points+" Points");
        ((TextView)view.findViewById(R.id.mileagePointsItem_date)).setText(data.date);
        return view;
    }
}
