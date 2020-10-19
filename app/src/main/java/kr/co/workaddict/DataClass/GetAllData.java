package kr.co.workaddict.DataClass;

import java.util.ArrayList;

public class GetAllData {
    private static final String TAG = "GetAllData";

    public static ArrayList<CategoryData> categoryData = new ArrayList<CategoryData>(); // 카테고리명만 있는 경우
    public static ArrayList<String> categoryDataKeyList = new ArrayList<String>();
    public static ArrayList<PlaceData> placeData = new ArrayList<PlaceData>();
    public static ArrayList<String> placeDataKeyList = new ArrayList<String>();
    public static ArrayList<TimeLine> timeLines = new ArrayList<TimeLine>();
    public static ArrayList<String> timeLineDataKeyList = new ArrayList<String>();
    public static ArrayList<FollowerData> followerData = new ArrayList<FollowerData>();
    public static ArrayList<String> followerKeyList = new ArrayList<String>();
    public static ArrayList<FollowingData> followingData = new ArrayList<FollowingData>();
    public static ArrayList<String> followingKeyList = new ArrayList<String>();


    public static ArrayList<CategoryData> getCategoryData() {
        return categoryData;
    }

    public static void setCategoryData(ArrayList<CategoryData> categoryData) {
        GetAllData.categoryData = categoryData;
    }

    public static ArrayList<String> getCategoryDataKeyList() {
        return categoryDataKeyList;
    }

    public static void setCategoryDataKeyList(ArrayList<String> categoryDataKeyList) {
        GetAllData.categoryDataKeyList = categoryDataKeyList;
    }

    public static ArrayList<PlaceData> getPlaceData() {
        return placeData;
    }

    public static void setPlaceData(ArrayList<PlaceData> placeData) {
        GetAllData.placeData = placeData;
    }

    public static ArrayList<String> getPlaceDataKeyList() {
        return placeDataKeyList;
    }

    public static void setPlaceDataKeyList(ArrayList<String> placeDataKeyList) {
        GetAllData.placeDataKeyList = placeDataKeyList;
    }

    public static ArrayList<TimeLine> getTimeLines() {
        return timeLines;
    }

    public static void setTimeLines(ArrayList<TimeLine> timeLines) {
        GetAllData.timeLines = timeLines;
    }

    public static ArrayList<String> getTimeLineDataKeyList() {
        return timeLineDataKeyList;
    }

    public static void setTimeLineDataKeyList(ArrayList<String> timeLineDataKeyList) {
        GetAllData.timeLineDataKeyList = timeLineDataKeyList;
    }

    public static ArrayList<FollowerData> getFollowerData() {
        return followerData;
    }

    public static void setFollowerData(ArrayList<FollowerData> followerData) {
        GetAllData.followerData = followerData;
    }

    public static ArrayList<String> getFollowerKeyList() {
        return followerKeyList;
    }

    public static void setFollowerKeyList(ArrayList<String> followerKeyList) {
        GetAllData.followerKeyList = followerKeyList;
    }

    public static ArrayList<FollowingData> getFollowingData() {
        return followingData;
    }

    public static void setFollowingData(ArrayList<FollowingData> followingData) {
        GetAllData.followingData = followingData;
    }

    public static ArrayList<String> getFollowingKeyList() {
        return followingKeyList;
    }

    public static void setFollowingKeyList(ArrayList<String> followingKeyList) {
        GetAllData.followingKeyList = followingKeyList;
    }
}
