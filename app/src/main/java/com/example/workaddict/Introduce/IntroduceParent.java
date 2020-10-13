package com.example.workaddict.Introduce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.workaddict.LoginPage;
import com.example.workaddict.R;
import com.google.android.material.tabs.TabLayout;

public class IntroduceParent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_parent);

        ViewPager introduceIndicator = findViewById(R.id.introduceIndicator);
        introduceIndicator.setAdapter(new IntroduceViewPagerAdapter(getSupportFragmentManager()));

        TabLayout introduceTabLayout = findViewById(R.id.introduceTabLayout);
        introduceTabLayout.setupWithViewPager(introduceIndicator);

        (findViewById(R.id.skipTextView)).setOnClickListener(v ->{
            startActivity(new Intent(this, LoginPage.class));
            finish();
        });

        TextView appStartTextView = findViewById(R.id.appStartTextView);
        appStartTextView.setVisibility(View.GONE);
        appStartTextView.setOnClickListener(v ->{
            startActivity(new Intent(this, LoginPage.class));
            finish();
        });

        introduceTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (introduceTabLayout.getSelectedTabPosition()){
                    case 0:
                        appStartTextView.setVisibility(View.GONE);
                        break;
                    case 1:
                        appStartTextView.setVisibility(View.GONE);
                        break;
                    case 2:
                        appStartTextView.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }
}