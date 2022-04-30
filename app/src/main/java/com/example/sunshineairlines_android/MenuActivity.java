package com.example.sunshineairlines_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class MenuActivity extends AppCompatActivity {

    VideoView videoView;
    int videoPosition=0;

    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(videoPosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPosition=videoView.getCurrentPosition();
        videoView.stopPlayback();
    }

    LayoutInflater layoutInflater;
    ArrayList<View> sponsorViews=new ArrayList<>();
    ViewPager sponsorPager;
    Timer sponsorTimer=new Timer();
    TimerTask sponsorTimerTask;

    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        userEmail=getIntent().getStringExtra("userEmail");

        sponsorPager = findViewById(R.id.menu_sponsor_pager);

        Future<String> sponsorFuture = Utils.threads.submit(new Callable<String>() {
            @Override
            public String call() {
                return Utils.sendGet(Utils.baseUrl + "/sponsor/list", null);
            }
        });

        videoView = findViewById(R.id.menu_video);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.menu_video));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
                videoView.start();
            }
        });

        layoutInflater = getLayoutInflater();
        ArrayList<String> sponsorNames = new ArrayList<>();
        try {
            String sponsorStr = sponsorFuture.get();
            JSONArray sponsorArr = new JSONArray(sponsorStr);
            for (int i = 0; i < sponsorArr.length(); i += 1) {
                String name = sponsorArr.getJSONObject(i).getString("Name");
                sponsorNames.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addSponsorView(sponsorNames.get(sponsorNames.size() - 1));
        for (String name : sponsorNames) {
            addSponsorView(name);
        }
        addSponsorView(sponsorNames.get(0));

        sponsorPager.setAdapter(new ListPagerAdapter(sponsorViews));
        sponsorPager.setCurrentItem(1, false);
        sponsorPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state== ViewPager.SCROLL_STATE_IDLE){
                    int position=sponsorPager.getCurrentItem();
                    if (position == 0) {
                        sponsorPager.setCurrentItem(sponsorViews.size() - 1 - 1,false);
                    } else if (position == sponsorViews.size() - 1) {
                        sponsorPager.setCurrentItem(1,false);
                    }
                }
            }
        });

        sponsorTimerTask=new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int index=sponsorPager.getCurrentItem()+1;
                        if(index>=sponsorViews.size()){
                            index=0;
                        }
                        sponsorPager.setCurrentItem(index,true);
                    }
                });
            }
        };
        sponsorTimer.scheduleAtFixedRate(sponsorTimerTask,4000,4000);

        Button btn0=findViewById(R.id.menu_button_0);
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MenuActivity.this,SearchFlightsActivity.class);
                startActivity(intent);
            }
        });
        Button btn1=findViewById(R.id.menu_button_1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MenuActivity.this,MileagePointsActivity.class);
                startActivity(intent);
            }
        });
        Button btn2=findViewById(R.id.menu_button_2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void addSponsorView(String name){
        View view=layoutInflater.inflate(R.layout.menu_sponser_text,sponsorPager,false);
        ((TextView)view.findViewById(R.id.menu_sponsor_text)).setText(name);
        sponsorViews.add(view);
    }
}
