package com.example.sunshineairlines_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SearchFlightsResultActivity extends AppCompatActivity {

    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flights_result);

        userInfo=(UserInfo)getIntent().getSerializableExtra("userInfo");
    }
}
