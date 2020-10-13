package com.example.workaddict.DataClass;

import com.example.workaddict.DataClass.Address;
import com.example.workaddict.DataClass.RoadAddress;

public class PressedAddress   {

    private RoadAddress roadAddress;
    private Address address;

    public RoadAddress getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(RoadAddress roadAddress) {
        this.roadAddress = roadAddress;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    //    @SerializedName("road_address")
//    @Expose
//    private List<RoadAddress> roadAddress;

//    @SerializedName("address")
//    @Expose
//    private List<Address> address;


//    public List<RoadAddress> getRoadAddress() {
//        return roadAddress;
//    }
//
//    public void setRoadAddress(List<RoadAddress> roadAddress) {
//        this.roadAddress = roadAddress;
//    }
//
//    public List<Address> getAddress() {
//        return address;
//    }
//
//    public void setAddress(List<Address> address) {
//        this.address = address;
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeList(this.getRoadAddress());
//    }
//
//    public PressedAddress() {
//    }
//
//    protected PressedAddress(Parcel in) {
//        this.roadAddress = new ArrayList<RoadAddress>();
//        this.address = new ArrayList<Address>();
//        in.readList(this.roadAddress, RoadAddress.class.getClassLoader());
//        in.readList(this.address, Address.class.getClassLoader());
//    }
//
//    public static final Parcelable.Creator<PressedAddress> CREATOR = new Parcelable.Creator<PressedAddress>() {
//        @Override
//        public PressedAddress createFromParcel(Parcel source) {
//            return new PressedAddress(source);
//        }
//
//        @Override
//        public PressedAddress[] newArray(int size) {
//            return new PressedAddress[size];
//        }
//    };
}
