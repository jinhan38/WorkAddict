package com.example.workaddict.TimeLineDateClass;

public class TimeClassification extends DateItem{


    /**
     * 부모 클래스인 AdapterItem의 생성자가 두개다
     * 그래서 여기도 생성자를 두개 만들어서 super해줘야된다.
     */
    public TimeClassification(long time) {
        super(time);
    }

    public TimeClassification(int year, int month, int dayOfMonth, int hour, int minute) {
        super(year, month, dayOfMonth, hour, minute);
    }


    @Override
    public int getType() {
        return TYPE_TIME;
    }
}
