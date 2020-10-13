package com.example.workaddict.DataClass;

import com.example.workaddict.FollowInfo.Follower;
import com.example.workaddict.FollowInfo.Following;

import java.io.Serializable;
import java.util.ArrayList;

public class MasterData {

    private ArrayList<CategoryData> categoryData;
    private ArrayList<PlaceData> placeData;
    private ArrayList<TimeLine> timeLines;
    private ArrayList<Follower> followers;
    private ArrayList<Following> followings;

    public ArrayList<CategoryData> getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(ArrayList<CategoryData> categoryData) {
        this.categoryData = categoryData;
    }

    public ArrayList<PlaceData> getPlaceData() {
        return placeData;
    }

    public void setPlaceData(ArrayList<PlaceData> placeData) {
        this.placeData = placeData;
    }

    public ArrayList<TimeLine> getTimeLines() {
        return timeLines;
    }

    public void setTimeLines(ArrayList<TimeLine> timeLines) {
        this.timeLines = timeLines;
    }

    public ArrayList<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<Follower> followers) {
        this.followers = followers;
    }

    public ArrayList<Following> getFollowings() {
        return followings;
    }

    public void setFollowings(ArrayList<Following> followings) {
        this.followings = followings;
    }
}
