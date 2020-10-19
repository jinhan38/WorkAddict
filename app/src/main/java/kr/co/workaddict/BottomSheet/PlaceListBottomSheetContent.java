package kr.co.workaddict.BottomSheet;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.workaddict.BottomFragment.MapFragment;
import kr.co.workaddict.DataClass.Document;
import kr.co.workaddict.DataClass.PlaceData;
import kr.co.workaddict.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

import kr.co.workaddict.Fragment.PlaceListAdapter;

public class PlaceListBottomSheetContent {


    private View view;
    private List<Document> documents;
    private ArrayList<PlaceData> placeData = new ArrayList<PlaceData>();
    private boolean isMyCategoryList;
    private BottomSheetBehavior behaviorList;
    private FragmentActivity fragmentActivity;

    public PlaceListBottomSheetContent(View view, List<Document> documents, ArrayList<PlaceData> placeData,
                                       boolean isMyCategoryList, BottomSheetBehavior behaviorList, FragmentActivity fragmentActivity) {
        this.view = view;
        this.documents = documents;
        this.placeData = placeData;
        this.isMyCategoryList = isMyCategoryList;
        this.behaviorList = behaviorList;
        this.fragmentActivity = fragmentActivity;
    }

    public void setTask(){
        initView();
        setupListener();
    }

    private void initView(){

        view.setVisibility(View.VISIBLE);
        behaviorList.setState(BottomSheetBehavior.STATE_EXPANDED);

        RecyclerView bottomSheetRecyclerView = view.findViewById(R.id.recycle_place_list);
        bottomSheetRecyclerView.setHasFixedSize(true);
        bottomSheetRecyclerView.setLayoutManager(new LinearLayoutManager(fragmentActivity));
        PlaceListAdapter adapter;
        if (isMyCategoryList){
            adapter = new PlaceListAdapter(placeData, fragmentActivity, true);

        }   else{
            adapter = new PlaceListAdapter(documents, fragmentActivity, false);
        }

        bottomSheetRecyclerView.setAdapter(adapter);

        MapFragment.singlton.b.etSearch.setOnClickListener(v -> this.dismiss());
    }

    private void setupListener(){

        // bottomeSheet를 dismiss=GONE해야 위에 설정창들이 아래로 내려온다.
        behaviorList.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN){
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    public void dismiss(){
        view.setVisibility(View.GONE);
        behaviorList.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

}
