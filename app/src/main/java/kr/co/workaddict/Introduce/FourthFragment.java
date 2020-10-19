package kr.co.workaddict.Introduce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kr.co.workaddict.R;

public class FourthFragment extends Fragment {



    public static FourthFragment singleton;

    public static FourthFragment newInstance() {
        if (singleton == null) {
            singleton = new FourthFragment();
        }
        return singleton;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fourth_fragment, container, false);

        return view;
    }
}
