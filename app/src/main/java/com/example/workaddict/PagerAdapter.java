package com.example.workaddict;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.workaddict.FollowInfo.Follower;
import com.example.workaddict.FollowInfo.Following;

public class PagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;
    private int tabCount ;

    public PagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.tabCount = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Follower(BottomNavi.followerData);
            case 1:
                return new Following(BottomNavi.followingData);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
