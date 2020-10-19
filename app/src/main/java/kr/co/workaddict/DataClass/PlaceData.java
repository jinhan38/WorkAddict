package kr.co.workaddict.DataClass;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class PlaceData implements Comparable<PlaceData>{

    private String address;
    private String categoryGroupName;
    private String categoryName;
    private String comment;
    private String dateTime;
    private String favorites;
    private String id;
    private String latitude;
    private String longitude;
    private String phone;
    private String placeName;
    private String roadAddress;
    private String someThing;

    public String getFavorites() {
        return favorites;
    }

    public void setFavorites(String favorites) {
        this.favorites = favorites;
    }

    public String getCategoryGroupName() {
        return categoryGroupName;
    }

    public String getPhone() {
        return phone;
    }

    public String getComment() {
        return comment;
    }

    private boolean selected = false;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getAddress() {
        return address;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getSomeThing() {
        return someThing;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Exclude
    public Map<String, Object> toMap(String afterCategoryName) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("categoryName", afterCategoryName);
        return result;
    }


    @Override
    public int compareTo(PlaceData o) {
        return this.getPlaceName().compareTo(o.placeName);
    }
}
