package kr.co.workaddict;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import kr.co.workaddict.BottomFragment.ListFragment;
import kr.co.workaddict.BottomFragment.MapFragment;
import kr.co.workaddict.BottomFragment.TimeLinePage;
import kr.co.workaddict.BottomSheet.MyBottomSheet;
import kr.co.workaddict.Interface.ActivitySetting;

import kr.co.workaddict.R;

import kr.co.workaddict.TimeLineClass.AddTimeLineContent;
import kr.co.workaddict.Utility.UserInfo;
import kr.co.workaddict.Utility.Util;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.skt.Tmap.TMapTapi;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowMyPlaceDetailInfo extends AppCompatActivity implements ActivitySetting, View.OnClickListener {
    private static final String TAG = "ShowMyPlaceDetailInfo";
    public TextView categoryName;
    private EditText placeName;
    private EditText comment;
    private EditText address;
    private EditText phone;
    private ImageButton ibtn_phone;
    private Button timeline_in_place_detail;
    private Double latitude;
    private Double longitude;
    public static ShowMyPlaceDetailInfo showMyPlaceDetailInfo;
    private Toolbar toolbar;
    private RelativeLayout map;
    public ViewGroup mapViewContainer;
    public MapView mapView;
    private String telPhone;
    private Intent intent;
    private MapPoint[] mapPoint;
    private ImageButton btn_center;
    private TextView naver_navi;
    private TextView kakao_navi;
    private TextView tMap_navi;
    private MenuItem share;
    private MenuItem edit;
    private MenuItem delete;
    private String strCategoryName = "";
    private String strPlaceName = "";
    private String strComment = "";
    private String strAddress = "";
    private String strRoadAddress = "";
    private String strPhone = "";
    private Button btnComplete;
    private boolean isEditOpen = false;
    private int getPosition = 0;
    public MyBottomSheet myBottomSheet;
    public String clickedCategoryName = "";
    private TMapTapi tmaptapi;
    private final static int NAVER_NAVIGATION = 1;
    private final static int KAKAO_NAVIGATION = 2;
    private final static int TMAP_NAVIGATION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_place_detail_info);
        initView();
        setUpListener();

    }


    @Override
    public void initView() {
        showMyPlaceDetailInfo = this;
        tmaptapi = new TMapTapi(this);
        tmaptapi.setSKTMapAuthentication(Util.TMAP_APP_KEY);


        categoryName = findViewById(R.id.categoryName);
        placeName = findViewById(R.id.placeName);
        comment = findViewById(R.id.comment);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        ibtn_phone = findViewById(R.id.ibtn_phone);
        timeline_in_place_detail = findViewById(R.id.timeline_in_place_detail);

        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("상세보기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btn_center = findViewById(R.id.btn_center);
        naver_navi = findViewById(R.id.naver_navi);
        kakao_navi = findViewById(R.id.kakao_navi);
        tMap_navi = findViewById(R.id.tMap_navi);
        btnComplete = findViewById(R.id.btnComplete);
        enableEditText(false);


        intent = getIntent();
        strCategoryName = intent.getExtras().getString("categoryName");
        categoryName.setText(strCategoryName);
        strPlaceName = intent.getExtras().getString("placeName");
        placeName.setText(strPlaceName);
        Log.e(TAG, "initView: strPlaceName : " + strPlaceName );

        if (intent.getExtras().getString("comment").length() > 0) {
            strComment = intent.getExtras().getString("comment");
            comment.setText(strComment);
        } else comment.setVisibility(View.GONE);

        if (intent.getExtras().getString("address").length() > 0) {
            strAddress = intent.getExtras().getString("address");
            address.setText(strAddress);
        } else {
            strRoadAddress = intent.getExtras().getString("roadAddress");
            address.setText(strRoadAddress);
        }

        if (intent.getExtras().getString("phone").length() > 0) {
            strPhone = intent.getExtras().getString("phone");
            phone.setText(Util.replaceTel(strPhone));
            telPhone = intent.getExtras().getString("phone").replaceAll("-", "");
        } else phone.setText("-");

        getPosition = intent.getExtras().getInt("position");

        editInfo();
    }

    @Override
    public void setUpListener() {

        ibtn_phone.setOnClickListener(this);
        timeline_in_place_detail.setOnClickListener(this);
        btn_center.setOnClickListener(this);
        naver_navi.setOnClickListener(this);
        kakao_navi.setOnClickListener(this);

        final TMapTapi tmaptapi = new TMapTapi(this);
        tmaptapi.setSKTMapAuthentication(Util.TMAP_APP_KEY);

        tMap_navi.setOnClickListener(v -> {

            if (tmaptapi.isTmapApplicationInstalled()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("T-map 내비게이션으로 연결하시겠습니까?");

                builder.setPositiveButton("연결", (DialogInterface.OnClickListener) (dialog, which) -> {
                    tmaptapi.invokeRoute(strPlaceName, (float) longitude.floatValue(), (float) latitude.floatValue());
                    dialog.dismiss();

                }).setNegativeButton("취소", (DialogInterface.OnClickListener) (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("T-map 앱을 설치하지 않았습니다.\n앱을 다운로드하시겠습니까?");

                builder1.setPositiveButton("설치", (dialog1, which1) -> {
                    try {

                        Uri uri = Uri.parse(tmaptapi.getTMapDownUrl().get(0));
                        Log.e(TAG, "init: result : " + uri);
                        Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent1);
                        dialog1.dismiss();

                    } catch (Exception e) {

                        Toast.makeText(this, "일시적인 오류가 발생했습니다.\n다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }

                }).setNegativeButton("취소", (dialog1, which1) -> dialog1.dismiss());

                AlertDialog dialog = builder1.create();
                dialog.show();

            }
        });
        btnComplete.setOnClickListener(this);
        categoryName.setOnClickListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  // 왼쪽 상단 버튼 눌렀을 때
                onBackPressed();
                break;
            case R.id.share:
                Util.developingMessage(this);
                break;
            case R.id.edit:
                timeline_in_place_detail.setVisibility(View.GONE);
                btnComplete.setVisibility(View.VISIBLE);
                isEditOpen = true;
                enableEditText(true);
                getSupportActionBar().setTitle("편집");
                break;
            case R.id.delete:
                showDeletePlaceDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeletePlaceDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(strPlaceName + "을 삭제하시겠습니까?");
        builder.setPositiveButton("삭제", (dialog, which) -> {
            deletePlace(strPlaceName);
            dialog.dismiss();
        }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        final AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_detail_place, menu);
        share = menu.findItem(R.id.share);
        edit = menu.findItem(R.id.edit);
        delete = menu.findItem(R.id.delete);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timeline_in_place_detail:
                Intent intent = new Intent(showMyPlaceDetailInfo, AddTimeLineContent.class);
                intent.putExtra("pageType", "2");
                intent.putExtra("categoryName", strCategoryName);
                intent.putExtra("placeName", strPlaceName);
                startActivity(intent);
                break;
            case R.id.ibtn_phone:
                Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telPhone));
                startActivity(intent2);
                break;
            case R.id.btn_center:
                mapView.setMapCenterPoint(mapPoint[0], true);
                break;
            case R.id.naver_navi:
                navigationConnect("네이버 내비게이션", NAVER_NAVIGATION);
                break;
            case R.id.kakao_navi:
                navigationConnect("카카오 내비게이션", KAKAO_NAVIGATION);
                break;
            case R.id.btnComplete:
                Log.e(TAG, "onClick: btnComplete");
                btnComplete.setVisibility(View.GONE);
                timeline_in_place_detail.setVisibility(View.VISIBLE);
                editInfo();
                isEditOpen = false;
                enableEditText(false);
                dataUpdate();
                break;
            case R.id.categoryName:
                if (isEditOpen) {
                    myBottomSheet = new MyBottomSheet(this);
                    myBottomSheet.show(showMyPlaceDetailInfo);
                    BottomNavi.checkedPositions.add(getPosition);
                }
                break;
        }
    }


    /**
     * EditText 편집 활성화 여부 설정
     *
     * @param b
     */
    private void enableEditText(boolean b) {

        placeName.setEnabled(b);
        categoryName.setEnabled(b);
        comment.setEnabled(b);
        address.setEnabled(b);
        phone.setEnabled(b);

        placeName.setClickable(b);
        categoryName.setClickable(b);
        comment.setClickable(b);
        address.setClickable(b);
        phone.setClickable(b);

        placeName.setCursorVisible(b);

//        placeName.setFocusable(b);

//        placeName.setFocusableInTouchMode(b);


        if (b) {
//            ColorStateList colorStateList = ColorStateList.valueOf(R.color.softPurple);
//            placeName.setBackgroundTintList(colorStateList);
//            comment.setBackgroundTintList(colorStateList);
//            address.setBackgroundTintList(colorStateList);
//            phone.setBackgroundTintList(colorStateList);
            placeName.requestFocus();
            placeName.setSelection(placeName.length());

        } else {

//            ColorStateList colorStateList = ColorStateList.valueOf(R.color.basic_background);
//            placeName.setBackgroundTintList(colorStateList);
//            comment.setBackgroundTintList(colorStateList);
//            address.setBackgroundTintList(colorStateList);
//            phone.setBackgroundTintList(colorStateList);
//            placeName.setBackground(null);
//            comment.setBackground(null);
//            address.setBackground(null);
//            phone.setBackground(null);
            Util.hideKeyboard(this);
        }

    }

    private void editInfo() {
        strCategoryName = categoryName.getText().toString().trim();
        strPlaceName = placeName.getText().toString().trim();
        strComment = comment.getText().toString().trim();
        strAddress = address.getText().toString().trim();
        strPhone = Util.replaceTel(phone.getText().toString().trim() + "");
    }


    private void dataUpdate() {

        // 현재 장소를 수정하려면 key값을 가져와서 해당 key값에서 특정 키밸류를 업데이트해야한다.
        //리스트페이지에서 position값을 가져온다. 그리고 그 포지션에 해당되는 키값을 가져온다.
        Log.e(TAG, "dataUpdate: 진입");

//        DatabaseReference myRef = database.getReference("users");
        DatabaseReference myRef = Util.getFirebaseDatabase().getReference("users").child(UserInfo.getID().replaceAll("\\.", ""));
        Map<String, Object> taskMap = null;

        //바텀시트에서 클릭된 것으로 가져와야 한다.
        taskMap = new HashMap<String, Object>();
        taskMap.put("/categoryName", clickedCategoryName);
        taskMap.put("/placeName", strPlaceName);
        taskMap.put("/comment", strComment);
        taskMap.put("/address", strAddress);
        taskMap.put("/phone", strPhone);

        myRef.child("PlaceData").child(BottomNavi.placeDataKeyList.get(getPosition)).updateChildren(taskMap).addOnCompleteListener(task -> {
            Log.e(TAG, "PlaceData update :  완료됨");
            ListFragment.singlton.p.clear();
            ListFragment.singlton.pKeyList.clear();
            ListFragment.singlton.setAdapter(ListFragment.singlton.b.tabLayout.getSelectedTabPosition());
            Toast.makeText(showMyPlaceDetailInfo, "수정 완료", Toast.LENGTH_SHORT).show();
        });

        int timeLinePosition = 0;
        Log.e(TAG, "dataUpdate: BottomNavi.timeLines.size() : " + BottomNavi.timeLines.size());

        for (int i = 0; i < BottomNavi.timeLines.size(); i++) {
            Log.e(TAG, "dataUpdate: 111");
            Log.e(TAG, "dataUpdate: strPlaceName : " + strPlaceName);
            if (BottomNavi.timeLines.get(i).getPlaceName().equals(strPlaceName)) {
                timeLinePosition = i;
                Log.e(TAG, "dataUpdate: 222");

                Map<String, Object> timeLineTaskMap = new HashMap<String, Object>();
                timeLineTaskMap.put("/categoryName", clickedCategoryName);
                timeLineTaskMap.put("/PlaceName", strPlaceName);

                myRef.child("TimeLine")
                        .child(BottomNavi.timeLineDataKeyList.get(timeLinePosition)).updateChildren(timeLineTaskMap)
                        .addOnCompleteListener(task -> {
                            Log.e(TAG, "TimeLineData update :  완료됨");
                            TimeLinePage.singlton.timeLineAdapterNotify();
                        });
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (myBottomSheet != null && myBottomSheet.bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            myBottomSheet.setHidden();
        } else if (isEditOpen) {
            placeName.setText(strPlaceName);
            categoryName.setText(strCategoryName);
            comment.setText(strComment);
            phone.setText(strPhone);
            comment.setText(strComment);
            isEditOpen = false;
            btnComplete.setVisibility(View.GONE);
            timeline_in_place_detail.setVisibility(View.VISIBLE);
            enableEditText(false);
            getSupportActionBar().setTitle("상세보기");

        } else {
            super.onBackPressed();

            BottomNavi.checkedPositions.clear();
        }
    }


    @Override
    protected void onResume() {
        ListFragment.CATEGORYLIST_NUMBER = 3;
        mapView = new MapView(this);
        mapViewContainer = findViewById(R.id.map);
        mapViewContainer.addView(mapView);

        if (mapViewContainer == null) map.addView(MapFragment.singlton.mapView);

        if (mapViewContainer != null) {

            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

            MapPOIItem marker = new MapPOIItem();
            mapPoint = new MapPoint[1];

            latitude = Double.parseDouble(intent.getExtras().getString("latitude")); //latitude가 y
            longitude = Double.parseDouble(intent.getExtras().getString("longitude"));//longitude가 x
            mapPoint[0] = MapPoint.mapPointWithGeoCoord(latitude, longitude);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            marker.setItemName(intent.getExtras().getString("placeName"));
            marker.setMapPoint(mapPoint[0]);
            marker.setTag(0);
            mapView.addPOIItem(marker);
            mapView.selectPOIItem(marker, true);
            mapView.setMapCenterPoint(mapPoint[0], true);

        }
        super.onResume();
    }


    private void navigationConnect(String navigationType, int navigationNum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(navigationType + "으로 연결하시겠습니까?");
        builder.setPositiveButton("연결", (dialog, which) -> {

            switch (navigationNum) {
                case NAVER_NAVIGATION:
                    Util.showNaviNaver(strPlaceName, longitude, latitude, showMyPlaceDetailInfo);
                    break;
                case KAKAO_NAVIGATION:
                    Util.showNaviKakao(strPlaceName, longitude, latitude, showMyPlaceDetailInfo);
                    break;
                case TMAP_NAVIGATION:
//                    dialog.dismiss();

                    break;
                default:
                    break;
            }

            dialog.dismiss();

        }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        final AlertDialog dialog = builder.create();
        dialog.show();
    }



    public void deletePlace(String placeName) {

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("삭제중");
        dialog.show();

        ArrayList<String> keyList = new ArrayList<>();
        for (int i = 0; i < BottomNavi.timeLines.size(); i++) {
            if (BottomNavi.timeLines.get(i).getPlaceName().equals(placeName)) {
                keyList.add(BottomNavi.timeLineDataKeyList.get(i));
            }
        }

        Log.e(TAG, "deletePlace: 선택 키값 " + BottomNavi.placeDataKeyList.get(getPosition));

        Util.getFirebaseDatabase().getReference("users").child(UserInfo.getID().replaceAll("\\.", "")).child("PlaceData")
                .child(BottomNavi.placeDataKeyList.get(getPosition))
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.e(TAG, "deletePlace: 데이터 삭제 성공");
                    Toast.makeText(showMyPlaceDetailInfo, "삭제 완료", Toast.LENGTH_SHORT).show();

                    if (keyList.size() > 0) {

                        for (int k = 0; k < keyList.size(); k++) {

                            Util.getFirebaseDatabase().getReference("users").
                                    child(UserInfo.getID().replaceAll("\\.", "")).
                                    child("TimeLine").child(keyList.get(k)).
                                    removeValue().
                                    addOnSuccessListener(aVoid2 -> {
                                        Log.e(TAG, "onSuccess: 타임라인 제거 성공");
                                        if (TimeLinePage.singlton != null) {
                                            TimeLinePage.singlton.setTimeLineSpinner();
                                            TimeLinePage.singlton.setAdapter(BottomNavi.timeLines);
                                        }

                                        if (ListFragment.singlton != null) {
                                            ListFragment.singlton.setCategoryTabLayout();
                                            ListFragment.singlton.setAdapter(0);
                                        }

                                        dialog.dismiss();
                                        finish();
                                    }).
                                    addOnFailureListener(e -> {
                                        dialog.dismiss();
                                        Log.e(TAG, "onFailure: 타임라인 삭제 실패 : " + e);
                                    });
                        }

                    } else {
                        dialog.dismiss();
                        finish();
                    }

                })
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Log.e(TAG, "deletePlace: 데이터 삭제 실패");
                    Toast.makeText(showMyPlaceDetailInfo, "네트워크가 원활하지 않습니다. 다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                });


    }



    @Override
    protected void onPause() {
        mapViewContainer.removeView(mapView);
        mapView = null;
        super.onPause();
    }


    @Override
    protected void onDestroy() {

        mapViewContainer.removeView(mapView);
        mapView = null;
        super.onDestroy();
    }
}