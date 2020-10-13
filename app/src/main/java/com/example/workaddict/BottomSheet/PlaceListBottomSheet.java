package com.example.workaddict.BottomSheet;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workaddict.BottomFragment.MapFragment;
import com.example.workaddict.DataClass.Document;
import com.example.workaddict.DataClass.PlaceData;
import com.example.workaddict.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class PlaceListBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final String TAG = "MyBottomSheet";

    public BottomSheetBehavior bottomSheetBehavior;
    private List<Document> documents;
    private ArrayList<PlaceData> placeData = new ArrayList<PlaceData>();
    private boolean isMyCategoryList;

    public PlaceListBottomSheet(List<Document> documents, boolean isMyCategoryList) {
        this.documents = documents;
        this.isMyCategoryList = isMyCategoryList;
    }

    public PlaceListBottomSheet(ArrayList<PlaceData> placeData, boolean isMyCategoryList) {
        this.placeData = placeData;
        this.isMyCategoryList = isMyCategoryList;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        final View view = View.inflate(getContext(), R.layout.placelist_bottom_sheet, null);
        dialog.setContentView(view);

        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        initView(view);
        return dialog;
    }


    private void initView(View view){

        RecyclerView bottomSheetRecyclerView = view.findViewById(R.id.recycle_place_list);
        bottomSheetRecyclerView.setHasFixedSize(true);
        bottomSheetRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        com.example.workaddict.Fragment.PlaceListAdapter adapter;
        if (isMyCategoryList){
            adapter = new com.example.workaddict.Fragment.PlaceListAdapter(placeData, getActivity(), isMyCategoryList);
        }   else{
            adapter = new com.example.workaddict.Fragment.PlaceListAdapter(documents, getActivity(), isMyCategoryList);
        }

        bottomSheetRecyclerView.setAdapter(adapter);

        MapFragment.singlton.b.etSearch.setOnClickListener(v -> this.dismiss());
    }


    public void show(final FragmentActivity fragmentActivity) {
        show(fragmentActivity.getSupportFragmentManager(), TAG);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
