package com.example.workaddict.Introduce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.workaddict.R;

public class FirstFragment extends Fragment {


    public static FirstFragment singleton;

    public static FirstFragment newInstance() {
        if (singleton == null) {
            singleton = new FirstFragment();
        }
        return singleton;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_first_fragment, container, false);

        return view;
    }
}
