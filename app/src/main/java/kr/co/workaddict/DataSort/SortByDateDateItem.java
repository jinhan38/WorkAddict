package kr.co.workaddict.DataSort;

import kr.co.workaddict.TimeLineDateClass.DateItem;

import java.util.Comparator;

public class SortByDateDateItem implements Comparator<DateItem> {


    @Override
    public int compare(DateItem o1, DateItem o2) {
        return o1.getAllTime().compareTo(o2.getAllTime());
    }
}
