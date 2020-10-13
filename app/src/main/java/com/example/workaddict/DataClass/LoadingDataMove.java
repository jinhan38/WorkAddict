package com.example.workaddict.DataClass;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.workaddict.BottomFragment.MyPageFragment;
import com.example.workaddict.BottomNavi;
import com.example.workaddict.LoginPage;
import com.example.workaddict.SaveSharedPreferences;
import com.example.workaddict.Utility.UserInfo;
import com.example.workaddict.Utility.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoadingDataMove {
    private static final String TAG = "LoadingDataMove";

    public ArrayList<CategoryData> categoryData = new ArrayList<CategoryData>(); // 카테고리명만 있는 경우
    public ArrayList<String> categoryDataKeyList = new ArrayList<String>();
    public ArrayList<PlaceData> placeData = new ArrayList<PlaceData>();
    public ArrayList<String> placeDataKeyList = new ArrayList<String>();
    public ArrayList<TimeLine> timeLines = new ArrayList<TimeLine>();
    public ArrayList<String> timeLineDataKeyList = new ArrayList<String>();
    public ArrayList<FollowerData> followerData = new ArrayList<FollowerData>();
    public ArrayList<String> followerKeyList = new ArrayList<String>();
    public ArrayList<FollowingData> followingData = new ArrayList<FollowingData>();
    public ArrayList<String> followingKeyList = new ArrayList<String>();
    public static boolean addTimeline = false;


    private Activity activity;

    public LoadingDataMove(Activity activity) {
        this.activity = activity;
    }

    public void getFirebaseData() {


        Log.e(TAG, "getFirebaseData: 다운로드 진입" );
        String id = SaveSharedPreferences.getPrefId(activity);
        DatabaseReference myRef = Util.getFirebaseDatabase().getReference("users").child(id.replaceAll("\\.", ""));
        Log.e(TAG, "getFirebaseData: myRef호출 직전" );

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange: 다운로드 데이터 안에 진입" );
                categoryData.clear();
                categoryDataKeyList.clear();
                placeData.clear();
                placeDataKeyList.clear();
                timeLines.clear();
                timeLineDataKeyList.clear();
                followerData.clear();
                followerKeyList.clear();
                followingData.clear();
                followingKeyList.clear();

                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        String key = dataSnapshot1.getKey();

                        switch (key) {
                            case "CategoryData":
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    categoryData.add(dataSnapshot2.getValue(CategoryData.class));
                                    categoryDataKeyList.add(dataSnapshot2.getKey());
                                }
                                break;
                            case "PlaceData":
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    placeData.add(dataSnapshot2.getValue(PlaceData.class));
                                    placeDataKeyList.add(dataSnapshot2.getKey());
                                }
                                break;
                            case "TimeLine":
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    TimeLine timeLine = dataSnapshot2.getValue(TimeLine.class);
                                    timeLines.add(timeLine);
                                    timeLineDataKeyList.add(dataSnapshot2.getKey());

                                }

                                break;
                            case "Follower":
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    followerData.add(dataSnapshot2.getValue(FollowerData.class));
                                    followerKeyList.add(dataSnapshot2.getKey());
                                }

//                                if (MyPageFragment.singleton != null) {
//                                    MyPageFragment.singleton.b.followerCount.setText(followerData.size() + "\n팔로워");
//                                }


                                break;
                            case "Following":
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    followingData.add(dataSnapshot2.getValue(FollowingData.class));
                                    followingKeyList.add(dataSnapshot2.getKey());
                                }
                                break;
                            case "Privacy":
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    Privacy privacy = dataSnapshot2.getValue(Privacy.class);

                                    if (privacy.getId().length() > 0) {

                                        if (MyPageFragment.singleton != null) {
                                            MyPageFragment.singleton.b.myPageID.setText(UserInfo.getID());
                                            MyPageFragment.singleton.b.myPageName.setText(UserInfo.getName());
                                        }

                                        UserInfo.setName(privacy.getName());
                                        UserInfo.setID(privacy.getId());
                                        if (SaveSharedPreferences.getPrefAutoLogin(activity).equals("y")) {
                                            SaveSharedPreferences.setPrefName(activity, privacy.getName());
                                            SaveSharedPreferences.setPrefId(activity, privacy.getId());
                                        }

                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }


                    if (BottomNavi.bottomNavi != null) {
                        Log.e(TAG, "onDataChange: 바텀네비 있음" );
                        BottomNavi.categoryData = categoryData;
                        BottomNavi.categoryDataKeyList = categoryDataKeyList;
                        BottomNavi.placeData = placeData;
                        BottomNavi.placeDataKeyList = placeDataKeyList;
                        BottomNavi.timeLines = timeLines;
                        BottomNavi.timeLineDataKeyList = timeLineDataKeyList;
                        BottomNavi.followerData = followerData;
                        BottomNavi.followerKeyList = followerKeyList;
                        BottomNavi.followingData = followingData;
                        BottomNavi.followingKeyList = followingKeyList;



//                        if (MyPageFragment.singleton != null) {
//                            MyPageFragment.singleton.b.followerCount.setText(followerData.size() + "\n팔로워");
//                            MyPageFragment.singleton.b.followingCount.setText(followingData.size() + "\n팔로잉");
//                        }

                    } else {
                        completeCheck();

                    }

                }else{
                    if (SaveSharedPreferences.getPrefIsLogin(activity).equals("y")){
                        activity.startActivity(new Intent(activity, BottomNavi.class));
                    }   else{
                        activity.startActivity(new Intent(activity, LoginPage.class));
                    }
                }

                Log.e(TAG, "onDataChange: 데이터 업데이트 완료");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }

    public void completeCheck() {
        Log.e(TAG, "completeCheck: " );

        GetAllData.setPlaceData(placeData);
        GetAllData.setPlaceDataKeyList(placeDataKeyList);
        GetAllData.setCategoryData(categoryData);
        GetAllData.setCategoryDataKeyList(categoryDataKeyList);
        GetAllData.setCategoryDataKeyList(categoryDataKeyList);
        GetAllData.setTimeLines(timeLines);
        GetAllData.setTimeLineDataKeyList(timeLineDataKeyList);
        GetAllData.setFollowerData(followerData);
        GetAllData.setFollowerKeyList(followerKeyList);
        GetAllData.setFollowingData(followingData);
        GetAllData.setFollowingKeyList(followingKeyList);

        UserInfo.setID(SaveSharedPreferences.getPrefId(activity));
        UserInfo.setName(SaveSharedPreferences.getPrefName(activity));
        activity.startActivity(new Intent(activity, BottomNavi.class));
        activity.finish();

    }
    
}
