package com.example.workaddict;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.workaddict.BottomFragment.ListFragment;
import com.example.workaddict.BottomFragment.MapFragment;
import com.example.workaddict.BottomFragment.MyPageFragment;
import com.example.workaddict.BottomFragment.TimeLinePage;

public class BottomPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 4;
    private static final String TAG = "BottomPagerAdapter";


    public BottomPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Log.e(TAG, "getItem: 0");
                return MapFragment.newInstance();
            case 1:
                Log.e(TAG, "getItem: 1");
                return ListFragment.newInstance();
            case 2:
                Log.e(TAG, "getItem: 2");
                return TimeLinePage.newInstance();
            case 3:
                Log.e(TAG, "getItem: 3");
                return MyPageFragment.newInstance();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public int getItemPosition(Object object) {
        return BottomPagerAdapter.POSITION_NONE;
    }


}
