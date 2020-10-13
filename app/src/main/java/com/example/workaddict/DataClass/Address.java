package com.example.workaddict.DataClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Address implements Parcelable  {

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

    @SerializedName("mountain_yn")
    @Expose
    private String mountain_yn;

    @SerializedName("main_address_no")
    @Expose
    private String main_address_no;

    @SerializedName("sub_address_no")
    @Expose
    private String sub_address_no;

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

    public String getMountain_yn() {
        return mountain_yn;
    }

    public void setMountain_yn(String mountain_yn) {
        this.mountain_yn = mountain_yn;
    }

    public String getMain_address_no() {
        return main_address_no;
    }

    public void setMain_address_no(String main_address_no) {
        this.main_address_no = main_address_no;
    }

    public String getSub_address_no() {
        return sub_address_no;
    }

    public void setSub_address_no(String sub_address_no) {
        this.sub_address_no = sub_address_no;
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
        dest.writeString(this.mountain_yn);
        dest.writeString(this.main_address_no);
        dest.writeString(this.sub_address_no);
    }

    public Address() {

    }

    protected Address(Parcel in){
        this.address_name = in.readString();
        this.region_1depth_name = in.readString();
        this.region_2depth_name = in.readString();
        this.region_3depth_name = in.readString();
        this.mountain_yn = in.readString();
        this.main_address_no = in.readString();
        this.sub_address_no = in.readString();
    }

    public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
}
