package com.example.workaddict.popup;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.workaddict.R;
import com.example.workaddict.Utility.Util;
import com.skt.Tmap.TMapTapi;


public class NavigationPopup extends Dialog {
    private static final String TAG = "navigationPopup";
    public static NavigationPopup navigationPopup;
    private String placeName = "";
    private double longitude;
    private double latitude;
    private FragmentActivity fragmentActivity;


    public NavigationPopup(@NonNull Context context, String placeName, double longitude, double latitude, FragmentActivity fragmentActivity) {
        super(context);
        this.placeName = placeName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.fragmentActivity = fragmentActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_popup);
        navigationPopup = this;

        Log.e(TAG, "onCreate: ");
        initView();

    }

    @SuppressLint("SetTextI18n")
    private void initView() {


        ((TextView) findViewById(R.id.connectKakao)).setOnClickListener(v -> {
            Log.e(TAG, "initView: 카카오내비");
            //카카오내비 설치 요구는 안됨
            //원가 요청 패키지 부분에서 에러가 있음, 계속 설치값 true로 반환됨
            Util.showNaviKakao(placeName, longitude, latitude, fragmentActivity);
            this.dismiss();
        });

        ((TextView) findViewById(R.id.connectNaver)).setOnClickListener(v -> {
            Util.showNaviNaver(placeName, longitude, latitude, fragmentActivity);
            this.dismiss();
        });


        final TMapTapi tmaptapi = new TMapTapi(fragmentActivity);
        tmaptapi.setSKTMapAuthentication(Util.TMAP_APP_KEY);

        ((TextView) findViewById(R.id.connectTMap)).setOnClickListener(v -> {

            if (tmaptapi.isTmapApplicationInstalled()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
                builder.setMessage("T-map 내비게이션으로 연결하시겠습니까?");

                builder.setPositiveButton("연결", (DialogInterface.OnClickListener) (dialog, which) -> {
                    Log.e(TAG, "initView: longitude : " + longitude + ", latitude : " + latitude);
                    tmaptapi.invokeRoute(placeName, (float) longitude, (float) latitude);
                    dialog.dismiss();

                }).setNegativeButton("취소", (DialogInterface.OnClickListener) (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();
                
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
                builder.setMessage("T-map 앱을 설치하지 않았습니다.\n다운로드하시겠습니까?");

                builder.setPositiveButton("설치", (dialog, which) -> {
                    try {

                        Uri uri = Uri.parse(tmaptapi.getTMapDownUrl().get(0));
                        Log.e(TAG, "init: result : " + uri);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        fragmentActivity.startActivity(intent);
                        dialog.dismiss();

                    } catch (Exception e) {

                        Toast.makeText(fragmentActivity, "일시적인 오류가 발생했습니다.\n다시 시도해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }

                }).setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();

            }


        });
    }

}
