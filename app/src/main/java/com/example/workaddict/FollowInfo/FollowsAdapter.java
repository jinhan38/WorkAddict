package com.example.workaddict.FollowInfo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.workaddict.DataClass.Privacy;
import com.example.workaddict.R;
import com.example.workaddict.Utility.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class FollowsAdapter extends RecyclerView.Adapter<FollowsAdapter.MyViewHolder> {
    private static final String TAG = "FollowsAdapter";
    private ArrayList<Privacy> privacies = new ArrayList<Privacy>();
    private Activity activity;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView followProfile;
        public TextView followID;
        public TextView followContentButton;
        public TextView followName;

        public MyViewHolder(View v) {
            super(v);
            followProfile = v.findViewById(R.id.followProfile);
            followID = v.findViewById(R.id.followID);
            followContentButton = v.findViewById(R.id.followContentButton);
            followName = v.findViewById(R.id.followName);
        }
    }

    public FollowsAdapter(ArrayList<Privacy> privacies, Activity activity) {
        this.activity = activity;
        this.privacies = privacies;
        Log.e(TAG, "FollowsAdapter: 생성자");
    }


    @Override
    public FollowsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        Log.e(TAG, "onCreateViewHolder: ");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        holder.followContentButton.setText("추가");

        holder.followID.setText(privacies.get(position).getId());
        holder.followName.setText(privacies.get(position).getName());

        downloadProfileImage(position, holder.followProfile);

        holder.followContentButton.setOnClickListener(v -> {

            Util.addFollower(privacies.get(position).getId(), privacies.get(position).getName(), FollowInvite.followInvite);
            Util.addFollowing(privacies.get(position).getId(), privacies.get(position).getName(), FollowInvite.followInvite);

            privacies.remove(position);
            notifyItemRemoved(position);
            notifyItemChanged(0, privacies.size());

        });
    }


    private void downloadProfileImage(int position, ImageView imageView) {


        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child("users/" +
                privacies.get(position).getId().replaceAll("\\.", "") + "/profile/profile.jpg");


        Log.e(TAG, "onBindViewHolder: 아이디 : " + privacies.get(position).getId());

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
        });


    }


    @Override
    public int getItemCount() {
        return privacies.size();
    }
}

