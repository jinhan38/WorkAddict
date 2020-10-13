package com.example.workaddict.Utility;

import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.example.workaddict.TimeLineDateClass.DateItem;
import com.example.workaddict.TimeLineDateClass.MyDateTime;

import java.util.ArrayList;

public class TextSearchAndHighLight {

    private static final String TAG = "TextSearchAndHighLight";
    private String keyword;
    private ArrayList<DateItem> dateItems;
    private ArrayList<Integer> containPosition = new ArrayList<Integer>();
    private TextView placeName;
    private TextView someThing;


    public TextSearchAndHighLight(String keyword, ArrayList<DateItem> dateItems, TextView placeName, TextView someThing) {
        this.keyword = keyword;
        this.dateItems = dateItems;
        this.placeName = placeName;
        this.someThing = someThing;
        containPosition.clear();

        searchText();
    }

    public void searchText() {

        if (keyword != null && keyword.length() > 0) {

            for (int i = 0; i < dateItems.size(); i++) {
                if (dateItems.get(i) instanceof MyDateTime) {

                    if (((MyDateTime) dateItems.get(i)).getPlaceName().contains(keyword) ||
                            (((MyDateTime) dateItems.get(i)).getSomeThing().contains(keyword))) {
                        containPosition.add(i);
                        textHighlight(i);
                    }
                }
            }
        }
    }

    public void textHighlight(int position) {

        Log.e(TAG, "textHighlight: keyword : " + keyword);
//        String highlighted = "<font color='red'>" + keyword + "</font>";
        String highlighted =  "<span style=\"background-color:yellow;\">" + keyword +  "</span>";

        if (((MyDateTime) dateItems.get(position)).getPlaceName().contains(keyword)) {
            Log.e(TAG, "textHighlight: 장소 키워드 포함");
            placeName.setText(Html.fromHtml(((MyDateTime) dateItems.get(position)).getPlaceName().replace(keyword, highlighted)));
        }

        if (((MyDateTime) dateItems.get(position)).getSomeThing().contains(keyword)) {
            Log.e(TAG, "textHighlight: 내용 키워드 포함");
            someThing.setText(Html.fromHtml(((MyDateTime) dateItems.get(position)).getSomeThing().replace(keyword, highlighted)));
        }

    }

    public int getCount() {
        return containPosition.size();
    }
}

