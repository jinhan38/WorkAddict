package kr.co.workaddict.Introduce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kr.co.workaddict.R;

public class ThirdFragment extends Fragment {



    public static ThirdFragment singleton;

    public static ThirdFragment newInstance() {
        if (singleton == null) {
            singleton = new ThirdFragment();
        }
        return singleton;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_third_fragment, container, false);

        return view;
    }
}
