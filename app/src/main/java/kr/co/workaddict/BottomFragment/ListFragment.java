package kr.co.workaddict.BottomFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import kr.co.workaddict.BottomNavi;
import kr.co.workaddict.BottomSheet.MyBottomSheet;
import kr.co.workaddict.CategoryList;
import kr.co.workaddict.DataClass.PlaceData;
import kr.co.workaddict.Interface.BackButton;
import kr.co.workaddict.PlaceListAdapter;
import kr.co.workaddict.R;
import kr.co.workaddict.Utility.PlaceListExcelExport;
import kr.co.workaddict.Utility.UserInfo;
import kr.co.workaddict.Utility.Util;
import kr.co.workaddict.databinding.ActivityListFragmentBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListFragment extends Fragment implements View.OnClickListener, BackButton {
    private static final String TAG = "ListFragment";
    public static ListFragment singlton;
    private View v;
    public boolean isOpenCheckBox = false;
    public ActivityListFragmentBinding b;
    public final static int OPTION_MENU_MOVE = 101;
    public final static int OPTION_MENU_DELETE = 102;
    public final static int FAVORITES_FILTER_DEFAULT = 200;
    public final static int FAVORITES_FILTER_Y = 201;
    public final static int FAVORITES_FILTER_N = 202;
    public int currentFavoritesFilterStatus = FAVORITES_FILTER_DEFAULT;
    public int getOptionMenuNumber = 0;
    public static ListFragment listFragment;
    public int optionMenuCheckedCount = 0;
    private long backBtnTime = 0;
    public ArrayList<PlaceData> p = new ArrayList<PlaceData>();
    public ArrayList<String> pKeyList = new ArrayList<String>();
    public PlaceListAdapter adapter;
    public MyBottomSheet myBottomSheet;
    public static int CATEGORYLIST_NUMBER = 0;
    public ArrayList<Integer> clickedPosition = new ArrayList<>();
    public ArrayList<String> deleteKeyList = new ArrayList<>();
    public ArrayList<String> clickedPlaceName = new ArrayList<>();
    public boolean isAllChecked = false;


    public static ListFragment newInstance() {
        if (singlton == null) {
            singlton = new ListFragment();
        }
        return singlton;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        listFragment = this;
        b = DataBindingUtil.inflate(inflater, R.layout.activity_list_fragment, container, false);
        Log.e(TAG, "onCreateView: ");
        listSizeZeroCheck();
        setCategoryTabLayout();
        setAdapter(0);
        setupListener();

        return b.getRoot();
    }

    public void listSizeZeroCheck() {

        if (BottomNavi.categoryData.size() == 0) {
            b.addCategoryFirstPage.setVisibility(View.VISIBLE);
            b.listPageWrap.setVisibility(View.GONE);

            b.addCategoryButtonFirst.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), CategoryList.class);
                intent.putExtra("fromListPage", "y");
                getActivity().startActivity(intent);
            });

        } else {

            b.addCategoryFirstPage.setVisibility(View.GONE);
            b.listPageWrap.setVisibility(View.VISIBLE);

        }
    }


    private void setupListener() {

        b.addCategoryButton.setOnClickListener(this);
        b.listPageFavoritesFilter.setOnClickListener(this);
        b.btnComplete.setOnClickListener(this);
        b.seeCategoryTextView.setOnClickListener(this);
//        b.listPageAllCheckBox.setOnClickListener(this);
        BottomNavi.bottomNavi.b.searchPlaceName.setOnClickListener(this);
        BottomNavi.bottomNavi.b.listSearchBackButton.setOnClickListener(this);
        BottomNavi.bottomNavi.b.listPageOptionMenu.setOnClickListener(this);


        b.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isAllChecked = false;
                p.clear();
                BottomNavi.checkedPositions.clear();
                clickedPosition.clear();
                optionMenuCheckedCount = 0;
                setAdapter(b.tabLayout.getSelectedTabPosition());
                Log.e(TAG, "onTabSelected: tabLayout.getSelectedTabPosition() : " + b.tabLayout.getSelectedTabPosition());
                if (isOpenCheckBox) {
                    if (ListFragment.singlton.getOptionMenuNumber == OPTION_MENU_MOVE) {
                        BottomNavi.bottomNavi.b.tvMapPageAppName.setText("이동(" + ListFragment.singlton.optionMenuCheckedCount + "/" + BottomNavi.placeData.size() + ")");
                    } else {
                        BottomNavi.bottomNavi.b.tvMapPageAppName.setText("삭제(" + ListFragment.singlton.optionMenuCheckedCount + "/" + BottomNavi.placeData.size() + ")");
                    }
                }

                b.btnComplete.setBackgroundDrawable(Objects.requireNonNull(getActivity()).getResources().getDrawable(R.drawable.back_gray_round_4dp));
                b.btnComplete.setEnabled(false);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        BottomNavi.bottomNavi.b.listSearchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textSearch();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textSearch();

            }

            @Override
            public void afterTextChanged(Editable s) {
                textSearch();

            }
        });

    }

    private void textSearch() {
        String str = BottomNavi.bottomNavi.b.listSearchEditText.getText().toString().trim();
        Stream<PlaceData> filterSearch = BottomNavi.placeData.stream().filter(x -> x.getPlaceName().contains(str));
        b.recyclerView.setHasFixedSize(true);
        b.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        b.recyclerView.setAdapter(new PlaceListAdapter(getArrayListFromStream(filterSearch)));

    }


    /**
     * 리스트 페이지 카테고리 탭 추가
     */
    public void setCategoryTabLayout() {
        Log.e(TAG, "setCategoryTabLayout: 진입");
        b.seeCategoryTextView.setVisibility(View.GONE);
        b.tabLayout.setVisibility(View.VISIBLE);
        b.addCategoryButton.setVisibility(View.VISIBLE);

        if (BottomNavi.categoryData != null && BottomNavi.categoryData.size() > 0) {

            b.tabLayout.removeAllTabs();
            for (int i = 0; i < BottomNavi.categoryData.size(); i++) {
                if (!BottomNavi.categoryData.get(i).getCD().equals("기타")) {

                    int count = 0;

                    for (PlaceData placeData : BottomNavi.placeData) {
                        if (placeData.getCategoryName().equals(BottomNavi.categoryData.get(i).getCD())) {
                            count += 1;
                        }
                    }

                    b.tabLayout.addTab(b.tabLayout.newTab().setText(BottomNavi.categoryData.get(i).getCD() + "(" + count + ")"));
                }
            }

        }
    }


    /**
     * tab 카테고리와 일치하는 장소리스트 붙이기  붙이기
     *
     * @param num
     */
    public void setAdapter(int num) {

        Log.e(TAG, "setAdapter: num : " + num);
        Log.e(TAG, "setAdapter: 바텀시트 장소 사이즈 확인 : " + BottomNavi.placeData.size());
        Log.e(TAG, "setAdapter: 바텀시트 카테고리 사이즈 확인 : " + BottomNavi.categoryData.size());
        p.clear();
        pKeyList.clear();
        b.recyclerView.setHasFixedSize(true);
        b.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        for (int i = 0; i < BottomNavi.placeData.size(); i++) {
            if (BottomNavi.placeData.get(i).getCategoryName().equals(BottomNavi.categoryData.get(num).getCD())) {
                p.add(BottomNavi.placeData.get(i));
                pKeyList.add(BottomNavi.placeDataKeyList.get(i));
            }
        }
        adapter = new PlaceListAdapter(p);
        b.recyclerView.setAdapter(adapter);
    }


    public void recyclerViewTabDataUpdate(int num) {
        Log.e(TAG, "setAdapterDataUpdate: num : " + num);
        p.clear();
        pKeyList.clear();

        for (int i = 0; i < BottomNavi.placeData.size(); i++) {
            if (BottomNavi.placeData.get(i).getCategoryName().equals(BottomNavi.categoryData.get(num).getCD())) {
                p.add(BottomNavi.placeData.get(i));
                pKeyList.add(BottomNavi.placeDataKeyList.get(i));
            }
        }
    }

    private static <T> ArrayList<T> getArrayListFromStream(Stream<T> stream) {
        List<T> list = stream.collect(Collectors.toList());
        ArrayList<T> arrayList = new ArrayList<T>(list);
        for (int i = 0; i < arrayList.size(); i++) {
        }
        return arrayList;
    }

    public void deletePlace() {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("삭제중...");
        dialog.show();

        isOpenCheckBox = true;

        for (int i = 0; i < deleteKeyList.size(); i++) {
            Util.getFirebaseDatabase().getReference("users").child(UserInfo.getID().replaceAll("\\.", "")).child("PlaceData")
                    .child(deleteKeyList.get(i))
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Log.e(TAG, "deletePlace: 데이터 삭제 성공");
                        Toast.makeText(getActivity(), "삭제 완료", Toast.LENGTH_SHORT).show();
                        editFinish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "deletePlace: 데이터 삭제 실패");
                        Toast.makeText(getActivity(), "네트워크가 원활하지 않습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    });
        }

        for (int i = 0; i < BottomNavi.timeLines.size(); i++) {

            for (int k = 0; k < clickedPlaceName.size(); k++) {

                if (BottomNavi.timeLines.get(i).getPlaceName().equals(clickedPlaceName.get(k))) {

                    Util.getFirebaseDatabase().getReference("users").child(UserInfo.getID().replaceAll("\\.", "")).child("TimeLine")
                            .child(BottomNavi.timeLineDataKeyList.get(i))
                            .removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Log.e(TAG, "onSuccess: 장소리스트 하나 삭제, 해당 타임라인 삭제 성공");
                                TimeLinePage.singlton.setTimeLineSpinner();
                                TimeLinePage.singlton.setAdapter(BottomNavi.timeLines);
                                TimeLinePage.singlton.timelineSizeCheck();
                                dialog.dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "onSuccess: 장소리스트 하나 삭제, 해당 타임라인 삭제 실패");
                                dialog.dismiss();
                            });

                }

            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addCategoryButton:
                Intent intent = new Intent(getActivity(), CategoryList.class);
                intent.putExtra("fromListPage", "y");
                getActivity().startActivity(intent);
                break;
            case R.id.btnComplete:
                if (clickedPosition.size() > 0) setOptionMenuBottomButton();
                else Toast.makeText(getActivity(), "장소를 선택해주세요", Toast.LENGTH_SHORT).show();
                break;
            case R.id.searchPlaceName:
                BottomNavi.bottomNavi.b.listSearchLayout.setVisibility(View.VISIBLE);
                BottomNavi.bottomNavi.b.listOptionMenu.setVisibility(View.GONE);
                BottomNavi.bottomNavi.b.toolbarAppNameWrap.setVisibility(View.GONE);
                b.tabLayoutWrap.setVisibility(View.GONE);
                BottomNavi.bottomNavi.b.listSearchEditText.requestFocus();
                Util.showKeyboard(getActivity());
                b.recyclerView.setHasFixedSize(true);
                b.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new PlaceListAdapter(BottomNavi.placeData);
                b.recyclerView.setAdapter(adapter);
                BottomNavi.bottomNavi.b.navView.setVisibility(View.GONE);
                Log.e(TAG, "onClick: searchPlaceName 클릭");
                break;
            case R.id.listSearchBackButton:
                onBackPressed();
