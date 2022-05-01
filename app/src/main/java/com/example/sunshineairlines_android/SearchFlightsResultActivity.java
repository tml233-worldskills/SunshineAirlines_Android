package com.example.sunshineairlines_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchFlightsResultActivity extends AppCompatActivity {

    UserInfo userInfo;
    ArrayList<FlightData> flights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flights_result);

        userInfo=(UserInfo)getIntent().getSerializableExtra("userInfo");
        flights=(ArrayList<FlightData>)getIntent().getSerializableExtra("flights");

        TextView totalText=findViewById(R.id.searchFlightsResult_text_total);
        totalText.setText(String.format("Total %d flights",flights.size()));

        ListView list=findViewById(R.id.searchFlightsResult_list);
        list.setAdapter(new FlightAdapter(this,flights));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FlightData data=flights.get(i);
                if(!data.cabinTypeId.equals("first")){
                    Toast.makeText(SearchFlightsResultActivity.this,"You can only select seat for first class tickets!",Toast.LENGTH_SHORT).show();
                    return;
                }
                //Intent intent=new Intent(SearchFlightsResultActivity.this,)
            }
        });

        ((Button)findViewById(R.id.searchFlightsResult_btn_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
