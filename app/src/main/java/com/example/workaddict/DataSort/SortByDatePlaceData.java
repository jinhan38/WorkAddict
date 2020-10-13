package com.example.workaddict.DataSort;

import com.example.workaddict.DataClass.PlaceData;
import com.example.workaddict.DataClass.TimeLine;

import java.util.Comparator;

public class SortByDatePlaceData implements Comparator<PlaceData> {
    @Override
    public int compare(PlaceData o1, PlaceData o2) {
        return o1.getDateTime().compareTo(o2.getDateTime());
    }
}
