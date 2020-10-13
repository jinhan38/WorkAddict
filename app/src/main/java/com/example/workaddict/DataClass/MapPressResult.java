package com.example.workaddict.DataClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MapPressResult implements Parcelable {


        @SerializedName("meta")
    @Expose
    private Meta meta;

    @SerializedName("documents")
    @Expose
    private ArrayList<PressedAddress> pressedAddresses = null;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<PressedAddress> getPressedAddresses() {
        return pressedAddresses;
    }

    public void setPressedAddresses(ArrayList<PressedAddress> pressedAddresses) {
        this.pressedAddresses = pressedAddresses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.pressedAddresses);
    }

    public MapPressResult() {
    }

    protected MapPressResult(Parcel in) {
        this.pressedAddresses = new ArrayList<PressedAddress>();
        in.readList(this.pressedAddresses, MapPressResult.class.getClassLoader());
    }


    public static final Parcelable.Creator<MapPressResult> CREATOR = new Parcelable.Creator<MapPressResult>() {
        @Override
        public MapPressResult createFromParcel(Parcel source) {
            return new MapPressResult(source);
        }

        @Override
        public MapPressResult[] newArray(int size) {
            return new MapPressResult[size];
        }
    };
}
