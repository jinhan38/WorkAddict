package com.example.workaddict.MyPageFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.workaddict.BottomFragment.MyPageFragment;
import com.example.workaddict.BottomNavi;
import com.example.workaddict.Interface.BackButton;
import com.example.workaddict.R;
import com.example.workaddict.databinding.ActivitySettingFragmentBinding;
import com.example.workaddict.databinding.ActivityTermsFragmentBinding;

import kotlin.jvm.internal.PropertyReference0Impl;

public class TermsFragment extends Fragment implements View.OnClickListener, BackButton {
    private ActivityTermsFragmentBinding b;
    public static final int TERM_FIRST_NUM = 1;
    public static final int TERM_SECOND_NUM = 2;
    public static final int TERM_THIRD_NUM = 3;
    public static final int TERM_FOURTH_NUM = 4;
    public static int CURRENT_TERM_NUMBER = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DataBindingUtil.inflate(inflater, R.layout.activity_terms_fragment, container, false);
        setupListener();
        return b.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setupListener() {
        b.termsFirstWrap.setOnClickListener(this);
        b.termsSecondWrap.setOnClickListener(this);
        b.termsThirdWrap.setOnClickListener(this);
        b.termsFourthWrap.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibMapPageBack:
                MyPageFragment.singleton.onBackPressed();
                break;
            case R.id.termsFirstWrap:
                CURRENT_TERM_NUMBER = TERM_FIRST_NUM;
                getActivity().startActivity(new Intent(getActivity(), ShowTermsText.class));
                break;
            case R.id.termsSecondWrap:
                CURRENT_TERM_NUMBER = TERM_SECOND_NUM;
                getActivity().startActivity(new Intent(getActivity(), ShowTermsText.class));
                break;
            case R.id.termsThirdWrap:
                CURRENT_TERM_NUMBER = TERM_THIRD_NUM;
                getActivity().startActivity(new Intent(getActivity(), ShowTermsText.class));
                break;
            case R.id.termsFourthWrap:
                CURRENT_TERM_NUMBER = TERM_FOURTH_NUM;
                getActivity().startActivity(new Intent(getActivity(), ShowTermsText.class));
                break;
            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {

    }
}