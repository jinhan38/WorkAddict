package com.example.workaddict.BottomSheet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workaddict.BottomNavi;
import com.example.workaddict.CategoryAdapter;
import com.example.workaddict.R;
import com.example.workaddict.Utility.Util;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.FirebaseDatabase;

public class MyBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = "MyBottomSheet";

    public BottomSheetBehavior bottomSheetBehavior;
    private Activity activity;
    private RecyclerView bottomSheetRecyclerView;


    public MyBottomSheet(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View view = View.inflate(getContext(), R.layout.list_page_bottom_sheet, null);

        bottomSheetRecyclerView = view.findViewById(R.id.bottomSheetRecyclerView);
        LinearLayout addCategoryInCategoryBottomSheet = view.findViewById(R.id.addCategoryInCategoryBottomSheet);

        addCategoryInCategoryBottomSheet.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            View view1 = LayoutInflater.from(activity).inflate(R.layout.activity_popup, null, false);
            builder.setView(view1);

            EditText et_place_name = view1.findViewById(R.id.et_place_name);
            ImageButton closeInputCategoryNameInPopup = view1.findViewById(R.id.closeInputCategoryNameInPopup);
            TextView categoryNameLengthInPopup = view1.findViewById(R.id.categoryNameLengthInPopup);
            TextView popup_title = view1.findViewById(R.id.popup_title);
            popup_title.setText("카테고리 이름을 입력해주세요.");


            et_place_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    categoryNameLengthInPopup.setText("(" + count + "/20)");
                    if (count > 0) closeInputCategoryNameInPopup.setVisibility(View.VISIBLE);
                    else closeInputCategoryNameInPopup.setVisibility(View.GONE);

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    categoryNameLengthInPopup.setText("(" + count + "/20)");
                    if (count > 0) closeInputCategoryNameInPopup.setVisibility(View.VISIBLE);
                    else closeInputCategoryNameInPopup.setVisibility(View.GONE);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    int textCount = s.length();
                    categoryNameLengthInPopup.setText("(" + textCount + "/20)");

                    if (textCount > 0) closeInputCategoryNameInPopup.setVisibility(View.VISIBLE);
                    else closeInputCategoryNameInPopup.setVisibility(View.GONE);

                }
            });


            closeInputCategoryNameInPopup.setOnClickListener(view2->{

                et_place_name.setText("");
                closeInputCategoryNameInPopup.setVisibility(View.GONE);

            });


            builder.setPositiveButton("생성", (dialog1, which) -> {

                Util.addCategoryData(et_place_name.getText().toString(), activity);
                setRecyclerView();
                dialog1.dismiss();

            }).setNegativeButton("취소", (dialog12, which) -> dialog12.dismiss());

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            Util.showKeyboard(activity);

        });

        setRecyclerView();
        dialog.setContentView(view);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        ((View) view.getParent()).setBackgroundColor(Color.TRANSPARENT);
    }

    private void setRecyclerView() {

        bottomSheetRecyclerView.setHasFixedSize(true);
        bottomSheetRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        CategoryAdapter categoryAdapter = new CategoryAdapter(BottomNavi.categoryData);
        bottomSheetRecyclerView.setAdapter(categoryAdapter);
    }


    public void show(final FragmentActivity fragmentActivity) {
        show(fragmentActivity.getSupportFragmentManager(), TAG);
    }

    public void setHidden() {

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    /**
     * 왠지 모르겠지만 dismiss하면 에러 발생한다.
     */
    public void dismiss() {
        this.dismiss();
    }
}
