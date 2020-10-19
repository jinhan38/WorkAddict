package kr.co.workaddict.TimeLineClass;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.workaddict.BottomNavi;
import kr.co.workaddict.DataClass.CategoryData;

import java.util.ArrayList;

public class ChoiceCategoryList {
    private RecyclerView categoryRecyclerView;
    private RecyclerView placeRecyclerView;
    private ArrayList<CategoryData> categoryData;
    private View view;
    private Context context;

    public ChoiceCategoryList(RecyclerView categoryRecyclerView, RecyclerView placeRecyclerView,
                              ArrayList<CategoryData> categoryData, View view, Context context) {

        this.categoryRecyclerView = categoryRecyclerView;
        this.placeRecyclerView = placeRecyclerView;
        this.categoryData = categoryData;
        this.view = view;
        this.context = context;

        view.setVisibility(View.VISIBLE);
        setRecyclerView();

    }

    private void setRecyclerView(){
        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        categoryRecyclerView.setAdapter(new AddTimeLineCategoryRecyclerView(BottomNavi.categoryData, placeRecyclerView));
    }


}
