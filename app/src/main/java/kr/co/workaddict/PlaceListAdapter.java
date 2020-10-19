package kr.co.workaddict;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.workaddict.BottomFragment.ListFragment;
import kr.co.workaddict.BottomFragment.TimeLinePage;
import kr.co.workaddict.DataClass.CategoryData;
import kr.co.workaddict.DataClass.PlaceData;

import kr.co.workaddict.R;

import kr.co.workaddict.Utility.UserInfo;
import kr.co.workaddict.Utility.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.ViewHolder> {

    public static ArrayList<PlaceData> placeDataList = new ArrayList<PlaceData>();
    private static final String TAG = "PlaceListAdapter";
    public static int clickedCount = 0;
    public Context context;
    private int all = 0;
    private int selected = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView imageView;
        private TextView placeName;
        private TextView address;
        private CheckBox checkBox;
        private View view;
        private ImageButton myListStar;

        public ViewHolder(View v) {
            super(v);
            this.imageView = v.findViewById(R.id.imageView);
            this.placeName = v.findViewById(R.id.placeName);
            this.address = v.findViewById(R.id.address);
            this.checkBox = v.findViewById(R.id.checkBox);
            this.myListStar = v.findViewById(R.id.myListStar);
            this.view = v;


            if (ListFragment.singlton.isOpenCheckBox) {

                this.checkBox.setVisibility(View.VISIBLE);
                this.checkBox.setClickable(false);

                this.view.setOnClickListener(v1 -> {

                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                    } else {
                        checkBox.setChecked(true);
                    }

                    if (ListFragment.singlton.clickedPosition.size() > 0) {

                        if (ListFragment.singlton.clickedPlaceName.contains(placeName.getText().toString())) {

                            for (int i = 0; i < BottomNavi.placeData.size(); i++) {

                                if (BottomNavi.placeData.get(i).getPlaceName().equals(placeName.getText().toString())) {

                                    Object objectKey = (Object) BottomNavi.placeDataKeyList.get(i);
                                    Log.e(TAG, "ViewHolder: objectKey : " + objectKey);
                                    ListFragment.singlton.deleteKeyList.remove(objectKey);

                                    Object objectPosition = (Object) i;
                                    Log.e(TAG, "ViewHolder: object : " + objectPosition);
                                    ListFragment.singlton.clickedPosition.remove(objectPosition);

                                    Log.e(TAG, "ViewHolder: (Object) placeName.getText().toString() : " + (Object) placeName.getText().toString());
                                    ListFragment.singlton.clickedPlaceName.remove((Object) placeName.getText().toString());

                                }

                            }

                        } else {

                            for (int i = 0; i < BottomNavi.placeData.size(); i++) {
                                if (BottomNavi.placeData.get(i).getPlaceName().equals(placeName.getText().toString())) {
                                    ListFragment.singlton.deleteKeyList.add(BottomNavi.placeDataKeyList.get(i));
                                    ListFragment.singlton.clickedPosition.add(i);
                                    ListFragment.singlton.clickedPlaceName.add(placeName.getText().toString());
                                    break;
                                }
                            }
                        }


                    } else {

                        for (int i = 0; i < BottomNavi.placeData.size(); i++) {
                            if (BottomNavi.placeData.get(i).getPlaceName().equals(placeName.getText().toString())) {
                                Log.e(TAG, "ViewHolder: 사이즈 없음");
                                ListFragment.singlton.deleteKeyList.add(BottomNavi.placeDataKeyList.get(i));
                                ListFragment.singlton.clickedPosition.add(i);
                                ListFragment.singlton.clickedPlaceName.add(placeName.getText().toString());
                                break;
                            }
                        }

                    }

                    ListFragment.singlton.adapter.checkBoxSelectedCount();

                    Log.e(TAG, "ViewHolder: ListFragment.singlton.deleteKeyList : " + ListFragment.singlton.deleteKeyList);
                    Log.e(TAG, "ViewHolder: ListFragment.singlton.clickedPosition : " + ListFragment.singlton.clickedPosition);
                    Log.e(TAG, "ViewHolder: ListFragment.singlton.clickedPosition : " + ListFragment.singlton.clickedPlaceName);
                    Log.e(TAG, "ViewHolder: 클릭 ");

                });


            } else {

                this.checkBox.setVisibility(View.GONE);

                this.view.setOnClickListener(v1 -> {

                    int bottomPlaceDataInt = 0;

                    Log.e(TAG, "ViewHolder: 장소명 : " + placeName.getText().toString());
                    for (int i = 0; i < BottomNavi.placeData.size(); i++) {
                        if (BottomNavi.placeData.get(i).getPlaceName().equals(placeName.getText().toString())) {
                            bottomPlaceDataInt = i;
                            break;
                        }
                    }

                    Intent intent = new Intent(ListFragment.singlton.getActivity(), ShowMyPlaceDetailInfo.class);
                    intent.putExtra("categoryName", BottomNavi.placeData.get(bottomPlaceDataInt).getCategoryName());
                    intent.putExtra("placeName", BottomNavi.placeData.get(bottomPlaceDataInt).getPlaceName());
                    intent.putExtra("comment", BottomNavi.placeData.get(bottomPlaceDataInt).getComment());
                    intent.putExtra("address", BottomNavi.placeData.get(bottomPlaceDataInt).getAddress());
                    intent.putExtra("roadAddress", BottomNavi.placeData.get(bottomPlaceDataInt).getRoadAddress());
                    intent.putExtra("phone", BottomNavi.placeData.get(bottomPlaceDataInt).getPhone());
                    intent.putExtra("latitude", BottomNavi.placeData.get(bottomPlaceDataInt).getLatitude()); //위도 = x
                    intent.putExtra("longitude", BottomNavi.placeData.get(bottomPlaceDataInt).getLongitude());     //경도 = y
                    intent.putExtra("position", bottomPlaceDataInt);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    Objects.requireNonNull(ListFragment.singlton.getActivity()).startActivity(intent);

                });

            }
        }

    }

    public PlaceListAdapter(ArrayList<PlaceData> placeDataList) {
        this.placeDataList = placeDataList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_place_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        context = parent.getContext();
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.placeName.setText(placeDataList.get(position).getPlaceName());
        all = BottomNavi.placeData.size();


        for (CategoryData categoryData : BottomNavi.categoryData) {
            if (placeDataList.get(position).getCategoryName().equals(categoryData.getCD())) {
                switch (categoryData.getColor()) {
                    case Util.CATEGORY_COLOR_RED:
                        holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.marker_light_red));
                        break;
                    case Util.CATEGORY_COLOR_ORANGE:
                        holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.marker_orange));
                        break;
                    case Util.CATEGORY_COLOR_YELLOW:
                        holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.marker_yellow));
                        break;
                    case Util.CATEGORY_COLOR_GREEN:
                        holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.marker_green));
                        break;
                    case Util.CATEGORY_COLOR_BLUE:
                        holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.marker_blue));
                        break;
                    case Util.CATEGORY_COLOR_INDIGO:
                        holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.marker_indigo));
                        break;
                    case Util.CATEGORY_COLOR_PURPLE:
                        holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.marker_purple));
                        break;
                }
            }
        }

        if (placeDataList.get(position).getAddress().length() > 0) {
            holder.address.setText(placeDataList.get(position).getAddress());
        } else if (placeDataList.get(position).getRoadAddress().length() > 0) {
            holder.address.setText(placeDataList.get(position).getRoadAddress());
        }

        if (ListFragment.singlton.isOpenCheckBox) {

            holder.checkBox.setVisibility(View.VISIBLE);
            holder.myListStar.setVisibility(View.GONE);

        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.myListStar.setVisibility(View.VISIBLE);
        }


        if (placeDataList.get(position).getFavorites().equals("n")) {
            holder.myListStar.setImageDrawable(ListFragment.singlton.getResources().getDrawable(R.drawable.star_blank));
        } else {
            holder.myListStar.setImageDrawable(ListFragment.singlton.getResources().getDrawable(R.drawable.star_full));
        }

        holder.myListStar.setOnClickListener(v -> {


            String key = "";

            //즐겨찾기 추가
            Map<String, Object> placeFavorite = new HashMap<String, Object>();

            if (placeDataList.get(position).getFavorites().equals("n")) {
                placeFavorite.put("/favorites", "y");
                holder.myListStar.setImageDrawable(ListFragment.singlton.getResources().getDrawable(R.drawable.star_full));
            } else {
                placeFavorite.put("/favorites", "n");
                holder.myListStar.setImageDrawable(ListFragment.singlton.getResources().getDrawable(R.drawable.star_blank));
            }

            for (int i = 0; i < BottomNavi.placeData.size(); i++) {
                if (BottomNavi.placeData.get(i).getPlaceName().equals(placeDataList.get(position).getPlaceName())) {
                    key = BottomNavi.placeDataKeyList.get(i);
                    break;
                }
            }

            Util.getFirebaseDatabase().getReference("users")
                    .child(UserInfo.getID().replaceAll("\\.", ""))
                    .child("PlaceData").child(key)
                    .updateChildren(placeFavorite)
                    .addOnCompleteListener(task -> {
                        Log.e(TAG, "onBindViewHolder: 업데이트 완료");
                        ListFragment.singlton.recyclerViewTabDataUpdate(ListFragment.singlton.b.tabLayout.getSelectedTabPosition());
                        notifyDataSetChanged();
                    }).addOnFailureListener(e -> {
                Log.e(TAG, "setFavorites: 즐겨찾기 추가 실패");
            });

        });


        if (ListFragment.singlton.isOpenCheckBox) {


            Object object = holder.getAdapterPosition();
            if (ListFragment.singlton.clickedPosition.contains(object)) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }

        }

        holder.view.setOnLongClickListener(v -> {
            Log.e(TAG, "onLongClick: 롱클릭");

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            String placeName = holder.placeName.getText().toString();

            builder.setMessage(placeName + "을(를) 삭제하시겠습니까?");

            builder.setPositiveButton("삭제", (dialog, which) -> {

                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("삭제중...");
                progressDialog.show();

                String key = "";
                for (int i = 0; i < BottomNavi.placeData.size(); i++) {
                    if (BottomNavi.placeData.get(i).getPlaceName().equals(placeName)) {
                        key = BottomNavi.placeDataKeyList.get(i);
                        break;
                    }
                }

                Log.e(TAG, "onLongClick: key : " + key);
                Util.getFirebaseDatabase().getReference("users").child(UserInfo.getID().replaceAll("\\.", "")).child("PlaceData")
                        .child(key)
                        .removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show();
                            placeDataList.remove(position);
                            notifyDataSetChanged();
//                            ListFragment.singlton.setCategoryTabLayout();
                            dialog.dismiss();
                            progressDialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "네트워크가 원활하지 않습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            progressDialog.dismiss();
                        });


                for (int i = 0; i < BottomNavi.timeLines.size(); i++) {

                    if (BottomNavi.timeLines.get(i).getPlaceName().equals(placeName)) {

                        Util.getFirebaseDatabase().getReference("users").child(UserInfo.getID().replaceAll("\\.", "")).child("TimeLine")
                                .child(BottomNavi.timeLineDataKeyList.get(i))
                                .removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Log.e(TAG, "onSuccess: 장소리스트 하나 삭제, 해당 타임라인 삭제 성공");
                                    TimeLinePage.singlton.setTimeLineSpinner();
                                    TimeLinePage.singlton.setAdapter(BottomNavi.timeLines);
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "onSuccess: 장소리스트 하나 삭제, 해당 타임라인 삭제 실패"));
                    }
                }

            }).setNegativeButton("취소", (dialog, which) -> {
                dialog.dismiss();
            });

            final AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return placeDataList.size();
    }


    /**
     * 체크박스 선택됐을 때 툴바에 수치 보여주기
     */
    private void checkBoxSelectedCount() {

        int size = ListFragment.singlton.clickedPosition.size();

        if (ListFragment.singlton.getOptionMenuNumber == ListFragment.singlton.OPTION_MENU_MOVE) {
            BottomNavi.bottomNavi.b.tvMapPageAppName.setText("이동(" + size + "/" + all + ")");
        } else {
            BottomNavi.bottomNavi.b.tvMapPageAppName.setText("삭제(" + size + "/" + all + ")");
        }

        if (size > 0) {
            ListFragment.singlton.b.btnComplete.setBackground(ListFragment.singlton.getResources().getDrawable(R.drawable.back_purple_round_3dp));
            ListFragment.singlton.b.btnComplete.setEnabled(true);
        } else {
            ListFragment.singlton.b.btnComplete.setBackground(ListFragment.singlton.getResources().getDrawable(R.drawable.back_gray_round_3dp));
            ListFragment.singlton.b.btnComplete.setEnabled(false);
        }


    }


}


