package kr.co.workaddict.BottomSheet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import kr.co.workaddict.BottomFragment.ListFragment;
import kr.co.workaddict.BottomNavi;
import kr.co.workaddict.CategoryList;
import kr.co.workaddict.DataClass.PlaceData;

import kr.co.workaddict.R;
import kr.co.workaddict.Utility.UserInfo;
import kr.co.workaddict.Utility.Util;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class MyBottomSheetCategory extends BottomSheetDialogFragment {
    private static final String TAG = "MyBottomSheetCategory";
    private Activity activity;
    private String categoryName = "";
    public BottomSheetBehavior bottomSheetBehavior;

    public MyBottomSheetCategory(Activity activity, String categoryName) {
        this.activity = activity;
        this.categoryName = categoryName;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);


        final View view = View.inflate(getContext(), R.layout.category_list_bottom_sheet, null);
        dialog.setContentView(view);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        ((View) view.getParent()).setBackgroundColor(Color.TRANSPARENT);


        ConstraintLayout seeCategoryListInMap = view.findViewById(R.id.seeCategoryListInMap);
        seeCategoryListInMap.setOnClickListener(v->{

            Util.developingMessage(activity);

        });

        TextView categoryNameChange = view.findViewById(R.id.categoryNameChange);
        categoryNameChange.setOnClickListener(v -> {

            CategoryList.categoryList.edit = true;
            CategoryList.categoryList.myBottomSheetCategory.dismiss();
            CategoryList.categoryList.addCategoryPage.setVisibility(View.VISIBLE);
            CategoryList.categoryList.categoryListPage.setVisibility(View.GONE);
            CategoryList.categoryList.inputCategoryName.setText(CategoryList.categoryList.beforeCategoryName);
            CategoryList.categoryList.inputCategoryName.requestFocus();
            Util.showKeyboard(getActivity());
            Log.e(TAG, "onCreateDialog: 수정 버튼 클릭");
        });


        TextView categoryNameDelete = view.findViewById(R.id.categoryNameDelete);
        categoryNameDelete.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("카테고리를 삭제하면 저장된 장소와 타임라인이 함께 삭제됩니다.\n" + categoryName + "을(를) 삭제하시겠습니까?");

            builder.setPositiveButton("삭제", (dialog12, which) -> {
                delete(dialog);
                dialog12.dismiss();
                ListFragment.singlton.listSizeZeroCheck();
            }).setNegativeButton("취소", (dialog1, which) -> dialog1.dismiss());

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        return dialog;
        
    }


    /**
     * 카테고리 페이지에서 삭제 메소드
     * 카테고리, 해당 카테고리에 속한 장소 모두 삭제하고, 타임라인에는 삭제 타임라인 추가
     */
    public void delete( BottomSheetDialog bottomSheetDialog) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("삭제중...");
        dialog.show();

        Log.e(TAG, "deletePlace: 선택 키값 " + BottomNavi.categoryDataKeyList.get(CategoryList.categoryList.clickedPosition));

        Util.getFirebaseDatabase().getReference("users").child(UserInfo.getID().replaceAll("\\.", "")).child("CategoryData")
                .child(BottomNavi.categoryDataKeyList.get(CategoryList.categoryList.clickedPosition))
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.e(TAG, "deleteCategoryData: 데이터 삭제 성공");
                    CategoryList.categoryList.categoryAdapter.notifyDataSetChanged();
                    ListFragment.singlton.b.tabLayout.removeAllTabs();
                    ListFragment.singlton.setCategoryTabLayout();
                    dialog.dismiss();
                    bottomSheetDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    bottomSheetDialog.dismiss();
                    Log.e(TAG, "deleteCategoryData: 데이터 삭제 실패");
                    Toast.makeText(CategoryList.categoryList, "네트워크가 원활하지 않습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                });


        ArrayList<PlaceData> pList = new ArrayList<PlaceData>();

        for (int i = 0; i < BottomNavi.placeData.size(); i++) {
            if (BottomNavi.placeData.get(i).getCategoryName().equals(CategoryList.categoryList.beforeCategoryName)) {
                pList.add(BottomNavi.placeData.get(i));

                Log.e(TAG, "delete: 키 리스트 : " + BottomNavi.placeDataKeyList.get(i));

                Util.getFirebaseDatabase().getReference("users").child(UserInfo.getID().replaceAll("\\.", "")).child("PlaceData")
                        .child(BottomNavi.placeDataKeyList.get(i))
                        .removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Log.e(TAG, "deletePlace: 데이터 삭제 성공");
                            dialog.dismiss();
                            bottomSheetDialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "deletePlace: 데이터 삭제 실패");
                            Toast.makeText(CategoryList.categoryList, "네트워크가 원활하지 않습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            bottomSheetDialog.dismiss();
                        });
            }
        }

    }


    public void show(final FragmentActivity fragmentActivity) {
        show(fragmentActivity.getSupportFragmentManager(), TAG);
    }
}
