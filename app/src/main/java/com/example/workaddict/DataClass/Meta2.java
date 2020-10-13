package com.example.workaddict.DataClass;

import android.os.Parcel;
import android.os.Parcelable;

public class Meta2 implements Parcelable {

    private Integer total_count;

    public Integer getTotal_count() {
        return total_count;
    }

    public void setTotal_count(Integer total_count) {
        this.total_count = total_count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.total_count);
    }

    public Meta2() {

    }

    public Meta2(Parcel in) {
        this.total_count = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<Meta2> CREATOR = new Creator<Meta2>() {
        @Override
        public Meta2 createFromParcel(Parcel source) {
            return new Meta2(source);
        }

        @Override
        public Meta2[] newArray(int size) {
            return new Meta2[size];
        }
    };
}
