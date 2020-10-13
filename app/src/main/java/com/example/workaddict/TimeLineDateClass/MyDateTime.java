package com.example.workaddict.TimeLineDateClass;

import android.util.Log;
import android.widget.ImageView;

import com.example.workaddict.TimeLineClass.TimeFormatChange;

public class MyDateTime extends DateItem {

    private static final String TAG = "MyDateTime";
    public String date;
    public String placeName;
    public String someThing;
    public String action;
    public ImageView imageView;
    public String imageKey;
    public boolean isAction = false;


    public MyDateTime(long time, String placeName, String someThing, String action) {
        super(time);
        this.placeName = placeName;
        this.someThing = someThing;
        this.action = action;
    }

    public MyDateTime(int year, int month, int dayOfMonth, int hour, int minute
            , String placeName, String someThing, String action, String imageKey) {
        super(year, month, dayOfMonth, hour, minute);
        this.placeName = placeName;
        this.someThing = someThing;
        this.action = action;
        this.imageKey = imageKey;

        String date = "";

        TimeFormatChange timeFormatChange = new TimeFormatChange(hour, minute);

        String strMonth = "";
        String strDay = "";

        if (month < 10) strMonth = "0" + month;
        else strMonth = month + "";

        if (dayOfMonth < 10) strDay = "0" + dayOfMonth;
        else strDay = String.valueOf(dayOfMonth);


        date = year + "-" +
                strMonth + "-" +
                strDay + " " +
                timeFormatChange.getRenewHour() + ":" +
                timeFormatChange.getRenewMinute() + ":00 " +
                timeFormatChange.getA();

//        this.date = year + "-" + month + "-" + dayOfMonth + " " + hour + ":" + minute;
        this.date = date;

    }

    @Override
    public int getType() {
        return TYPE_DATA;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getSomeThing() {
        return someThing;
    }

    public void setSomeThing(String someThing) {
        this.someThing = someThing;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }
}
