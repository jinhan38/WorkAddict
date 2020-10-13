package com.example.workaddict.TimeLineClass;

import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class TimePickerTask {
    private static final String TAG = "TimePickerTask";

    private TimePicker timePicker;
    private Button button;

    public TimePickerTask(TimePicker timePicker, Button button) {
        this.timePicker = timePicker;
        this.button = button;
        init();
    }

    private void init() {


        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(calendar.get(Calendar.MINUTE));


        button.setOnClickListener(v -> {
            Log.e(TAG, "init: timePicker hour : " + timePicker.getHour());
            Log.e(TAG, "init: timePicker minute : " + timePicker.getMinute());
            getHourAndMinute(timePicker.getHour(), timePicker.getMinute());
            AddTimeLineContent.addTimeLineContent.pageChange(AddTimeLineContent.INPUT_SOMETHING);
        });
    }

    public void getHourAndMinute(int hour, int minute) {

        TimeFormatChange timeFormatChange = new TimeFormatChange(hour, minute);


//        String a = "";
//        String renewHour = "";
//        String renewMinute =  minute + "";
//        if (hour > 12){
//            a = "오후";
//            renewHour = (hour - 12) + "";
//            if (renewHour.length() == 0){
//                renewHour = "00";
//            }else if (renewHour.length() == 1){
//                renewHour = "0" + renewHour;
//            }
//
//        } else {
//
//            Log.e(TAG, "getHourAndMinute: 12보다 작은 경우" );
//            renewHour = "0" + renewHour;
//            a = "오전";
//        }
//
//        if (renewMinute.length() == 1){
//            renewMinute = "0" + renewMinute;
//        }

//        if (hour == 0) renewHour = hour + "0";
//        else renewHour = hour + "";

//        Log.e(TAG, "getHourAndMinute: " + renewHour + ":" + renewMinute + ":00 " + a);
//        AddTimeLineContent.addTimeLineContent.pickedTime = renewHour + ":" + renewMinute + ":00 " + a;

        Log.e(TAG, "getHourAndMinute: " +
                timeFormatChange.getRenewHour() + ":" +
                timeFormatChange.getRenewMinute() + ":00 " +
                timeFormatChange.getA());

        AddTimeLineContent.addTimeLineContent.pickedTime = timeFormatChange.getRenewHour() + ":" +
                timeFormatChange.getRenewMinute() + ":00 " +
                timeFormatChange.getA();
    }

}
