package com.example.sunshineairlines_android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class SearchFlightsResultActivity extends AppCompatActivity {

    UserInfo userInfo;
    FlightSearchData searchData;
    ArrayList<FlightData> flights;

    TextView totalText;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flights_result);

        userInfo=(UserInfo)getIntent().getSerializableExtra("userInfo");
        searchData=(FlightSearchData)getIntent().getSerializableExtra("searchData");

        totalText=findViewById(R.id.searchFlightsResult_text_total);
        listView=findViewById(R.id.searchFlightsResult_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FlightData data=flights.get(i);
                if(!data.cabinTypeId.equals("first")){
                    Toast.makeText(SearchFlightsResultActivity.this,"You can only select seat for first class tickets!",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(SearchFlightsResultActivity.this,SelectSeatsActivity.class);
                intent.putExtra("userInfo",userInfo);
                intent.putExtra("flightData",data);
                startActivityForResult(intent,0);
            }
        });
        ((Button)findViewById(R.id.searchFlightsResult_btn_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        refreshData();
    }

    void refreshData(){
        Future<ArrayList<FlightData>> future=Utils.threads.submit(new Callable<ArrayList<FlightData>>() {
            @Override
            public ArrayList<FlightData> call() {
                ArrayList<FlightData> list=new ArrayList<>();
                try {
                    String str = Utils.sendGet(String.format(Utils.baseUrl + "/flight/list?From=%s&To=%s&CabinType=%s&Date=%s&isAsc=1", searchData.from, searchData.to, searchData.cabinTypeId, searchData.date), null);
                    JSONArray arr=new JSONArray(str);
                    for(int i=0;i<arr.length();i+=1){
                        JSONObject obj=arr.getJSONObject(i);
                        FlightData data=new FlightData();
                        data.id=obj.getString("Id");
                        data.airlineName=obj.getString("AirlineName");
                        data.flightNumber=obj.getString("FlightNumber");
                        data.price=Float.valueOf(obj.getString("Price"));
                        String dt=obj.getString("DepartureTime");
                        String[] split=dt.split(" ");
                        data.departureDate=split[0];
                        data.departureTime=split[1];
                        data.aircraft=obj.getString("Aircraft");
                        data.availableTickets=Integer.valueOf(obj.getString("AvailableTickets"));
                        data.cabinTypeId=searchData.cabinTypeId;
                        data.cabinTypeName=searchData.cabinTypeName;
                        list.add(data);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return list;
            }
        });

        try{
            flights=future.get();
        }catch (Exception e){
            e.printStackTrace();
        }

        totalText.setText(String.format("Total %d flights",flights.size()));
        listView.setAdapter(new FlightAdapter(this,flights));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==0&&resultCode==RESULT_OK){
            refreshData();
        }
    }
}
