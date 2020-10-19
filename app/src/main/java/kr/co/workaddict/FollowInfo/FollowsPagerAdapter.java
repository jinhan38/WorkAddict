package kr.co.workaddict.FollowInfo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import kr.co.workaddict.BottomNavi;

public class FollowsPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "FollowsPagerAdapter";
    private int tabCount;

    public FollowsPagerAdapter(@NonNull FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
        Log.e(TAG, "FollowsPagerAdapter: 생성자" );
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.e(TAG, "getItem: ");
        switch (position) {
            case 0:
                Log.e(TAG, "getItem: 0");
                return new Following(BottomNavi.followingData);
            case 1:
                Log.e(TAG, "getItem: 1");
                return new Follower(BottomNavi.followerData);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
