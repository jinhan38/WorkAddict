package kr.co.workaddict.FirebaseDataCall;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.workaddict.BottomNavi;
import kr.co.workaddict.DataClass.FollowerData;
import kr.co.workaddict.DataClass.FollowingData;
import kr.co.workaddict.DataClass.Privacy;
import kr.co.workaddict.FollowInfo.FollowInvite;
import kr.co.workaddict.FollowInfo.FollowsAdapter;
import kr.co.workaddict.Utility.UserInfo;
import kr.co.workaddict.Utility.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserCall {
    private static final String TAG = "UserCall";
    private ArrayList<Privacy> searchPrivacy = new ArrayList<Privacy>();
    private RecyclerView recyclerView;
    private Activity activity;


    public UserCall(String searchKeyword, RecyclerView recyclerView,  Activity activity) {
        this.recyclerView = recyclerView;
        this.activity = activity;
        dataCall(searchKeyword.toUpperCase());
    }

    public void dataCall(String searchKeyword) {

        searchPrivacy.clear();


        DatabaseReference myRef2 = Util.getFirebaseDatabase().getReference("users");

        myRef2.child("Privacy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Privacy privacy = postSnapshot.getValue(Privacy.class);
                        if ((privacy.getId().toUpperCase().contains(searchKeyword) || privacy.getName().toUpperCase().contains(searchKeyword))
                                && !privacy.getId().toUpperCase().equals(UserInfo.getID().toUpperCase())
                                && !followerIdCheck(privacy)) {
                            searchPrivacy.add(privacy);
                            Log.e(TAG, "onDataChange: 일치하는 정보 찾음  : ");

                        }
                    }
                }

                setRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: 실패");

            }
        });
    }


    private boolean followerIdCheck(Privacy privacy){
        boolean result = false;
        for (FollowerData data : BottomNavi.bottomNavi.followerData){
            if (privacy.getId().toUpperCase().equals(data.getId().toUpperCase())){
                result = true;
            }
        }
        return result;
    }



    private boolean followingIdCheck(Privacy privacy){
        boolean result = false;
        for (FollowingData data : BottomNavi.bottomNavi.followingData){
            if (privacy.getId().equals(data.getId())){
                result = true;
            }
        }
        return result;
    }


    private void setRecyclerView() {

        if (recyclerView != null && searchPrivacy.size() > 0) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(new FollowsAdapter(searchPrivacy, activity));

        }else{
            Toast.makeText(activity, "일치하는 정보가 없습니다", Toast.LENGTH_SHORT).show();
        }
        FollowInvite.followInvite.hideProgressBar();
    }


    public ArrayList<Privacy> searchResult() {
        return searchPrivacy;
    }


}
