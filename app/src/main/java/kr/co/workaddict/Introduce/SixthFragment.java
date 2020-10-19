package kr.co.workaddict.Introduce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kr.co.workaddict.R;

public class SixthFragment extends Fragment {



    public static SixthFragment singleton;

    public static SixthFragment newInstance() {
        if (singleton == null) {
            singleton = new SixthFragment();
        }
        return singleton;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sixth_fragment, container, false);

        return view;
    }
}
