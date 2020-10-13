package com.example.workaddict;

import android.util.Log;

public class CustomMarkerIcon {

    private static final String TAG = "CustomMarkerIcon";


    public CustomMarkerIcon() {
    }

    public int typeSwich(String type) {
        Log.e(TAG, "typeSwich: 진입" );

        int result = 0;
        switch (type) {
            case "MT1":
                //대형마트
                break;
            case "CS2":
                //편의점
                break;
            case "PS3":
                //어린이집, 유치원
                break;
            case "SC4":
                //학교
                break;
            case "AC5":
                //학원
                break;
            case "PK6":
                //주차장
                break;
            case "OL7":
                //주유소, 충전소
                break;
            case "SW8":
                //지하철역
                break;
            case "BK9":
                //	은행
                break;
            case "CT1":
                //문화시설
                break;
            case "AG2":
                //중개업소
                break;
            case "PO3":
                //공공기관
                break;
            case "AT4":
                //관광명소
                break;
            case "AD5":
                //숙박
                break;
            case "FD6":
                //음식점
                break;
            case "CE7":
                //카페
                break;
            case "HP8":
                //병원
                break;
            case "PM9":
                //약국
                break;
            default:
                Log.e(TAG, "typeSwich: 디폴트 왕관" );
                result = R.drawable.crown;
                break;
        }

        return result;
    }
}
