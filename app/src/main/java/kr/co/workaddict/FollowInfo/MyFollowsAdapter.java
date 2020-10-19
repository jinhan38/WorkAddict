package kr.co.workaddict.FollowInfo;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import kr.co.workaddict.BottomNavi;
import kr.co.workaddict.DataClass.FollowerData;
import kr.co.workaddict.DataClass.FollowingData;
import kr.co.workaddict.R;
import kr.co.workaddict.Utility.Util;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class MyFollowsAdapter extends RecyclerView.Adapter<MyFollowsAdapter.MyViewHolder> {
    private static final String TAG = "MyFollowsAdapter";
    private ArrayList<FollowerData> followers = new ArrayList<FollowerData>();
    private ArrayList<FollowingData> followings = new ArrayList<FollowingData>();
    private String type;
    private String aaa;

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

    public MyFollowsAdapter(ArrayList<FollowerData> followers, String type) {
        this.followers = followers;
        this.type = type;
        Log.e(TAG, "FollowsAdapter: 생성자");
    }

    public MyFollowsAdapter(ArrayList<FollowingData> followings, String type, String aaa) {
        this.followings = followings;
        this.type = type;
        this.aaa = aaa;
        Log.e(TAG, "FollowsAdapter: 생성자");
    }


    @Override
    public MyFollowsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        Log.e(TAG, "onCreateViewHolder: ");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        switch (type) {
            case "FOLLOWER":
                setTextMethod(holder.followID, holder.followName, followers.get(position).getId(), followers.get(position).getName());
                holder.followContentButton.setText("삭제");
                holder.followContentButton.setOnClickListener(v -> {
                    followers.remove(position);
                    Util.deleteFollower(BottomNavi.bottomNavi.followerKeyList.get(position));
                    Util.deleteOtherFollowing(followers.get(position).getId());
                });
                break;
            case "FOLLOWING":
                setTextMethod(holder.followID, holder.followName, followings.get(position).getId(), followings.get(position).getName());
                holder.followContentButton.setText("취소");
                holder.followContentButton.setOnClickListener(v -> {
                    followings.remove(position);
                    Util.deleteFollowing(BottomNavi.bottomNavi.followingKeyList.get(position));
                    Util.deleteOtherFollower(followings.get(position).getId());
                });
                break;

        }

        downloadProfileImage((type.endsWith("FOLLOWER") ? Follower.activity : Following.activity),
                (type.equals("FOLLOWER") ? followers.get(position).getId() : followings.get(position).getId()),
                holder.followProfile);

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
        }).addOnFailureListener(e -> { });

    }


    @Override
    public int getItemCount() {
        return (type.equals("FOLLOWER") ? followers.size() : followings.size());
    }
}

