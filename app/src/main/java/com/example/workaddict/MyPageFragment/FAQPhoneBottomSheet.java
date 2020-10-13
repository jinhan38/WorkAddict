package com.example.workaddict.MyPageFragment;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.workaddict.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class FAQPhoneBottomSheet {
    private BottomSheetBehavior behavior;


    public FAQPhoneBottomSheet(BottomSheetBehavior behavior,
                               View view,
                               View.OnClickListener positiveListener,
                               View.OnClickListener negativeListener,
                               View faqCoverView) {

        this.behavior = behavior;

        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        view.findViewById(R.id.faqPhoneCancel).setOnClickListener(negativeListener);
        view.findViewById(R.id.faqPhoneConnect).setOnClickListener(positiveListener);


        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        faqCoverView.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_HIDDEN:
                    default:
                        faqCoverView.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    public void bottomSheetClose() {
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
