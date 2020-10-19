package kr.co.workaddict.MyPageFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import kr.co.workaddict.BottomFragment.MyPageFragment;
import kr.co.workaddict.BottomNavi;
import kr.co.workaddict.Interface.BackButton;
import kr.co.workaddict.R;
import kr.co.workaddict.SaveSharedPreferences;
import kr.co.workaddict.databinding.ActivitySettingFragmentBinding;

public class SettingFragment extends Fragment implements View.OnClickListener, BackButton {
    private ActivitySettingFragmentBinding b;
    private static final String TAG = "SettingFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DataBindingUtil.inflate(inflater, R.layout.activity_setting_fragment, container, false);

        if (SaveSharedPreferences.getMarketingAgreement(getActivity()).equals("y")) {
            b.marketingAgreement.setChecked(true);
        } else {
            b.marketingAgreement.setChecked(false);
        }
        
        setupListener();
        return b.getRoot();
    }

    private void setupListener() {
        BottomNavi.bottomNavi.b.ibMapPageBack.setOnClickListener(this);

        b.marketingAgreement.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibMapPageBack:
                MyPageFragment.singleton.onBackPressed();
                break;
            case R.id.marketingAgreement:
                if (b.marketingAgreement.isChecked()) {
                    Log.e(TAG, "onClick: isCheckd");
                    SaveSharedPreferences.setMarketingAgreement(getActivity(), "y");
                } else {
                    Log.e(TAG, "onClick: not isCheckd");
                    SaveSharedPreferences.setMarketingAgreement(getActivity(), "n");

                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }
}