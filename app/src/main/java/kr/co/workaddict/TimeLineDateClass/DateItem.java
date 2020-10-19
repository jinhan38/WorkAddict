package kr.co.workaddict.TimeLineDateClass;

import java.util.Calendar;

public abstract class DateItem {
    private static final String TAG = "DateItem";
    public static final int TYPE_TIME = 1;
    public static final int TYPE_DATA = 2;

    private long time;

    public DateItem(long time) {
        this.time = time;
    }

    public DateItem(int year, int month, int dayOfMonth, int hour, int minute) {
        setTime(year, month, dayOfMonth, hour, minute);
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTime(int year, int month, int dayOfMonth, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, dayOfMonth, hour, minute);
        time = calendar.getTimeInMillis();
    }

    public int getYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.HOUR);
    }

    public int getMinute() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.MINUTE);
    }

    public String getAmPm() {
        String amPm = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        if (calendar.get(Calendar.AM_PM) == 0) {
            amPm = "오전";
        } else {
            amPm = "오후";
        }
        return amPm;
    }


    public long getTime() {
        return time;
    }

    /**
     * 년 월 일 더해서 스트링으로 반환하는 메소드
     *
     * @return
     */
    public String getTimeToString() {
        String day = String.valueOf(getDayOfMonth());
        if (day.length() == 1) {
            day = "0" + day;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);
        String dayWeek = "";
        switch (dayNum) {
            case 1:
                dayWeek = "일요일";
                break;
            case 2:
                dayWeek = "월요일";
                break;
            case 3:
                dayWeek = "화요일";
                break;
            case 4:
                dayWeek = "수요일";
                break;
            case 5:
                dayWeek = "목요일";
                break;
            case 6:
                dayWeek = "금요일";
                break;
            case 7:
                dayWeek = "토요일";
                break;
        }
        return getYear() + "년 " + getMonth() + "월 " + day + "일 " + dayWeek;
    }

    public String getAllTime() {
        return getTime() + "";
    }


    /**
     * 시 : 분 리턴
     *
     * @return
     */
    public String getTimeHour() {
        String hour = "";
        String minute = "";


        if (String.valueOf(getHour()).length() == 1) hour = "0" + getHour();
        else hour = String.valueOf(getHour());

        if (String.valueOf(getMinute()).length() == 1) minute = "0" + getMinute();
        else minute = getMinute() + "";

        if (getAmPm().equals("오후")){
           hour = (Integer.parseInt(hour) + 12) + "";
        }
        
        return hour + " : " + minute;

    }


    /**
     * getType은 abstract로 했기 때문에 반드시 오버라이드 해야함
     *
     * @return
     */
    public abstract int getType();

}
