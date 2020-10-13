package com.example.workaddict.DataClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoadAddress implements Parcelable {

    @SerializedName("address_name")
    @Expose
    private String address_name;

    @SerializedName("region_1depth_name")
    @Expose
    private String region_1depth_name;

    @SerializedName("region_2depth_name")
    @Expose
    private String region_2depth_name;

    @SerializedName("region_3depth_name")
    @Expose
    private String region_3depth_name;

    @SerializedName("road_name")
    @Expose
    private String road_name;

    @SerializedName("underground_yn")
    @Expose
    private String underground_yn;

    @SerializedName("main_building_no")
    @Expose
    private String main_building_no;

    @SerializedName("sub_building_no")
    @Expose
    private String sub_building_no;

    @SerializedName("building_name")
    @Expose
    private String building_name;

    @SerializedName("zone_no")
    @Expose
    private String zone_no;

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public String getRegion_1depth_name() {
        return region_1depth_name;
    }

    public void setRegion_1depth_name(String region_1depth_name) {
        this.region_1depth_name = region_1depth_name;
    }

    public String getRegion_2depth_name() {
        return region_2depth_name;
    }

    public void setRegion_2depth_name(String region_2depth_name) {
        this.region_2depth_name = region_2depth_name;
    }

    public String getRegion_3depth_name() {
        return region_3depth_name;
    }

    public void setRegion_3depth_name(String region_3depth_name) {
        this.region_3depth_name = region_3depth_name;
    }

    public String getRoad_name() {
        return road_name;
    }

    public void setRoad_name(String road_name) {
        this.road_name = road_name;
    }

    public String getUnderground_yn() {
        return underground_yn;
    }

    public void setUnderground_yn(String underground_yn) {
        this.underground_yn = underground_yn;
    }

    public String getMain_building_no() {
        return main_building_no;
    }

    public void setMain_building_no(String main_building_no) {
        this.main_building_no = main_building_no;
    }

    public String getSub_building_no() {
        return sub_building_no;
    }

    public void setSub_building_no(String sub_building_no) {
        this.sub_building_no = sub_building_no;
    }

    public String getBuilding_name() {
        return building_name;
    }

    public void setBuilding_name(String building_name) {
        this.building_name = building_name;
    }

    public String getZone_no() {
        return zone_no;
    }

    public void setZone_no(String zone_no) {
        this.zone_no = zone_no;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address_name);
        dest.writeString(this.region_1depth_name);
        dest.writeString(this.region_2depth_name);
        dest.writeString(this.region_3depth_name);
        dest.writeString(this.road_name);
        dest.writeString(this.underground_yn);
        dest.writeString(this.main_building_no);
        dest.writeString(this.sub_building_no);
        dest.writeString(this.building_name);
        dest.writeString(this.zone_no);
    }

    public RoadAddress() {

    }

    protected RoadAddress(Parcel in){
        this.address_name = in.readString();
        this.region_1depth_name = in.readString();
        this.region_2depth_name = in.readString();
        this.region_3depth_name = in.readString();
        this.road_name = in.readString();
        this.underground_yn = in.readString();
        this.main_building_no = in.readString();
        this.sub_building_no = in.readString();
        this.building_name = in.readString();
        this.zone_no = in.readString();
    }

    public static final Parcelable.Creator<RoadAddress> CREATOR = new Parcelable.Creator<RoadAddress>() {
        @Override
        public RoadAddress createFromParcel(Parcel source) {
            return new RoadAddress(source);
        }

        @Override
        public RoadAddress[] newArray(int size) {
            return new RoadAddress[size];
        }
    };
}
