package com.example.workaddict.BottomSheet;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.workaddict.BottomFragment.TimeLinePage;
import com.example.workaddict.BottomNavi;
import com.example.workaddict.CustomDialog;
import com.example.workaddict.DataClass.TimeLine;
import com.example.workaddict.OnSingleClickListener;
import com.example.workaddict.R;
import com.example.workaddict.Utility.Util;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyBottomSheetTimeLine extends BottomSheetDialogFragment {
    private static final String TAG = "MyBottomSheetTimeLine";

    public BottomSheetBehavior bottomSheetBehavior;
    private CustomDialog customDialog;
    private FragmentActivity fragmentActivity;
    private ArrayList<TimeLine> timeLines;
    private int position;
    private FirebaseDatabase database;
    private TimeLinePage timeLinePage;
    private ArrayList<String> timeLineDataKeyList;
    private MyBottomSheetTimeLine myBottomSheetTimeLine;

    public MyBottomSheetTimeLine(FragmentActivity fragmentActivity, ArrayList<TimeLine> timeLines,
                                 int position, FirebaseDatabase database, TimeLinePage timeLinePage, ArrayList<String> timeLineDataKeyList) {
        this.fragmentActivity = fragmentActivity;
        this.timeLines = timeLines;
        this.position = position;
        this.database = database;
        this.timeLinePage = timeLinePage;
        this.timeLineDataKeyList = timeLineDataKeyList;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        myBottomSheetTimeLine = this;

        final View view = View.inflate(getContext(), R.layout.timeline_bottom_sheet, null);
        dialog.setContentView(view);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        ((View) view.getParent()).setBackgroundColor(Color.TRANSPARENT);


        Switch switchActionTimeLine = view.findViewById(R.id.switchActionTimeLine);

        if (timeLines.get(position).getAction().equals("y")) {
            switchActionTimeLine.setChecked(true);

        } else {
            switchActionTimeLine.setChecked(false);
        }

        switchActionTimeLine.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String action = "";

            if (isChecked) {
                action = "y";

            } else {
                action = "n";
            }

//            Util.updateTimeLineAction(this, position, BottomNavi.timeLineDataKeyList, action, timeLinePage);

        });


        if (BottomNavi.bottomNavi.b.viewPager.getCurrentItem() == 0) (view.findViewById(R.id.editButtonWrap)).setVisibility(View.GONE);


        (view.findViewById(R.id.editButtonWrap)).setOnClickListener(v -> {
//            timeLinePage.setTimeLineSomeThingChangePage(true);
            this.dismiss();
        });


        (view.findViewById(R.id.timeLineDelete)).setOnClickListener(v -> {
            String contents = "타임라인을 삭제하시겠습니까?";
            customDialog = new CustomDialog(timeLinePage.getActivity(), positiveListenerDelete, negativeListenerDelete, contents);
            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            customDialog.show();
        });

//        (view.findViewById(R.id.bottomSheetSomeThingEditBack)).setOnClickListener(v -> onBackPressed());


        return dialog;
    }

    public void setDismiss() {
        myBottomSheetTimeLine.dismiss();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }


    private OnSingleClickListener positiveListenerDelete = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            myBottomSheetTimeLine.dismiss();
            customDialog.dismiss();
            ArrayList<String> strKeyValue = new ArrayList<String>();
            strKeyValue.add(timeLineDataKeyList.get(position));
//            Util.removeTimeLine(database, strKeyValue, getActivity());
            Log.e(TAG, "onSingleClick: 삭제");
        }
    };

    private OnSingleClickListener negativeListenerDelete = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            customDialog.dismiss();
            Log.e(TAG, "onSingleClick: 취소");
        }
    };


    public void show(final FragmentActivity fragmentActivity) {
        show(fragmentActivity.getSupportFragmentManager(), TAG);
    }

//    @Override
//    public void onBackPressed() {
//        Log.e(TAG, "onBackPressed: ");
//        if (BottomNavi.bottomNavi.b.viewPager.getCurrentItem() == 0) {
//            if ((getView().findViewById(R.id.bottomSheetSomeThingEditWrap)) != null) {
//
//                if ((getView().findViewById(R.id.bottomSheetSomeThingEditWrap)).getVisibility() == View.VISIBLE) {
//                    Log.e(TAG, "onBackPressed: 수정페이지 오픈 확인");
//                    (getView().findViewById(R.id.timeLineBottomSheetWrap)).setVisibility(View.VISIBLE);
//                    (getView().findViewById(R.id.bottomSheetSomeThingEditWrap)).setVisibility(View.GONE);
//
//                }
//            }
//
//        } else {
//            Log.e(TAG, "onBackPressed: 그냥 뒤로가기 ");
//            fragmentActivity.onBackPressed();
//        }
//    }

    //onBackpress 넣기
}
