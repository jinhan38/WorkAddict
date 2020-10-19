package kr.co.workaddict.TimeLineClass;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import kr.co.workaddict.DataClass.PlaceData;
import kr.co.workaddict.R;

import java.util.ArrayList;

class AddTimeLinePlaceRecyclerView extends RecyclerView.Adapter<AddTimeLinePlaceRecyclerView.MyViewHolder> {
    private ArrayList<PlaceData> placeData = new ArrayList<PlaceData>();
    private static final String TAG = "AddTimeLinePlaceRecycle";

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView contentText;
        public View view;

        public MyViewHolder(View v) {
            super(v);
            view = v;
            contentText = v.findViewById(R.id.contentText);

        }
    }

    public AddTimeLinePlaceRecyclerView(ArrayList<PlaceData> placeData) {
        this.placeData = placeData;
    }

    @Override
    public AddTimeLinePlaceRecyclerView.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_time_line_item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.contentText.setText(placeData.get(position).getPlaceName());
        holder.contentText.setOnClickListener(v -> {
            Log.e(TAG, "onBindViewHolder: " + placeData.get(position).getPlaceName());
            AddTimeLineContent.addTimeLineContent.strPlaceName = placeData.get(position).getPlaceName();
            AddTimeLineContent.addTimeLineContent.pageChange(AddTimeLineContent.CHOICE_DATE);

        });

    }


    @Override
    public int getItemCount() {
        return placeData.size();
    }
}