package com.example.workaddict.FollowInfo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.workaddict.BottomFragment.TimeLinePage;
import com.example.workaddict.BottomNavi;
import com.example.workaddict.R;
import com.example.workaddict.Utility.Util;
import com.example.workaddict.databinding.ActivityFollowsBinding;
import com.google.android.material.tabs.TabLayout;

public class Follows extends AppCompatActivity {

    private static final String TAG = "Follows";
    public static Follows follows;

    private ActivityFollowsBinding b;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = DataBindingUtil.setContentView(this, R.layout.activity_follows);


        Log.e(TAG, "onCreateView: ");
        follows = this;
        initView();
    }


    private void initView() {
        b.followsToolbar.setTitle("타임라인 공유");
        b.followsToolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(b.followsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setViewPager();
    }

    private void setViewPager() {
        b.tabLayout.addTab(b.tabLayout.newTab().setText("공유받기"));
        b.tabLayout.addTab(b.tabLayout.newTab().setText("공유하기"));

        b.followsViewPager.setAdapter(new FollowsPagerAdapter(getSupportFragmentManager(), b.tabLayout.getTabCount()));

        b.followsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(b.tabLayout));

        b.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                b.followsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.follows_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //menu 클릭 이벤트를 설정합니다.

        switch (item.getItemId()) {
            case android.R.id.home:  // 왼쪽 상단 버튼 눌렀을 때
                onBackPressed();
                break;
            case R.id.menu_invite:
                startActivity(new Intent(Follows.this, FollowInvite.class));
                break;
            case R.id.menu_question:
                Toast.makeText(follows, "정보전달", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onResume() {
        Util.isFollowTimeline = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        Util.isFollowTimeline = false;

        if (BottomNavi.timeLines != null) {
            if (BottomNavi.timeLines.size() > 0) {
                Log.e(TAG, "onDestroy: 셋어댑터");
                TimeLinePage.singlton.setAdapter(BottomNavi.timeLines);
            }
        }

        super.onDestroy();
    }
}
