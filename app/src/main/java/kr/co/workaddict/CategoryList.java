package kr.co.workaddict;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.workaddict.BottomFragment.ListFragment;
import kr.co.workaddict.BottomFragment.TimeLinePage;
import kr.co.workaddict.BottomSheet.MyBottomSheetCategory;
import kr.co.workaddict.DataClass.CategoryData;

import kr.co.workaddict.R;

import kr.co.workaddict.Utility.UserInfo;
import kr.co.workaddict.Utility.Util;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryList extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CategoryList";
    private ImageButton ibtn_back;
    private LinearLayout showAddCategoryPage;
    private FirebaseAuth mAuth;
    public RecyclerView recyclerView;
    public CategoryAdapter categoryAdapter;
    private boolean fabOpen = false;
    private String categoryName = "";
    public int selectedCatetoryPosition = 0;
    public static CategoryList categoryList;
    public String fromListPage = "n";
    public LinearLayout categoryListPage;
    public LinearLayout addCategoryPage;
    private Button btnAddCategory;
    public EditText inputCategoryName;
    private TextView categoryNameLength;
    private ImageButton closeInputCategoryname;
    public LinearLayout bottomSheet;
    public BottomSheetBehavior bottomSheetBehavior;
    private TextView categoryNameDelete;
    private ConstraintLayout seeCategoryListInMap;
    public MyBottomSheetCategory myBottomSheetCategory;
    public boolean edit = false;
    public int clickedPosition;
    public String beforeCategoryName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        categoryList = this;
        initView();
        setupListener();

        bottomSheet = findViewById(R.id.categoryMoreBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        /////categoryData가 null인 아이디로 로그인 했을 때 리싸이클러뷰에서 에러 발생
    }

    private void initView() {
        ibtn_back = findViewById(R.id.ibtn_back);
        showAddCategoryPage = findViewById(R.id.showAddCategoryPage);
        categoryListPage = findViewById(R.id.categoryListPage);
        addCategoryPage = findViewById(R.id.addCategoryPage);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        inputCategoryName = findViewById(R.id.inputCategoryName);
        categoryNameLength = findViewById(R.id.categoryNameLength);
        closeInputCategoryname = findViewById(R.id.closeInputCategoryname);
        categoryNameDelete = findViewById(R.id.categoryNameDelete);
        seeCategoryListInMap = findViewById(R.id.seeCategoryListInMap);
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        fromListPage = intent.getExtras().getString("fromListPage");
        setRecyclerView();


    }

    private void setupListener() {
        showAddCategoryPage.setOnClickListener(this);
        btnAddCategory.setOnClickListener(this);
        closeInputCategoryname.setOnClickListener(this);
        categoryNameDelete.setOnClickListener(this);
        seeCategoryListInMap.setOnClickListener(this);
        ibtn_back.setOnClickListener(this);

        inputCategoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                categoryNameLength.setText("(" + count + "/20)");
                if (count > 0) closeInputCategoryname.setVisibility(View.VISIBLE);
                else closeInputCategoryname.setVisibility(View.GONE);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                categoryNameLength.setText("(" + count + "/20)");
                if (count > 0) closeInputCategoryname.setVisibility(View.VISIBLE);
                else closeInputCategoryname.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                int textCount = inputCategoryName.length();
                categoryNameLength.setText("(" + textCount + "/20)");

                if (textCount > 0) closeInputCategoryname.setVisibility(View.VISIBLE);
                else closeInputCategoryname.setVisibility(View.GONE);

            }
        });


    }

    public void setRecyclerView() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(BottomNavi.categoryData);
        recyclerView.setAdapter(categoryAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showAddCategoryPage:
                addCategoryPage.setVisibility(View.VISIBLE);
                categoryListPage.setVisibility(View.GONE);
                Util.showKeyboard(categoryList);
                break;
            case R.id.closeInputCategoryname:
                inputCategoryName.setText("");
                closeInputCategoryname.setVisibility(View.GONE);
                break;
            case R.id.ibtn_back:
                onBackPressed();
                Util.hideKeyboard(this);
                break;
            case R.id.seeCategoryListInMap:
                myBottomSheetCategory.dismiss();
                break;
            case R.id.btnAddCategory:
                boolean isExist = false;
                categoryName = inputCategoryName.getText().toString().trim();
                if (categoryName.length() > 0) {

                    if (BottomNavi.categoryData.size() > 0) {
                        for (CategoryData c : BottomNavi.categoryData) {
                            if (c.getCD().equals(categoryName)) {
                                isExist = true;
                            }
                        }
                    }

                    if (isExist) {
                        Util.showToast("동일한 이름의 카테고리가 있습니다.");

                    } else {

                        if (edit) {
                            edit = false;
                            categoryNameEdit(inputCategoryName.getText().toString().trim());
                        } else {
                            Util.addCategoryData(categoryName, categoryList);
                        }

                        Util.hideKeyboard(categoryList);

                        categoryListPage.setVisibility(View.VISIBLE);
                        addCategoryPage.setVisibility(View.GONE);
                        inputCategoryName.setText("");
                        closeInputCategoryname.setVisibility(View.GONE);

                    }

                } else {
                    Toast.makeText(categoryList, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {

        if (inputCategoryName.length() > 0) {
            Log.e(TAG, "onBackPressed: 렝스 진입" );
            inputCategoryName.setText("");
            closeInputCategoryname.setVisibility(View.GONE);
        } else if (addCategoryPage.getVisibility() == View.VISIBLE) {
            Log.e(TAG, "onBackPressed: 진입" );
            categoryListPage.setVisibility(View.VISIBLE);
            addCategoryPage.setVisibility(View.GONE);
            inputCategoryName.setText("");

        } else {
            super.onBackPressed();

        }
    }


    public void showBottomSheet(String categoryName) {

        Log.e(TAG, "showBottomSheet: categoryName : " + categoryName);
        myBottomSheetCategory = new MyBottomSheetCategory(this, categoryName);
        myBottomSheetCategory.show(getSupportFragmentManager(), TAG);

    }

    /**
     * 카테고리 이름 변경 메소드
     *
     * @param afterCategoryName
     */
    private void categoryNameEdit(String afterCategoryName) {

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("수정중...");
        dialog.show();

        DatabaseReference myRef = Util.getFirebaseDatabase().getReference("users").child(UserInfo.getID().replaceAll("\\.", ""));

        // 변경된 카테고리로 PlaceData 카테고리들 수정
        ArrayList<Integer> placeDataPosition = new ArrayList<Integer>();
        for (int i = 0; i < BottomNavi.placeData.size(); i++) {
            if (BottomNavi.placeData.get(i).getCategoryName().equals(beforeCategoryName)) {
                placeDataPosition.add(i);
            }
        }

        // 변경된 카테고리로 타임라인 카테고리들 수정
        ArrayList<Integer> timeLinePosition = new ArrayList<Integer>();
        for (int i = 0; i < BottomNavi.timeLines.size(); i++) {
            if (BottomNavi.timeLines.get(i).getCategoryName().equals(beforeCategoryName)) {
                timeLinePosition.add(i);
            }
        }


        //카테고리 이름 변경
        Map<String, Object> taskMap = null;

        taskMap = new HashMap<String, Object>();
        taskMap.put("/CD", afterCategoryName);
        myRef.child("CategoryData").child(BottomNavi.categoryDataKeyList.get(clickedPosition)).updateChildren(taskMap).addOnCompleteListener(task -> {


            //카테고리에 장소가 있는지 없는지 확인, 장소가 없다면 타임라인도 수정할 필요가 없음
            if (placeDataPosition.size() > 0) {

                Map<String, Object> placeDataTaskMap = new HashMap<String, Object>();
                placeDataTaskMap.put("/categoryName", afterCategoryName);
                for (int i = 0; i < placeDataPosition.size(); i++) {
                    myRef.child("PlaceData").child(BottomNavi.placeDataKeyList.get(placeDataPosition.get(i))).updateChildren(placeDataTaskMap).
                            addOnCompleteListener(task2 -> {
                                listPageReset();

                                dialog.dismiss();
                                ListFragment.singlton.adapter.notifyDataSetChanged();
                            }).addOnFailureListener(e -> dialog.dismiss());

                }


                if (timeLinePosition.size() > 0) {

                    Map<String, Object> timeLineTaskMap = new HashMap<String, Object>();
                    timeLineTaskMap.put("/categoryName", afterCategoryName);

                    for (int i = 0; i < timeLinePosition.size(); i++) {
                        myRef.child("TimeLine").child(BottomNavi.timeLineDataKeyList.get(timeLinePosition.get(i))).updateChildren(timeLineTaskMap).
                                addOnCompleteListener(task3 -> {
                                    if (TimeLinePage.singlton != null) {
                                        TimeLinePage.singlton.setTimeLineSpinner();
                                        TimeLinePage.singlton.setAdapter(BottomNavi.timeLines);
                                    }

                                    dialog.dismiss();
                                }).addOnFailureListener(e -> {

                            dialog.dismiss();
                        });
                    }

                }

            } else {

                listPageReset();
                dialog.dismiss();
            }


        });


    }

    private void listPageReset() {
        categoryListPage.setVisibility(View.VISIBLE);
        addCategoryPage.setVisibility(View.GONE);
        categoryAdapter.notifyDataSetChanged();
        ListFragment.singlton.b.tabLayout.removeAllTabs();
        ListFragment.singlton.setCategoryTabLayout();
    }


    @Override
    protected void onResume() {
        super.onResume();
        ListFragment.CATEGORYLIST_NUMBER = 1;
    }
}