//                BottomNavi.bottomNavi.b.listSearchLayout.setVisibility(View.GONE);
//                BottomNavi.bottomNavi.b.listOptionMenu.setVisibility(View.VISIBLE);
//                BottomNavi.bottomNavi.b.toolbarAppNameWrap.setVisibility(View.VISIBLE);
//                b.tabLayoutWrap.setVisibility(View.VISIBLE);
//                editFinish();
                break;
            case R.id.listPageFavoritesFilter:
                favoritesFilter(currentFavoritesFilterStatus, BottomNavi.placeData);
                break;
            case R.id.listPageOptionMenu:
                optionMenuClick();
                break;
            case R.id.seeCategoryTextView:
                Log.e(TAG, "onClick: seeCategoryTextView : ");
                b.tabLayout.setVisibility(View.VISIBLE);
                b.addCategoryButton.setVisibility(View.VISIBLE);
                b.seeCategoryTextView.setVisibility(View.GONE);
                currentFavoritesFilterStatus = FAVORITES_FILTER_DEFAULT;
                b.listPageFavoritesFilter.setImageResource(R.drawable.star_blank);
                setAdapter(b.tabLayout.getSelectedTabPosition());
                break;
//            case R.id.listPageAllCheckBox:
//                if (isOpenCheckBox) {
//                    if (isAllChecked) isAllChecked = false;
//                    else isAllChecked = true;
//                    setAdapter(b.tabLayout.getSelectedTabPosition());
//                    Log.e(TAG, "onClick: 전체버튼 클릭" );
//                }
//                break;
            default:
                break;
        }
    }


    private void favoritesFilter(int currentFavoritesFilterStatus, ArrayList<PlaceData> placeDataList) {
        ArrayList<PlaceData> favoritesFilterTimeline = new ArrayList<>();
        favoritesFilterTimeline.clear();
        Log.e(TAG, "favoritesFilter: 진입");

        switch (currentFavoritesFilterStatus) {

            case FAVORITES_FILTER_DEFAULT:
                for (PlaceData placeData : placeDataList) {
                    if (placeData.getFavorites().equals("y")) {
                        favoritesFilterTimeline.add(placeData);
                    }
                }
                this.currentFavoritesFilterStatus = FAVORITES_FILTER_Y;
                b.listPageFavoritesFilter.setImageResource(R.drawable.star_full);
                break;

            case FAVORITES_FILTER_Y:
                for (PlaceData placeData : placeDataList) {
                    if (placeData.getFavorites().equals("n"))
                        favoritesFilterTimeline.add(placeData);
                }
                this.currentFavoritesFilterStatus = FAVORITES_FILTER_N;
                b.listPageFavoritesFilter.setImageResource(R.drawable.star_blank);
                break;

            case FAVORITES_FILTER_N:
                favoritesFilterTimeline = BottomNavi.placeData;
                this.currentFavoritesFilterStatus = FAVORITES_FILTER_DEFAULT;
                b.listPageFavoritesFilter.setImageResource(R.drawable.star_black);
                break;
        }

        b.tabLayout.setVisibility(View.INVISIBLE);
        b.addCategoryButton.setVisibility(View.INVISIBLE);
        b.seeCategoryTextView.setVisibility(View.VISIBLE);
        b.recyclerView.setHasFixedSize(true);
        b.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        b.recyclerView.setAdapter(new PlaceListAdapter(favoritesFilterTimeline));


    }

    /**
     * 툴바에 있는 편집 버튼 클릭했을 때 기본 세팅들
     */
    private void editSettingVisible() {
        CATEGORYLIST_NUMBER = 0;
        optionMenuCheckedCount = 0;
        isOpenCheckBox = true;
        isAllChecked = false;
        p.clear();
        pKeyList.clear();
        BottomNavi.checkedPositions.clear();
        deleteKeyList.clear();
        clickedPlaceName.clear();
        clickedPosition.clear();
        setAdapter(b.tabLayout.getSelectedTabPosition());
        b.addCategoryButton.setVisibility(View.INVISIBLE);
        b.listPageFavoritesFilter.setVisibility(View.INVISIBLE);
//        b.listPageAllCheckBox.setVisibility(View.VISIBLE);
        b.optionMenuBottomButton.setVisibility(View.VISIBLE);
        BottomNavi.bottomNavi.b.ibMapPageBack.setVisibility(View.VISIBLE);
        BottomNavi.bottomNavi.b.searchPlaceName.setVisibility(View.GONE);
        b.btnComplete.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.back_gray_round_4dp));
        b.btnComplete.setEnabled(false);
    }

    /**
     * Toolbar에 있는 옵션메뉴 클릭하면 bottom에 확인 창 올라오게 만드는 메소드
     * 확인 클릭했을 때 옵션메뉴 각 항목 기능 실행
     */
    private void setOptionMenuBottomButton() {
        b.btnComplete.setEnabled(false);
        switch (getOptionMenuNumber) {
            case (OPTION_MENU_MOVE):
                myBottomSheet = new MyBottomSheet(getActivity());
                myBottomSheet.show(getActivity());
                break;
            case (OPTION_MENU_DELETE):
                //업데이트처리에서 선택한 키값을 삭제하면 됨
                deletePlace();
                break;
            default:
                break;
        }

    }

    private void optionMenuClick() {
        PopupMenu popup = new PopupMenu(getActivity(), BottomNavi.bottomNavi.b.listPageOptionMenu);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.list_menu, popup.getMenu());


        popup.setOnMenuItemClickListener(menuItem -> {

            switch (menuItem.getItemId()) {
                case R.id.menu_edit_list:
                    if (BottomNavi.placeData.size() > 0) {
                        getOptionMenuNumber = OPTION_MENU_MOVE;
                        editSettingVisible();
                    }
                    BottomNavi.bottomNavi.b.tvMapPageAppName.setText("이동(0/" + BottomNavi.placeData.size() + ")");
                    break;
                case R.id.menu_share_list:
                    new PlaceListExcelExport(getActivity(), BottomNavi.placeData, UserInfo.getID());
                    break;
                case R.id.menu_delete_list:
                    if (BottomNavi.placeData.size() > 0) {
                        getOptionMenuNumber = OPTION_MENU_DELETE;
                        editSettingVisible();
                    }
                    BottomNavi.bottomNavi.b.tvMapPageAppName.setText("삭제(0/" + BottomNavi.placeData.size() + ")");
                    break;
            }

            return false;
        });

        popup.show();

    }


    @Override
    public void onBackPressed() {

        if (BottomNavi.bottomNavi.b.viewPager.getCurrentItem() == 1) {
            if (myBottomSheet != null && myBottomSheet.bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                myBottomSheet.setHidden();
            } else if (BottomNavi.bottomNavi.b.listSearchLayout.getVisibility() == View.VISIBLE) {
                Log.e(TAG, "onBackPressed: back 진입");
                BottomNavi.bottomNavi.b.navView.setVisibility(View.VISIBLE);
                Util.hideKeyboard(getActivity());
                editFinish();
            } else if (isOpenCheckBox) {
                editFinish();
            } else {
                long curTime = System.currentTimeMillis();
                long gapTime = curTime - backBtnTime;
                if (0 <= gapTime && 2000 >= gapTime) {
                    getActivity().finish();
                } else {
                    backBtnTime = curTime;
                    Toast.makeText(getActivity(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
                }
            } //뒤로가기 두번 누르면 종료

        }
    }


    public void editFinish() {
        Log.e(TAG, "editFinish: ");
        isOpenCheckBox = false;
        isAllChecked = false;
        setCategoryTabLayout();
        p.clear();
        pKeyList.clear();
        deleteKeyList.clear();
        clickedPosition.clear();
        clickedPlaceName.clear();
        BottomNavi.checkedPositions.clear();
        b.tabLayoutWrap.setVisibility(View.VISIBLE);
        b.optionMenuBottomButton.setVisibility(View.GONE);
        b.addCategoryButton.setVisibility(View.VISIBLE);
        b.listPageFavoritesFilter.setVisibility(View.VISIBLE);
//        b.listPageAllCheckBox.setVisibility(View.GONE);
        BottomNavi.bottomNavi.b.listSearchEditText.setText("");
        BottomNavi.bottomNavi.b.tvMapPageAppName.setText(getString(R.string.list));
        BottomNavi.bottomNavi.b.tvMapPageAppName.setVisibility(View.VISIBLE);
        BottomNavi.bottomNavi.b.toolbarAppNameWrap.setVisibility(View.VISIBLE);
        BottomNavi.bottomNavi.b.ibMapPageBack.setVisibility(View.GONE);
        BottomNavi.bottomNavi.b.searchPlaceName.setVisibility(View.VISIBLE);
        BottomNavi.bottomNavi.b.listOptionMenu.setVisibility(View.VISIBLE);
        BottomNavi.bottomNavi.b.listSearchLayout.setVisibility(View.GONE);
        b.optionMenuBottomButton.setVisibility(View.GONE);
        b.addCategoryButton.setVisibility(View.VISIBLE);

        if (myBottomSheet != null) {
            myBottomSheet.setHidden();
        }
        getOptionMenuNumber = 0;

//        TabLayout.Tab tab = b.tabLayout.getTabAt(b.tabLayout.getSelectedTabPosition());
//        tab.select();

        b.btnComplete.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.back_gray_round_4dp));
        b.btnComplete.setEnabled(false);
    }


    @Override
    public void onResume() {
        super.onResume();
        CATEGORYLIST_NUMBER = 0;
    }
}



