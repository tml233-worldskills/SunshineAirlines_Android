package com.example.sunshineairlines_android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class SearchFlightsActivity extends AppCompatActivity {

    UserInfo userInfo;

    ArrayList<String> airportIds=new ArrayList<>();
    ArrayList<String> airportNames=new ArrayList<>();

    ArrayList<String> cabinTypeIds =new ArrayList<>();
    ArrayList<String> cabinTypeNames =new ArrayList<>();

    Spinner comboFrom;
    Spinner comboTo;
    Spinner comboCabinType;
    EditText textDate;
    ImageButton btnDate;
    Button btnSearch;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flights);

        userInfo=(UserInfo)getIntent().getSerializableExtra("userInfo");

        Future airportsFuture=Utils.threads.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    String str=Utils.sendGet(Utils.baseUrl+"/airport/list",null);
                    JSONArray arr=new JSONArray(str);
                    for(int i=0;i<arr.length();i+=1){
                        JSONObject obj=arr.getJSONObject(i);
                        airportIds.add(obj.getString("IATA"));
                        airportNames.add(obj.getString("Name"));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        cabinTypeIds.add("economy");
        cabinTypeNames.add("Economy");
        cabinTypeIds.add("business");
        cabinTypeNames.add("Business");
        cabinTypeIds.add("first");
        cabinTypeNames.add("First");

        comboFrom=findViewById(R.id.searchFlights_spinner_from);
        comboTo=findViewById(R.id.searchFlights_spinner_to);
        comboCabinType=findViewById(R.id.searchFlights_spinner_cabinType);
        textDate=findViewById(R.id.searchFlights_text_date);
        btnDate=findViewById(R.id.searchFlights_btn_date);
        btnSearch=findViewById(R.id.searchFlights_btn_search);
        btnBack=findViewById(R.id.searchFlights_btn_back);

        comboCabinType.setAdapter(new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, cabinTypeNames));
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c=Calendar.getInstance();
                new DatePickerDialog(SearchFlightsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar c=Calendar.getInstance();
                        c.set(i,i1,i2);
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                        textDate.setText(sdf.format(c.getTime()));
                    }
                },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int fromIndex=comboFrom.getSelectedItemPosition();
                if(fromIndex<0){
                    Toast.makeText(SearchFlightsActivity.this,"Please select From!",Toast.LENGTH_SHORT).show();
                    return;
                }
                int toIndex=comboTo.getSelectedItemPosition();
                if(toIndex<0){
                    Toast.makeText(SearchFlightsActivity.this,"Please select To!",Toast.LENGTH_SHORT).show();
                    return;
                }
                int cabinTypeIndex=comboCabinType.getSelectedItemPosition();
                if(cabinTypeIndex<0){
                    Toast.makeText(SearchFlightsActivity.this,"Please select Cabin Type!",Toast.LENGTH_SHORT).show();
                    return;
                }
                final String date=textDate.getText().toString();
                if(date.length()==0){
                    Toast.makeText(SearchFlightsActivity.this,"Please select Date!",Toast.LENGTH_SHORT).show();
                    return;
                }
                final String from=airportIds.get(fromIndex);
                final String to=airportIds.get(toIndex);
                final String cabinType=cabinTypeIds.get(cabinTypeIndex);
                final String cabinTypeName=cabinTypeNames.get(cabinTypeIndex);

                FlightSearchData searchData=new FlightSearchData();
                searchData.from=from;
                searchData.to=to;
                searchData.cabinTypeId=cabinType;
                searchData.cabinTypeName=cabinTypeName;
                searchData.date=date;

                Intent intent=new Intent(SearchFlightsActivity.this,SearchFlightsResultActivity.class);
                intent.putExtra("userInfo",userInfo);
                intent.putExtra("searchData",searchData);
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try{
            airportsFuture.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        comboFrom.setAdapter(new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,airportNames));
        comboTo.setAdapter(new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,airportNames));
    }
}
