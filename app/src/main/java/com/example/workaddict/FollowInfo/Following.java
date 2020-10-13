package com.example.workaddict.FollowInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.workaddict.BottomFragment.MyPageFragment;
import com.example.workaddict.BottomNavi;
import com.example.workaddict.DataClass.FollowingData;
import com.example.workaddict.R;
import com.example.workaddict.databinding.ActivityFollowingBinding;

import java.util.ArrayList;

public class Following extends Fragment {
    private static final String TAG = "Following";
    public static Following singlton;
    public static Activity activity;
    public static Following following;
    private ArrayList<FollowingData> followingData;
    private ActivityFollowingBinding b;
    public FollowingAdapter adapter;


    public void clearSingleTon() {
        this.singlton = null;
    }


    public Following(ArrayList<FollowingData> followingData) {
        this.followingData = followingData;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DataBindingUtil.inflate(inflater, R.layout.activity_following, container, false);
        activity = getActivity();
        following = this;
        Log.e(TAG, "onCreateView: ");
        initView();

        //여기서 초대하기를 보내면 동시에 카톡으로도 초대하기가 발송되면 된다.
        return b.getRoot();
    }

    private void initView() {
        setRecyclerView();
    }


    public void setRecyclerView() {
        if (b.recyclerViewFollowing != null && followingData.size() > 0) {
            b.recyclerViewFollowing.setHasFixedSize(true);
            b.recyclerViewFollowing.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new FollowingAdapter(followingData, getActivity());
            b.recyclerViewFollowing.setAdapter(adapter);
        }
    }


}
