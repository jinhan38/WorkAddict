package com.example.workaddict.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workaddict.BottomFragment.ListFragment;
import com.example.workaddict.BottomFragment.MapFragment;
import com.example.workaddict.BottomNavi;
import com.example.workaddict.CustomDialog;
import com.example.workaddict.DataClass.Document;
import com.example.workaddict.DataClass.PlaceData;
import com.example.workaddict.TimeLineClass.MoveAddTimeLine;
import com.example.workaddict.BottomSheet.MyBottomSheet;
import com.example.workaddict.OnSingleClickListener;
import com.example.workaddict.R;

import java.util.ArrayList;
import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.ViewHolder> {
    private static final String TAG = "PlaceListAdapter";
    private CustomDialog customDialog;
    private String phone;
    private List<Document> documents;
    private ArrayList<PlaceData> placeData = new ArrayList<PlaceData>();
    private boolean isMyCategoryList;
    private FragmentActivity fragmentActivity;
    public static MyBottomSheet myBottomSheet;

    public PlaceListAdapter(List<Document> documents, FragmentActivity fragmentActivity, boolean isMyCategoryList) {
        this.documents = documents;
        this.fragmentActivity = fragmentActivity;
        this.isMyCategoryList = isMyCategoryList;
    }

    public PlaceListAdapter(ArrayList<PlaceData> placeData, FragmentActivity fragmentActivity, boolean isMyCategoryList) {
        this.placeData = placeData;
        this.fragmentActivity = fragmentActivity;
        this.isMyCategoryList = isMyCategoryList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView place_name;
        TextView category_name;
        TextView address_name;
        TextView phone;
        LinearLayout ll_search_list_item;
        ImageButton clickedPlaceListTimeLine;
        ImageButton addPlaceList;
        LinearLayout addPlaceListWrap;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.place_name = itemView.findViewById(R.id.place_name);
            this.category_name = itemView.findViewById(R.id.category_name);
            this.address_name = itemView.findViewById(R.id.address_name);
            this.phone = itemView.findViewById(R.id.phone);
            this.ll_search_list_item = itemView.findViewById(R.id.ll_search_list_item);
            this.clickedPlaceListTimeLine = itemView.findViewById(R.id.clickedPlaceListTimeLine);
            this.addPlaceList = itemView.findViewById(R.id.addPlaceList);
            this.addPlaceListWrap = itemView.findViewById(R.id.addPlaceListWrap);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = "";
        String category = "";
        String address = "";

        if (isMyCategoryList) {
            Log.e(TAG, "onBindViewHolder: isMyCategoryList = true ");
            name = placeData.get(position).getPlaceName();
            if (placeData.get(position).getCategoryGroupName() != null) {
                category = placeData.get(position).getCategoryGroupName();
            }
            address = BottomNavi.placeData.get(position).getAddress();
            phone = BottomNavi.placeData.get(position).getPhone();
            holder.addPlaceListWrap.setVisibility(View.INVISIBLE);
        } else {
            name = documents.get(position).getPlaceName();
            category = documents.get(position).getCategoryGroupName();
            address = documents.get(position).getAddressName();
            phone = documents.get(position).getPhone();
        }


        if (name.length() > 0) holder.place_name.setText(name);
        else holder.place_name.setText("-");
        if (category.length() > 0 && category != null) holder.category_name.setText(category);
        else holder.category_name.setText("-");
        if (address.length() > 0) holder.address_name.setText(address);
        else holder.address_name.setText("-");
        if (phone.length() > 0) {
            holder.phone.setText(phone);
        } else {
            holder.phone.setText("-");
        }


        holder.phone.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String contents = "";
                if (isMyCategoryList) {
                    contents = placeData.get(position).getPhone() + "\n전화연결 연결하시겠습니까?";
                } else {
                    contents = documents.get(position).getPhone() + "\n전화연결 연결하시겠습니까?";
                }

                if (!phone.equals("-")) {

                    customDialog = new CustomDialog(fragmentActivity, positiveListener, negativeListener, contents);
                    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    customDialog.show();
                }
            }
        });

        holder.ll_search_list_item.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Log.e(TAG, "onSingleClick: 클릭");
                MapFragment.singlton.mapView.selectPOIItem(MapFragment.singlton.markers[position], true);
                MapFragment.singlton.mapView.setMapCenterPoint(MapFragment.singlton.mapPoints[position], true);
                MapFragment.singlton.tagNum = position;
                if (isMyCategoryList){
                    MapFragment.singlton.setClickedBottomSheetContent(MapFragment.singlton.categoryPlaceData);
                }else{
                    MapFragment.singlton.setClickedBottomSheetContent(BottomNavi.placeData);
                }


            }
        });

        holder.clickedPlaceListTimeLine.setOnClickListener(v -> {
            Log.e(TAG, "onBindViewHolder: 클릭했어");
            String placeName = "";
            if (isMyCategoryList) {
                placeName = placeData.get(position).getPlaceName();
            } else {
                placeName = documents.get(position).getPlaceName();
            }

            new MoveAddTimeLine(placeData, placeName, fragmentActivity).execute();
        });

        holder.addPlaceList.setOnClickListener(v -> {
            MapFragment.singlton.selectPosition = position;
            ListFragment.CATEGORYLIST_NUMBER = 4;
             myBottomSheet = new MyBottomSheet(fragmentActivity);
            myBottomSheet.show(fragmentActivity);

        });


    }




    @Override
    public int getItemCount() {
        int count = 0;
        if (isMyCategoryList) {
            if (placeData != null && placeData.size() > 0) count = placeData.size();
        } else {
            if (documents != null && documents.size() > 0) count = documents.size();
        }
        return count;

    }


    private View.OnClickListener positiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String place_tel = "tel:" + phone;
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(place_tel));
            fragmentActivity.startActivity(intent);
            customDialog.dismiss();
        }
    };

    private View.OnClickListener negativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customDialog.dismiss();
        }
    };

}
