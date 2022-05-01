package com.example.sunshineairlines_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class MileagePointsActivity extends AppCompatActivity {

    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mileage_points);

        userInfo=(UserInfo)getIntent().getSerializableExtra("userInfo");
        ListView listView=findViewById(R.id.searchFlightsResult_list);

        try {
            ArrayList<MileagePointsData> mpList = Utils.threads.submit(new Callable<ArrayList<MileagePointsData>>() {
                @Override
                public ArrayList<MileagePointsData> call() {
                    ArrayList<MileagePointsData> list = new ArrayList<>();
                    try {
                        String str = Utils.sendGet(Utils.baseUrl + "/mileagepoints/" + userInfo.id, null);
                        JSONArray arr = new JSONArray(str);
                        for (int i = 0; i < arr.length(); i += 1) {
                            MileagePointsData data = new MileagePointsData();
                            JSONObject obj = arr.getJSONObject(i);
                            data.points = obj.getString("Points");
                            data.date = obj.getString("Date");
                            list.add(data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return list;
                }
            }).get();

            int totalPoints=0;
            for(MileagePointsData data:mpList){
                totalPoints+=Integer.valueOf(data.points);
            }
            ((TextView)findViewById(R.id.mileagePoints_text_pointsTitle)).setText(String.format("Hi, %s %s %s. Your total mileage points is",userInfo.male?"Mr.":"Mrs.",userInfo.firstName,userInfo.lastName));
            ((TextView)findViewById(R.id.mileagePoints_text_points)).setText(String.format("%d Points",totalPoints));

            listView.setAdapter(new MileagePointsAdapter(this,mpList));

            ((Button)findViewById(R.id.mileagePoints_btn_back)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
