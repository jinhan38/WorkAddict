package kr.co.workaddict.MyPageFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import kr.co.workaddict.BottomFragment.MyPageFragment;
import kr.co.workaddict.Interface.BackButton;
import kr.co.workaddict.R;
import kr.co.workaddict.databinding.ActivityAlertFragmentBinding;

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