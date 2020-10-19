package kr.co.workaddict.FollowInfo;

import android.app.Activity;
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
import kr.co.workaddict.DataClass.FollowerData;
import kr.co.workaddict.R;
import kr.co.workaddict.Utility.Util;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.MyViewHolder> {

    private static final String TAG = "FollowerAdapter";
    private ArrayList<FollowerData> followers = new ArrayList<FollowerData>();
    private Activity activity;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView followProfile;
        public TextView followID;
        public TextView followContentButton;
        public TextView followName;

        public MyViewHolder(@NonNull View v) {
            super(v);
            followProfile = v.findViewById(R.id.followProfile);
            followID = v.findViewById(R.id.followID);
            followContentButton = v.findViewById(R.id.followContentButton);
            followName = v.findViewById(R.id.followName);
        }
    }


    public FollowerAdapter(ArrayList<FollowerData> followers, Activity activity) {
        this.followers = followers;
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

        setTextMethod(holder.followID, holder.followName, followers.get(position).getId(), followers.get(position).getName());
        holder.followContentButton.setText("삭제");
        holder.followContentButton.setOnClickListener(v -> {
            Util.deleteFollower(BottomNavi.bottomNavi.followerKeyList.get(position));
            Util.deleteOtherFollowing(followers.get(position).getId());

            followers.remove(position);
            notifyItemRemoved(position);
            notifyItemChanged(0, followers.size());

        });


        downloadProfileImage(activity, followers.get(position).getId(), holder.followProfile);

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
        return followers.size();
    }


}
