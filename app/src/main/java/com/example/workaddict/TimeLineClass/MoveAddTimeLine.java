package com.example.workaddict.TimeLineClass;

import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.example.workaddict.DataClass.PlaceData;
import com.example.workaddict.TimeLineClass.AddTimeLineContent;

import java.util.ArrayList;

public class MoveAddTimeLine {

    private static final String TAG = "MoveAddTimeLine";

    private ArrayList<PlaceData> placeData;
    private String placeName;
    private FragmentActivity fragmentActivity;

    public MoveAddTimeLine(ArrayList<PlaceData> placeData, String placeName, FragmentActivity fragmentActivity) {
        this.placeData = placeData;
        this.placeName = placeName;
        this.fragmentActivity = fragmentActivity;
    }

    public void execute() {

        boolean isExist = false;
        String strCategoryName = "";
        String strPlaceName = "";
        for (int i = 0; i < placeData.size(); i++) {
            if (placeData.get(i).getPlaceName().equals(placeName)) {
                isExist = true;
                strCategoryName = placeData.get(i).getCategoryName();
                strPlaceName = placeData.get(i).getPlaceName();
            }
        }

        Intent intent = new Intent(fragmentActivity, AddTimeLineContent.class);
        intent.putExtra("pageType", "2");
        Log.e(TAG, "addTimeLine: isExist : " + isExist);

        if (isExist) {

            intent.putExtra("categoryName", strCategoryName);
            intent.putExtra("placeName", strPlaceName);
        } else {


            intent.putExtra("categoryName", "기타");
            intent.putExtra("placeName", placeName);

        }

        fragmentActivity.startActivity(intent);


    }
}
