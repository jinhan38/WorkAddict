package kr.co.workaddict.TimeLineClass;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.workaddict.BottomNavi;
import kr.co.workaddict.DataClass.CategoryData;
import kr.co.workaddict.DataClass.PlaceData;
import kr.co.workaddict.R;

import java.util.ArrayList;

public class AddTimeLineCategoryRecyclerView extends RecyclerView.Adapter<AddTimeLineCategoryRecyclerView.MyViewHolder> {
    private ArrayList<CategoryData> categoryData = new ArrayList<CategoryData>();
    private ArrayList<PlaceData> placeData = new ArrayList<PlaceData>();
    private static final String TAG = "AddTimeLineCategoryRecy";
    private int selectedPosition = 0;
    private int lastCheckedPosition = 0;
    private TextView lastCheckedView = null;
    private RecyclerView placeRecyclerview;
    private Context context;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView contentText;
        public View view;

        public MyViewHolder(View v) {
            super(v);
            view = v;
            contentText = v.findViewById(R.id.contentText);

        }
    }

    public AddTimeLineCategoryRecyclerView(ArrayList<CategoryData> categoryData, RecyclerView placeRecyclerview) {
        this.categoryData = categoryData;
        this.placeRecyclerview = placeRecyclerview;
    }

    @Override
    public AddTimeLineCategoryRecyclerView.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_time_line_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.contentText.setText(categoryData.get(position).getCD());

        if (position == 0) {
            lastCheckedPosition = position;
            lastCheckedView = holder.contentText;
        }

        holder.contentText.setOnClickListener(v -> {
            holder.contentText.setTextColor(AddTimeLineContent.addTimeLineContent.getResources().getColor(R.color.bright_blue));
            holder.contentText.setTextSize(16);

            if (lastCheckedPosition != position) {
                lastCheckedView.setTextColor(AddTimeLineContent.addTimeLineContent.getResources().getColor(R.color.strong_gray));
                lastCheckedView.setTextSize(15);
            }
            lastCheckedPosition = position;
            lastCheckedView = holder.contentText;


            placeData.clear();
            Log.e(TAG, "onBindViewHolder: size " + categoryData.size());
            for (int i = 0; i < BottomNavi.placeData.size(); i++) {

                if (holder.contentText.getText().toString().equals(BottomNavi.placeData.get(i).getCategoryName())) {
                    placeData.add(BottomNavi.placeData.get(i));
                    Log.e(TAG, "onBindViewHolder: " + BottomNavi.placeData.get(i).getCategoryName());
                }
            }
            if (placeData.size() > 0) {
                placeRecyclerview.setVisibility(View.VISIBLE);
                placeRecyclerview.setHasFixedSize(true);
                placeRecyclerview.setLayoutManager(new LinearLayoutManager(context));
            }
            placeRecyclerview.setAdapter(new AddTimeLinePlaceRecyclerView(placeData));
            AddTimeLineContent.addTimeLineContent.strCategoryName = categoryData.get(position).getCD();

        });
    }

    @Override
    public int getItemCount() {
        return categoryData.size();
    }


}