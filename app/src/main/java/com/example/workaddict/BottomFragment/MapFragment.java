package com.example.workaddict.BottomFragment;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.workaddict.BottomNavi;
import com.example.workaddict.BottomSheet.ClickedBottomSheetContent;
import com.example.workaddict.BottomSheet.PlaceListBottomSheetContent;
import com.example.workaddict.DataClass.CategoryData;
import com.example.workaddict.DataClass.CategoryResult;
import com.example.workaddict.DataClass.MapPressResult;
import com.example.workaddict.DataClass.PlaceData;
import com.example.workaddict.Fragment.PlaceListAdapter;
import com.example.workaddict.Interface.BackButton;
import com.example.workaddict.MasterApplication;
import com.example.workaddict.R;
import com.example.workaddict.SaveSharedPreferences;
import com.example.workaddict.Utility.Util;
import com.example.workaddict.databinding.ActivityMapFragmentBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapFragment extends Fragment implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener,
        MapView.MapViewEventListener, MapView.POIItemEventListener, View.OnClickListener, BackButton {
    public static MapFragment singlton;
    public ActivityMapFragmentBinding b;
    public static boolean isMyCategoryList = false;
    public static int FRAGMENT_NUM = 0;
    public ViewGroup mapViewContainer;
    public MapView mapView;
    private static final String TAG = "MapFragment";
    private String address = "";
    private boolean setMarker = false;
    public MasterApplication masterApplication;
    public MapPOIItem[] markers;
    public MapPoint[] mapPoints;
    private double latitude;  //y
    private double longitude; //x
    private double firstLatitude;
    private double firstLongitude;
    private boolean firstOnCreate = true;
    public String selectedMarkerTag = "";
    public boolean isUpKeyboard = false;
    private long backBtnTime = 0;
    public boolean createView = false;
    private MapPOIItem mapPOIItem;
    private Animation scrollDown;
    private boolean searchBoxVisibility = false;
    public int selectedCategoryPosition = 0;
    private double mapCenterLatitude;
    private double mapCenterLongitude;
    private boolean clickedCurrentmapCenter = false;
    public int tagNum = 0;
    private View clickedPlaceBottomSheet;
    private View placeListBottomSheet;
    public BottomSheetBehavior behavior;
    public BottomSheetBehavior behaviorList;
    public ClickedBottomSheetContent clickedBottomSheetContent;
    public PlaceListBottomSheetContent placeListBottomSheetContent;
    public int selectPosition;
    public ArrayList<PlaceData> categoryPlaceData = new ArrayList<>();
    private boolean showCategoryBox = false;
    private String currentCategoryColor = "";


    public static MapFragment newInstance() {
        if (singlton == null) {
            singlton = new MapFragment();
        }
        return singlton;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DataBindingUtil.inflate(inflater, R.layout.activity_map_fragment, container, false);
        createView = true;
        Log.e(TAG, "onCreateView: mapFragment");


        scrollDown = AnimationUtils.loadAnimation(getActivity(), R.anim.scroll_slide_down);

        clickedPlaceBottomSheet = b.coordinatorBottomSheetWrap.findViewById(R.id.clickedPlaceBottomSheet);
        behavior = BottomSheetBehavior.from(clickedPlaceBottomSheet);
        behavior.setHideable(true);

        placeListBottomSheet = b.coordinatorBottomSheetWrap.findViewById(R.id.placeListBottomSheet);
        behaviorList = BottomSheetBehavior.from(placeListBottomSheet);
        behaviorList.setHideable(true);

        if (clickedBottomSheetContent != null) clickedBottomSheetContent.dismiss();
        if (placeListBottomSheetContent != null) placeListBottomSheetContent.dismiss();

        b.btnUnClickedGps.setVisibility(View.GONE);
        b.btnClickedGps.setVisibility(View.VISIBLE);

        zoomInAndOut(b.btnPlus);
        zoomInAndOut(b.btnMinus);
        b.btnClickedPlaceInfo2.setVisibility(View.GONE);

        b.etSearch.setText("");
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setupListener();
        searchBoxAnim(false);

        categoryFilterButtonSetting();
        b.mapCategoryButtonWrap.setVisibility(View.GONE);

        return b.getRoot();
    }


    /**
     * 카테고리 필터 보여주기 세팅
     */
    private void categoryFilterButtonSetting() {

        Log.e(TAG, "categoryFilterButtonSetting: 진입");
        if (BottomNavi.categoryData.size() > 0) {
            b.btnSeeMyCategory.setVisibility(View.VISIBLE);

            switch (BottomNavi.categoryData.size()) {
                case 1:
                    b.categoryOrange.setVisibility(View.GONE);
                    b.categoryYellow.setVisibility(View.GONE);
                    b.categoryGreen.setVisibility(View.GONE);
                    b.categoryBlue.setVisibility(View.GONE);
                    b.categoryIndigo.setVisibility(View.GONE);
                    b.categoryPurple.setVisibility(View.GONE);
                    break;
                case 2:
                    b.categoryYellow.setVisibility(View.GONE);
                    b.categoryGreen.setVisibility(View.GONE);
                    b.categoryBlue.setVisibility(View.GONE);
                    b.categoryIndigo.setVisibility(View.GONE);
                    b.categoryPurple.setVisibility(View.GONE);
                    break;
                case 3:
                    b.categoryGreen.setVisibility(View.GONE);
                    b.categoryBlue.setVisibility(View.GONE);
                    b.categoryIndigo.setVisibility(View.GONE);
                    b.categoryPurple.setVisibility(View.GONE);
                    break;
                case 4:
                    b.categoryBlue.setVisibility(View.GONE);
                    b.categoryIndigo.setVisibility(View.GONE);
                    b.categoryPurple.setVisibility(View.GONE);
                    break;
                case 5:
                    b.categoryIndigo.setVisibility(View.GONE);
                    b.categoryPurple.setVisibility(View.GONE);
                    break;
                case 6:
                    b.categoryPurple.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void setupListener() {
        b.btnSeeMyCategory.setOnClickListener(this);
        b.IbSearchClose.setOnClickListener(this);
        b.btSearch.setOnClickListener(this);
        b.btnUnClickedGps.setOnClickListener(this);
        b.btnClickedGps.setOnClickListener(this);
        b.btnClickedPlaceInfo2.setOnClickListener(this);
        b.currentPlaceSearch.setOnClickListener(this);
        b.categoryRed.setOnClickListener(this);
        b.categoryOrange.setOnClickListener(this);
        b.categoryYellow.setOnClickListener(this);
        b.categoryGreen.setOnClickListener(this);
        b.categoryBlue.setOnClickListener(this);
        b.categoryIndigo.setOnClickListener(this);
        b.categoryPurple.setOnClickListener(this);
        BottomNavi.bottomNavi.b.searchMap.setOnClickListener(this);

        b.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            switch (actionId) {
                case EditorInfo.IME_ACTION_SEARCH:
                    addressSearchButton();
                    break;
                default:
                    return false;
            }
            return true;
        });


        b.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count > 0) {
                    b.currentPlaceSearch.setVisibility(View.VISIBLE);
                } else {
                    b.currentPlaceSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    b.IbSearchClose.setVisibility(View.VISIBLE);
                    b.currentPlaceSearch.setVisibility(View.VISIBLE);
                } else {
                    b.IbSearchClose.setVisibility(View.GONE);
                    b.currentPlaceSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    b.IbSearchClose.setVisibility(View.VISIBLE);
                    b.currentPlaceSearch.setVisibility(View.VISIBLE);
                } else {
                    b.IbSearchClose.setVisibility(View.GONE);
                    b.currentPlaceSearch.setVisibility(View.GONE);
                }
            }
        });
    }


    private void showMyPlaceListInMap(ArrayList<PlaceData> placeData, ArrayList<CategoryData> bottomNaviCategoryData) {
        //latitude와 longitude의 순서 바뀌면 오류남
        Log.e("좌표", latitude + ", " + longitude);

        resetMarkers();
        if (isMyCategoryList) {
            //기존 지도 마커들 다 지울것
            //클리어시키면 Bottomnavi.placeData까지 clear된다 . 왜그런지 이유 찾아야함
//            categoryPlaceData.clear();
            categoryPlaceData = placeData;
            if (placeData.size() > 0) {
                markers = new MapPOIItem[placeData.size()];
                mapPoints = new MapPoint[placeData.size()];

                for (int i = 0; i < placeData.size(); i++) {
                    longitude = Double.parseDouble(placeData.get(i).getLongitude());//longitude가 x
                    latitude = Double.parseDouble(placeData.get(i).getLatitude()); //latitude가 y
                    mapPoints[i] = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                    MapPOIItem marker = new MapPOIItem();

//                    marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                    marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); //마커 타입 지정

                    Bitmap bitmap = Util.getMarkerDrawableType(getActivity(), placeData.get(i), bottomNaviCategoryData);
                    marker.setCustomImageBitmap(bitmap);

                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);//마커가 선택되었을 때의 타입 지정
                    marker.setCustomSelectedImageBitmap(Util.getBitmapFromVectorDrawable(getActivity(), R.drawable.marker_black));

//                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
//                    marker.setCustomImageAnchor(1.5f, 1.0f);
//                    marker.setCustomImageAutoscale(true);


//                    marker.setCustomSelectedImageResourceId(R.drawable.marker_purple_20);
                    marker.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(30, 0));
                    marker.setItemName(placeData.get(i).getPlaceName());
                    marker.setMapPoint(mapPoints[i]);
                    marker.setTag(i);

                    marker.setItemName(placeData.get(i).getPlaceName());
                    markers[i] = marker;

                }

                for (MapPOIItem marker : markers) {
                    mapView.selectPOIItem(marker, false);
                }

                mapView.addPOIItems(markers);
                mapView.fitMapViewAreaToShowMapPoints(mapPoints);//fitMapViewAreaToShowMapPoints는 위에서 찍은 마커들이 한 화면에 보이도록 설정함
                setMarker = true;
                b.btnClickedPlaceInfo2.setVisibility(View.VISIBLE);

            }

        }

    }


    public void mapSetting() {
        //맵 트래킹 허용 여부
        b.btnUnClickedGps.setVisibility(View.VISIBLE);
        b.btnClickedGps.setVisibility(View.GONE);
        mapView.setShowCurrentLocationMarker(true);
        //맵 타일 캐시 허용여부, true로 해놓으면 맵 줌인아웃 시 버벅거림 감소
        mapView.setMapTilePersistentCacheEnabled(true);
        // 고해상도 가능 여부 설정
        mapView.setHDMapTileEnabled(true);
        mapView.setMapRotationAngle(360, false);
        mapView.setZoomLevel(-4, true);
        mapView.removeAllPOIItems();
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapView.setCurrentLocationEventListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSeeMyCategory:

                currentCategoryColor = Util.CATEGORY_COLOR_BLACK;
                Log.e(TAG, "onClick: isMyCategoryList : " + isMyCategoryList);
                if (showCategoryBox) {
                    showCategoryBox = false;
                    b.mapCategoryButtonWrap.setVisibility(View.GONE);
                } else {
                    showCategoryBox = true;
                    b.mapCategoryButtonWrap.setVisibility(View.VISIBLE);
                }


                if (!isMyCategoryList) {


                    if (BottomNavi.bottomNavi.documents != null && BottomNavi.bottomNavi.documents.size() > 0) {
                        BottomNavi.bottomNavi.documents.clear();
                    }

                    if (BottomNavi.addressDocuments != null && BottomNavi.addressDocuments.size() > 0) {
                        BottomNavi.addressDocuments.clear();
                    }

                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                    b.btnUnClickedGps.setVisibility(View.VISIBLE);
                    b.btnClickedGps.setVisibility(View.GONE);

                    isMyCategoryList = true;
                    showMyPlaceListInMap(BottomNavi.placeData, BottomNavi.categoryData);

                }

                break;
            case R.id.btSearch:
                if (b.etSearch.getText().toString().length() > 0) addressSearchButton();
                break;
            case R.id.etSearch:
                FRAGMENT_NUM = 0;
                isUpKeyboard = true;
                break;
            case R.id.IbSearchClose:
                b.etSearch.setText("");
                break;
            case R.id.searchMap:
                if (searchBoxVisibility) {
                    searchBoxAnim(false);
                } else {
                    searchBoxAnim(true);
                }
                break;
            case R.id.btnUnClickedGps:
                //맵의 현재 위치 표시 설정
                setCurrentLocation();
                break;
            case R.id.btnClickedGps:
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                b.btnUnClickedGps.setVisibility(View.VISIBLE);
                b.btnClickedGps.setVisibility(View.GONE);
                break;
            case R.id.currentPlaceSearch:
                clickedCurrentmapCenter = true;
                try {
                    getAddressMapCenter();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ibMapPageBack:
                searchBoxAnim(false);
                break;
            case R.id.btnClickedPlaceInfo2:
                if (isMyCategoryList) setPlaceListBottomSheet(categoryPlaceData);
                else setPlaceListBottomSheet(BottomNavi.placeData);
                break;
            case R.id.categoryRed:
                showCategoryBox = false;
                b.mapCategoryButtonWrap.setVisibility(View.GONE);
                ArrayList<PlaceData> placeDataInstance = BottomNavi.placeData;
                currentCategoryColor = Util.CATEGORY_COLOR_RED;
                showMyPlaceListInMap(setCategoryPlaceData(placeDataInstance, BottomNavi.categoryData, Util.CATEGORY_COLOR_RED), BottomNavi.categoryData);
                Log.e(TAG, "onClick: 레드 클릭");
                break;
            case R.id.categoryOrange:
                showCategoryBox = false;
                b.mapCategoryButtonWrap.setVisibility(View.GONE);
                currentCategoryColor = Util.CATEGORY_COLOR_ORANGE;
                showMyPlaceListInMap(setCategoryPlaceData(BottomNavi.placeData, BottomNavi.categoryData, Util.CATEGORY_COLOR_ORANGE), BottomNavi.categoryData);
                Log.e(TAG, "onClick: 오렌지 클릭");
                break;
            case R.id.categoryYellow:
                showCategoryBox = false;
                b.mapCategoryButtonWrap.setVisibility(View.GONE);
                currentCategoryColor = Util.CATEGORY_COLOR_YELLOW;
                showMyPlaceListInMap(setCategoryPlaceData(BottomNavi.placeData, BottomNavi.categoryData, Util.CATEGORY_COLOR_YELLOW), BottomNavi.categoryData);
                Log.e(TAG, "onClick: 노랑 클릭");
                break;
            case R.id.categoryGreen:
                showCategoryBox = false;
                b.mapCategoryButtonWrap.setVisibility(View.GONE);
                currentCategoryColor = Util.CATEGORY_COLOR_GREEN;
                showMyPlaceListInMap(setCategoryPlaceData(BottomNavi.placeData, BottomNavi.categoryData, Util.CATEGORY_COLOR_GREEN), BottomNavi.categoryData);
                Log.e(TAG, "onClick: 그린 클릭");
                break;
            case R.id.categoryBlue:
                showCategoryBox = false;
                b.mapCategoryButtonWrap.setVisibility(View.GONE);
                currentCategoryColor = Util.CATEGORY_COLOR_BLUE;
                showMyPlaceListInMap(setCategoryPlaceData(BottomNavi.placeData, BottomNavi.categoryData, Util.CATEGORY_COLOR_BLUE), BottomNavi.categoryData);
                Log.e(TAG, "onClick: 블루 클릭");
                break;
            case R.id.categoryIndigo:
                showCategoryBox = false;
                b.mapCategoryButtonWrap.setVisibility(View.GONE);
                currentCategoryColor = Util.CATEGORY_COLOR_INDIGO;
                showMyPlaceListInMap(setCategoryPlaceData(BottomNavi.placeData, BottomNavi.categoryData, Util.CATEGORY_COLOR_INDIGO), BottomNavi.categoryData);
                Log.e(TAG, "onClick: 남색 클릭");
                break;
            case R.id.categoryPurple:
                showCategoryBox = false;
                b.mapCategoryButtonWrap.setVisibility(View.GONE);
                currentCategoryColor = Util.CATEGORY_COLOR_PURPLE;
                showMyPlaceListInMap(setCategoryPlaceData(BottomNavi.placeData, BottomNavi.categoryData, Util.CATEGORY_COLOR_PURPLE), BottomNavi.categoryData);
                Log.e(TAG, "onClick: 보라 클릭");
                break;
            default:
                break;
        }
    }


    private ArrayList<PlaceData> setCategoryPlaceData(ArrayList<PlaceData> bottomNaviPlaceData,
                                                      ArrayList<CategoryData> bottomNaviCategoryData,
                                                      String categoryColor) {

        ArrayList<PlaceData> placeData = new ArrayList<>();
        String categoryName = "";

        for (CategoryData categoryData : bottomNaviCategoryData) {
            if (categoryData.getColor().equals(categoryColor)) {
                categoryName = categoryData.getCD();
                break;
            }
        }

        for (PlaceData pd : bottomNaviPlaceData) {
            if (pd.getCategoryName().equals(categoryName)) {
                placeData.add(pd);
            }
        }

        return placeData;

    }


    /**
     * 검색창 애니메이션 설정
     *
     * @param show
     */
    public void searchBoxAnim(boolean show) {
        searchBoxVisibility = show;
        b.mapPageSearchBox.setVisibility(View.GONE);
        if (show) {
            b.mapPageSearchBox.setVisibility(View.VISIBLE);
            b.mapPageSearchBox.requestFocus();
            Util.showKeyboard(getActivity());
            b.mapPageSearchBox.startAnimation(scrollDown);
            BottomNavi.bottomNavi.b.ibMapPageBack.setVisibility(View.VISIBLE);
            BottomNavi.bottomNavi.b.searchMap.setVisibility(View.GONE);
            BottomNavi.bottomNavi.b.tvMapPageAppName.setVisibility(View.GONE);

        } else {

            if (BottomNavi.bottomNavi.b.ibMapPageBack.getVisibility() == View.VISIBLE) {
                BottomNavi.bottomNavi.b.ibMapPageBack.setVisibility(View.GONE);
            }

            BottomNavi.bottomNavi.b.searchMap.setVisibility(View.VISIBLE);
            BottomNavi.bottomNavi.b.tvMapPageAppName.setVisibility(View.VISIBLE);
            b.etSearch.setText("");
            b.mapPageSearchBox.setVisibility(View.GONE);
        }

    }


    private void addressSearchButton() {
        try {
            address = b.etSearch.getText().toString();
//            getAddress();
//            getAddressMapCenter();
            getAddressCurrentPlace();
            //검색버튼 누르면 트래킹모드 끄기
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            b.btnUnClickedGps.setVisibility(View.VISIBLE);
            b.btnClickedGps.setVisibility(View.GONE);
            resetMarkers();          //기존에 있던 마커 지우기
            b.btnClickedPlaceInfo2.setVisibility(View.VISIBLE);
            Util.hideKeyboard(getActivity());
            isMyCategoryList = false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 현재위치 기준 2키로 반경 가까운 순서대로 검색
     */
    public void getAddressCurrentPlace() throws UnsupportedEncodingException {


        resetMarkers();

        masterApplication.service.getAddress(address, firstLongitude, firstLatitude, "accuracy").enqueue(new Callback<CategoryResult>() {
            @Override
            public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (BottomNavi.bottomNavi.documents != null) {
                            BottomNavi.bottomNavi.documents.clear();
                        }
                        CategoryResult result = response.body();
                        BottomNavi.bottomNavi.documents = result.getDocuments();

                        showSearchKeyword();
                    }


                } else Log.e("isSuccessful :  ", "실패");
            }

            @Override
            public void onFailure(Call<CategoryResult> call, Throwable t) {
                Log.e("onFailure ", "진입");
            }
        });
    } // 검색한 주소 정보 얻어오기

    /**
     * 현재 지도의 중앙 기준 2키로 반경 가까운 순서대로 검색
     */
    public void getAddressMapCenter() throws UnsupportedEncodingException {
        resetMarkers();
        if (BottomNavi.bottomNavi.documents != null) {
            BottomNavi.bottomNavi.documents.clear();
        }

        if (mapCenterLatitude > 0) {

        } else {
            mapCenterLatitude = firstLatitude;
            mapCenterLongitude = firstLongitude;
        }


        masterApplication.service
                .getAddress(address, mapCenterLongitude, mapCenterLatitude, "distance").enqueue(new Callback<CategoryResult>() {
            @Override
            public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        CategoryResult result = response.body();
                        BottomNavi.bottomNavi.documents = result.getDocuments();

                        showSearchKeyword();
                    }


                } else Log.e("isSuccessful :  ", "실패");
            }

            @Override
            public void onFailure(Call<CategoryResult> call, Throwable t) {
                Log.e("onFailure ", "진입");
            }
        });
    } // 검색한 주소 정보 얻어오기


    /**
     * 정확도 검색
     */
    public void getAddress() throws UnsupportedEncodingException {
        resetMarkers();

        masterApplication.service
                .getAddress(address).enqueue(new Callback<CategoryResult>() {
            //                .getAddress(address).enqueue(new Callback<CategoryResult>() {
            @Override
            public void onResponse(Call<CategoryResult> call, Response<CategoryResult> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        if (BottomNavi.bottomNavi.documents != null)
                            BottomNavi.bottomNavi.documents.clear();
                        CategoryResult result = response.body();
                        BottomNavi.bottomNavi.documents = result.getDocuments();

                        if (BottomNavi.bottomNavi.documents != null && BottomNavi.bottomNavi.documents.size() == 0) {
                            if (BottomNavi.addressDocuments.size() == 0) {
//                                Toast.makeText(getActivity(), "검색명을 확인해주세요", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onResponse: 검색명을 확인해주세요");
                            }

                        } else {
                            showSearchKeyword();
                        }
                    }

                } else Log.e("isSuccessful :  ", "실패");
            }

            @Override
            public void onFailure(Call<CategoryResult> call, Throwable t) {
                Log.e("onFailure ", "진입");
            }
        });
    } // 검색한 주소 정보 얻어오기


    /**
     * 중심점 기준 검색
     *
     * @param longitude
     * @param latitude
     * @throws UnsupportedEncodingException
     */
    public void getAddress(double longitude, double latitude) throws UnsupportedEncodingException {
        masterApplication.service
                .getAddressByLatLon(longitude, latitude).enqueue(new Callback<MapPressResult>() {
            @Override
            public void onResponse(Call<MapPressResult> call, Response<MapPressResult> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        MapPressResult result = response.body();
                        if (result != null) {
                            try {

                                if (result.getPressedAddresses().get(0).getAddress().getAddress_name() != null) {
                                    address = result.getPressedAddresses().get(0).getAddress().getAddress_name();
                                } else if (result.getPressedAddresses().get(0).getRoadAddress().getAddress_name() != null) {
                                    address = result.getPressedAddresses().get(0).getRoadAddress().getAddress_name();
                                } else {
                                    Util.showToast("다른 장소를 클릭해주세요");
                                }

                                getAddress();

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        if (BottomNavi.addressDocuments != null && BottomNavi.addressDocuments.size() == 0) {
//                            Util.showToast("다시 눌러주세요");
                        }
                    }
                } else Log.e("isSuccessful :  ", "실패");
            }

            @Override
            public void onFailure(Call<MapPressResult> call, Throwable t) {

            }

        });
    } // 검색한 주소 정보 얻어오기

    /**
     * 호출한 주소정보들 지도에 마커 찍기
     */
    private void showSearchKeyword() {
        currentCategoryColor = Util.CATEGORY_COLOR_GRAY;
        //latitude와 longitude의 순서 바뀌면 오류남
        resetMarkers();
        if (BottomNavi.bottomNavi.documents != null && BottomNavi.bottomNavi.documents.size() > 0) {
            markers = new MapPOIItem[BottomNavi.bottomNavi.documents.size()];
            mapPoints = new MapPoint[BottomNavi.bottomNavi.documents.size()];

            for (int i = 0; i < BottomNavi.bottomNavi.documents.size(); i++) {
                latitude = Double.parseDouble(BottomNavi.bottomNavi.documents.get(i).getY()); //latitude가 y
                longitude = Double.parseDouble(BottomNavi.bottomNavi.documents.get(i).getX());//longitude가 x
                mapPoints[i] = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                MapPOIItem marker = new MapPOIItem();


//                marker.setMarkerType(MapPOIItem.MarkerType.CustomImage); //마커 타입 지정

                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); //마커 타입 지정
//                marker.setCustomImageResourceId(R.drawable.marker_light_red); //마커의 이미지
//                marker.setCustomImageResourceId(new CustomMarkerIcon().typeSwich(BottomNavi.bottomNavi.documents.get(i).getCategoryGroupCode())); //마커의 이미지
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

//                marker.setCustomImageAnchor(1.5f, 1.0f);
//                marker.setCustomImageAutoscale(true);
//                marker.setCustomSelectedImageResourceId(R.drawable.marker_purple);
                marker.setItemName(BottomNavi.bottomNavi.documents.get(i).getPlaceName());
                marker.setMapPoint(mapPoints[i]);
                marker.setTag(i);
                markers[i] = marker;
            }

            mapView.addPOIItems(markers);
            mapView.fitMapViewAreaToShowMapPoints(mapPoints);//fitMapViewAreaToShowMapPoints는 위에서 찍은 마커들이 한 화면에 보이도록 설정함
            mapView.selectPOIItem(markers[0], true);
            selectedMarkerTag = "0";
            tagNum = 0;
            mapView.setMapCenterPoint(mapPoints[0], true);
            setMarker = true;
            b.btnClickedPlaceInfo2.setVisibility(View.VISIBLE);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);


            if (isMyCategoryList) {
                setClickedBottomSheetContent(categoryPlaceData, currentCategoryColor);
            } else {
                setClickedBottomSheetContent(BottomNavi.placeData, currentCategoryColor);
            }


        } else {

            if (!clickedCurrentmapCenter) {
                try {
                    getAddress();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        clickedCurrentmapCenter = false;
    }


    /**
     * 클릭된 장소 바텀시트로 보기
     */
    public void setClickedBottomSheetContent(ArrayList<PlaceData> placeData, String currentCategoryColor) {
        Log.e(TAG, "setClickedBottomSheetContent: ");
        if (clickedBottomSheetContent != null) clickedBottomSheetContent.dismiss();
        if (placeListBottomSheetContent != null) placeListBottomSheetContent.dismiss();
        clickedBottomSheetContent = new ClickedBottomSheetContent(behavior, isMyCategoryList, clickedPlaceBottomSheet,
                BottomNavi.bottomNavi.documents, placeData, tagNum, getActivity(), BottomNavi.timeLines, currentCategoryColor);
        clickedBottomSheetContent.setTask();
    }


    /**
     * 클릭된 장소 바텀시트로 보기
     */
    public void setClickedBottomSheetContent(ArrayList<PlaceData> placeData) {
        Log.e(TAG, "setClickedBottomSheetContent: ");
        if (clickedBottomSheetContent != null) clickedBottomSheetContent.dismiss();
        if (placeListBottomSheetContent != null) placeListBottomSheetContent.dismiss();
        clickedBottomSheetContent = new ClickedBottomSheetContent(behavior, isMyCategoryList, clickedPlaceBottomSheet,
                BottomNavi.bottomNavi.documents, BottomNavi.placeData, tagNum, getActivity(), BottomNavi.timeLines);
        clickedBottomSheetContent.setTask();
    }


    /**
     * 장소목록 보기
     */
    private void setPlaceListBottomSheet(ArrayList<PlaceData> placeData) {
        if (clickedBottomSheetContent != null) clickedBottomSheetContent.dismiss();
        if (placeListBottomSheetContent != null) placeListBottomSheetContent.dismiss();
        placeListBottomSheetContent = new PlaceListBottomSheetContent(placeListBottomSheet, BottomNavi.bottomNavi.documents,
                placeData, isMyCategoryList, behaviorList, getActivity());
        placeListBottomSheetContent.setTask();
    }


    /**
     * 현재위치로 이동
     */
    private void setCurrentLocation() {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        b.btnUnClickedGps.setVisibility(View.GONE);
        b.btnClickedGps.setVisibility(View.VISIBLE);
        //현재위치 표시설정할 때 테두리 설정
        mapView.setCurrentLocationRadius(20);
        mapView.setCurrentLocationRadiusStrokeColor(getActivity().getColor(R.color.current_gps_border));
        mapView.setCurrentLocationRadiusFillColor(getActivity().getColor(R.color.current_gps_border_in));
        mapView.setZoomLevel(-2, true);

    }

    private void zoomInAndOut(ImageButton imageButton) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnPlus:
                        mapView.zoomIn(true);
                        break;
                    case R.id.btnMinus:
                        mapView.zoomOut(true);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void resetMarkers() {
        if (clickedBottomSheetContent != null) clickedBottomSheetContent.dismiss();
        if (placeListBottomSheetContent != null) placeListBottomSheetContent.dismiss();

        if (setMarker) {
            if (markers != null) {
                if (markers.length > 0) {
                    b.btnClickedPlaceInfo2.setVisibility(View.GONE);
                }
            }
        }
        mapView.removeAllPOIItems();
        setMarker = false;
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {

    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        firstLatitude = mapPointGeo.latitude;
        firstLongitude = mapPointGeo.longitude;
//        Log.e(TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, v));
//        Log.e(TAG, "onCurrentLocationUpdate: " + firstLatitude);

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        mapCenterLatitude = mapPointGeo.latitude;
        mapCenterLongitude = mapPointGeo.longitude;
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        Log.e(TAG, "onMapViewSingleTapped: zoomLevel : " + mapView.getZoomLevel());


        if (mapView.getZoomLevel() < 3) {

            if (BottomNavi.bottomNavi.documents != null) BottomNavi.bottomNavi.documents.clear();

            isMyCategoryList = false;
            if (mapPOIItem != null && selectedMarkerTag.length() == 1) {
                mapView.deselectPOIItem(mapPOIItem);
            } else if (selectedMarkerTag.length() > 1) {
                for (int i = 0; i < markers.length; i++) {
                    mapView.deselectPOIItem(markers[i]);
                    selectedMarkerTag = "";
                }
            }

            mapView.getMapCenterPoint();
            MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
            longitude = mapPointGeo.longitude;
            latitude = mapPointGeo.latitude;
            try {
                getAddress(longitude, latitude);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            b.btnUnClickedGps.setVisibility(View.VISIBLE);
            b.btnClickedGps.setVisibility(View.GONE);

        } else {
            Toast.makeText(getActivity(), "지도를 확대해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {


    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }


    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        tagNum = mapPOIItem.getTag();
        if (mapPOIItem.getTag() >= 0) {
            selectedMarkerTag = String.valueOf(mapPOIItem.getTag());

            mapView.setMapCenterPoint(mapPoints[mapPOIItem.getTag()], true);
            Util.hideKeyboard(getActivity());
            isUpKeyboard = false;

            //여기서 바텀시트 수정하기
            if (isMyCategoryList) {
                setClickedBottomSheetContent(categoryPlaceData, currentCategoryColor);
            } else {
                setClickedBottomSheetContent(BottomNavi.placeData);
            }

        }


    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Toast.makeText(getActivity(), "말풍선 터치", Toast.LENGTH_SHORT).show();
        if (isMyCategoryList) {
            setClickedBottomSheetContent(categoryPlaceData, currentCategoryColor);
        } else {
            setClickedBottomSheetContent(BottomNavi.placeData,currentCategoryColor);
        }

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }


    public void showPopup() {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view1 = LayoutInflater.from(getActivity()).
                inflate(R.layout.activity_popup, null, false);
        builder.setView(view1);

        EditText et_place_name = view1.findViewById(R.id.et_place_name);
        ImageButton closeInputCategoryNameInPopup = view1.findViewById(R.id.closeInputCategoryNameInPopup);
        TextView categoryNameLengthInPopup = view1.findViewById(R.id.categoryNameLengthInPopup);


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

        closeInputCategoryNameInPopup.setOnClickListener(view2 -> {

            et_place_name.setText("");
            closeInputCategoryNameInPopup.setVisibility(View.GONE);

        });

        int position = Integer.parseInt(selectedMarkerTag);
        et_place_name.setText(BottomNavi.bottomNavi.documents.get(position).getPlaceName());


        builder.setPositiveButton("확인", (dialogInterface, i) -> {
            boolean isExist = false;
            String placeName = et_place_name.getText().toString().trim();

            if (BottomNavi.placeData.size() > 0) {
                for (PlaceData p : BottomNavi.placeData) {
                    if (p.getPlaceName().equals(placeName)) {
                        isExist = true;
                    }
                }
            }

            if (isExist) {
                Toast.makeText(getActivity(), "동일한 이름의 장소가 있습니다.", Toast.LENGTH_SHORT).show();
            } else {
                String comment = "";
                Log.e(TAG, "onSingleClick: 선택 카테고리 포지션 : " + BottomNavi.categoryData.get(MapFragment.singlton.selectedCategoryPosition).getCD());
                Util.addPlaceData(Util.getFirebaseDatabase()
                        , BottomNavi.categoryData.get(MapFragment.singlton.selectedCategoryPosition).getCD()
                        , BottomNavi.bottomNavi.documents.get(position).getCategoryGroupName()
                        , placeName
                        , BottomNavi.bottomNavi.documents.get(position).getAddressName()
                        , BottomNavi.bottomNavi.documents.get(position).getRoadAddressName()
                        , BottomNavi.bottomNavi.documents.get(position).getY()
                        , BottomNavi.bottomNavi.documents.get(position).getX()
                        , BottomNavi.bottomNavi.documents.get(position).getPhone()
                        , comment
                        , "장소 추가"
                        , "");

//                Util.addTimeLine(BottomNavi.categoryData.get(MapFragment.singlton.selectedCategoryPosition).getCD(),
//                        placeName,
//                        "",
//                        "장소 추가",
//                        "장소 추가", getActivity(),
//                        TimeLinePage.singlton.adapter);

                Toast.makeText(getActivity(), "추가 완료", Toast.LENGTH_SHORT).show();

                if (ClickedBottomSheetContent.myBottomSheet != null)
                    ClickedBottomSheetContent.myBottomSheet.setHidden();
                if (PlaceListAdapter.myBottomSheet != null)
                    PlaceListAdapter.myBottomSheet.setHidden();

                dialogInterface.dismiss();
            }


        }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss());


        final AlertDialog dialog = builder.create();
        dialog.show();

    }


    @Override
    public void onResume() {
        if (SaveSharedPreferences.getPermission(getActivity()).equals("y")) {
            if (mapView == null) {
                mapView = new MapView(getActivity());
                b.mapViewContainer.addView(mapView);
                mapSetting();
                mapView.setCurrentLocationRadius(10);
                mapView.setCurrentLocationRadiusStrokeColor(getActivity().getColor(R.color.current_gps_border));
                mapView.setCurrentLocationRadiusFillColor(getActivity().getColor(R.color.current_gps_border_in));

                if (isMyCategoryList) {
                    showMyPlaceListInMap(BottomNavi.placeData, BottomNavi.categoryData);
                } else if (BottomNavi.bottomNavi.documents != null && BottomNavi.bottomNavi.documents.size() > 0) {
                    showSearchKeyword();
                }

                if (createView) {
                    setCurrentLocation();
                    createView = false;
                }


            }

        }
        super.onResume();
    }


    @Override
    public void onPause() {
        firstOnCreate = false;
        createView = false;

        if (SaveSharedPreferences.getPermission(getActivity()).equals("y")) {
            b.mapViewContainer.removeView(mapView);
            mapViewContainer = null;
            mapView = null;
        }
        super.onPause();
    }


    @Override
    public void onDestroy() {
        createView = false;
        Log.e(TAG, "onDestroy: 진입");
        if (mapView != null) {
            BottomNavi.bottomNavi.dataClear();
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            mapView.setShowCurrentLocationMarker(false);
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (BottomNavi.bottomNavi.b.viewPager.getCurrentItem() == 0) {

            if (searchBoxVisibility) {
                searchBoxAnim(false);
            } else if (FRAGMENT_NUM > 0) {
                FRAGMENT_NUM = 0;
                b.btnClickedPlaceInfo2.setVisibility(View.VISIBLE);
            } else if ((behaviorList.getState() == (BottomSheetBehavior.STATE_EXPANDED)) ||
                    (behavior.getState() == (BottomSheetBehavior.STATE_EXPANDED))) {

                if (clickedBottomSheetContent != null) clickedBottomSheetContent.dismiss();

                if (placeListBottomSheetContent != null) placeListBottomSheetContent.dismiss();

                showCategoryBox = false;
                b.mapCategoryButtonWrap.setVisibility(View.GONE);
                isMyCategoryList = false;
            } else if (selectedMarkerTag.length() > 0) {
                for (int i = 0; i < markers.length; i++) {
                    mapView.deselectPOIItem(markers[i]);
                    selectedMarkerTag = "";
                }
                b.btnClickedPlaceInfo2.setVisibility(View.VISIBLE);
            } else if (setMarker) {
                resetMarkers();
                BottomNavi.bottomNavi.documents = null;
                b.mapCategoryButtonWrap.setVisibility(View.GONE);
                isMyCategoryList = false;
            } else if (b.etSearch.getText().length() > 0) {
                b.etSearch.getText().clear();
                markers = new MapPOIItem[0];
                mapPoints = new MapPoint[0];
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

}

class StringDateComparator implements Comparator<PlaceData> {

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public int compare(PlaceData o1, PlaceData o2) {
        int compareResult = 0;
        try {
            Date a1 = dateFormat.parse(o1.getDateTime());
            Date a2 = dateFormat.parse(o1.getDateTime());
            compareResult = a1.compareTo(a2);
            dateFormat.parse(o1.getDateTime()).compareTo(dateFormat.parse(o2.getDateTime()));
        } catch (ParseException e) {
            e.printStackTrace();
            compareResult = o1.getDateTime().compareTo(o2.getDateTime());
        }
        return compareResult;
    }


}