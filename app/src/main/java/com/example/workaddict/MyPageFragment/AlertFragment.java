package com.example.workaddict.MyPageFragment;

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
import com.example.workaddict.databinding.ActivityAlertFragmentBinding;

public class AlertFragment extends Fragment implements View.OnClickListener, BackButton {
    private ActivityAlertFragmentBinding b;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DataBindingUtil.inflate(inflater, R.layout.activity_alert_fragment, container, false);
        setupListener();
        return b.getRoot();
    }

    private void setupListener(){
//        BottomNavi.bottomNavi.b.ibMapPageBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibMapPageBack:
                MyPageFragment.singleton.onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }
}