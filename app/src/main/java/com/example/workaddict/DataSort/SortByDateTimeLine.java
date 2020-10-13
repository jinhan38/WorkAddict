package com.example.workaddict.DataSort;

import com.example.workaddict.DataClass.TimeLine;

import java.util.Comparator;

public class SortByDateTimeLine implements Comparator<TimeLine> {
    @Override
    public int compare(TimeLine o1, TimeLine o2) {
        return o1.getDate().compareTo(o2.getDate());
    }
}
