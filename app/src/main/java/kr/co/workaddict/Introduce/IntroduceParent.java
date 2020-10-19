package kr.co.workaddict.Introduce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import kr.co.workaddict.LoginPage;
import kr.co.workaddict.R;
import com.google.android.material.tabs.TabLayout;

public class IntroduceParent extends AppCompatActivity {


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            getWindow().setNavigationBarColor(getColor(R.color.softPurple));
        }
        super.onWindowFocusChanged(hasFocus);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_parent);

        String intentType = "";
        Intent intent = getIntent();
        intentType = intent.getExtras().getString("intentType");


        ViewPager introduceIndicator = findViewById(R.id.introduceIndicator);
        introduceIndicator.setAdapter(new IntroduceViewPagerAdapter(getSupportFragmentManager()));

        TabLayout introduceTabLayout = findViewById(R.id.introduceTabLayout);
        introduceTabLayout.setupWithViewPager(introduceIndicator);

        (findViewById(R.id.skipTextView)).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginPage.class));
            finish();
        });

        TextView skipTextView = findViewById(R.id.skipTextView);
        TextView appStartTextView = findViewById(R.id.appStartTextView);
        appStartTextView.setVisibility(View.GONE);
        appStartTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginPage.class));
            finish();
        });

        String finalIntentType = intentType;

        if (finalIntentType.equals("2")){
            appStartTextView.setVisibility(View.GONE);
            skipTextView.setVisibility(View.GONE);
        }

        introduceTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (finalIntentType.equals("1")){

                    switch (introduceTabLayout.getSelectedTabPosition()) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            appStartTextView.setVisibility(View.GONE);
                            break;
                        case 5:
                            appStartTextView.setVisibility(View.VISIBLE);
                            break;
                    }
                }  else{
                    appStartTextView.setVisibility(View.GONE);
                    skipTextView.setVisibility(View.GONE);
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