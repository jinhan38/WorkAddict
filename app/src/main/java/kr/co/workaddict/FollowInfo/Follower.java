package kr.co.workaddict.FollowInfo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import kr.co.workaddict.DataClass.FollowerData;
import kr.co.workaddict.R;
import kr.co.workaddict.databinding.ActivityFollowerBinding;

import java.util.ArrayList;

public class Follower extends Fragment {
    private static final String TAG = "Follower";
    private View v;
    public ActivityFollowerBinding b;
    public static Activity activity;
    public static Follower follower;
    private ArrayList<FollowerData> followerData;
    private FollowerAdapter adapter;


    public Follower(ArrayList<FollowerData> followerData) {

        if (this.followerData != null && this.followerData.size() > 0) {
            this.followerData.clear();
            Log.e(TAG, "Follower: clear");
        }

        this.followerData = followerData;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DataBindingUtil.inflate(inflater, R.layout.activity_follower, container, false);
        activity = getActivity();
        follower = this;
        initView();
        return b.getRoot();
    }


    private void initView() {
        setRecyclerView();
    }


    public void setRecyclerView() {
        if (b.recyclerViewFollower != null && followerData.size() > 0) {

            b.recyclerViewFollower.setHasFixedSize(true);

            b.recyclerViewFollower.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new FollowerAdapter(followerData, getActivity());
            b.recyclerViewFollower.setAdapter(adapter);


        }
    }


}
