package kr.co.workaddict.FollowInfo;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import kr.co.workaddict.BottomNavi;
import kr.co.workaddict.DataClass.FollowingData;
import kr.co.workaddict.R;
import kr.co.workaddict.Utility.Util;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.MyViewHolder> {

    private static final String TAG = "FollowerAdapter";
    private ArrayList<FollowingData> followings = new ArrayList<FollowingData>();
    public Activity activity;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView followProfile;
        public TextView followID;
        public TextView followContentButton;
        public TextView followName;
        public View view;

        public MyViewHolder(@NonNull View v) {
            super(v);
            this.view = v;
            followProfile = v.findViewById(R.id.followProfile);
            followID = v.findViewById(R.id.followID);
            followContentButton = v.findViewById(R.id.followContentButton);
            followName = v.findViewById(R.id.followName);


        }
    }


    public FollowingAdapter(ArrayList<FollowingData> followings, Activity activity) {
        this.followings = followings;
        this.activity = activity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        setTextMethod(holder.followID, holder.followName, followings.get(position).getId(), followings.get(position).getName());
        holder.followContentButton.setText("취소");
        holder.followContentButton.setOnClickListener(v -> {
            Log.e(TAG, "onBindViewHolder: 팔로잉 취소 : " + position );
            Log.e(TAG, "onBindViewHolder: 팔로잉 키값 : " + BottomNavi.bottomNavi.followingKeyList.get(position) );
            Util.deleteFollowing(BottomNavi.bottomNavi.followingKeyList.get(position));
            Util.deleteOtherFollower(followings.get(position).getId());

            followings.remove(position);
            notifyItemRemoved(position);
            notifyItemChanged(0, followings.size());

        });

        holder.view.setOnClickListener(v -> {

            Intent intent = new Intent(activity, FollowTimeline.class);
            intent.putExtra("ID", followings.get(position).getId());
            activity.startActivity(intent);

        });


        downloadProfileImage(activity, followings.get(position).getId(), holder.followProfile);

    }


    private void setTextMethod(TextView idTextView, TextView nameTextView, String id, String name) {
        idTextView.setText(id);
        nameTextView.setText(name);

    }

    private void downloadProfileImage(Activity activity, String id, ImageView imageView) {


        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child("users/" + id.replaceAll("\\.", "") + "/profile/profile.jpg");

        riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.e(TAG, "onSuccess: uri : " + uri);

            if (uri != null) {
                Log.e(TAG, "onSuccess: 성공");
                Glide.with(activity)
                        .load(uri)
                        .override(200, 200)
                        .placeholder(R.drawable.image_download_loading)
                        .fitCenter()
                        .circleCrop()
                        .into(imageView);
            } else {
                Glide.with(activity)
                        .load(R.drawable.basic_profile_icon)
                        .override(200, 200)
                        .fitCenter()
                        .circleCrop()
                        .into(imageView);
            }

        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: error : " + e);
        });

    }


    @Override
    public int getItemCount() {
        return followings.size();
    }


}
