package com.example.workaddict.BottomSheet;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workaddict.BottomFragment.ListFragment;
import com.example.workaddict.BottomFragment.TimeLinePage;
import com.example.workaddict.DataClass.CategoryData;
import com.example.workaddict.DataClass.Document;
import com.example.workaddict.DataClass.PlaceData;
import com.example.workaddict.DataClass.TimeLine;
import com.example.workaddict.TimeLineClass.MoveAddTimeLine;
import com.example.workaddict.OnSingleClickListener;
import com.example.workaddict.R;
import com.example.workaddict.TimeLineClass.TimeLineAdapter;
import com.example.workaddict.Utility.Util;
import com.example.workaddict.popup.NavigationPopup;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class ClickedBottomSheetContent {
    private static final String TAG = "ClickedBottomSheetConte";
    private boolean isMyCategoryList;
    private View view;
    private List<Document> documents;
    private ArrayList<PlaceData> placeData;
    private int tagNum;
    private FragmentActivity fragmentActivity;
    private BottomSheetBehavior behavior;
    public static MyBottomSheet myBottomSheet;
    private String placeName = "";
    private ArrayList<TimeLine> timeLines;
    private String currentCategoryColor = "";

    public ClickedBottomSheetContent(BottomSheetBehavior behavior, boolean isMyCategoryList, View view, List<Document> documents,
                                     ArrayList<PlaceData> placeData, int tagNum, FragmentActivity fragmentActivity,
                                     ArrayList<TimeLine> timeLines) {
        this.behavior = behavior;
        this.isMyCategoryList = isMyCategoryList;
        this.view = view;
        this.documents = documents;
        this.placeData = placeData;
        this.tagNum = tagNum;
        this.fragmentActivity = fragmentActivity;
        this.timeLines = timeLines;
    }

    public ClickedBottomSheetContent(BottomSheetBehavior behavior, boolean isMyCategoryList, View view, List<Document> documents,
                                     ArrayList<PlaceData> placeData, int tagNum, FragmentActivity fragmentActivity,
                                     ArrayList<TimeLine> timeLines, String currentCategoryColor) {
        this.behavior = behavior;
        this.isMyCategoryList = isMyCategoryList;
        this.view = view;
        this.documents = documents;
        this.placeData = placeData;
        this.tagNum = tagNum;
        this.fragmentActivity = fragmentActivity;
        this.timeLines = timeLines;
        this.currentCategoryColor = currentCategoryColor;
    }

    public void setTask() {

        initView(view);
        setClickedBottomSheetContent(isMyCategoryList, view, documents, placeData, tagNum);
        callPhone(view);
        setupListener();
        confirmTimeLine();
        addTimeLine();
    }

    private void initView(View view) {

        view.setVisibility(View.VISIBLE);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        LinearLayout addPlaceWrap = view.findViewById(R.id.addPlaceWrap);
        TextView place_name = view.findViewById(R.id.place_name);

        //타임라인중에 해당 장소와 이름이 같은 경우 검색해서 있으면 보여주고, 숫자 카운트 입력

        int drawable = 0;
        switch (currentCategoryColor) {
            case Util.CATEGORY_COLOR_RED:
                drawable = R.drawable.back_red_round_3dp;
                break;
            case Util.CATEGORY_COLOR_ORANGE:
                drawable = R.drawable.back_orange_round_3dp;
                break;
            case Util.CATEGORY_COLOR_YELLOW:
                drawable = R.drawable.back_yellow_round_3dp;
                break;
            case Util.CATEGORY_COLOR_GREEN:
                drawable = R.drawable.back_green_round_3dp;
                break;
            case Util.CATEGORY_COLOR_BLUE:
                drawable = R.drawable.back_blue_round_3dp;
                break;
            case Util.CATEGORY_COLOR_INDIGO:
                drawable = R.drawable.back_indigo_round_3dp;
                break;
            case Util.CATEGORY_COLOR_PURPLE:
                drawable = R.drawable.back_purple_round_3dp;
                break;
            case Util.CATEGORY_COLOR_BLACK:
                drawable = R.drawable.back_black_round_3dp;
                break;
            case Util.CATEGORY_COLOR_GRAY:
                drawable = R.drawable.back_gray_round_3dp;
                break;
        }

        if(drawable == 0) {
            drawable = R.drawable.back_black_round_3dp;
        }

        ((ImageView) fragmentActivity.findViewById(R.id.viewClip)).setImageDrawable(TimeLinePage.singlton.getResources().getDrawable(drawable));

//        if (isMyCategoryList) {
//
//            addPlaceWrap.setVisibility(View.INVISIBLE);
//            place_name.setTextColor(fragmentActivity.getResources().getColor(R.color.deepPurple));
//        } else {
//            addPlaceWrap.setVisibility(View.VISIBLE);
//            place_name.setTextColor(fragmentActivity.getResources().getColor(R.color.black));
//        }

        addPlaceWrap.setOnClickListener(v -> makeCategoryListBottomSheet());

        ((LinearLayout) view.findViewById(R.id.clickedPlaceTimeLine)).setOnClickListener(v -> addTimeLine());

        ((LinearLayout) view.findViewById(R.id.clickedPlaceNav)).setOnClickListener(v -> showNavigationPopup(isMyCategoryList, view));

        ((TextView) view.findViewById(R.id.timeLineText)).setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
            View view1 = LayoutInflater.from(fragmentActivity).
                    inflate(R.layout.timeline_popup_in_map, null, false);
            builder.setView(view1);

            ((TextView) view1.findViewById(R.id.timeLinePopupTitle)).setText(((TextView) view.findViewById(R.id.place_name)).getText().toString());

            RecyclerView recyclerView = view1.findViewById(R.id.timeLinePopupRecyclerView);

            builder.setPositiveButton("확인", (dialogInterface, i) -> dialogInterface.dismiss());

            if (timeLines != null) {

                if (timeLines.size() > 0) {

                    ArrayList<TimeLine> filteredTimeLines = new ArrayList<TimeLine>();

                    for (int i = 0; i < timeLines.size(); i++) {
                        if (timeLines.get(i).getPlaceName().equals(placeName)) {
                            filteredTimeLines.add(timeLines.get(i));
                        }
                    }

                    recyclerView.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentActivity);
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(
                            new TimeLineAdapter(filteredTimeLines, false));
                }
            }

            final AlertDialog dialog = builder.create();
            dialog.show();
        });


    }


    /**
     * 타임라인 추가하는 메소드
     * addTimeLine 클래스로 보내면서 type을 구분해서 보내야한다. 여기서는 addTimeLine class에서 카테고리를 선택할 필요가 없다.
     * 다만 placeData는 구분해야한다. placeData로 리스트에 등록한 장소인지 아닌지를 구분해서 만약에 등록이 안돼있으면 category에 기타로 넣어야 한다.
     * 등록이 되있느면 intent를 type 2로 설정해서 카테고리이름이랑 placename을 넣어야 한다.
     */
    private void addTimeLine() {

        ((LinearLayout) view.findViewById(R.id.clickedPlaceTimeLine)).setOnClickListener(v -> {
            new MoveAddTimeLine(placeData, placeName, fragmentActivity).execute();
        });
    }


    /**
     * 현재 선택된 장소의 타임라인이 있는지 없는지 확인
     */
    private void confirmTimeLine() {
        if (timeLines != null) {
            int count = 0;
            for (int i = 0; i < timeLines.size(); i++) {
                if (timeLines.get(i).getPlaceName().equals(placeName)) {
                    count++;
                }
            }

            LinearLayout timeLineCheckwrap = view.findViewById(R.id.timeLineCheckwrap);

            if (count > 0) {
                ((TextView) view.findViewById(R.id.timeLineText)).setText("타임라인(" + count + ")");
                timeLineCheckwrap.setVisibility(View.VISIBLE);
            } else {
                timeLineCheckwrap.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setupListener() {

        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


    }


    private void setClickedBottomSheetContent(boolean isMyCategoryList, View view, List<Document> documents, ArrayList<PlaceData> placeData, int tagNum) {

        Log.e(TAG, "setClickedBottomSheetContent: tagNum " + tagNum);
        if (!isMyCategoryList && documents != null && documents.size() > 0) {

            ((TextView) view.findViewById(R.id.place_name)).setText(documents.get(tagNum).getPlaceName());

            if (documents.get(tagNum).getAddressName() != null) {
                ((TextView) view.findViewById(R.id.address_name)).setText(documents.get(tagNum).getAddressName());
            } else {
                ((TextView) view.findViewById(R.id.address_name)).setText(documents.get(tagNum).getRoadAddressName());
            }

        } else if (isMyCategoryList && placeData != null && placeData.size() > 0) {

            ((TextView) view.findViewById(R.id.place_name)).setText(placeData.get(tagNum).getPlaceName());
            if (placeData.get(tagNum).getAddress() != null) {
                ((TextView) view.findViewById(R.id.address_name)).setText(placeData.get(tagNum).getAddress());
            } else {
                ((TextView) view.findViewById(R.id.address_name)).setText(placeData.get(tagNum).getRoadAddress());
            }
        }

        placeName = String.valueOf(((TextView) view.findViewById(R.id.place_name)).getText());
        Log.e(TAG, "setClickedBottomSheetContent: getText 장소명");
    }


    /**
     * 전화거는 메소드
     *
     * @param view
     */
    private void callPhone(View view) {

        ((LinearLayout) view.findViewById(R.id.clickedPlacePhone)).setOnClickListener(new OnSingleClickListener() {

            @Override
            public void onSingleClick(View v) {

                String phoneNum = "";
                if (isMyCategoryList) phoneNum = placeData.get(tagNum).getPhone();
                else phoneNum = documents.get(tagNum).getPhone();

                if (phoneNum.length() > 0) {
                    String place_tel = "tel:" + phoneNum;
                    Log.e("place_tel", place_tel);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(place_tel));
                    fragmentActivity.startActivity(intent);

                } else
                    Toast.makeText(fragmentActivity, "등록된 연락처가 없습니다.-", Toast.LENGTH_SHORT).show();

            }
        });

    }


    /**
     * 추가 버튼 눌러서 카테고리 bottomSheet 띄우는 메소드
     */
    private void makeCategoryListBottomSheet() {
        ListFragment.CATEGORYLIST_NUMBER = 2;
        myBottomSheet = new MyBottomSheet(fragmentActivity);
        myBottomSheet.show(fragmentActivity);
    }

    /**
     * 카테고리 추가 바텀시트 dismiss 메소드
     */
    public static void addCategoryBottomSheetDismiss() {
        Log.e(TAG, "addCategoryBottomSheetDismiss: ");
        myBottomSheet.setHidden();
    }

    /**
     * 네비게이션 팝업 띄우는 메소드
     *
     * @param isMyCategoryList
     * @param view
     */
    private void showNavigationPopup(boolean isMyCategoryList, View view) {

        String placeName = "";
        double longitude;
        double latitude;

        if (isMyCategoryList) {
            placeName = placeData.get(tagNum).getPlaceName();
            longitude = Double.parseDouble(placeData.get(tagNum).getLongitude());
            latitude = Double.parseDouble(placeData.get(tagNum).getLatitude());
        } else {
            placeName = documents.get(tagNum).getPlaceName();
            longitude = Double.parseDouble(documents.get(tagNum).getX());
            latitude = Double.parseDouble(documents.get(tagNum).getY());
        }


        String finalPlaceName = placeName;


        new NavigationPopup(fragmentActivity, finalPlaceName, longitude, latitude, fragmentActivity).show();


    }


    public void dismiss() {
        view.setVisibility(View.GONE);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}
