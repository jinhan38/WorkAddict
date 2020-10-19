package kr.co.workaddict;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.workaddict.BottomFragment.ListFragment;
import kr.co.workaddict.BottomFragment.MapFragment;
import kr.co.workaddict.BottomSheet.ClickedBottomSheetContent;
import kr.co.workaddict.DataClass.CategoryData;

import kr.co.workaddict.R;

import kr.co.workaddict.Utility.UserInfo;
import kr.co.workaddict.Utility.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private ArrayList<CategoryData> categoryData = null;
    private static final String TAG = "CategoryAdapter";
    private CategoryList c = new CategoryList();
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_name;
        //        public static CheckBox checkBox;
        public ConstraintLayout const_view;

        public MyViewHolder(View v) {
            super(v);
            tv_name = v.findViewById(R.id.tv_name);
            const_view = v.findViewById(R.id.const_view);
        }


    }

    public CategoryAdapter(ArrayList<CategoryData> categoryData) {
        this.categoryData = categoryData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item_list, parent, false);
        context = parent.getContext();
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_name.setText(categoryData.get(position).getCD());

        if (CategoryList.categoryList != null) {

            if (CategoryList.categoryList.fromListPage.equals("n")) {

            }
        }

        Log.e(TAG, "onBindViewHolder: CATEGORYLIST_NUMBER : " + ListFragment.CATEGORYLIST_NUMBER);

        switch (ListFragment.CATEGORYLIST_NUMBER) {
            case 0:
                //listFragment의 이동
                holder.const_view.setOnClickListener(v -> {

                    ProgressDialog dialog = new ProgressDialog(context);
                    dialog.setMessage("업데이트중...");
                    dialog.show();

                    MapFragment.isMyCategoryList = false; // 바텀시트에서 카테고리 명을  클릭했을 때 isMyCategoryList가 true면 removePoI에 들어가서 에러 발생

                    if (ListFragment.singlton != null && ListFragment.singlton.isOpenCheckBox) {
                        if (ListFragment.singlton.myBottomSheet.bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                            ListFragment.singlton.myBottomSheet.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }

                        DatabaseReference myRef = Util.getFirebaseDatabase().getReference("users").child(UserInfo.getID().replaceAll("\\.", ""));
                        Map<String, Object> taskMap = null;

                        for (int i = 0; i < ListFragment.singlton.deleteKeyList.size(); i++) {
                            taskMap = new HashMap<String, Object>();
                            taskMap.put(ListFragment.singlton.deleteKeyList.get(i) + "/categoryName", categoryData.get(position).getCD());

                            myRef.child("PlaceData").updateChildren(taskMap).addOnCompleteListener(task -> {
                                Log.e(TAG, "onComplete:  완료됨");
                                ListFragment.singlton.editFinish();
                                if (ClickedBottomSheetContent.myBottomSheet != null) {
                                    ClickedBottomSheetContent.addCategoryBottomSheetDismiss();
                                }

                                ListFragment.singlton.setAdapter(0);
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onFailure: 카테고리 이동 실패 : " + e );
                                }
                            });
                        }

                        Util.timeLineCategoryNameUpdate(ListFragment.singlton.clickedPlaceName, categoryData.get(position).getCD(), dialog);

                        ListFragment.singlton.optionMenuCheckedCount = 0;
                        if (BottomNavi.bottomNavi.b.viewPager.getCurrentItem() == 0) {
                            BottomNavi.bottomNavi.b.tvMapPageAppName.setText(context.getString(R.string.map));
                        } else if (BottomNavi.bottomNavi.b.viewPager.getCurrentItem() == 1) {
                            BottomNavi.bottomNavi.b.tvMapPageAppName.setText(context.getString(R.string.list));
                        } else {
                            BottomNavi.bottomNavi.b.tvMapPageAppName.setText("일중독");
                        }

                    }

                    BottomNavi.checkedPositions.clear();
                });
                break;

            case 1:

                holder.const_view.setOnClickListener(v -> {
                    CategoryList.categoryList.clickedPosition = position;
                    CategoryList.categoryList.beforeCategoryName = categoryData.get(position).getCD();
                    CategoryList.categoryList.showBottomSheet(holder.tv_name.getText().toString());
                });
                break;
            case 2:
                //장소추가할 때 카테고리 선택
                holder.const_view.setOnClickListener(v -> {
                    MapFragment.singlton.selectedCategoryPosition = position;
                    MapFragment.singlton.showPopup();
                });
                break;
            case 3:
                //showDetailInfo 페이지
                holder.const_view.setOnClickListener(v -> {
                    ShowMyPlaceDetailInfo.showMyPlaceDetailInfo.categoryName.setText(categoryData.get(position).getCD());
                    ShowMyPlaceDetailInfo.showMyPlaceDetailInfo.clickedCategoryName = categoryData.get(position).getCD();
                    ShowMyPlaceDetailInfo.showMyPlaceDetailInfo.myBottomSheet.setHidden();
                });

                break;
            case 4:
                holder.const_view.setOnClickListener(v -> {
                    MapFragment.singlton.selectedMarkerTag = MapFragment.singlton.selectPosition + "";
                    MapFragment.singlton.showPopup();
                });
                break;
            default:
                break;
        }


    }


    @Override
    public int getItemCount() {
        return categoryData.size();
    }
}
