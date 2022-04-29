package com.example.sunshineairlines_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity {

    LayoutInflater layoutInflater;
    LinearLayout pagerIndicatorLayout;
    ArrayList<View> pagerViews =new ArrayList<>();
    ArrayList<ImageView> pagerIndicatorImages = new ArrayList<>();

    boolean isDraggingPage=false;
    boolean canOpenActivity=true;

    private void gotoMenu(){
        //Intent intent=new Intent(WelcomeActivity.this, LoginActivity.class);
        //startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btn=findViewById(R.id.welcome_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoMenu();
            }
        });

        layoutInflater=getLayoutInflater();
        pagerIndicatorLayout=findViewById(R.id.welcome_pagerIndicatorLayout);
        ViewPager pager=findViewById(R.id.welcome_pager);

        addPage("Sunshine Airlines\nManagement System\n\nV1.0");
        addPage("You can search flights\nand book flights in this app.");
        addPage("Our App will give you\na good experience.");

        WelcomePagerAdapter adp=new WelcomePagerAdapter(pagerViews);
        pager.setAdapter(adp);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(isDraggingPage&&position==pagerViews.size()-1&&canOpenActivity){
                    canOpenActivity=false;
                    gotoMenu();
                }
            }

            @Override
            public void onPageSelected(int position) {
                ArrayList<ImageView> list=pagerIndicatorImages;
                for(int i=0;i<list.size();i+=1){
                    list.get(i).setImageResource(position==i?R.drawable.pager_indicator_on:R.drawable.pager_indicator_off);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                isDraggingPage=state==ViewPager.SCROLL_STATE_DRAGGING;
            }
        });
    }

    void addPage(String content){
        View view=layoutInflater.inflate(R.layout.welcome_page,null,false);
        ((TextView)view.findViewById(R.id.welcomePage_text_content)).setText(content);
        pagerViews.add(view);

        final ImageView imageView=(ImageView) layoutInflater.inflate(R.layout.welcome_pager_indicator,pagerIndicatorLayout,false);
        pagerIndicatorImages.add(imageView);

        pagerIndicatorLayout.addView(imageView);
    }
}
