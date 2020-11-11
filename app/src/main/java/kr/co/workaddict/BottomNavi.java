package kr.co.workaddict;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import kr.co.workaddict.BottomFragment.ListFragment;
import kr.co.workaddict.BottomFragment.MapFragment;
import kr.co.workaddict.BottomFragment.MyPageFragment;
import kr.co.workaddict.BottomFragment.TimeLinePage;
import kr.co.workaddict.DataClass.CategoryData;
import kr.co.workaddict.DataClass.Document;
import kr.co.workaddict.DataClass.FollowerData;
import kr.co.workaddict.DataClass.FollowingData;
import kr.co.workaddict.DataClass.GetAllData;
import kr.co.workaddict.DataClass.PlaceData;
import kr.co.workaddict.DataClass.PressedAddress;
import kr.co.workaddict.DataClass.Privacy;
import kr.co.workaddict.DataClass.TimeLine;
import kr.co.workaddict.Interface.BackButton;
import kr.co.workaddict.Utility.UserInfo;
import kr.co.workaddict.Utility.Util;
import kr.co.workaddict.databinding.ActivityBottomNaviBinding;

public class BottomNavi extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BottomNavi";
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction transaction;
    private FrameLayout nav_host_fragment_wrap;
    public static ArrayList<CategoryData> categoryData = new ArrayList<CategoryData>(); // 카테고리명만 있는 경우
    public static ArrayList<String> categoryDataKeyList = new ArrayList<String>();
    public static ArrayList<PlaceData> placeData = new ArrayList<PlaceData>();
    public static ArrayList<String> placeDataKeyList = new ArrayList<String>();
    public static ArrayList<TimeLine> timeLines = new ArrayList<TimeLine>();
    public static ArrayList<String> timeLineDataKeyList = new ArrayList<String>();
    public ArrayList<Uri> imageUri = new ArrayList<Uri>();
    public static ArrayList<FollowerData> followerData = new ArrayList<FollowerData>();
    public static ArrayList<String> followerKeyList = new ArrayList<String>();
    public static ArrayList<FollowingData> followingData = new ArrayList<FollowingData>();
    public static ArrayList<String> followingKeyList = new ArrayList<String>();

    public static ArrayList<Integer> checkedPositions = new ArrayList<Integer>();
    public static ArrayList<PressedAddress> addressDocuments = new ArrayList<PressedAddress>();
    public List<Document> documents = null;
    public static BottomNavi bottomNavi;
    public ActivityBottomNaviBinding b;
    public boolean result = false;
    private boolean viewCreated = false;


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            getWindow().setNavigationBarColor(getColor(R.color.navigation_bar_color));
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bottomNavi = this;
        b = DataBindingUtil.setContentView(this, R.layout.activity_bottom_navi);

        try {
            PackageInfo info = getPackageManager().getPackageInfo("kr.co.workaddict", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String keyHash = com.kakao.util.helper.Utility.getKeyHash(this);
        Log.e(TAG, "onCreate: 키해시 : " + keyHash);


        b.progressWrap.setVisibility(View.VISIBLE);
        b.viewPager.setPagingEnabled(false);

        Log.e(TAG, "onCreate: 바텀내비 로그인 확인 ; " + SaveSharedPreferences.getPrefIsLogin(this));

        if (SaveSharedPreferences.getPrefIsLogin(this).equals("n")) {

            if (SaveSharedPreferences.getPrefAutoLogin(this).equals("y")) {
                SaveSharedPreferences.setPrefIsLogin(this, "y");
            }
            //로딩페이지에서 데이터 안받아온 경우
            Log.e(TAG, "onCreate: 데이터 안받아옴");
            getFirebaseData();
        } else {
            //로딩페이지에서 데이터 받아온 경우
            Log.e(TAG, "onCreate: 로딩페이지에서 데이터 받아옴");

            placeData = GetAllData.getPlaceData();
            placeDataKeyList = GetAllData.getPlaceDataKeyList();
            categoryData = GetAllData.getCategoryData();
            categoryDataKeyList = GetAllData.getCategoryDataKeyList();
            timeLines = GetAllData.getTimeLines();
            timeLineDataKeyList = GetAllData.getTimeLineDataKeyList();
            followerData = GetAllData.getFollowerData();
            followerKeyList = GetAllData.getFollowerKeyList();
            followingData = GetAllData.getFollowingData();
            followingKeyList = GetAllData.getFollowingKeyList();


            if (!viewCreated) {
                viewCreated = true;
                initView();
            }

        }


        getReleaseHashKey();
        setupListener();
    }


    //https://puch-android.tistory.com/6
    private void getReleaseHashKey() {
        byte[] sha1 = {
                0x60, 0x5a, (byte) 0xa9, 0x52, 0x11, 0x37, (byte) 0xaf, (byte) 0xd1, (byte) 0x0e,
                (byte) 0x8e, 0x40, (byte) 0x86, (byte) 0xad, 0x39, (byte) 0xc2, 0x77, (byte) 0xf1, (byte) 0xea, 0x5f, (byte) 0xd8

        };
        Log.i(TAG, "구글 사이닝 getReleaseHashKey: " + Base64.encodeToString(sha1, Base64.NO_WRAP));
    }

    private void initView() {

        Log.e(TAG, "initView: 진입");
        Toast.makeText(bottomNavi, UserInfo.getID() + "으로 로그인되었습니다.", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "initView: PlaceData : " + placeData.size());
        transaction = fragmentManager.beginTransaction();

        BottomPagerAdapter bottomPagerAdapter = new BottomPagerAdapter(getSupportFragmentManager());
        b.viewPager.setAdapter(bottomPagerAdapter);


        b.viewPager.setOffscreenPageLimit(5);

        b.navView.getMenu().getItem(0).setChecked(true);
        optionMenuVisibility(0);

        b.container.setVisibility(View.VISIBLE);
        b.progressWrap.setVisibility(View.GONE);
    }


    public void getFirebaseData() {

        Log.e(TAG, "getFirebaseData: get UserID : " + UserInfo.getID());
        DatabaseReference myRef = Util.getFirebaseDatabase().getReference("users").child(UserInfo.getID().replaceAll("\\.", ""));
        Log.e(TAG, "getFirebaseData: myRef : " + myRef);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e(TAG, "onDataChange: 데이터 받기 시작");
                categoryData.clear();
                categoryDataKeyList.clear();
                placeData.clear();
                placeDataKeyList.clear();
                timeLines.clear();
                timeLineDataKeyList.clear();
                followerData.clear();
                followerKeyList.clear();
                followingData.clear();
                followingKeyList.clear();


                if (dataSnapshot.exists()) {


                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        String key = dataSnapshot1.getKey();

                        switch (key) {
                            case "CategoryData":
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    categoryData.add(dataSnapshot2.getValue(CategoryData.class));
                                    categoryDataKeyList.add(dataSnapshot2.getKey());
                                }
                                break;
                            case "PlaceData":
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    placeData.add(dataSnapshot2.getValue(PlaceData.class));
                                    placeDataKeyList.add(dataSnapshot2.getKey());
                                }
                                break;
                            case "TimeLine":
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    TimeLine timeLine = dataSnapshot2.getValue(TimeLine.class);
                                    timeLines.add(timeLine);
                                    timeLineDataKeyList.add(dataSnapshot2.getKey());
                                }

                                break;
                            case "Follower":
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    followerData.add(dataSnapshot2.getValue(FollowerData.class));
                                    followerKeyList.add(dataSnapshot2.getKey());
                                }
                                break;
                            case "Following":
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    followingData.add(dataSnapshot2.getValue(FollowingData.class));
                                    followingKeyList.add(dataSnapshot2.getKey());
                                }
                                break;
                            case "Privacy":
                                Log.e(TAG, "onDataChange: 프라이버시 진입");
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                    Privacy privacy = dataSnapshot2.getValue(Privacy.class);

                                    if (privacy.getId().length() > 0) {

                                        UserInfo.setName(privacy.getName());
                                        UserInfo.setID(privacy.getId());
                                        if (SaveSharedPreferences.getPrefAutoLogin(BottomNavi.this).equals("y")) {
                                            SaveSharedPreferences.setPrefName(BottomNavi.this, privacy.getName());
                                            SaveSharedPreferences.setPrefId(BottomNavi.this, privacy.getId());
                                        }
                                    }


                                }
                            default:
                                break;
                        }
                    }


                    if (!viewCreated) {
                        Log.e(TAG, "onDataChange: viewCreated fales");
                        viewCreated = true;
                        initView();
                    }

                } else {
                    Util.userAddPrivacy();
                    Util.addPrivacy();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled:databaseError : " + databaseError);
            }
        });

        Log.e(TAG, "getFirebaseData: 완전 끝");


        b.progressWrap.setVisibility(View.GONE);
    }


    private void setupListener() {

        b.ibMapPageBack.setOnClickListener(this);

        b.navView.setOnNavigationItemSelectedListener(menuItem -> {
            b.MapOptionMenu.setVisibility(View.GONE);
            b.listOptionMenu.setVisibility(View.GONE);
            b.listSearchLayout.setVisibility(View.GONE);
            b.timeLineOptionMenu.setVisibility(View.GONE);
            b.myPageOptionMenu.setVisibility(View.GONE);
            if (b.viewPager.getCurrentItem() == 3) {
                if (MyPageFragment.singleton.b.myPageFragmentContainer.getVisibility() == View.VISIBLE) {
                    MyPageFragment.singleton.b.myPageMenuTextWrap.setVisibility(View.VISIBLE);
                    MyPageFragment.singleton.b.myPageFragmentContainer.setVisibility(View.GONE);
                    Log.e(TAG, "setupListener: 컨테이너 열려있음");
                    b.tvMapPageAppName.setText(getString(R.string.mypage));
                }

            }

//            if (b.viewPager.getCurrentItem() == 2) {
//                if (TimeLinePage.singlton != null) {
//                    TimeLinePage.singlton.onPause();
//                }
//            }


            MapFragment.singlton.searchBoxAnim(false);

            if (ListFragment.singlton.isOpenCheckBox) ListFragment.singlton.editFinish();

            transaction = fragmentManager.beginTransaction();
            switch (menuItem.getItemId()) {

                case R.id.map:
                    b.viewPager.setCurrentItem(0);
                    b.MapOptionMenu.setVisibility(View.VISIBLE);
                    b.tvMapPageAppName.setText(getString(R.string.map));
                    break;
                case R.id.list:

                    b.viewPager.setCurrentItem(1);
                    b.listOptionMenu.setVisibility(View.VISIBLE);
                    b.tvMapPageAppName.setText(getString(R.string.list));

                    if (ListFragment.singlton != null && categoryData.size() > 0) {

                        if (ListFragment.singlton.isOpenCheckBox) {
                            Log.e(TAG, "setupListener: 바텀내비 리스트 버튼 클릭");
                            ListFragment.singlton.p.clear();
                            ListFragment.singlton.pKeyList.clear();
                            ListFragment.singlton.isOpenCheckBox = false;
                            ListFragment.singlton.setCategoryTabLayout();
                            ListFragment.singlton.setAdapter(0);
                        }

                    }
//
                    if (placeData.size() > 0) {
                        b.listOptionMenu.setVisibility(View.VISIBLE);
                    }

                    break;
                case R.id.timeLine:
                    b.viewPager.setCurrentItem(2);
                    b.timeLineOptionMenu.setVisibility(View.VISIBLE);
                    b.tvMapPageAppName.setText(getString(R.string.timeline));
                    break;
                case R.id.myPage:
                    b.viewPager.setCurrentItem(3);
                    b.myPageOptionMenu.setVisibility(View.VISIBLE);
                    b.tvMapPageAppName.setText(getString(R.string.mypage));
                    break;
                default:
                    break;

            }
            return true;
        });

        b.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                b.navView.getMenu().getItem(position).setChecked(true);
                optionMenuVisibility(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 툴바 아이콘 visibility 설정
     *
     * @param pageNum
     */
    private void optionMenuVisibility(int pageNum) {
        b.MapOptionMenu.setVisibility(View.GONE);
        b.listOptionMenu.setVisibility(View.GONE);
        b.timeLineOptionMenu.setVisibility(View.GONE);
        switch (pageNum) {
            case 0:
                b.MapOptionMenu.setVisibility(View.VISIBLE);
                break;
            case 1:
                b.listOptionMenu.setVisibility(View.VISIBLE);
                break;
            case 2:
                b.timeLineOptionMenu.setVisibility(View.VISIBLE);
                break;
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibMapPageBack:
                if (b.viewPager.getCurrentItem() == 0) {
                    MapFragment.singlton.onBackPressed();
                } else if (b.viewPager.getCurrentItem() == 1) {
                    ListFragment.singlton.onBackPressed();
                    Util.hideKeyboard(this);
                } else if (b.viewPager.getCurrentItem() == 2) {
                    Log.e(TAG, "onClick: 바텀 페이지 2");
                    TimeLinePage.singlton.onBackPressed();
                } else if (b.viewPager.getCurrentItem() == 3) {
                    Log.e(TAG, "onClick: 바텀 페이지 2");
                    MyPageFragment.singleton.onBackPressed();
                }
                break;
            default:
                break;
        }

    }


    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof BackButton) {
                    ((BackButton) fragment).onBackPressed();
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataClear();
    }


    public void dataClear() {
        if (documents != null) documents.clear();
        if (categoryData != null) categoryData.clear();
        if (categoryDataKeyList != null) categoryDataKeyList.clear();
        if (placeData != null) placeData.clear();
        if (placeDataKeyList != null) placeDataKeyList.clear();
        if (timeLines != null) timeLines.clear();
        if (timeLineDataKeyList != null) timeLineDataKeyList.clear();
        if (checkedPositions != null) checkedPositions.clear();
        if (followerData != null) followerData.clear();
        if (followerKeyList != null) followerKeyList.clear();
        if (followingData != null) followingData.clear();
        if (followingKeyList != null) followingKeyList.clear();
        if (addressDocuments != null) addressDocuments.clear();

    }
}
