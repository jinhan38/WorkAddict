package com.example.workaddict.Introduce;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class IntroduceViewPagerAdapter extends FragmentPagerAdapter {

    public IntroduceViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return FirstFragment.newInstance();
            case 1:
                return SecondFragment.newInstance();
            case 2:
                return ThirdFragment.newInstance();
            case 3:
                return FourthFragment.newInstance();
            case 4:
                return FifthFragment.newInstance();
            case 5:
                return SixthFragment.newInstance();
            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 6;
    }
}
