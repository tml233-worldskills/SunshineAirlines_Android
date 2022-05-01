package com.example.sunshineairlines_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class SelectSeatsActivity extends AppCompatActivity {

    HashMap<String,SeatData> seatData=new HashMap<>();
    String selectedSeat=null;

    UserInfo userInfo;
    FlightData flightData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_seats);

        userInfo=(UserInfo)getIntent().getSerializableExtra("userInfo");
        flightData=(FlightData) getIntent().getSerializableExtra("flightData");

        Future<ArrayList<String>> future=Utils.threads.submit(new Callable<ArrayList<String>>() {
            @Override
            public ArrayList<String> call() {
                ArrayList<String> list=new ArrayList<>();
                try{
                    String str=Utils.sendGet(String.format(Utils.baseUrl+"/order?FlightId=%s&CabinType=first",flightData.id),null);
                    JSONArray arr=new JSONArray(str);
                    for(int i=0;i<arr.length();i+=1){
                        JSONObject obj=arr.getJSONObject(i);
                        list.add(obj.getString("RowNumber")+obj.getString("ColumnName"));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return list;
            }
        });

        registerSeat("1A",(ImageView) findViewById(R.id.seat1A));
        registerSeat("2A",(ImageView) findViewById(R.id.seat2A));
        registerSeat("3A",(ImageView) findViewById(R.id.seat3A));
        registerSeat("1C",(ImageView) findViewById(R.id.seat1C));
        registerSeat("2C",(ImageView) findViewById(R.id.seat2C));
        registerSeat("3C",(ImageView) findViewById(R.id.seat3C));
        registerSeat("1J",(ImageView) findViewById(R.id.seat1J));
        registerSeat("2J",(ImageView) findViewById(R.id.seat2J));
        registerSeat("3J",(ImageView) findViewById(R.id.seat3J));
        registerSeat("1L",(ImageView) findViewById(R.id.seat1L));
        registerSeat("2L",(ImageView) findViewById(R.id.seat2L));
        registerSeat("3L",(ImageView) findViewById(R.id.seat3L));

        ((TextView)findViewById(R.id.selectSeats_text_info)).setText(
                String.format("Departure Time: %s\nFlight Number: %s %s\nAircraft: %s\nCabin Type: %s",
                        flightData.departureDate,flightData.departureTime,
                        flightData.flightNumber,
                        flightData.aircraft,
                        flightData.cabinTypeName
                )
        );
        ((Button)findViewById(R.id.selectSeats_btn_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ((Button)findViewById(R.id.selectSeats_btn_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedSeat==null){
                    Toast.makeText(SelectSeatsActivity.this,"Please select a seat!",Toast.LENGTH_SHORT).show();
                    return;
                }
                Future<Boolean> future=Utils.threads.submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        String str=Utils.sendPost(
                                String.format(Utils.baseUrl+"/order?FlightId=%s&UserId=%s&CabinType=%s&ColumnName=%s&RowNumber=%s",
                                    flightData.id,userInfo.id,flightData.cabinTypeId,selectedSeat.substring(1,2),selectedSeat.substring(0,1)
                                ),null
                        );
                        return str.equals("true");
                    }
                });

                try{
                    boolean succeeded=future.get();
                    if(succeeded){
                        Toast.makeText(SelectSeatsActivity.this,"Succeeded!",Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }else {
                        Toast.makeText(SelectSeatsActivity.this,"Failed!",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        try{
            ArrayList<String> list=future.get();
            for(String id:list){
                setSeatOccupied(id);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void registerSeat(final String id, ImageView view){
        SeatData data=new SeatData();
        data.view=view;
        seatData.put(id,data);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeatData data=seatData.get(id);
                if(!data.occupied){
                    setSeatImage(selectedSeat,0);
                    setSeatImage(id,2);
                    selectedSeat=id;
                }
            }
        });
    }

    void setSeatOccupied(String id){
        SeatData data=seatData.get(id);
        data.occupied=true;
        setSeatImage(id,1);
    }
    // 0 available
    // 1 occupied
    // 2 selected
    void setSeatImage(String id, int image){
        if(id==null){
            return;
        }
        int drawable;
        switch(image){
            case 1:
                drawable=R.drawable.chair_occupied;
                break;
            case 2:
                drawable=R.drawable.chair_yourchosen;
                break;
            default:
                drawable=R.drawable.chair_available;
                break;
        }
        SeatData data=seatData.get(id);
        data.view.setImageResource(drawable);
    }
}
