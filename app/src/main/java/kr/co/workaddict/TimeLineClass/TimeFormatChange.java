package kr.co.workaddict.TimeLineClass;

public class TimeFormatChange {
    private static final String TAG = "TimeFormatChange";
    int hour = 0;
    int minute = 0;
    String renewHour = "";
    String renewMinute = "";
    String a = "";

    public TimeFormatChange(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        change();
    }


    public void change() {
        renewMinute = minute + "";

        if (hour > 12) {
            a = "오후";
            renewHour = hour + "";
            if (renewHour.length() == 0) renewHour = "00";
            else if (renewHour.length() == 1) renewHour = "0" + renewHour;

        } else {

            if (hour < 10) renewHour = "0" + hour;
            else renewHour = hour + "";

            a = "오전";
        }

        if (renewMinute.length() == 1) {
            renewMinute = "0" + renewMinute;
        }
    }


    public String getRenewHour() {
        return renewHour;
    }

    public String getRenewMinute() {
        return renewMinute;
    }

    public String getA() {
        return a;
    }
}
